package net.viniciusaportela.electriclights.block.electriclight;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TubularLightBlock extends ElectricLightBlock {
    public TubularLightBlock(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState p_60555_, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_) {
        if (p_60555_.getValue(FACING) == Direction.NORTH || p_60555_.getValue(FACING) == Direction.SOUTH) {
            return Block.box(5, 13, 0, 11, 16, 16);
        } else {
            return Block.box(0, 13, 5, 16, 16, 11);
        }
    }
}
