package com.simibubi.create.compat;

import java.util.Optional;
import java.util.function.Supplier;

import net.createmod.catnip.lang.Lang;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * For compatibility with and without another mod present, we have to define load conditions of the specific code
 */
public enum Mods {
	AETHER,
	BETTEREND,
	COMPUTERCRAFT,
	CURIOS,
	DYNAMICTREES,
	JEI,
	FUNCTIONALSTORAGE,
	OCCULTISM,
	PACKETFIXER,
	SOPHISTICATEDBACKPACKS,
	SOPHISTICATEDSTORAGE,
	STORAGEDRAWERS,
	TCONSTRUCT,
	FRAMEDBLOCKS,
	XLPACKETS,
	MODERNUI,
	FTBCHUNKS,
	JOURNEYMAP,
	FTBLIBRARY,
	INVENTORYSORTER;

	private final String id;

	Mods() {
		id = Lang.asId(name());
	}

	/**
	 * @return the mod id
	 */
	public String id() {
		return id;
	}

	public ResourceLocation rl(String path) {
		return new ResourceLocation(id, path);
	}

	public Block getBlock(String id) {
		return ForgeRegistries.BLOCKS.getValue(rl(id));
	}

	public Item getItem(String id) {
		return ForgeRegistries.ITEMS.getValue(rl(id));
	}

	public boolean contains(ItemLike entry) {
		if (!isLoaded())
			return false;
		Item asItem = entry.asItem();
		return asItem != null && CatnipServices.REGISTRIES.getKeyOrThrow(asItem)
			.getNamespace()
			.equals(id);
	}

	/**
	 * @return a boolean of whether the mod is loaded or not based on mod id
	 */
	public boolean isLoaded() {
		return ModList.get().isLoaded(id);
	}

	/**
	 * Simple hook to run code if a mod is installed
	 * @param toRun will be run only if the mod is loaded
	 * @return Optional.empty() if the mod is not loaded, otherwise an Optional of the return value of the given supplier
	 */
	public <T> Optional<T> runIfInstalled(Supplier<Supplier<T>> toRun) {
		if (isLoaded())
			return Optional.of(toRun.get().get());
		return Optional.empty();
	}

	/**
	 * Simple hook to execute code if a mod is installed
	 * @param toExecute will be executed only if the mod is loaded
	 */
	public void executeIfInstalled(Supplier<Runnable> toExecute) {
		if (isLoaded()) {
			toExecute.get().run();
		}
	}
}
