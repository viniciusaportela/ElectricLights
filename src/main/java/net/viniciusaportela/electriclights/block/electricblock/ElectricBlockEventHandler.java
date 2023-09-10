package net.viniciusaportela.electriclights.block.electricblock;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.viniciusaportela.electriclights.ElectricLights;
import net.viniciusaportela.electriclights.block.electriclight.ElectricLightBlock;
import net.viniciusaportela.electriclights.utils.BlockUtils;
import org.slf4j.Logger;

import java.util.List;

@Mod.EventBusSubscriber(modid = ElectricLights.MODID)
public class ElectricBlockEventHandler {
    private static final Logger LOGGER = LogUtils.getLogger();

    @SubscribeEvent
    protected void onPlaceBlock(BlockEvent.EntityPlaceEvent event) {
        Block placedBlock = event.getPlacedBlock().getBlock();

        if (placedBlock.equals(ElectricLights.ELECTRIC_BLOCK.get())) {
            LOGGER.info("Placed electric block");
            lookForNearElectricLights(event.getPos(), 5, event.getLevel());
            lightUpElectricLights(event.getPos(), event.getLevel());
        }
    }

    @SubscribeEvent
    protected void onBreakBlock(BlockEvent.BreakEvent event) {
        LevelAccessor level = event.getLevel();
        Block brokeBlock = level.getBlockState(event.getPos()).getBlock();

        if (brokeBlock.equals(ElectricLights.ELECTRIC_BLOCK.get())) {
            LOGGER.info("Broke electric block");
            ElectricBlockEntity electricBlockEntity = (ElectricBlockEntity) level.getBlockEntity(event.getPos());

            if (electricBlockEntity != null) {
                lightDownElectricLights(event.getPos(), event.getLevel());
            }
        }
    }

    public void lookForNearElectricLights(BlockPos position, int radius, LevelAccessor level) {
        List<BlockPos> electricLights = BlockUtils.lookForBlocksInRange(position, radius, ElectricLights.ELECTRIC_LIGHT_BLOCK.get(), level);
        LOGGER.info("Found " + electricLights.size() + " lights");

        ElectricBlockEntity placedBlockEntity = (ElectricBlockEntity) level.getBlockEntity(position);

        if (placedBlockEntity != null) {
            LOGGER.info(placedBlockEntity.getClass().getSimpleName());
            placedBlockEntity.connectedLightPositions.addAll(electricLights);
            LOGGER.info(placedBlockEntity.connectedLightPositions.toString());
        }
    }

    public void lightUpElectricLights(BlockPos position, LevelAccessor level) {
        ElectricBlockEntity electricBlock = (ElectricBlockEntity) level.getBlockEntity(position);

        if (electricBlock != null) {
            for(BlockPos lightPos: electricBlock.connectedLightPositions) {
                BlockState blockState = level.getBlockState(lightPos);
                level.setBlock(lightPos, blockState.setValue(ElectricLightBlock.ACTIVE, true), 3);
            }
        }
    }

    public void lightDownElectricLights(BlockPos position, LevelAccessor level) {
        ElectricBlockEntity electricBlock = (ElectricBlockEntity) level.getBlockEntity(position);

        // TODO when break block, off all lights around (but before verify if has another electric light near by)
        if (electricBlock != null) {
            for(BlockPos lightPos: electricBlock.connectedLightPositions) {
                BlockState blockState = level.getBlockState(lightPos);

                if (blockState.getBlock().equals(ElectricLights.ELECTRIC_LIGHT_BLOCK.get())) {
                    List<BlockPos> electricBlocksPos = BlockUtils.lookForBlocksInRange(lightPos, 5, ElectricLights.ELECTRIC_BLOCK.get(), level);

                    boolean hasFoundReplacer = false;
                    for (BlockPos electricBlockPos: electricBlocksPos) {
                        if (!electricBlockPos.equals(position)) {
                            ElectricBlockEntity newConnectedElectricBlock = (ElectricBlockEntity) level.getBlockEntity(electricBlockPos);

                            if (newConnectedElectricBlock != null) {
                                newConnectedElectricBlock.connectedLightPositions.add(lightPos);
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
