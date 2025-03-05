package com.simibubi.create.content.logistics.packager;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.Create;
import com.simibubi.create.api.packager.unpacking.UnpackingHandler;
import com.simibubi.create.content.contraptions.actors.psi.PortableStorageInterfaceBlockEntity;
import com.simibubi.create.content.logistics.BigItemStack;
import com.simibubi.create.content.logistics.box.PackageItem;
import com.simibubi.create.content.logistics.crate.BottomlessItemHandler;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBehaviour;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlock;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlockEntity;
import com.simibubi.create.content.logistics.packagePort.frogport.FrogportBlockEntity;
import com.simibubi.create.content.logistics.packagerLink.LogisticallyLinkedBehaviour.RequestType;
import com.simibubi.create.content.logistics.packagerLink.PackagerLinkBlock;
import com.simibubi.create.content.logistics.packagerLink.PackagerLinkBlockEntity;
import com.simibubi.create.content.logistics.packagerLink.RequestPromiseQueue;
import com.simibubi.create.content.logistics.packagerLink.WiFiEffectPacket;
import com.simibubi.create.content.logistics.stockTicker.PackageOrder;
import com.simibubi.create.foundation.advancement.AdvancementBehaviour;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.inventory.CapManipulationBehaviourBase.InterfaceProvider;
import com.simibubi.create.foundation.blockEntity.behaviour.inventory.InvManipulationBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.inventory.VersionedInventoryTrackerBehaviour;
import com.simibubi.create.foundation.item.ItemHelper;

import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.level.block.state.BlockState;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;

public class PackagerBlockEntity extends SmartBlockEntity {

	public boolean redstonePowered;
	public int buttonCooldown;
	public String signBasedAddress;

	public InvManipulationBehaviour targetInventory;
	public ItemStack heldBox;
	public ItemStack previouslyUnwrapped;

	public List<ItemStack> queuedExitingPackages;

	public PackagerItemHandler inventory;
	private final LazyOptional<IItemHandler> invProvider;

	public static final int CYCLE = 20;
	public int animationTicks;
	public boolean animationInward;

	private InventorySummary availableItems;
	private VersionedInventoryTrackerBehaviour invVersionTracker;

	private AdvancementBehaviour advancements;

	//

	public PackagerBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
		super(typeIn, pos, state);
		redstonePowered = state.getOptionalValue(PackagerBlock.POWERED)
			.orElse(false);
		heldBox = ItemStack.EMPTY;
		previouslyUnwrapped = ItemStack.EMPTY;
		inventory = new PackagerItemHandler(this);
		invProvider = LazyOptional.of(() -> inventory);
		animationTicks = 0;
		animationInward = true;
		queuedExitingPackages = new LinkedList<>();
		signBasedAddress = "";
		buttonCooldown = 0;
	}

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
		behaviours.add(targetInventory = new InvManipulationBehaviour(this, InterfaceProvider.oppositeOfBlockFacing())
			.withFilter(this::supportsBlockEntity));
		behaviours.add(invVersionTracker = new VersionedInventoryTrackerBehaviour(this));
		behaviours.add(advancements = new AdvancementBehaviour(this, AllAdvancements.PACKAGER));
	}

	private boolean supportsBlockEntity(BlockEntity target) {
		return target != null && !(target instanceof PortableStorageInterfaceBlockEntity);
	}

	@Override
	public void initialize() {
		super.initialize();
		recheckIfLinksPresent();
	}

	@Override
	public void tick() {
		super.tick();

		if (buttonCooldown > 0)
			buttonCooldown--;

		if (animationTicks == 0) {
			previouslyUnwrapped = ItemStack.EMPTY;

			if (!level.isClientSide() && !queuedExitingPackages.isEmpty() && heldBox.isEmpty()) {
				heldBox = queuedExitingPackages.remove(0);
				animationInward = false;
				animationTicks = CYCLE;
				notifyUpdate();
			}

			return;
		}

		if (level.isClientSide) {
			if (animationTicks == CYCLE - (animationInward ? 5 : 1))
				AllSoundEvents.PACKAGER.playAt(level, worldPosition, 1, 1, true);
			if (animationTicks == (animationInward ? 1 : 5))
				level.playLocalSound(worldPosition, SoundEvents.IRON_TRAPDOOR_CLOSE, SoundSource.BLOCKS, 0.25f, 0.75f,
					true);
		}

		animationTicks--;

		if (animationTicks == 0 && !level.isClientSide()) {
			wakeTheFrogs();
			setChanged();
		}
	}

	public void triggerStockCheck() {
		getAvailableItems();
	}

	public InventorySummary getAvailableItems() {
		return getAvailableItems(false);
	}

	public InventorySummary getAvailableItems(boolean scanInputSlots) {
		if (availableItems != null && invVersionTracker.stillWaiting(targetInventory.getInventory()))
			return availableItems;

		InventorySummary availableItems = new InventorySummary();

		IItemHandler targetInv = targetInventory.getInventory();
		if (targetInv == null || targetInv instanceof PackagerItemHandler) {
			this.availableItems = availableItems;
			return availableItems;
		}

		if (targetInv instanceof BottomlessItemHandler bih) {
			availableItems.add(bih.getStackInSlot(0), BigItemStack.INF);
			this.availableItems = availableItems;
			return availableItems;
		}

		for (int slot = 0; slot < targetInv.getSlots(); slot++) {
			int slotLimit = targetInv.getSlotLimit(slot);
			availableItems.add(scanInputSlots ? targetInv.getStackInSlot(slot) : targetInv.extractItem(slot, slotLimit, true));
		}

		invVersionTracker.awaitNewVersion(targetInventory.getInventory());
		submitNewArrivals(this.availableItems, availableItems);
		this.availableItems = availableItems;
		return availableItems;
	}

	private void submitNewArrivals(InventorySummary before, InventorySummary after) {
		if (before == null || after.isEmpty())
			return;

		Set<RequestPromiseQueue> promiseQueues = new HashSet<>();

		for (Direction d : Iterate.directions) {
			if (!level.isLoaded(worldPosition.relative(d)))
				continue;

			BlockState adjacentState = level.getBlockState(worldPosition.relative(d));
			if (AllBlocks.FACTORY_GAUGE.has(adjacentState)) {
				if (FactoryPanelBlock.connectedDirection(adjacentState) != d)
					continue;
				if (!(level.getBlockEntity(worldPosition.relative(d)) instanceof FactoryPanelBlockEntity fpbe))
					continue;
				if (!fpbe.restocker)
					continue;
				for (FactoryPanelBehaviour behaviour : fpbe.panels.values()) {
					if (!behaviour.isActive())
						continue;
					promiseQueues.add(behaviour.restockerPromises);
				}
			}

			if (AllBlocks.STOCK_LINK.has(adjacentState)) {
				if (PackagerLinkBlock.getConnectedDirection(adjacentState) != d)
					continue;
				if (!(level.getBlockEntity(worldPosition.relative(d)) instanceof PackagerLinkBlockEntity plbe))
					continue;
				UUID freqId = plbe.behaviour.freqId;
				if (!Create.LOGISTICS.hasQueuedPromises(freqId))
					continue;
				promiseQueues.add(Create.LOGISTICS.getQueuedPromises(freqId));
			}
		}

		if (promiseQueues.isEmpty())
			return;

		for (BigItemStack entry : after.getStacks())
			before.add(entry.stack, -entry.count);
		for (RequestPromiseQueue queue : promiseQueues)
			for (BigItemStack entry : before.getStacks())
				if (entry.count < 0)
					queue.itemEnteredSystem(entry.stack, -entry.count);
	}

	@Override
	public void lazyTick() {
		super.lazyTick();
		if (level.isClientSide())
			return;
		recheckIfLinksPresent();
		if (!redstonePowered)
			return;
		redstonePowered = getBlockState().getOptionalValue(PackagerBlock.POWERED)
			.orElse(false);
		if (!redstoneModeActive())
			return;
		updateSignAddress();
		attemptToSend(null);
	}

	public void recheckIfLinksPresent() {
		if (level.isClientSide())
			return;
		BlockState blockState = getBlockState();
		if (!blockState.hasProperty(PackagerBlock.LINKED))
			return;
		boolean shouldBeLinked = getLinkPos() != null;
		boolean isLinked = blockState.getValue(PackagerBlock.LINKED);
		if (shouldBeLinked == isLinked)
			return;
		level.setBlockAndUpdate(worldPosition, blockState.cycle(PackagerBlock.LINKED));
	}

	public boolean redstoneModeActive() {
		return !getBlockState().getOptionalValue(PackagerBlock.LINKED)
			.orElse(false);
	}

	private BlockPos getLinkPos() {
		for (Direction d : Iterate.directions) {
			BlockState adjacentState = level.getBlockState(worldPosition.relative(d));
			if (!AllBlocks.STOCK_LINK.has(adjacentState))
				continue;
			if (PackagerLinkBlock.getConnectedDirection(adjacentState) != d)
				continue;
			return worldPosition.relative(d);
		}
		return null;
	}

	public void flashLink() {
		for (Direction d : Iterate.directions) {
			BlockState adjacentState = level.getBlockState(worldPosition.relative(d));
			if (!AllBlocks.STOCK_LINK.has(adjacentState))
				continue;
			if (PackagerLinkBlock.getConnectedDirection(adjacentState) != d)
				continue;
			WiFiEffectPacket.send(level, worldPosition.relative(d));
			return;
		}
	}

	public boolean isTooBusyFor(RequestType type) {
		int queue = queuedExitingPackages.size();
		return queue >= switch (type) {
		case PLAYER -> 50;
		case REDSTONE -> 20;
		case RESTOCK -> 10;
		};
	}

	public void activate() {
		redstonePowered = true;
		setChanged();

		recheckIfLinksPresent();
		if (!redstoneModeActive())
			return;

		updateSignAddress();
		attemptToSend(null);

		// dont send multiple packages when a button signal length is received
		buttonCooldown = 40;
	}

	public boolean unwrapBox(ItemStack box, boolean simulate) {
		if (animationTicks > 0)
			return false;

		Objects.requireNonNull(this.level);

		ItemStackHandler contents = PackageItem.getContents(box);
		List<ItemStack> items = ItemHelper.getNonEmptyStacks(contents);
		if (items.isEmpty())
			return true;

		PackageOrder orderContext = PackageItem.getOrderContext(box);

		Direction facing = getBlockState().getOptionalValue(PackagerBlock.FACING).orElse(Direction.UP);
		BlockPos target = worldPosition.relative(facing.getOpposite());
		BlockState targetState = level.getBlockState(target);

		UnpackingHandler handler = UnpackingHandler.REGISTRY.get(targetState);
		UnpackingHandler toUse = handler != null ? handler : UnpackingHandler.DEFAULT;
		// note: handler may modify the passed items
		boolean unpacked = toUse.unpack(level, target, targetState, facing, items, orderContext, simulate);

		if (unpacked && !simulate) {
			previouslyUnwrapped = box;
			animationInward = true;
			animationTicks = CYCLE;
			notifyUpdate();
		}

		return unpacked;
	}

	public void attemptToSend(List<PackagingRequest> queuedRequests) {
		if (queuedRequests == null && (!heldBox.isEmpty() || animationTicks != 0 || buttonCooldown > 0))
			return;

		IItemHandler targetInv = targetInventory.getInventory();
		if (targetInv == null || targetInv instanceof PackagerItemHandler)
			return;

		boolean anyItemPresent = false;
		ItemStackHandler extractedItems = new ItemStackHandler(PackageItem.SLOTS);
		ItemStack extractedPackageItem = ItemStack.EMPTY;
		PackagingRequest nextRequest = null;
		String fixedAddress = null;
		int fixedOrderId = 0;

		// Data written to packages for defrags
		int linkIndexInOrder = 0;
		boolean finalLinkInOrder = false;
		int packageIndexAtLink = 0;
		boolean finalPackageAtLink = false;
		PackageOrder orderContext = null;
		boolean requestQueue = queuedRequests != null;

		if (requestQueue && !queuedRequests.isEmpty()) {
			nextRequest = queuedRequests.get(0);
			fixedAddress = nextRequest.address();
			fixedOrderId = nextRequest.orderId();
			linkIndexInOrder = nextRequest.linkIndex();
			finalLinkInOrder = nextRequest.finalLink()
				.booleanValue();
			packageIndexAtLink = nextRequest.packageCounter()
				.getAndIncrement();
			orderContext = nextRequest.context();
		}

		Outer: for (int i = 0; i < PackageItem.SLOTS; i++) {
			boolean continuePacking = true;

			while (continuePacking) {
				continuePacking = false;

				for (int slot = 0; slot < targetInv.getSlots(); slot++) {
					int initialCount = requestQueue ? Math.min(64, nextRequest.getCount()) : 64;
					ItemStack extracted = targetInv.extractItem(slot, initialCount, true);
					if (extracted.isEmpty())
						continue;
					if (requestQueue && !ItemHandlerHelper.canItemStacksStack(extracted, nextRequest.item()))
						continue;

					boolean bulky = !extracted.getItem()
						.canFitInsideContainerItems();
					if (bulky && anyItemPresent)
						continue;

					anyItemPresent = true;
					int leftovers = ItemHandlerHelper.insertItemStacked(extractedItems, extracted.copy(), false)
						.getCount();
					int transferred = extracted.getCount() - leftovers;
					targetInv.extractItem(slot, transferred, false);

					if (extracted.getItem() instanceof PackageItem)
						extractedPackageItem = extracted;

					if (!requestQueue) {
						if (bulky)
							break Outer;
						continue;
					}

					nextRequest.subtract(transferred);

					if (!nextRequest.isEmpty()) {
						if (bulky)
							break Outer;
						continue;
					}

					finalPackageAtLink = true;
					queuedRequests.remove(0);
					if (queuedRequests.isEmpty())
						break Outer;
					int previousCount = nextRequest.packageCounter()
						.intValue();
					nextRequest = queuedRequests.get(0);
					if (!fixedAddress.equals(nextRequest.address()))
						break Outer;
					if (fixedOrderId != nextRequest.orderId())
						break Outer;

					nextRequest.packageCounter()
						.setValue(previousCount);
					finalPackageAtLink = false;
					continuePacking = true;
					if (nextRequest.context() != null)
						orderContext = nextRequest.context();

					if (bulky)
						break Outer;
					break;
				}
			}
		}

		if (!anyItemPresent) {
			if (nextRequest != null)
				queuedRequests.remove(0);
			return;
		}

		ItemStack createdBox =
			extractedPackageItem.isEmpty() ? PackageItem.containing(extractedItems) : extractedPackageItem.copy();
		PackageItem.clearAddress(createdBox);

		if (fixedAddress != null)
			PackageItem.addAddress(createdBox, fixedAddress);
		if (requestQueue)
			PackageItem.setOrder(createdBox, fixedOrderId, linkIndexInOrder, finalLinkInOrder, packageIndexAtLink,
				finalPackageAtLink, orderContext);
		if (!requestQueue && !signBasedAddress.isBlank())
			PackageItem.addAddress(createdBox, signBasedAddress);

		BlockPos linkPos = getLinkPos();
		if (extractedPackageItem.isEmpty() && linkPos != null
			&& level.getBlockEntity(linkPos) instanceof PackagerLinkBlockEntity plbe)
			plbe.behaviour.deductFromAccurateSummary(extractedItems);

		if (!heldBox.isEmpty() || animationTicks != 0) {
			queuedExitingPackages.add(createdBox);
			return;
		}

		heldBox = createdBox;
		animationInward = false;
		animationTicks = CYCLE;

		advancements.awardPlayer(AllAdvancements.PACKAGER);
		triggerStockCheck();
		notifyUpdate();
	}

	protected void updateSignAddress() {
		signBasedAddress = "";
		for (Direction side : Iterate.directions) {
			String address = getSign(side);
			if (address == null || address.isBlank())
				continue;
			signBasedAddress = address;
		}
	}

	protected String getSign(Direction side) {
		BlockEntity blockEntity = level.getBlockEntity(worldPosition.relative(side));
		if (!(blockEntity instanceof SignBlockEntity sign))
			return null;
		for (boolean front : Iterate.trueAndFalse) {
			SignText text = sign.getText(front);
			for (Component component : text.getMessages(false)) {
				String address = component.getString();
				if (!address.isBlank())
					return address;
			}
		}
		return null;
	}

	protected void wakeTheFrogs() {
		if (level.getBlockEntity(worldPosition.relative(Direction.UP)) instanceof FrogportBlockEntity port)
			port.tryPullingFromOwnAndAdjacentInventories();
	}

	@Override
	protected void read(CompoundTag compound, boolean clientPacket) {
		super.read(compound, clientPacket);
		redstonePowered = compound.getBoolean("Active");
		animationInward = compound.getBoolean("AnimationInward");
		animationTicks = compound.getInt("AnimationTicks");
		signBasedAddress = compound.getString("SignAddress");
		heldBox = ItemStack.of(compound.getCompound("HeldBox"));
		previouslyUnwrapped = ItemStack.of(compound.getCompound("InsertedBox"));
		if (clientPacket)
			return;
		queuedExitingPackages = NBTHelper.readItemList(compound.getList("QueuedPackages", Tag.TAG_COMPOUND));
		if (compound.contains("LastSummary"))
			availableItems = InventorySummary.read(compound.getCompound("LastSummary"));
	}

	@Override
	protected void write(CompoundTag compound, boolean clientPacket) {
		super.write(compound, clientPacket);
		compound.putBoolean("Active", redstonePowered);
		compound.putBoolean("AnimationInward", animationInward);
		compound.putInt("AnimationTicks", animationTicks);
		compound.putString("SignAddress", signBasedAddress);
		compound.put("HeldBox", heldBox.serializeNBT());
		compound.put("InsertedBox", previouslyUnwrapped.serializeNBT());
		if (clientPacket)
			return;
		compound.put("QueuedPackages", NBTHelper.writeItemList(queuedExitingPackages));
		if (availableItems != null)
			compound.put("LastSummary", availableItems.write());
	}

	@Override
	public void invalidate() {
		super.invalidate();
		invProvider.invalidate();
	}

	@Override
	public void destroy() {
		super.destroy();
		ItemHelper.dropContents(level, worldPosition, inventory);
		queuedExitingPackages.forEach(stack -> Containers.dropItemStack(level, worldPosition.getX(),
			worldPosition.getY(), worldPosition.getZ(), stack));
		queuedExitingPackages.clear();
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (cap == ForgeCapabilities.ITEM_HANDLER)
			return invProvider.cast();
		return super.getCapability(cap, side);
	}

	public float getTrayOffset(float partialTicks) {
		float tickCycle = animationInward ? animationTicks - partialTicks : animationTicks - 5 - partialTicks;
		float progress = Mth.clamp(tickCycle / (CYCLE - 5) * 2 - 1, -1, 1);
		progress = 1 - progress * progress;
		return progress * progress;
	}

	public ItemStack getRenderedBox() {
		if (animationInward)
			return animationTicks <= CYCLE / 2 ? ItemStack.EMPTY : previouslyUnwrapped;
		return animationTicks >= CYCLE / 2 ? ItemStack.EMPTY : heldBox;
	}

	public boolean isTargetingSameInventory(IItemHandler inventory) {
		IItemHandler myInventory = targetInventory.getInventory();
		if (myInventory == null || inventory == null)
			return false;

		if (myInventory == inventory)
			return true;

		// If a contained ItemStack instance is the same, we can be pretty sure these
		// inventories are the same (works for compound inventories)
		for (int i = 0; i < inventory.getSlots(); i++) {
			ItemStack stackInSlot = inventory.getStackInSlot(i);
			if (stackInSlot.isEmpty())
				continue;
			for (int j = 0; j < myInventory.getSlots(); j++)
				if (stackInSlot == myInventory.getStackInSlot(j))
					return true;
			break;
		}

		return false;
	}

}
