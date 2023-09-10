package net.viniciusaportela.electriclights.block.electricblock;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.viniciusaportela.electriclights.ElectricLights;

import java.util.ArrayList;
import java.util.List;

public class ElectricBlockEntity extends BlockEntity {
    // TODO use map instead, because every time add/update, will have duplicates
    public List<BlockPos> connectedLightPositions = new ArrayList<>();

    public ElectricBlockEntity(BlockPos pos, BlockState state) {
        super(ElectricLights.ELECTRIC_BLOCK_ENTITY.get() ,pos, state);
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        super.saveAdditional(nbt);
        saveConnectedBlocks(nbt);
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        loadConnectedBlocks(nbt);
    }

    private void saveConnectedBlocks(CompoundTag nbt) {
        List<Integer> entityPositions = new ArrayList<>();

        for (BlockPos connectedLight: connectedLightPositions) {
            entityPositions.add(connectedLight.getX());
            entityPositions.add(connectedLight.getZ());
            entityPositions.add(connectedLight.getY());
        }

        nbt.putIntArray("connectedPositions", entityPositions);
    }

    private void loadConnectedBlocks(CompoundTag nbt) {
        int[] connectedPositions = nbt.getIntArray("connectedPositions");

        for (int i = 0; i < connectedPositions.length ; i += 3) {
            int x = connectedPositions[i];
            int z = connectedPositions[i + 1];
            int y = connectedPositions[i + 2];

            connectedLightPositions.add(new BlockPos(x, z, y));
        }
    }
}
