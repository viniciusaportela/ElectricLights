package net.viniciusaportela.electriclights;

import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.energy.IEnergyStorage;

public class CustomEnergyStorage implements IEnergyStorage, INBTSerializable<Tag> {
    protected int energy;
    protected int energyUsage = 0;

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate)
    {
        if (!canReceive())
            return 0;

        if (maxReceive < energyUsage) {
            return 0;
        }

        if (!simulate)
            energy = energyUsage;
        return maxReceive;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate)
    {
        if (!canExtract())
            return 0;

        if (maxExtract >= energy) {
            int oldEnergy = energy;
            energy = 0;
            return oldEnergy;
        }

        if (!simulate)
            energy -= maxExtract;
        return maxExtract;
    }

    @Override
    public int getEnergyStored() {
        return energy;
    }

    @Override
    public int getMaxEnergyStored() {
        return energyUsage;
    }

    @Override
    public boolean canExtract() {
        return true;
    }

    @Override
    public boolean canReceive() {
        return true;
    }

    public void setEnergyUsage(int newEnergyUsage) {
        energyUsage = newEnergyUsage;
    }

    @Override
    public Tag serializeNBT()
    {
        return IntTag.valueOf(this.getEnergyStored());
    }

    @Override
    public void deserializeNBT(Tag nbt)
    {
        if (!(nbt instanceof IntTag intNbt))
            throw new IllegalArgumentException("Can not deserialize to an instance that isn't the default implementation");
        this.energy = intNbt.getAsInt();
    }
}
