package com.simibubi.create.content.kinetics;

import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;

import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.outliner.Outliner;
import net.createmod.catnip.render.SuperByteBufferCache;
import net.createmod.catnip.theme.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

public class KineticDebugger {
	public static boolean rainbowDebug = false;

	public static void tick() {
		if (!isActive()) {
			if (KineticBlockEntityRenderer.rainbowMode) {
				KineticBlockEntityRenderer.rainbowMode = false;
				SuperByteBufferCache.getInstance().invalidate();
			}
			return;
		}

		KineticBlockEntity be = getSelectedBE();
		if (be == null)
			return;

		Level world = Minecraft.getInstance().level;
		BlockPos toOutline = be.hasSource() ? be.source : be.getBlockPos();
		BlockState state = be.getBlockState();
		VoxelShape shape = world.getBlockState(toOutline)
			.getBlockSupportShape(world, toOutline);

		if (be.getTheoreticalSpeed() != 0 && !shape.isEmpty())
			Outliner.getInstance().chaseAABB("kineticSource", shape.bounds()
					.move(toOutline))
				.lineWidth(1 / 16f)
				.colored(be.hasSource() ? Color.generateFromLong(be.network).getRGB() : 0xffcc00);

		if (state.getBlock() instanceof IRotate) {
			Axis axis = ((IRotate) state.getBlock()).getRotationAxis(state);
			Vec3 vec = Vec3.atLowerCornerOf(Direction.get(AxisDirection.POSITIVE, axis)
				.getNormal());
			Vec3 center = VecHelper.getCenterOf(be.getBlockPos());
			Outliner.getInstance().showLine("rotationAxis", center.add(vec), center.subtract(vec))
				.lineWidth(1 / 16f);
		}

	}

	public static boolean isActive() {
		return isF3DebugModeActive() && KineticDebugger.rainbowDebug;
	}

	public static boolean isF3DebugModeActive() {
		return Minecraft.getInstance().options.renderDebug;
	}

	public static KineticBlockEntity getSelectedBE() {
		HitResult obj = Minecraft.getInstance().hitResult;
		ClientLevel world = Minecraft.getInstance().level;
		if (obj == null)
			return null;
		if (world == null)
			return null;
		if (!(obj instanceof BlockHitResult ray))
			return null;

		BlockEntity be = world.getBlockEntity(ray.getBlockPos());
		if (!(be instanceof KineticBlockEntity))
			return null;

		return (KineticBlockEntity) be;
	}

}
