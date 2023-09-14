package net.viniciusaportela.electriclights.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
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
        ElectricLights.LOGGER.info("getBlockPosFromStringified");
        ElectricLights.LOGGER.info(str);
        String[] parts = str.split(",");
        ElectricLights.LOGGER.info(Arrays.toString(parts));

        return new BlockPos(
            Integer.parseInt(parts[0]),
            Integer.parseInt(parts[1]),
            Integer.parseInt(parts[2])
        );
    }
}
