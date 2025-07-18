package com.simibubi.create;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

import org.apache.commons.lang3.mutable.MutableObject;

import com.simibubi.create.content.contraptions.actors.seat.SeatBlock;
import com.simibubi.create.content.decoration.palettes.AllPaletteBlocks;
import com.simibubi.create.content.equipment.armor.BacktankUtil;
import com.simibubi.create.content.equipment.toolbox.ToolboxBlock;
import com.simibubi.create.content.kinetics.crank.ValveHandleBlock;
import com.simibubi.create.content.logistics.box.PackageStyles;
import com.simibubi.create.content.logistics.packagePort.postbox.PostboxBlock;
import com.simibubi.create.content.logistics.tableCloth.TableClothBlock;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.item.TagDependentIngredientItem;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.entry.ItemProviderEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;

import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import it.unimi.dsi.fastutil.objects.ReferenceLinkedOpenHashSet;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTab.DisplayItemsGenerator;
import net.minecraft.world.item.CreativeModeTab.ItemDisplayParameters;
import net.minecraft.world.item.CreativeModeTab.Output;
import net.minecraft.world.item.CreativeModeTab.TabVisibility;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

@EventBusSubscriber(bus = Bus.MOD)
public class AllCreativeModeTabs {
	private static final DeferredRegister<CreativeModeTab> REGISTER =
		DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Create.ID);

	public static final RegistryObject<CreativeModeTab> BASE_CREATIVE_TAB = REGISTER.register("base",
		() -> CreativeModeTab.builder()
			.title(Component.translatable("itemGroup.create.base"))
			.withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
			.icon(() -> AllBlocks.COGWHEEL.asStack())
			.displayItems(new RegistrateDisplayItemsGenerator(true, AllCreativeModeTabs.BASE_CREATIVE_TAB))
			.build());

	public static final RegistryObject<CreativeModeTab> PALETTES_CREATIVE_TAB = REGISTER.register("palettes",
		() -> CreativeModeTab.builder()
			.title(Component.translatable("itemGroup.create.palettes"))
			.withTabsBefore(BASE_CREATIVE_TAB.getKey())
			.icon(() -> AllPaletteBlocks.ORNATE_IRON_WINDOW.asStack())
			.displayItems(new RegistrateDisplayItemsGenerator(false, AllCreativeModeTabs.PALETTES_CREATIVE_TAB))
			.build());

	public static void register(IEventBus modEventBus) {
		REGISTER.register(modEventBus);
	}

	private static class RegistrateDisplayItemsGenerator implements DisplayItemsGenerator {
		private static final Predicate<Item> IS_ITEM_3D_PREDICATE;

		static {
			MutableObject<Predicate<Item>> isItem3d = new MutableObject<>(item -> false);
			DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
				isItem3d.setValue(item -> {
					ItemRenderer itemRenderer = Minecraft.getInstance()
						.getItemRenderer();
					BakedModel model = itemRenderer.getModel(new ItemStack(item), null, null, 0);
					return model.isGui3d();
				});
			});
			IS_ITEM_3D_PREDICATE = isItem3d.getValue();
		}

		private final boolean addItems;
		private final RegistryObject<CreativeModeTab> tabFilter;

		public RegistrateDisplayItemsGenerator(boolean addItems, RegistryObject<CreativeModeTab> tabFilter) {
			this.addItems = addItems;
			this.tabFilter = tabFilter;
		}

		private static Predicate<Item> makeExclusionPredicate() {
			Set<Item> exclusions = new ReferenceOpenHashSet<>();

			List<ItemProviderEntry<?>> simpleExclusions = new ArrayList<>(List.of(
				AllItems.WHEAT_FLOUR,
				AllItems.DOUGH,
				AllItems.CINDER_FLOUR,
				// AllItems.ROSE_QUARTZ,
				// AllItems.POLISHED_ROSE_QUARTZ,
				AllItems.POWDERED_OBSIDIAN,
				AllItems.STURDY_SHEET,
				AllItems.PROPELLER,
				AllItems.WHISK,
				AllItems.BRASS_HAND,
				AllItems.CRAFTER_SLOT_COVER,
				AllItems.PULP,
				AllItems.CARDBOARD,
				AllItems.INCOMPLETE_PRECISION_MECHANISM,
				AllItems.INCOMPLETE_REINFORCED_SHEET,
				AllItems.INCOMPLETE_TRACK,
				AllItems.BLAZE_CAKE_BASE,
				AllItems.BLAZE_CAKE,
				AllItems.CREATIVE_BLAZE_CAKE,
				AllItems.BAR_OF_CHOCOLATE,
				AllItems.SWEET_ROLL,
				AllItems.CHOCOLATE_BERRIES,
				AllItems.HONEYED_APPLE,
				AllItems.BUILDERS_TEA,
				AllItems.CARDBOARD_SWORD,
				AllItems.RAW_ZINC,
				AllItems.ZINC_INGOT,
				AllItems.CHROMATIC_COMPOUND,
				AllItems.SHADOW_STEEL,
				AllItems.REFINED_RADIANCE,
				AllItems.COPPER_NUGGET,
				AllItems.ZINC_NUGGET,
				AllItems.EXP_NUGGET,
				AllItems.COPPER_SHEET,
				AllItems.BRASS_SHEET,
				AllItems.IRON_SHEET,
				AllItems.GOLDEN_SHEET,
				AllItems.CRUSHED_IRON,
				AllItems.CRUSHED_GOLD,
				AllItems.CRUSHED_COPPER,
				AllItems.CRUSHED_ZINC,
				AllItems.CRUSHED_OSMIUM,
				AllItems.CRUSHED_PLATINUM,
				AllItems.CRUSHED_SILVER,
				AllItems.CRUSHED_TIN,
				AllItems.CRUSHED_LEAD,
				AllItems.CRUSHED_QUICKSILVER,
				AllItems.CRUSHED_BAUXITE,
				AllItems.CRUSHED_URANIUM,
				AllItems.CRUSHED_NICKEL,
				AllItems.BELT_CONNECTOR,
				AllItems.VERTICAL_GEARBOX,
				AllItems.EMPTY_BLAZE_BURNER,
				AllItems.GOGGLES,
				AllItems.MINECART_COUPLING,
				AllItems.CRAFTING_BLUEPRINT,
				AllItems.COPPER_BACKTANK_PLACEABLE,
				AllItems.NETHERITE_BACKTANK_PLACEABLE,
				AllItems.COPPER_BACKTANK,
				AllItems.NETHERITE_BACKTANK,
				AllItems.COPPER_DIVING_HELMET,
				AllItems.NETHERITE_DIVING_HELMET,
				AllItems.COPPER_DIVING_BOOTS,
				AllItems.NETHERITE_DIVING_BOOTS,
				AllItems.CARDBOARD_HELMET,
				AllItems.CARDBOARD_CHESTPLATE,
				AllItems.CARDBOARD_LEGGINGS,
				AllItems.CARDBOARD_BOOTS,
				// AllItems.SAND_PAPER,
				// AllItems.RED_SAND_PAPER,
				AllItems.MINECART_CONTRAPTION,
				AllItems.FURNACE_MINECART_CONTRAPTION,
				AllItems.CHEST_MINECART_CONTRAPTION,
				AllItems.LINKED_CONTROLLER,
				AllItems.POTATO_CANNON,
				AllItems.EXTENDO_GRIP,
				AllItems.WAND_OF_SYMMETRY,
				AllItems.WORLDSHAPER,
				AllItems.TREE_FERTILIZER,
				AllItems.FILTER,
				AllItems.ATTRIBUTE_FILTER,
				AllItems.PACKAGE_FILTER,
				AllItems.SHOPPING_LIST,
				AllItems.SCHEMATIC,
				AllBlocks.SHAFT,
				AllBlocks.COGWHEEL,
				AllBlocks.LARGE_COGWHEEL,
				AllBlocks.ANDESITE_ENCASED_SHAFT,
				AllBlocks.BRASS_ENCASED_SHAFT,
				AllBlocks.ANDESITE_ENCASED_COGWHEEL,
				AllBlocks.BRASS_ENCASED_COGWHEEL,
				AllBlocks.ANDESITE_ENCASED_LARGE_COGWHEEL,
				AllBlocks.BRASS_ENCASED_LARGE_COGWHEEL,
				AllBlocks.GEARBOX,
				AllBlocks.CLUTCH,
				AllBlocks.GEARSHIFT,
				AllBlocks.ENCASED_CHAIN_DRIVE,
				AllBlocks.ADJUSTABLE_CHAIN_GEARSHIFT,
				AllBlocks.BELT,
				AllBlocks.CHAIN_CONVEYOR,
				AllBlocks.CREATIVE_MOTOR,
				AllBlocks.WATER_WHEEL,
				AllBlocks.LARGE_WATER_WHEEL,
				AllBlocks.WATER_WHEEL_STRUCTURAL,
				AllBlocks.ENCASED_FAN,
				AllBlocks.NOZZLE,
				AllBlocks.TURNTABLE,
				AllBlocks.HAND_CRANK,
				AllBlocks.CUCKOO_CLOCK,
				AllBlocks.MYSTERIOUS_CUCKOO_CLOCK,
				AllBlocks.MILLSTONE,
				AllBlocks.CRUSHING_WHEEL,
				AllBlocks.CRUSHING_WHEEL_CONTROLLER,
				AllBlocks.MECHANICAL_PRESS,
				AllBlocks.MECHANICAL_MIXER,
				AllBlocks.BASIN,
				AllBlocks.BLAZE_BURNER,
				AllBlocks.LIT_BLAZE_BURNER,
				AllBlocks.DEPOT,
				AllBlocks.WEIGHTED_EJECTOR,
				AllBlocks.CHUTE,
				AllBlocks.SMART_CHUTE,
				AllBlocks.SPEEDOMETER,
				AllBlocks.STRESSOMETER,
				AllBlocks.WOODEN_BRACKET,
				AllBlocks.METAL_BRACKET,
				AllBlocks.FLUID_PIPE,
				AllBlocks.ENCASED_FLUID_PIPE,
				AllBlocks.GLASS_FLUID_PIPE,
				AllBlocks.MECHANICAL_PUMP,
				AllBlocks.SMART_FLUID_PIPE,
				AllBlocks.FLUID_VALVE,
				AllBlocks.COPPER_VALVE_HANDLE,
				// AllBlocks.DYED_VALVE_HANDLES,
				AllBlocks.FLUID_TANK,
				AllBlocks.CREATIVE_FLUID_TANK,
				AllBlocks.HOSE_PULLEY,
				AllBlocks.ITEM_DRAIN,
				AllBlocks.SPOUT,
				AllBlocks.PORTABLE_FLUID_INTERFACE,
				AllBlocks.STEAM_ENGINE,
				AllBlocks.POWERED_SHAFT,
				AllBlocks.MECHANICAL_PISTON,
				AllBlocks.STICKY_MECHANICAL_PISTON,
				AllBlocks.PISTON_EXTENSION_POLE,
				AllBlocks.MECHANICAL_PISTON_HEAD,
				AllBlocks.GANTRY_CARRIAGE,
				AllBlocks.GANTRY_SHAFT,
				AllBlocks.WINDMILL_BEARING,
				AllBlocks.MECHANICAL_BEARING,
				AllBlocks.CLOCKWORK_BEARING,
				AllBlocks.ROPE_PULLEY,
				AllBlocks.ROPE,
				AllBlocks.PULLEY_MAGNET,
				AllBlocks.ELEVATOR_PULLEY,
				AllBlocks.CART_ASSEMBLER,
				AllBlocks.CONTROLLER_RAIL,
				AllBlocks.MINECART_ANCHOR,
				AllBlocks.LINEAR_CHASSIS,
				AllBlocks.SECONDARY_LINEAR_CHASSIS,
				AllBlocks.RADIAL_CHASSIS,
				AllBlocks.STICKER,
				AllBlocks.CONTRAPTION_CONTROLS,
				AllBlocks.MECHANICAL_DRILL,
				AllBlocks.MECHANICAL_SAW,
				AllBlocks.DEPLOYER,
				AllBlocks.PORTABLE_STORAGE_INTERFACE,
				AllBlocks.REDSTONE_CONTACT,
				AllBlocks.ELEVATOR_CONTACT,
				AllBlocks.MECHANICAL_HARVESTER,
				AllBlocks.MECHANICAL_PLOUGH,
				AllBlocks.MECHANICAL_ROLLER,
				AllBlocks.SAIL_FRAME,
				AllBlocks.SAIL,
				// AllBlocks.DYED_SAILS,
				AllBlocks.COPPER_CASING,
				AllBlocks.SHADOW_STEEL_CASING,
				AllBlocks.REFINED_RADIANCE_CASING,
				AllBlocks.MECHANICAL_CRAFTER,
				AllBlocks.SEQUENCED_GEARSHIFT,
				AllBlocks.FLYWHEEL,
				AllBlocks.ROTATION_SPEED_CONTROLLER,
				AllBlocks.MECHANICAL_ARM,
				AllBlocks.ANDESITE_FUNNEL,
				AllBlocks.ANDESITE_BELT_FUNNEL,
				AllBlocks.BRASS_FUNNEL,
				AllBlocks.BRASS_BELT_FUNNEL,
				AllBlocks.ANDESITE_TUNNEL,
				AllBlocks.BRASS_TUNNEL,
				AllBlocks.SMART_OBSERVER,
				AllBlocks.THRESHOLD_SWITCH,
				AllBlocks.CREATIVE_CRATE,
				AllBlocks.ITEM_VAULT,
				AllBlocks.ITEM_HATCH,
				AllBlocks.PACKAGER,
				AllBlocks.REPACKAGER,
				AllBlocks.PACKAGE_FROGPORT,
				// AllBlocks.PACKAGE_POSTBOXES,
				AllBlocks.STOCK_LINK,
				AllBlocks.STOCK_TICKER,
				AllBlocks.REDSTONE_REQUESTER,
				AllBlocks.FACTORY_GAUGE,
				// AllBlocks.TABLE_CLOTHS,
				AllBlocks.ANDESITE_TABLE_CLOTH,
				AllBlocks.BRASS_TABLE_CLOTH,
				AllBlocks.COPPER_TABLE_CLOTH,
				AllBlocks.ROSE_QUARTZ_LAMP,
				AllBlocks.REDSTONE_LINK,
				AllBlocks.ANALOG_LEVER,
				AllBlocks.PLACARD,
				AllBlocks.PULSE_REPEATER,
				AllBlocks.PULSE_EXTENDER,
				AllBlocks.PULSE_TIMER,
				AllBlocks.POWERED_LATCH,
				AllBlocks.POWERED_TOGGLE_LATCH,
				AllBlocks.LECTERN_CONTROLLER,
				AllBlocks.COPPER_BACKTANK,
				AllBlocks.NETHERITE_BACKTANK,
				AllBlocks.PECULIAR_BELL,
				AllBlocks.HAUNTED_BELL,
				AllBlocks.DESK_BELL,
				// AllBlocks.TOOLBOXES,
				AllBlocks.ANDESITE_LADDER,
				AllBlocks.BRASS_LADDER,
				AllBlocks.COPPER_LADDER,
				AllBlocks.ANDESITE_BARS,
				AllBlocks.BRASS_BARS,
				AllBlocks.COPPER_BARS,
				AllBlocks.ANDESITE_SCAFFOLD,
				AllBlocks.BRASS_SCAFFOLD,
				AllBlocks.COPPER_SCAFFOLD,
				AllBlocks.METAL_GIRDER_ENCASED_SHAFT,
				AllBlocks.COPPER_DOOR,
				AllBlocks.FRAMED_GLASS_DOOR,
				AllBlocks.FRAMED_GLASS_TRAPDOOR,
				AllBlocks.ZINC_ORE,
				AllBlocks.DEEPSLATE_ZINC_ORE,
				AllBlocks.RAW_ZINC_BLOCK,
				AllBlocks.ZINC_BLOCK,
				AllBlocks.INDUSTRIAL_IRON_BLOCK,
				AllBlocks.WEATHERED_IRON_BLOCK,
				AllBlocks.BRASS_BLOCK,
				AllBlocks.CARDBOARD_BLOCK,
				AllBlocks.BOUND_CARDBOARD_BLOCK,
				AllBlocks.EXPERIENCE_BLOCK,
				// AllBlocks.ROSE_QUARTZ_BLOCK,
				AllBlocks.ROSE_QUARTZ_TILES,
				AllBlocks.SMALL_ROSE_QUARTZ_TILES
				// AllBlocks.COPPER_SHINGLES,
				// AllBlocks.COPPER_TILES
			));
			simpleExclusions.addAll(List.of(AllBlocks.TOOLBOXES.toArray()));
			simpleExclusions.addAll(List.of(AllBlocks.SEATS.toArray()));
			simpleExclusions.addAll(List.of(AllBlocks.PACKAGE_POSTBOXES.toArray()));

			List<ItemEntry<TagDependentIngredientItem>> tagDependentExclusions = List.of(
				AllItems.CRUSHED_OSMIUM,
				AllItems.CRUSHED_PLATINUM,
				AllItems.CRUSHED_SILVER,
				AllItems.CRUSHED_TIN,
				AllItems.CRUSHED_LEAD,
				AllItems.CRUSHED_QUICKSILVER,
				AllItems.CRUSHED_BAUXITE,
				AllItems.CRUSHED_URANIUM,
				AllItems.CRUSHED_NICKEL
			);

			exclusions.addAll(PackageStyles.ALL_BOXES);

			for (ItemProviderEntry<?> entry : simpleExclusions) {
				exclusions.add(entry.asItem());
			}

			for (ItemEntry<TagDependentIngredientItem> entry : tagDependentExclusions) {
				TagDependentIngredientItem item = entry.get();
				if (item.shouldHide()) {
					exclusions.add(entry.asItem());
				}
			}

			return exclusions::contains;
		}

		private static List<ItemOrdering> makeOrderings() {
			List<ItemOrdering> orderings = new ReferenceArrayList<>();

			Map<ItemProviderEntry<?>, ItemProviderEntry<?>> simpleBeforeOrderings = Map.of(
				AllItems.EMPTY_BLAZE_BURNER, AllBlocks.BLAZE_BURNER,
				AllItems.SCHEDULE, AllBlocks.TRACK_STATION
			);

			Map<ItemProviderEntry<?>, ItemProviderEntry<?>> simpleAfterOrderings = Map.of(
				AllItems.VERTICAL_GEARBOX, AllBlocks.GEARBOX
			);

			simpleBeforeOrderings.forEach((entry, otherEntry) -> {
				orderings.add(ItemOrdering.before(entry.asItem(), otherEntry.asItem()));
			});

			simpleAfterOrderings.forEach((entry, otherEntry) -> {
				orderings.add(ItemOrdering.after(entry.asItem(), otherEntry.asItem()));
			});

			PackageStyles.STANDARD_BOXES.forEach(item -> {
				if (CatnipServices.REGISTRIES.getKeyOrThrow(item).getNamespace().equals(Create.ID))
					orderings.add(ItemOrdering.after(item, AllBlocks.PACKAGER.asItem()));
			});

			return orderings;
		}

		private static Function<Item, ItemStack> makeStackFunc() {
			Map<Item, Function<Item, ItemStack>> factories = new Reference2ReferenceOpenHashMap<>();

			Map<ItemProviderEntry<?>, Function<Item, ItemStack>> simpleFactories = Map.of(
				AllItems.COPPER_BACKTANK, item -> {
					ItemStack stack = new ItemStack(item);
					stack.getOrCreateTag().putInt("Air", BacktankUtil.maxAirWithoutEnchants());
					return stack;
				},
				AllItems.NETHERITE_BACKTANK, item -> {
					ItemStack stack = new ItemStack(item);
					stack.getOrCreateTag().putInt("Air", BacktankUtil.maxAirWithoutEnchants());
					return stack;
				}
			);

			simpleFactories.forEach((entry, factory) -> {
				factories.put(entry.asItem(), factory);
			});

			return item -> {
				Function<Item, ItemStack> factory = factories.get(item);
				if (factory != null) {
					return factory.apply(item);
				}
				return new ItemStack(item);
			};
		}

		private static Function<Item, TabVisibility> makeVisibilityFunc() {
			Map<Item, TabVisibility> visibilities = new Reference2ObjectOpenHashMap<>();

			Map<ItemProviderEntry<?>, TabVisibility> simpleVisibilities = Map.of(
				AllItems.BLAZE_CAKE_BASE, TabVisibility.SEARCH_TAB_ONLY
			);

			simpleVisibilities.forEach((entry, factory) -> {
				visibilities.put(entry.asItem(), factory);
			});

			for (BlockEntry<ValveHandleBlock> entry : AllBlocks.DYED_VALVE_HANDLES) {
				visibilities.put(entry.asItem(), TabVisibility.SEARCH_TAB_ONLY);
			}

			for (BlockEntry<SeatBlock> entry : AllBlocks.SEATS) {
				SeatBlock block = entry.get();
				if (block.getColor() != DyeColor.RED) {
					visibilities.put(entry.asItem(), TabVisibility.SEARCH_TAB_ONLY);
				}
			}

			for (BlockEntry<TableClothBlock> entry : AllBlocks.TABLE_CLOTHS) {
				TableClothBlock block = entry.get();
				if (block.getColor() != DyeColor.RED) {
					visibilities.put(entry.asItem(), TabVisibility.SEARCH_TAB_ONLY);
				}
			}

			for (BlockEntry<PostboxBlock> entry : AllBlocks.PACKAGE_POSTBOXES) {
				PostboxBlock block = entry.get();
				if (block.getColor() != DyeColor.WHITE) {
					visibilities.put(entry.asItem(), TabVisibility.SEARCH_TAB_ONLY);
				}
			}

			for (BlockEntry<ToolboxBlock> entry : AllBlocks.TOOLBOXES) {
				ToolboxBlock block = entry.get();
				if (block.getColor() != DyeColor.BROWN) {
					visibilities.put(entry.asItem(), TabVisibility.SEARCH_TAB_ONLY);
				}
			}

			return item -> {
				TabVisibility visibility = visibilities.get(item);
				if (visibility != null) {
					return visibility;
				}
				return TabVisibility.PARENT_AND_SEARCH_TABS;
			};
		}

		@Override
		public void accept(ItemDisplayParameters parameters, Output output) {
			Predicate<Item> exclusionPredicate = makeExclusionPredicate();
			// List<ItemOrdering> orderings = makeOrderings();
			Function<Item, ItemStack> stackFunc = makeStackFunc();
			Function<Item, TabVisibility> visibilityFunc = makeVisibilityFunc();

			List<Item> items = new LinkedList<>();
			if (addItems) {
				items.addAll(collectItems(exclusionPredicate.or(IS_ITEM_3D_PREDICATE.negate())));
			}
			items.addAll(collectBlocks(exclusionPredicate));
			if (addItems) {
				items.addAll(collectItems(exclusionPredicate.or(IS_ITEM_3D_PREDICATE)));
			}

			// applyOrderings(items, orderings);
			outputAll(output, items, stackFunc, visibilityFunc);
		}

		private List<Item> collectBlocks(Predicate<Item> exclusionPredicate) {
			List<Item> items = new ReferenceArrayList<>();
			for (RegistryEntry<Block> entry : Create.registrate().getAll(Registries.BLOCK)) {
				if (!CreateRegistrate.isInCreativeTab(entry, tabFilter))
					continue;
				Item item = entry.get()
					.asItem();
				if (item == Items.AIR)
					continue;
				if (!exclusionPredicate.test(item))
					items.add(item);
			}
			items = new ReferenceArrayList<>(new ReferenceLinkedOpenHashSet<>(items));
			return items;
		}

		private List<Item> collectItems(Predicate<Item> exclusionPredicate) {
			List<Item> items = new ReferenceArrayList<>();
			for (RegistryEntry<Item> entry : Create.registrate().getAll(Registries.ITEM)) {
				if (!CreateRegistrate.isInCreativeTab(entry, tabFilter))
					continue;
				Item item = entry.get();
				if (item instanceof BlockItem)
					continue;
				if (!exclusionPredicate.test(item))
					items.add(item);
			}
			return items;
		}

		private static void applyOrderings(List<Item> items, List<ItemOrdering> orderings) {
			for (ItemOrdering ordering : orderings) {
				int anchorIndex = items.indexOf(ordering.anchor());
				if (anchorIndex != -1) {
					Item item = ordering.item();
					int itemIndex = items.indexOf(item);
					if (itemIndex != -1) {
						items.remove(itemIndex);
						if (itemIndex < anchorIndex) {
							anchorIndex--;
						}
					}
					if (ordering.type() == ItemOrdering.Type.AFTER) {
						items.add(anchorIndex + 1, item);
					} else {
						items.add(anchorIndex, item);
					}
				}
			}
		}

		private static void outputAll(Output output, List<Item> items, Function<Item, ItemStack> stackFunc, Function<Item, TabVisibility> visibilityFunc) {
			for (Item item : items) {
				output.accept(stackFunc.apply(item), visibilityFunc.apply(item));
			}
		}

		private record ItemOrdering(Item item, Item anchor, Type type) {
			public static ItemOrdering before(Item item, Item anchor) {
				return new ItemOrdering(item, anchor, Type.BEFORE);
			}

			public static ItemOrdering after(Item item, Item anchor) {
				return new ItemOrdering(item, anchor, Type.AFTER);
			}

			public enum Type {
				BEFORE,
				AFTER;
			}
		}
	}
}
