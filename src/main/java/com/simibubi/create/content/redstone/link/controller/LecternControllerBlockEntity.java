package com.simibubi.create.content.redstone.link.controller;

import java.util.List;
import java.util.UUID;

import com.simibubi.create.AllItems;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.fml.DistExecutor;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class LecternControllerBlockEntity extends SmartBlockEntity {

	private CompoundTag controllerNbt = new CompoundTag();
	private UUID user;
	private UUID prevUser;    // used only on client
	private boolean deactivatedThisTick;    // used only on server

	public LecternControllerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
	}

	@Override
	protected void write(CompoundTag compound, boolean clientPacket) {
		super.write(compound, clientPacket);
		compound.put("ControllerData", controllerNbt);
		if (user != null)
			compound.putUUID("User", user);
	}

	@Override
	public void writeSafe(CompoundTag compound) {
		super.writeSafe(compound);
		compound.put("ControllerData", controllerNbt);
	}

	@Override
	protected void read(CompoundTag compound, boolean clientPacket) {
		super.read(compound, clientPacket);

		// Migrate old data if that is found
		if (compound.contains("Controller")) {
			controllerNbt = ItemStack.of(compound.getCompound("Controller")).getTag();
		} else {
			controllerNbt = compound.getCompound("ControllerData");
		}

		user = compound.hasUUID("User") ? compound.getUUID("User") : null;
	}

	public ItemStack getController() {
		return createLinkedController();
	}

	public boolean hasUser() {
		return user != null;
	}

	public boolean isUsedBy(Player player) {
		return hasUser() && user.equals(player.getUUID());
	}

	public void tryStartUsing(Player player) {
		if (!deactivatedThisTick && !hasUser() && !playerIsUsingLectern(player) && playerInRange(player, level, worldPosition))
			startUsing(player);
	}

	public void tryStopUsing(Player player) {
		if (isUsedBy(player))
			stopUsing(player);
	}

	private void startUsing(Player player) {
		user = player.getUUID();
		player.getPersistentData().putBoolean("IsUsingLecternController", true);
		sendData();
	}

	private void stopUsing(Player player) {
		user = null;
		if (player != null)
			player.getPersistentData().remove("IsUsingLecternController");
		deactivatedThisTick = true;
		sendData();
	}

	public static boolean playerIsUsingLectern(Player player) {
		return player.getPersistentData().contains("IsUsingLecternController");
	}

	@Override
	public void tick() {
		super.tick();

		if (level.isClientSide) {
			DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> this::tryToggleActive);
			prevUser = user;
		}

		if (!level.isClientSide) {
			deactivatedThisTick = false;

			if (!(level instanceof ServerLevel))
				return;
			if (user == null)
				return;

			Entity entity = ((ServerLevel) level).getEntity(user);
			if (!(entity instanceof Player player)) {
				stopUsing(null);
				return;
			}

			if (!playerInRange(player, level, worldPosition) || !playerIsUsingLectern(player))
				stopUsing(player);
		}
	}

	@OnlyIn(Dist.CLIENT)
	private void tryToggleActive() {
		if (user == null && Minecraft.getInstance().player.getUUID().equals(prevUser)) {
			LinkedControllerClientHandler.deactivateInLectern();
		} else if (prevUser == null && Minecraft.getInstance().player.getUUID().equals(user)) {
			LinkedControllerClientHandler.activateInLectern(worldPosition);
		}
	}

	public void setController(ItemStack newController) {
		if (newController != null) {
			controllerNbt = newController.getOrCreateTag();
			AllSoundEvents.CONTROLLER_PUT.playOnServer(level, worldPosition);
		}
	}

	public void swapControllers(ItemStack stack, Player player, InteractionHand hand, BlockState state) {
		ItemStack newController = stack.copy();
		stack.setCount(0);
		if (player.getItemInHand(hand).isEmpty()) {
			player.setItemInHand(hand, createLinkedController());
		} else {
			dropController(state);
		}
		setController(newController);
	}

	public void dropController(BlockState state) {
		Entity playerEntity = ((ServerLevel) level).getEntity(user);
		if (playerEntity instanceof Player)
			stopUsing((Player) playerEntity);

		Direction dir = state.getValue(LecternControllerBlock.FACING);
		double x = worldPosition.getX() + 0.5 + 0.25 * dir.getStepX();
		double y = worldPosition.getY() + 1;
		double z = worldPosition.getZ() + 0.5 + 0.25 * dir.getStepZ();
		ItemEntity itementity = new ItemEntity(level, x, y, z, createLinkedController());
		itementity.setDefaultPickUpDelay();
		level.addFreshEntity(itementity);
		controllerNbt = new CompoundTag();
	}

	public static boolean playerInRange(Player player, Level world, BlockPos pos) {
		//double modifier = world.isRemote ? 0 : 1.0;
		double reach = 0.4 * player.getAttributeValue(ForgeMod.BLOCK_REACH.get());// + modifier;
		return player.distanceToSqr(Vec3.atCenterOf(pos)) < reach * reach;
	}

	private ItemStack createLinkedController() {
		ItemStack stack = AllItems.LINKED_CONTROLLER.asStack();
		stack.setTag(controllerNbt.copy());
		return stack;
	}

}
