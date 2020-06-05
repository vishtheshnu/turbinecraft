package com.vnator.turbinecraft.blocks.consumers.rotopump;

import com.vnator.turbinecraft.blocks.MachineTileEntity;
import com.vnator.turbinecraft.capabilities.rotational_power.IRotationalAcceptor;
import com.vnator.turbinecraft.capabilities.rotational_power.RotationProvider;
import com.vnator.turbinecraft.capabilities.rotational_power.RotationalAcceptor;
import com.vnator.turbinecraft.setup.Registration;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.atomic.AtomicInteger;

public class RotopumpTile extends MachineTileEntity {

    private static final int MB_PER_THROUGHPUT = 100; //mb moved per throughput unit

    private int waitPeriod, ticks;

    public RotopumpTile() {
        super(Registration.ROTOPUMP_TILE);
    }

    @Override
    public void tick() {
        //Check rotational power, then transfer based on that
        AtomicInteger rate = new AtomicInteger(8);
        AtomicInteger throughput = new AtomicInteger(1);
        rotation.ifPresent(rot -> {
            rate.set(8 - getTier(rot.getSpeed()));
            throughput.set(1 + getTier(rot.getForce()));
            displayRotation.insertEnergy(rot.getSpeed(), rot.getForce());
            rot.setSpeed(0);
            rot.setForce(0);
        });
        waitPeriod = rate.get();
        ticks++;
        if(ticks >= waitPeriod){
            ticks = 0;
            Direction facing = getBlockState().get(BlockStateProperties.FACING);
            TileEntity entFrom = world.getTileEntity(pos.offset(facing.getOpposite()));
            TileEntity entTo = world.getTileEntity(pos.offset(facing));
            if(entFrom != null && entTo != null) {
                //Transfer Items
                entFrom.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(fromInv -> {
                    entTo.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(toInv -> {

                        for(int i = 0; i < fromInv.getSlots(); i++){
                            ItemStack extractStack = fromInv.extractItem(i, throughput.get(), true).copy();
                            if(extractStack != null && !extractStack.isEmpty()){
                                //Insert as much of this stack as possible
                                ItemStack remainderStack = extractStack.copy();
                                for(int j = 0; j < toInv.getSlots(); j++){
                                    remainderStack = toInv.insertItem(j, extractStack, false);
                                    if(remainderStack.isEmpty())
                                        break;
                                }
                                //Could not insert current ItemStack anywhere, try the next one
                                if(remainderStack.getCount() == extractStack.getCount())
                                    continue;
                                //Inserted item as much as possible. Finalize extraction and end loop
                                else{
                                    fromInv.extractItem(i, extractStack.getCount() - remainderStack.getCount(), false);
                                    break;
                                }
                            }

                        }
                    });
                });

                //Transfer Fluids
                entFrom.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).ifPresent(fromTank -> {
                    entTo.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).ifPresent(toTank -> {

                        FluidStack extractStack = fromTank.drain(throughput.get()*MB_PER_THROUGHPUT, IFluidHandler.FluidAction.SIMULATE).copy();
                        if(extractStack.getAmount() == 0)
                            return;
                        int startAmt = extractStack.getAmount();

                        int totalFilled = 0;
                        for(int j = 0; j < toTank.getTanks(); j++){
                            int filledStack = toTank.fill(extractStack, IFluidHandler.FluidAction.EXECUTE);
                            totalFilled += filledStack;
                            extractStack.setAmount(extractStack.getAmount() - filledStack);
                            if(totalFilled == startAmt)
                                break;
                        }

                        //Finalize drain and complete fluid transfer
                        if(totalFilled != 0){
                            fromTank.drain(totalFilled, IFluidHandler.FluidAction.EXECUTE);
                        }

                    });
                });
            }
        }
    }

    public int getTier(long val){
        int tier = 0;
        while(val > 0){
            val >>= 1;
            tier++;
        }
        return tier;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if(cap == RotationProvider.ROTATION_CAPABILITY)
            return rotation.cast();
        return super.getCapability(cap, side);
    }

    @Override
    protected IRotationalAcceptor createRotationAcceptor() {
        return new RotationalAcceptor();
    }
}
