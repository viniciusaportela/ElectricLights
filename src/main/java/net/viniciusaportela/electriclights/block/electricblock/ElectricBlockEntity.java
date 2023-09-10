package net.viniciusaportela.electriclights.block.electricblock;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import net.viniciusaportela.electriclights.ElectricLights;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class ElectricBlockEntity extends BlockEntity {
    private static final Logger LOGGER = LogUtils.getLogger();

    private LazyOptional<IEnergyStorage> lazyEnergyHandler = LazyOptional.empty();

    public static final int MAX_ENERGY = 500;
    public static final int ENERGY_USAGE_PER_LIGHT = 5;

    private final EnergyStorage energyStorage = new EnergyStorage(MAX_ENERGY, MAX_ENERGY) {
        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            LOGGER.info("receive energy");
            int value = super.receiveEnergy(maxReceive, simulate);
            if (!simulate) {
                setChanged();
//                level.sendBlockUpdated(worldPosition, level.getBlockState(worldPosition), level.getBlockState(worldPosition), 2);
            }
            return value;
        }

        // USE EXTRACT ENERGY (?)
        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            LOGGER.info("extract energy");
            int value = super.extractEnergy(maxExtract, simulate);
            if (!simulate) {
                setChanged();
//                level.sendBlockUpdated(worldPosition, level.getBlockState(worldPosition), level.getBlockState(worldPosition), 2);
            }
            return value;
        }
    };

    // TODO use map instead, because every time add/update, will have duplicates
    public List<BlockPos> connectedLightPositions = new ArrayList<>();

    @Override
    public void onLoad() {
        super.onLoad();
        lazyEnergyHandler = LazyOptional.of(() -> energyStorage);
    }

    public ElectricBlockEntity(BlockPos pos, BlockState state) {
        super(ElectricLights.ELECTRIC_BLOCK_ENTITY.get() ,pos, state);
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        super.saveAdditional(nbt);
        saveConnectedBlocks(nbt);
        nbt.putInt("energyStorage", energyStorage.getEnergyStored());
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ENERGY) {
            return LazyOptional.of(() -> energyStorage).cast();
        }

        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyEnergyHandler.invalidate();
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        loadConnectedBlocks(nbt);

        if (nbt.get("energyStorage") instanceof IntTag intTag)
            energyStorage.deserializeNBT(intTag);
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

    public static <T extends BlockEntity> void tick(Level level, BlockPos blockPos, BlockState blockState, T blockEntity) {
        ElectricBlockEntity electricBlockEntity = (ElectricBlockEntity) blockEntity;

        for (final var direction : Direction.values()) {
            final BlockEntity sideBlockEntity = level.getBlockEntity(blockPos.relative(direction));

            if (sideBlockEntity != null) {
                sideBlockEntity.getCapability(ForgeCapabilities.ENERGY, direction.getOpposite()).ifPresent(storage -> {
                    int energyAmount = electricBlockEntity.connectedLightPositions.size() * ElectricBlockEntity.ENERGY_USAGE_PER_LIGHT;

                    int extracted = storage.extractEnergy(energyAmount, false);
                    LOGGER.info("Extracting " + extracted);
                    electricBlockEntity.energyStorage.receiveEnergy(extracted, false);
                });
            }
        }

        if (electricBlockEntity.energyStorage.getEnergyStored() > electricBlockEntity.connectedLightPositions.size() * ElectricBlockEntity.ENERGY_USAGE_PER_LIGHT) {
            ElectricBlockEventHandler.lightUpElectricLights(electricBlockEntity.getBlockPos(), electricBlockEntity.getLevel());
            electricBlockEntity.energyStorage.extractEnergy(electricBlockEntity.connectedLightPositions.size() * ElectricBlockEntity.ENERGY_USAGE_PER_LIGHT, false);
        } else {
            ElectricBlockEventHandler.lightDownElectricLights(electricBlockEntity.getBlockPos(), electricBlockEntity.getLevel());
        }
    }
}
