package net.viniciusaportela.electriclights.block.electricblock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.viniciusaportela.electriclights.ElectricLights;
import net.viniciusaportela.electriclights.block.electriclight.ElectricLightBlock;
import net.viniciusaportela.electriclights.config.ServerConfig;
import net.viniciusaportela.electriclights.utils.BlockUtils;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = ElectricLights.MODID)
public class ElectricBlockEventHandler {

    @SubscribeEvent
    protected void onPlaceBlock(BlockEvent.EntityPlaceEvent event) {
        Block placedBlock = event.getPlacedBlock().getBlock();

        if (placedBlock.equals(ElectricLights.ELECTRIC_BLOCK.get())) {
            lookForNearElectricLights(event.getPos(), 5, event.getLevel());
        }

        if (placedBlock.equals(ElectricLights.ELECTRIC_LIGHT_BLOCK.get())) {
            connectToElectricBlock(event.getPos(), event.getLevel());
        }
    }

    void connectToElectricBlock(BlockPos blockPos, LevelAccessor level) {
        List<BlockPos> electricBlocks = BlockUtils.lookForBlocksInRange(blockPos, 5, ElectricLights.ELECTRIC_BLOCK.get(), level);

        for (BlockPos electricBlockPos: electricBlocks) {
            ElectricBlockEntity electricBlockE = (ElectricBlockEntity) level.getBlockEntity(electricBlockPos);
            if (electricBlockE != null) {
                electricBlockE.connectedLightPositions.add(BlockUtils.getKeyFromBlockPos(blockPos));
                break;
            }
        }
    }

    @SubscribeEvent
    protected void onBreakBlock(BlockEvent.BreakEvent event) {
        LevelAccessor level = event.getLevel();
        Block brokeBlock = level.getBlockState(event.getPos()).getBlock();

        if (brokeBlock.equals(ElectricLights.ELECTRIC_BLOCK.get())) {
            lightDownElectricLights(event.getPos(), event.getLevel());
        }

        if (brokeBlock.equals(ElectricLights.ELECTRIC_LIGHT_BLOCK.get())) {
            List<BlockPos> electricBlocks = BlockUtils.lookForBlocksInRange(event.getPos(), ServerConfig.ELECTRIC_BLOCK_RANGE.get(), ElectricLights.ELECTRIC_BLOCK.get(), event.getLevel());
            String electricLightStrinfiedPos = BlockUtils.getKeyFromBlockPos(event.getPos());

            for (BlockPos electricBlockPos: electricBlocks) {
                ElectricBlockEntity entity = (ElectricBlockEntity) event.getLevel().getBlockEntity(electricBlockPos);

                if (entity.connectedLightPositions.contains(electricLightStrinfiedPos)) {
                    entity.connectedLightPositions.remove(electricLightStrinfiedPos);
                }
            }
        }
    }

    public void lookForNearElectricLights(BlockPos position, int radius, LevelAccessor level) {
        List<BlockPos> electricLights = BlockUtils.lookForBlocksInRange(position, radius, ElectricLights.ELECTRIC_LIGHT_BLOCK.get(), level);

        ElectricBlockEntity placedBlockEntity = (ElectricBlockEntity) level.getBlockEntity(position);

        ArrayList<String> electricLightsKeys = new ArrayList<>();
        for (BlockPos electricLightPos: electricLights) {
            electricLightsKeys.add(BlockUtils.getKeyFromBlockPos(electricLightPos));
        }

        if (placedBlockEntity != null) {
            placedBlockEntity.connectedLightPositions.addAll(electricLightsKeys);
        }
    }

    static public void lightUpElectricLights(BlockPos position, LevelAccessor level) {
        BlockEntity blockEntity = level.getBlockEntity(position);

        if (blockEntity instanceof ElectricBlockEntity electricBlock) {
            for(String lightPosStringified: electricBlock.connectedLightPositions) {
                BlockPos lightPos = BlockUtils.getBlockPosFromStringified(lightPosStringified);

                BlockState blockState = level.getBlockState(lightPos);

                if (blockState.getBlock().equals(ElectricLights.ELECTRIC_LIGHT_BLOCK.get())) {
                    level.setBlock(lightPos, blockState.setValue(ElectricLightBlock.ACTIVE, true), 3);
                }
            }
        }
    }

    static public void lightDownElectricLights(BlockPos electricBlockPosition, LevelAccessor level) {
        ElectricBlockEntity electricBlock = (ElectricBlockEntity) level.getBlockEntity(electricBlockPosition);

        if (electricBlock != null) {
            for(String lightPosStringified: electricBlock.connectedLightPositions) {
                ElectricLights.LOGGER.info("light down electric lights");
                ElectricLights.LOGGER.info(lightPosStringified);
                BlockPos lightPos = BlockUtils.getBlockPosFromStringified(lightPosStringified);
                BlockState blockState = level.getBlockState(lightPos);

                if (blockState.getBlock().equals(ElectricLights.ELECTRIC_LIGHT_BLOCK.get())) {
                    List<BlockPos> electricBlocksPos = BlockUtils.lookForBlocksInRange(lightPos, 5, ElectricLights.ELECTRIC_BLOCK.get(), level);

                    boolean hasFoundReplacer = false;
                    for (BlockPos electricBlockPos: electricBlocksPos) {
                        if (!electricBlockPos.equals(electricBlockPosition)) {
                            ElectricBlockEntity newConnectedElectricBlock = (ElectricBlockEntity) level.getBlockEntity(electricBlockPos);

                            if (newConnectedElectricBlock != null) {
                                newConnectedElectricBlock.connectedLightPositions.add(lightPosStringified);
                                hasFoundReplacer = true;
                                break;
                            }
                        }
                    }

                    if (!hasFoundReplacer) {
                        level.setBlock(lightPos, blockState.setValue(ElectricLightBlock.ACTIVE, false), 3);
                    }
                }
            }
        }
    }
}
