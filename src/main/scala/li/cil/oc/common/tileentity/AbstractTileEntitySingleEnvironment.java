package li.cil.oc.common.tileentity;

import li.cil.oc.api.network.Environment;
import li.cil.oc.common.capabilities.CapabilityEnvironment;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public abstract class AbstractTileEntitySingleEnvironment extends AbstractTileEntityEnvironmentHost {
    // ----------------------------------------------------------------------- //
    // Computed data.

    // NBT tag names.
    private static final String TAG_ENVIRONMENT = "environment";

    // ----------------------------------------------------------------------- //
    // TileEntity

    @Override
    public boolean hasCapability(final Capability<?> capability, @Nullable final EnumFacing facing) {
        return capability == CapabilityEnvironment.ENVIRONMENT_CAPABILITY ||
                super.hasCapability(capability, facing);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public <T> T getCapability(final Capability<T> capability, @Nullable final EnumFacing facing) {
        if (capability == CapabilityEnvironment.ENVIRONMENT_CAPABILITY)
            return (T) getEnvironment();
        return super.getCapability(capability, facing);
    }

    // ----------------------------------------------------------------------- //
    // AbstractTileEntity

    @Override
    protected void readFromNBTCommon(final NBTTagCompound nbt) {
        super.readFromNBTCommon(nbt);
        nbt.setTag(TAG_ENVIRONMENT, getEnvironment().serializeNBT());
    }

    @Override
    protected void writeToNBTCommon(final NBTTagCompound nbt) {
        super.writeToNBTCommon(nbt);
        getEnvironment().deserializeNBT((NBTTagCompound) nbt.getTag(TAG_ENVIRONMENT));
    }

    // ----------------------------------------------------------------------- //

    protected abstract Environment getEnvironment();
}
