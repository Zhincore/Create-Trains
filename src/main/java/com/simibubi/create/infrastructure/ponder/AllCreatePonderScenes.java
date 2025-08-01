package com.simibubi.create.infrastructure.ponder;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.Create;
import com.simibubi.create.content.trains.track.TrackBlock;
import com.simibubi.create.content.trains.track.TrackMaterial;
import com.simibubi.create.infrastructure.ponder.scenes.ArmScenes;
import com.simibubi.create.infrastructure.ponder.scenes.BearingScenes;
import com.simibubi.create.infrastructure.ponder.scenes.BeltScenes;
import com.simibubi.create.infrastructure.ponder.scenes.CartAssemblerScenes;
import com.simibubi.create.infrastructure.ponder.scenes.ChainDriveScenes;
import com.simibubi.create.infrastructure.ponder.scenes.ChassisScenes;
import com.simibubi.create.infrastructure.ponder.scenes.ChuteScenes;
import com.simibubi.create.infrastructure.ponder.scenes.CrafterScenes;
import com.simibubi.create.infrastructure.ponder.scenes.DeployerScenes;
import com.simibubi.create.infrastructure.ponder.scenes.DetectorScenes;
import com.simibubi.create.infrastructure.ponder.scenes.DisplayScenes;
import com.simibubi.create.infrastructure.ponder.scenes.EjectorScenes;
import com.simibubi.create.infrastructure.ponder.scenes.ElevatorScenes;
import com.simibubi.create.infrastructure.ponder.scenes.FanScenes;
import com.simibubi.create.infrastructure.ponder.scenes.FunnelScenes;
import com.simibubi.create.infrastructure.ponder.scenes.GantryScenes;
import com.simibubi.create.infrastructure.ponder.scenes.ItemVaultScenes;
import com.simibubi.create.infrastructure.ponder.scenes.KineticsScenes;
import com.simibubi.create.infrastructure.ponder.scenes.MechanicalDrillScenes;
import com.simibubi.create.infrastructure.ponder.scenes.MechanicalSawScenes;
import com.simibubi.create.infrastructure.ponder.scenes.MovementActorScenes;
import com.simibubi.create.infrastructure.ponder.scenes.PistonScenes;
import com.simibubi.create.infrastructure.ponder.scenes.ProcessingScenes;
import com.simibubi.create.infrastructure.ponder.scenes.PulleyScenes;
import com.simibubi.create.infrastructure.ponder.scenes.RedstoneScenes;
import com.simibubi.create.infrastructure.ponder.scenes.RedstoneScenes2;
import com.simibubi.create.infrastructure.ponder.scenes.RollerScenes;
import com.simibubi.create.infrastructure.ponder.scenes.SteamScenes;
import com.simibubi.create.infrastructure.ponder.scenes.TunnelScenes;
import com.simibubi.create.infrastructure.ponder.scenes.fluid.DrainScenes;
import com.simibubi.create.infrastructure.ponder.scenes.fluid.FluidMovementActorScenes;
import com.simibubi.create.infrastructure.ponder.scenes.fluid.FluidTankScenes;
import com.simibubi.create.infrastructure.ponder.scenes.fluid.HosePulleyScenes;
import com.simibubi.create.infrastructure.ponder.scenes.fluid.PipeScenes;
import com.simibubi.create.infrastructure.ponder.scenes.fluid.PumpScenes;
import com.simibubi.create.infrastructure.ponder.scenes.fluid.SpoutScenes;
import com.simibubi.create.infrastructure.ponder.scenes.highLogistics.FactoryGaugeScenes;
import com.simibubi.create.infrastructure.ponder.scenes.highLogistics.FrogAndConveyorScenes;
import com.simibubi.create.infrastructure.ponder.scenes.highLogistics.PackagerScenes;
import com.simibubi.create.infrastructure.ponder.scenes.highLogistics.PostboxScenes;
import com.simibubi.create.infrastructure.ponder.scenes.highLogistics.RepackagerScenes;
import com.simibubi.create.infrastructure.ponder.scenes.highLogistics.RequesterAndShopScenes;
import com.simibubi.create.infrastructure.ponder.scenes.highLogistics.StockLinkScenes;
import com.simibubi.create.infrastructure.ponder.scenes.highLogistics.StockTickerScenes;
import com.simibubi.create.infrastructure.ponder.scenes.highLogistics.TableClothScenes;
import com.simibubi.create.infrastructure.ponder.scenes.trains.TrackObserverScenes;
import com.simibubi.create.infrastructure.ponder.scenes.trains.TrackScenes;
import com.simibubi.create.infrastructure.ponder.scenes.trains.TrainScenes;
import com.simibubi.create.infrastructure.ponder.scenes.trains.TrainSignalScenes;
import com.simibubi.create.infrastructure.ponder.scenes.trains.TrainStationScenes;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.entry.ItemProviderEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;

import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.minecraft.resources.ResourceLocation;

public class AllCreatePonderScenes {

	public static void register(PonderSceneRegistrationHelper<ResourceLocation> helper) {
		PonderSceneRegistrationHelper<ItemProviderEntry<?>> HELPER = helper.withKeyFunction(RegistryEntry::getId);

		// HELPER.forComponents(AllBlocks.SHAFT)
		// 	.addStoryBoard("shaft/relay", KineticsScenes::shaftAsRelay, AllCreatePonderTags.KINETIC_RELAYS);
		// HELPER.forComponents(AllBlocks.SHAFT, AllBlocks.ANDESITE_ENCASED_SHAFT, AllBlocks.BRASS_ENCASED_SHAFT)
		// 	.addStoryBoard("shaft/encasing", KineticsScenes::shaftsCanBeEncased);

		// HELPER.forComponents(AllBlocks.COGWHEEL)
		// 	.addStoryBoard("cog/small", KineticsScenes::cogAsRelay, AllCreatePonderTags.KINETIC_RELAYS)
		// 	.addStoryBoard("cog/speedup", KineticsScenes::cogsSpeedUp)
		// 	.addStoryBoard("cog/encasing", KineticsScenes::cogwheelsCanBeEncased);

		// HELPER.forComponents(AllBlocks.LARGE_COGWHEEL)
		// 	.addStoryBoard("cog/speedup", KineticsScenes::cogsSpeedUp)
		// 	.addStoryBoard("cog/large", KineticsScenes::largeCogAsRelay, AllCreatePonderTags.KINETIC_RELAYS)
		// 	.addStoryBoard("cog/encasing", KineticsScenes::cogwheelsCanBeEncased);

		// HELPER.forComponents(AllItems.BELT_CONNECTOR)
		// 	.addStoryBoard("belt/connect", BeltScenes::beltConnector, AllCreatePonderTags.KINETIC_RELAYS)
		// 	.addStoryBoard("belt/directions", BeltScenes::directions)
		// 	.addStoryBoard("belt/transport", BeltScenes::transport, AllCreatePonderTags.LOGISTICS)
		// 	.addStoryBoard("belt/encasing", BeltScenes::beltsCanBeEncased);

		HELPER.forComponents(AllBlocks.ANDESITE_CASING, AllBlocks.BRASS_CASING)
			.addStoryBoard("shaft/encasing", KineticsScenes::shaftsCanBeEncased)
			.addStoryBoard("belt/encasing", BeltScenes::beltsCanBeEncased);

		// HELPER.forComponents(AllBlocks.GEARBOX, AllItems.VERTICAL_GEARBOX)
		// 	.addStoryBoard("gearbox", KineticsScenes::gearbox, AllCreatePonderTags.KINETIC_RELAYS);

		// HELPER.addStoryBoard(AllBlocks.CLUTCH, "clutch", KineticsScenes::clutch, AllCreatePonderTags.KINETIC_RELAYS);
		// HELPER.addStoryBoard(AllBlocks.GEARSHIFT, "gearshift", KineticsScenes::gearshift, AllCreatePonderTags.KINETIC_RELAYS);

		// HELPER.forComponents(AllBlocks.SEQUENCED_GEARSHIFT)
		// 	.addStoryBoard("sequenced_gearshift", KineticsScenes::sequencedGearshift);

		// HELPER.forComponents(AllBlocks.ENCASED_FAN)
		// 	.addStoryBoard("fan/direction", FanScenes::direction, AllCreatePonderTags.KINETIC_APPLIANCES)
		// 	.addStoryBoard("fan/processing", FanScenes::processing);

		// HELPER.forComponents(AllBlocks.CREATIVE_MOTOR)
		// 	.addStoryBoard("creative_motor", KineticsScenes::creativeMotor, AllCreatePonderTags.KINETIC_SOURCES)
		// 	.addStoryBoard("creative_motor_mojang", KineticsScenes::creativeMotorMojang);
		// HELPER.addStoryBoard(AllBlocks.WATER_WHEEL, "water_wheel", KineticsScenes::waterWheel,
		// 	AllCreatePonderTags.KINETIC_SOURCES);
		// HELPER.addStoryBoard(AllBlocks.LARGE_WATER_WHEEL, "large_water_wheel", KineticsScenes::largeWaterWheel,
		// 	AllCreatePonderTags.KINETIC_SOURCES);

		// HELPER.addStoryBoard(AllBlocks.HAND_CRANK, "hand_crank", KineticsScenes::handCrank, AllCreatePonderTags.KINETIC_SOURCES);

		// HELPER.addStoryBoard(AllBlocks.COPPER_VALVE_HANDLE, "valve_handle", KineticsScenes::valveHandle,
		// 	AllCreatePonderTags.KINETIC_SOURCES);
		// HELPER.forComponents(AllBlocks.DYED_VALVE_HANDLES.toArray())
		// 	.addStoryBoard("valve_handle", KineticsScenes::valveHandle);

		// HELPER.addStoryBoard(AllBlocks.ENCASED_CHAIN_DRIVE, "chain_drive/relay", ChainDriveScenes::chainDriveAsRelay,
		// 	AllCreatePonderTags.KINETIC_RELAYS);
		// HELPER.forComponents(AllBlocks.ENCASED_CHAIN_DRIVE, AllBlocks.ADJUSTABLE_CHAIN_GEARSHIFT)
		// 	.addStoryBoard("chain_drive/gearshift", ChainDriveScenes::adjustableChainGearshift);

		// HELPER.forComponents(AllBlocks.ROTATION_SPEED_CONTROLLER)
		// 	.addStoryBoard("speed_controller", KineticsScenes::speedController);

		// // Gauges
		// HELPER.addStoryBoard(AllBlocks.SPEEDOMETER, "gauges", KineticsScenes::speedometer);
		// HELPER.addStoryBoard(AllBlocks.STRESSOMETER, "gauges", KineticsScenes::stressometer);

		// // Item Processing
		// HELPER.addStoryBoard(AllBlocks.MILLSTONE, "millstone", ProcessingScenes::millstone);
		// HELPER.addStoryBoard(AllBlocks.CRUSHING_WHEEL, "crushing_wheel", ProcessingScenes::crushingWheels);
		// HELPER.addStoryBoard(AllBlocks.MECHANICAL_MIXER, "mechanical_mixer/mixing", ProcessingScenes::mixing);
		// HELPER.forComponents(AllBlocks.MECHANICAL_PRESS)
		// 	.addStoryBoard("mechanical_press/pressing", ProcessingScenes::pressing)
		// 	.addStoryBoard("mechanical_press/compacting", ProcessingScenes::compacting);
		// HELPER.forComponents(AllBlocks.BASIN)
		// 	.addStoryBoard("basin", ProcessingScenes::basin)
		// 	.addStoryBoard("mechanical_mixer/mixing", ProcessingScenes::mixing)
		// 	.addStoryBoard("mechanical_press/compacting", ProcessingScenes::compacting);
		// HELPER.addStoryBoard(AllItems.EMPTY_BLAZE_BURNER, "empty_blaze_burner", ProcessingScenes::emptyBlazeBurner);
		// HELPER.addStoryBoard(AllBlocks.BLAZE_BURNER, "blaze_burner", ProcessingScenes::blazeBurner);
		// HELPER.addStoryBoard(AllBlocks.DEPOT, "depot", BeltScenes::depot);
		// HELPER.forComponents(AllBlocks.WEIGHTED_EJECTOR)
		// 	.addStoryBoard("weighted_ejector/eject", EjectorScenes::ejector)
		// 	.addStoryBoard("weighted_ejector/split", EjectorScenes::splitY)
		// 	.addStoryBoard("weighted_ejector/redstone", EjectorScenes::redstone);

		// // Crafters
		// HELPER.forComponents(AllBlocks.MECHANICAL_CRAFTER)
		// 	.addStoryBoard("mechanical_crafter/setup", CrafterScenes::setup)
		// 	.addStoryBoard("mechanical_crafter/connect", CrafterScenes::connect);
		// HELPER.forComponents(AllBlocks.MECHANICAL_CRAFTER, AllItems.CRAFTER_SLOT_COVER)
		// 	.addStoryBoard("mechanical_crafter/covers", CrafterScenes::covers);

		// // Vaults
		// HELPER.forComponents(AllBlocks.ITEM_VAULT)
		// 	.addStoryBoard("item_vault/storage", ItemVaultScenes::storage, AllCreatePonderTags.LOGISTICS)
		// 	.addStoryBoard("item_vault/sizes", ItemVaultScenes::sizes);

		// // Chutes
		// HELPER.forComponents(AllBlocks.CHUTE)
		// 	.addStoryBoard("chute/downward", ChuteScenes::downward, AllCreatePonderTags.LOGISTICS)
		// 	.addStoryBoard("chute/upward", ChuteScenes::upward);
		// HELPER.forComponents(AllBlocks.CHUTE, AllBlocks.SMART_CHUTE)
		// 	.addStoryBoard("chute/smart", ChuteScenes::smart);

		// // Funnels
		// HELPER.addStoryBoard(AllBlocks.BRASS_FUNNEL, "funnels/brass", FunnelScenes::brass);
		// HELPER.forComponents(AllBlocks.ANDESITE_FUNNEL, AllBlocks.BRASS_FUNNEL)
		// 	.addStoryBoard("funnels/intro", FunnelScenes::intro, AllCreatePonderTags.LOGISTICS)
		// 	.addStoryBoard("funnels/direction", FunnelScenes::directionality)
		// 	.addStoryBoard("funnels/compat", FunnelScenes::compat)
		// 	.addStoryBoard("funnels/redstone", FunnelScenes::redstone)
		// 	.addStoryBoard("funnels/transposer", FunnelScenes::transposer);
		// HELPER.addStoryBoard(AllBlocks.ANDESITE_FUNNEL, "funnels/brass", FunnelScenes::brass);

		// // Tunnels
		// HELPER.addStoryBoard(AllBlocks.ANDESITE_TUNNEL, "tunnels/andesite", TunnelScenes::andesite);
		// HELPER.forComponents(AllBlocks.BRASS_TUNNEL)
		// 	.addStoryBoard("tunnels/brass", TunnelScenes::brass)
		// 	.addStoryBoard("tunnels/brass_modes", TunnelScenes::brassModes);

		// // Chassis & Super Glue
		// HELPER.forComponents(AllBlocks.LINEAR_CHASSIS, AllBlocks.SECONDARY_LINEAR_CHASSIS)
		// 	.addStoryBoard("chassis/linear_group", ChassisScenes::linearGroup, AllCreatePonderTags.CONTRAPTION_ASSEMBLY)
		// 	.addStoryBoard("chassis/linear_attachment", ChassisScenes::linearAttachement);
		// HELPER.forComponents(AllBlocks.RADIAL_CHASSIS)
		// 	.addStoryBoard("chassis/radial", ChassisScenes::radial, AllCreatePonderTags.CONTRAPTION_ASSEMBLY);
		HELPER.forComponents(AllItems.SUPER_GLUE)
			.addStoryBoard("super_glue", ChassisScenes::superGlue, AllCreatePonderTags.CONTRAPTION_ASSEMBLY);
		// HELPER.forComponents(AllBlocks.STICKER)
		// 	.addStoryBoard("sticker", RedstoneScenes::sticker, AllCreatePonderTags.CONTRAPTION_ASSEMBLY);

		// // Mechanical Arm
		// HELPER.forComponents(AllBlocks.MECHANICAL_ARM)
		// 	.addStoryBoard("mechanical_arm/setup", ArmScenes::setup, AllCreatePonderTags.ARM_TARGETS)
		// 	.addStoryBoard("mechanical_arm/filter", ArmScenes::filtering)
		// 	.addStoryBoard("mechanical_arm/modes", ArmScenes::modes)
		// 	.addStoryBoard("mechanical_arm/redstone", ArmScenes::redstone);

		// // Mechanical Piston
		// HELPER.forComponents(AllBlocks.MECHANICAL_PISTON, AllBlocks.STICKY_MECHANICAL_PISTON)
		// 	.addStoryBoard("mechanical_piston/anchor", PistonScenes::movement, AllCreatePonderTags.KINETIC_APPLIANCES,
		// 		AllCreatePonderTags.MOVEMENT_ANCHOR);
		// HELPER
		// 	.forComponents(AllBlocks.MECHANICAL_PISTON, AllBlocks.STICKY_MECHANICAL_PISTON,
		// 		AllBlocks.PISTON_EXTENSION_POLE)
		// 	.addStoryBoard("mechanical_piston/piston_pole", PistonScenes::poles);
		// HELPER.forComponents(AllBlocks.MECHANICAL_PISTON, AllBlocks.STICKY_MECHANICAL_PISTON)
		// 	.addStoryBoard("mechanical_piston/modes", PistonScenes::movementModes);

		// // Pulleys
		// HELPER.forComponents(AllBlocks.ROPE_PULLEY)
		// 	.addStoryBoard("rope_pulley/anchor", PulleyScenes::movement, AllCreatePonderTags.KINETIC_APPLIANCES,
		// 		AllCreatePonderTags.MOVEMENT_ANCHOR)
		// 	.addStoryBoard("rope_pulley/modes", PulleyScenes::movementModes)
		// 	.addStoryBoard("rope_pulley/multi_rope", PulleyScenes::multiRope)
		// 	.addStoryBoard("rope_pulley/attachment", PulleyScenes::attachment);
		// HELPER.forComponents(AllBlocks.ELEVATOR_PULLEY)
		// 	.addStoryBoard("elevator_pulley/elevator", ElevatorScenes::elevator)
		// 	.addStoryBoard("elevator_pulley/multi_rope", ElevatorScenes::multiRope);

		// // Windmill Bearing
		// HELPER.forComponents(AllBlocks.WINDMILL_BEARING)
		// 	.addStoryBoard("windmill_bearing/source", BearingScenes::windmillsAsSource, AllCreatePonderTags.KINETIC_SOURCES)
		// 	.addStoryBoard("windmill_bearing/structure", BearingScenes::windmillsAnyStructure,
		// 		AllCreatePonderTags.MOVEMENT_ANCHOR);
		// HELPER.forComponents(AllBlocks.SAIL)
		// 	.addStoryBoard("sail", BearingScenes::sail);
		// HELPER.forComponents(AllBlocks.SAIL_FRAME)
		// 	.addStoryBoard("sail", BearingScenes::sailFrame);

		// // Mechanical Bearing
		// HELPER.forComponents(AllBlocks.MECHANICAL_BEARING)
		// 	.addStoryBoard("mechanical_bearing/anchor", BearingScenes::mechanicalBearing, AllCreatePonderTags.KINETIC_APPLIANCES,
		// 		AllCreatePonderTags.MOVEMENT_ANCHOR)
		// 	.addStoryBoard("mechanical_bearing/modes", BearingScenes::bearingModes)
		// 	.addStoryBoard("mechanical_bearing/stabilized", BearingScenes::stabilizedBearings,
		// 		AllCreatePonderTags.CONTRAPTION_ACTOR);

		// // Clockwork Bearing
		// HELPER.addStoryBoard(AllBlocks.CLOCKWORK_BEARING, "clockwork_bearing", BearingScenes::clockwork,
		// 	AllCreatePonderTags.KINETIC_APPLIANCES, AllCreatePonderTags.MOVEMENT_ANCHOR);

		// // Gantries
		// HELPER.addStoryBoard(AllBlocks.GANTRY_SHAFT, "gantry/intro", GantryScenes::introForShaft,
		// 	AllCreatePonderTags.KINETIC_APPLIANCES, AllCreatePonderTags.MOVEMENT_ANCHOR);
		// HELPER.addStoryBoard(AllBlocks.GANTRY_CARRIAGE, "gantry/intro", GantryScenes::introForPinion,
		// 	AllCreatePonderTags.KINETIC_APPLIANCES, AllCreatePonderTags.MOVEMENT_ANCHOR);
		// HELPER.forComponents(AllBlocks.GANTRY_SHAFT, AllBlocks.GANTRY_CARRIAGE)
		// 	.addStoryBoard("gantry/redstone", GantryScenes::redstone)
		// 	.addStoryBoard("gantry/direction", GantryScenes::direction)
		// 	.addStoryBoard("gantry/subgantry", GantryScenes::subgantry);

		// // Cart Assembler
		// HELPER.forComponents(AllBlocks.CART_ASSEMBLER)
		// 	.addStoryBoard("cart_assembler/anchor", CartAssemblerScenes::anchor, AllCreatePonderTags.MOVEMENT_ANCHOR)
		// 	.addStoryBoard("cart_assembler/modes", CartAssemblerScenes::modes)
		// 	.addStoryBoard("cart_assembler/dual", CartAssemblerScenes::dual)
		// 	.addStoryBoard("cart_assembler/rails", CartAssemblerScenes::rails);

		// // Movement Actors
		// HELPER.forComponents(AllBlocks.PORTABLE_STORAGE_INTERFACE)
		// 	.addStoryBoard("portable_interface/transfer", MovementActorScenes::psiTransfer, AllCreatePonderTags.CONTRAPTION_ACTOR)
		// 	.addStoryBoard("portable_interface/redstone", MovementActorScenes::psiRedstone);
		// HELPER.forComponents(AllBlocks.REDSTONE_CONTACT)
		// 	.addStoryBoard("redstone_contact", RedstoneScenes::contact);
		// HELPER.forComponents(AllBlocks.MECHANICAL_SAW)
		// 	.addStoryBoard("mechanical_saw/processing", MechanicalSawScenes::processing, AllCreatePonderTags.KINETIC_APPLIANCES)
		// 	.addStoryBoard("mechanical_saw/breaker", MechanicalSawScenes::treeCutting)
		// 	.addStoryBoard("mechanical_saw/contraption", MechanicalSawScenes::contraption, AllCreatePonderTags.CONTRAPTION_ACTOR);
		// HELPER.forComponents(AllBlocks.MECHANICAL_DRILL)
		// 	.addStoryBoard("mechanical_drill/breaker", MechanicalDrillScenes::breaker, AllCreatePonderTags.KINETIC_APPLIANCES)
		// 	.addStoryBoard("mechanical_drill/contraption", MechanicalDrillScenes::contraption,
		// 		AllCreatePonderTags.CONTRAPTION_ACTOR);
		// HELPER.forComponents(AllBlocks.DEPLOYER)
		// 	.addStoryBoard("deployer/filter", DeployerScenes::filter, AllCreatePonderTags.KINETIC_APPLIANCES)
		// 	.addStoryBoard("deployer/modes", DeployerScenes::modes)
		// 	.addStoryBoard("deployer/processing", DeployerScenes::processing)
		// 	.addStoryBoard("deployer/redstone", DeployerScenes::redstone)
		// 	.addStoryBoard("deployer/contraption", DeployerScenes::contraption, AllCreatePonderTags.CONTRAPTION_ACTOR);
		// HELPER.forComponents(AllBlocks.MECHANICAL_HARVESTER)
		// 	.addStoryBoard("harvester", MovementActorScenes::harvester);
		// HELPER.forComponents(AllBlocks.MECHANICAL_PLOUGH)
		// 	.addStoryBoard("plough", MovementActorScenes::plough);
		// HELPER.forComponents(AllBlocks.CONTRAPTION_CONTROLS)
		// 	.addStoryBoard("contraption_controls", MovementActorScenes::contraptionControls);
		// HELPER.forComponents(AllBlocks.MECHANICAL_ROLLER)
		// 	.addStoryBoard("mechanical_roller/clear_and_pave", RollerScenes::clearAndPave)
		// 	.addStoryBoard("mechanical_roller/fill", RollerScenes::fill);

		// // Fluids
		// HELPER.forComponents(AllBlocks.FLUID_PIPE)
		// 	.addStoryBoard("fluid_pipe/flow", PipeScenes::flow, AllCreatePonderTags.FLUIDS)
		// 	.addStoryBoard("fluid_pipe/interaction", PipeScenes::interaction)
		// 	.addStoryBoard("fluid_pipe/encasing", PipeScenes::encasing);
		// HELPER.forComponents(AllBlocks.COPPER_CASING)
		// 	.addStoryBoard("fluid_pipe/encasing", PipeScenes::encasing);
		// HELPER.forComponents(AllBlocks.MECHANICAL_PUMP)
		// 	.addStoryBoard("mechanical_pump/flow", PumpScenes::flow, AllCreatePonderTags.FLUIDS, AllCreatePonderTags.KINETIC_APPLIANCES)
		// 	.addStoryBoard("mechanical_pump/speed", PumpScenes::speed);
		// HELPER.forComponents(AllBlocks.FLUID_VALVE)
		// 	.addStoryBoard("fluid_valve", PipeScenes::valve, AllCreatePonderTags.FLUIDS, AllCreatePonderTags.KINETIC_APPLIANCES);
		// HELPER.forComponents(AllBlocks.SMART_FLUID_PIPE)
		// 	.addStoryBoard("smart_pipe", PipeScenes::smart, AllCreatePonderTags.FLUIDS);
		// HELPER.forComponents(AllBlocks.FLUID_TANK)
		// 	.addStoryBoard("fluid_tank/storage", FluidTankScenes::storage, AllCreatePonderTags.FLUIDS)
		// 	.addStoryBoard("fluid_tank/sizes", FluidTankScenes::sizes);
		// HELPER.forComponents(AllBlocks.CREATIVE_FLUID_TANK)
		// 	.addStoryBoard("fluid_tank/storage_creative", FluidTankScenes::creative, AllCreatePonderTags.FLUIDS,
		// 		AllCreatePonderTags.CREATIVE)
		// 	.addStoryBoard("fluid_tank/sizes_creative", FluidTankScenes::sizes);
		// HELPER.forComponents(AllBlocks.HOSE_PULLEY)
		// 	.addStoryBoard("hose_pulley/intro", HosePulleyScenes::intro, AllCreatePonderTags.FLUIDS, AllCreatePonderTags.KINETIC_APPLIANCES)
		// 	.addStoryBoard("hose_pulley/level", HosePulleyScenes::level)
		// 	.addStoryBoard("hose_pulley/infinite", HosePulleyScenes::infinite);
		// HELPER.forComponents(AllBlocks.SPOUT)
		// 	.addStoryBoard("spout", SpoutScenes::filling, AllCreatePonderTags.FLUIDS);
		// HELPER.forComponents(AllBlocks.ITEM_DRAIN)
		// 	.addStoryBoard("item_drain", DrainScenes::emptying, AllCreatePonderTags.FLUIDS);
		// HELPER.forComponents(AllBlocks.PORTABLE_FLUID_INTERFACE)
		// 	.addStoryBoard("portable_interface/transfer_fluid", FluidMovementActorScenes::transfer, AllCreatePonderTags.FLUIDS,
		// 		AllCreatePonderTags.CONTRAPTION_ACTOR)
		// 	.addStoryBoard("portable_interface/redstone_fluid", MovementActorScenes::psiRedstone);

		// // Redstone
		// HELPER.forComponents(AllBlocks.PULSE_EXTENDER)
		// 	.addStoryBoard("pulse_extender", RedstoneScenes::pulseExtender);
		// HELPER.forComponents(AllBlocks.PULSE_REPEATER)
		// 	.addStoryBoard("pulse_repeater", RedstoneScenes::pulseRepeater);
		// HELPER.forComponents(AllBlocks.POWERED_LATCH)
		// 	.addStoryBoard("powered_latch", RedstoneScenes::poweredLatch);
		// HELPER.forComponents(AllBlocks.POWERED_TOGGLE_LATCH)
		// 	.addStoryBoard("powered_toggle_latch", RedstoneScenes::poweredToggleLatch);
		// HELPER.forComponents(AllBlocks.ANALOG_LEVER)
		// 	.addStoryBoard("analog_lever", RedstoneScenes::analogLever);
		HELPER.forComponents(AllBlocks.ORANGE_NIXIE_TUBE)
			.addStoryBoard("nixie_tube", RedstoneScenes::nixieTube);
		// HELPER.forComponents(AllBlocks.REDSTONE_LINK)
		// 	.addStoryBoard("redstone_link", RedstoneScenes::redstoneLink);
		// HELPER.forComponents(AllBlocks.ROSE_QUARTZ_LAMP)
		// 	.addStoryBoard("rose_quartz_lamp", RedstoneScenes2::roseQuartzLamp);
		// HELPER.forComponents(AllBlocks.PULSE_TIMER)
		// 	.addStoryBoard("pulse_timer", RedstoneScenes2::pulseTimer);

		// HELPER.forComponents(AllBlocks.SMART_OBSERVER)
		// 	.addStoryBoard("smart_observer", DetectorScenes::smartObserver);
		// HELPER.forComponents(AllBlocks.THRESHOLD_SWITCH)
		// 	.addStoryBoard("threshold_switch", DetectorScenes::thresholdSwitch);

		// // Hilo
		// HELPER.forComponents(AllBlocks.CHAIN_CONVEYOR)
		// 	.addStoryBoard("high_logistics/chain_conveyor", FrogAndConveyorScenes::conveyor);
		// HELPER.forComponents(AllBlocks.PACKAGE_FROGPORT)
		// 	.addStoryBoard("high_logistics/package_frogport", FrogAndConveyorScenes::frogPort);
		// HELPER.forComponents(AllBlocks.PACKAGE_POSTBOXES.toArray())
		// 	.addStoryBoard("high_logistics/package_postbox", PostboxScenes::postbox);
		// HELPER.forComponents(AllBlocks.PACKAGER)
		// 	.addStoryBoard("high_logistics/packager", PackagerScenes::packager)
		// 	.addStoryBoard("high_logistics/packager_address", PackagerScenes::packagerAddress);
		// HELPER.forComponents(AllBlocks.STOCK_LINK)
		// 	.addStoryBoard("high_logistics/stock_link", StockLinkScenes::stockLink);
		// HELPER.forComponents(AllBlocks.STOCK_TICKER)
		// 	.addStoryBoard("high_logistics/stock_ticker", StockTickerScenes::stockTicker)
		// 	.addStoryBoard("high_logistics/stock_ticker_address", StockTickerScenes::stockTickerAddress);
		// HELPER.forComponents(AllBlocks.REDSTONE_REQUESTER)
		// 	.addStoryBoard("high_logistics/redstone_requester", RequesterAndShopScenes::requester);
		// HELPER.forComponents(AllBlocks.REPACKAGER)
		// 	.addStoryBoard("high_logistics/repackager", RepackagerScenes::repackager);
		// HELPER.forComponents(AllBlocks.TABLE_CLOTHS.toArray())
		// 	.addStoryBoard("high_logistics/table_cloth", TableClothScenes::tableCloth);
		// HELPER.forComponents(AllBlocks.ANDESITE_TABLE_CLOTH, AllBlocks.BRASS_TABLE_CLOTH, AllBlocks.COPPER_TABLE_CLOTH)
		// 	.addStoryBoard("high_logistics/table_cloth", TableClothScenes::tableCloth);
		// HELPER.forComponents(AllBlocks.FACTORY_GAUGE)
		// 	.addStoryBoard("high_logistics/factory_gauge_restocker", FactoryGaugeScenes::restocker)
		// 	.addStoryBoard("high_logistics/factory_gauge_recipe", FactoryGaugeScenes::recipe)
		// 	.addStoryBoard("high_logistics/factory_gauge_crafting", FactoryGaugeScenes::crafting)
		// 	.addStoryBoard("high_logistics/factory_gauge_links", FactoryGaugeScenes::links);

		// Trains
		HELPER.forComponents(TrackMaterial.allBlocks()
				.stream()
				.map((trackSupplier) -> new BlockEntry<TrackBlock>(
					// note: these blocks probably WON'T be in the Create Registrate, but a simple
					// code trace reveals the Entry's registrate isn't used
					Create.registrate(),
					RegistryObject.create(ForgeRegistries.BLOCKS.getKey(trackSupplier.get()), ForgeRegistries.BLOCKS)
				))
				.toArray(BlockEntry[]::new))
			.addStoryBoard("train_track/placement", TrackScenes::placement)
			.addStoryBoard("train_track/portal", TrackScenes::portal)
			.addStoryBoard("train_track/chunks", TrackScenes::chunks);

		HELPER.forComponents(AllBlocks.TRACK_STATION)
			.addStoryBoard("train_station/assembly", TrainStationScenes::assembly)
			.addStoryBoard("train_station/schedule", TrainStationScenes::autoSchedule);

		HELPER.forComponents(AllBlocks.TRACK_SIGNAL)
			.addStoryBoard("train_signal/placement", TrainSignalScenes::placement)
			.addStoryBoard("train_signal/signaling", TrainSignalScenes::signaling)
			.addStoryBoard("train_signal/redstone", TrainSignalScenes::redstone);

		HELPER.forComponents(AllItems.SCHEDULE)
			.addStoryBoard("train_schedule", TrainScenes::schedule);

		HELPER.forComponents(AllBlocks.TRAIN_CONTROLS)
			.addStoryBoard("train_controls", TrainScenes::controls);

		HELPER.forComponents(AllBlocks.TRACK_OBSERVER)
			.addStoryBoard("train_observer", TrackObserverScenes::observe);

		// Display Link
		HELPER.forComponents(AllBlocks.DISPLAY_LINK)
			.addStoryBoard("display_link", DisplayScenes::link)
			.addStoryBoard("display_link_redstone", DisplayScenes::redstone);
		HELPER.forComponents(AllBlocks.DISPLAY_BOARD)
			.addStoryBoard("display_board", DisplayScenes::board);

		// Steam
		HELPER.forComponents(AllBlocks.STEAM_WHISTLE)
			.addStoryBoard("steam_whistle", SteamScenes::whistle);
		// HELPER.forComponents(AllBlocks.STEAM_ENGINE)
		// 	.addStoryBoard("steam_engine", SteamScenes::engine);

	}
}
