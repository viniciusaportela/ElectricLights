package net.viniciusaportela.electriclights.block.electricblock;

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
import net.minecraftforge.energy.IEnergyStorage;
import net.viniciusaportela.electriclights.CustomEnergyStorage;
import net.viniciusaportela.electriclights.ElectricLights;
import net.viniciusaportela.electriclights.ElectricLightsEventHandler;
import net.viniciusaportela.electriclights.config.ServerConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ElectricBlockEntity extends BlockEntity {

    private LazyOptional<IEnergyStorage> lazyEnergyHandler = LazyOptional.empty();

    private final CustomEnergyStorage energyStorage = new CustomEnergyStorage() {
        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            int value = super.receiveEnergy(maxReceive, simulate);
            if (!simulate) {
                setChanged();
            }
            return value;
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            int value = super.extractEnergy(maxExtract, simulate);
            if (!simulate) {
                setChanged();
            }
            return value;
        }
    };

    public Set<String> connectedLightPositions = new HashSet<>();

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
        if (!connectedLightPositions.isEmpty()) {
            nbt.putString("connectedPositions", String.join(";", connectedLightPositions));
        }
    }

    private void loadConnectedBlocks(CompoundTag nbt) {
        String stringifedPositionsRaw = nbt.getString("connectedPositions");

        if(!stringifedPositionsRaw.isEmpty()) {
            String[] stringifedPositions = stringifedPositionsRaw.split(";");

            connectedLightPositions.addAll(Arrays.asList(stringifedPositions));
        }
    }

    public static <T extends BlockEntity> void tick(Level level, BlockPos blockPos, BlockState blockState, T blockEntity) {
        int energyUsagePerTick = Math.min(ServerConfig.ELECTRIC_LIGHT_COST.get() / 20, 1);

        ElectricBlockEntity electricBlockEntity = (ElectricBlockEntity) blockEntity;

        for (final var direction : Direction.values()) {
            final BlockEntity sideBlockEntity = level.getBlockEntity(blockPos.relative(direction));

            if (sideBlockEntity != null) {
                sideBlockEntity.getCapability(ForgeCapabilities.ENERGY, direction.getOpposite()).ifPresent(storage -> {
                    int energyAmount = electricBlockEntity.connectedLightPositions.size() * energyUsagePerTick;
                    electricBlockEntity.energyStorage.setEnergyUsage(energyAmount);

                    int extracted = storage.extractEnergy(energyAmount, false);
                    electricBlockEntity.energyStorage.receiveEnergy(extracted, false);
                });
            }
        }

        if (electricBlockEntity.energyStorage.getEnergyStored() >= electricBlockEntity.connectedLightPositions.size() * energyUsagePerTick) {
            ElectricLightsEventHandler.lightUpElectricLights(electricBlockEntity.getBlockPos(), electricBlockEntity.getLevel());
            electricBlockEntity.energyStorage.extractEnergy(electricBlockEntity.connectedLightPositions.size() * energyUsagePerTick, false);
        } else {
            ElectricLightsEventHandler.lightDownElectricLights(electricBlockEntity.getBlockPos(), electricBlockEntity.getLevel());
        }
    }
}
