package net.viniciusaportela.electriclights.block.electriclight;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.viniciusaportela.electriclights.ElectricLights;
import org.jetbrains.annotations.Nullable;

public class ElectricLightBlock extends Block {
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");

    public ElectricLightBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(ACTIVE, false));
    }

//    @Override
//    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
//        if (!pLevel.isClientSide() && pHand == InteractionHand.MAIN_HAND) {
//            boolean currentState = pState.getValue(ACTIVE);
//            pLevel.setBlock(pPos, pState.setValue(ACTIVE, !currentState), 3);
//        }
//
//        return InteractionResult.SUCCESS;
//    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(ACTIVE);
    }
}
