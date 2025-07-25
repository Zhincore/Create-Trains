package com.simibubi.create.compat.computercraft.implementation.peripherals;

import com.simibubi.create.content.logistics.packagePort.frogport.FrogportBlockEntity;
import com.simibubi.create.compat.computercraft.implementation.ComputerUtil;
import com.simibubi.create.AllPackets;
import com.simibubi.create.content.logistics.packagePort.PackagePortConfigurationPacket;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class FrogportPeripheral extends SyncedPeripheral<FrogportBlockEntity> {

	public FrogportPeripheral(FrogportBlockEntity blockEntity) {
		super(blockEntity);
	}

	@LuaFunction(mainThread = true)
	public final void setAddress(String address) throws LuaException {
		blockEntity.addressFilter = address;
		blockEntity.filterChanged();
		blockEntity.notifyUpdate();
	}

	@LuaFunction(mainThread = true)
	public final String getAddress() throws LuaException {
		return blockEntity.addressFilter;
	}

	@LuaFunction(mainThread = true)
	public final String getConfiguration() throws LuaException {
		if (blockEntity.target == null)
			return null;
		if (blockEntity.acceptsPackages)
			return "send_recieve";
		else
			return "send";
	}

	@LuaFunction(mainThread = true)
	public final boolean setConfiguration(String config) throws LuaException {
		if (blockEntity.target == null)
			return false;
		if (config.equals("send_recieve")) {
			blockEntity.acceptsPackages = true;
			blockEntity.filterChanged();
			blockEntity.notifyUpdate();
			return true;
		}
		if (config.equals("send")) {
			blockEntity.acceptsPackages = false;
			blockEntity.filterChanged();
			blockEntity.notifyUpdate();
			return true;
		}
		throw new LuaException("Unknown configuration: \"" + config
				+ "\" Possible configurations are: \"send_recieve\" and \"send\".");
	}

	@LuaFunction(mainThread = true)
	public Map<Integer, Map<String, ?>> list() {
		return ComputerUtil.list(blockEntity.inventory);
	}

	@LuaFunction(mainThread = true)
	public Map<String, ?> getItemDetail(int slot) throws LuaException {
		return ComputerUtil.getItemDetail(blockEntity.inventory, slot);
	}

	@NotNull
	@Override
	public String getType() {
		return "Create_Frogport";
	}

}
