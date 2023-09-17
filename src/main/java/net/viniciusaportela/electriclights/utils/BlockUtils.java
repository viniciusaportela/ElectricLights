package net.viniciusaportela.electriclights.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.viniciusaportela.electriclights.ElectricLights;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BlockUtils {
    public static List<BlockPos> lookForBlocksInRange(BlockPos centerPos, int radius, Block blockToLook, LevelAccessor level) {
        List<BlockPos> foundBlocks = new ArrayList<>();

        Vec3 centerPosVec = new Vec3(centerPos.getX(), centerPos.getZ(), centerPos.getY());

        Vec3 positiveCorner = centerPosVec.add(radius, radius, radius);
        Vec3 negativeCorner = centerPosVec.add(-radius, -radius, -radius);

        for (double x = negativeCorner.x; x <= positiveCorner.x; x++) {
            for (double z = negativeCorner.z; z <= positiveCorner.z; z++) {
                for (double y = negativeCorner.y; y <= positiveCorner.y; y++) {
                    BlockPos pos = new BlockPos(x, z, y);
                    Block block = level.getBlockState(pos).getBlock();

                    if (block.equals(blockToLook)) {
                        foundBlocks.add(pos);
                    }
                }
            }
        }

        return foundBlocks;
    }

    public static List<BlockPos> lookForBlocksInRange(BlockPos centerPos, int radius, TagKey<Block> blockToLook, LevelAccessor level) {
        ElectricLights.LOGGER.info("lookForBlocksInRange");
        List<BlockPos> foundBlocks = new ArrayList<>();

        Vec3 centerPosVec = new Vec3(centerPos.getX(), centerPos.getZ(), centerPos.getY());

        Vec3 positiveCorner = centerPosVec.add(radius, radius, radius);
        Vec3 negativeCorner = centerPosVec.add(-radius, -radius, -radius);

        for (double x = negativeCorner.x; x <= positiveCorner.x; x++) {
            for (double z = negativeCorner.z; z <= positiveCorner.z; z++) {
                for (double y = negativeCorner.y; y <= positiveCorner.y; y++) {
                    BlockPos pos = new BlockPos(x, z, y);
                    BlockState block = level.getBlockState(pos);
                    ElectricLights.LOGGER.info(block.getBlock().getName().getString());

                    if (block.is(blockToLook)) {
                        ElectricLights.LOGGER.info("FOUND!");
                        foundBlocks.add(pos);
                    }
                }
            }
        }

        return foundBlocks;
    }

    public static String getKeyFromBlockPos(BlockPos blockPos) {
        String finalString = "";

        finalString += blockPos.getX();
        finalString += ",";
        finalString += blockPos.getY();
        finalString += ",";
        finalString += blockPos.getZ();

        return finalString;
    }

    public static BlockPos getBlockPosFromStringified(String str) {
        String[] parts = str.split(",");

        // TODO broke sometimes
        // TODO When there is no connected light, it breaks, because inserts an empty array

        return new BlockPos(
            Integer.parseInt(parts[0]),
            Integer.parseInt(parts[1]),
            Integer.parseInt(parts[2])
        );
    }
}
