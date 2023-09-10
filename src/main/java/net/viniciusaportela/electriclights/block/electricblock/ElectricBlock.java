package net.viniciusaportela.electriclights.block.electricblock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.viniciusaportela.electriclights.ElectricLights;
import org.jetbrains.annotations.Nullable;

public class ElectricBlock extends Block implements EntityBlock {
    public ElectricBlock(Properties p_49795_) {
        super(p_49795_);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return ElectricLights.ELECTRIC_BLOCK_ENTITY.get().create(pos, state);
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return type == ElectricLights.ELECTRIC_BLOCK_ENTITY.get() ? ElectricBlockEntity::tick : null;
    }
}
