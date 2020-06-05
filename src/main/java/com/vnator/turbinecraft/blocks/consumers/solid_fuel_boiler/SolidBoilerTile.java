package com.vnator.turbinecraft.blocks.consumers.solid_fuel_boiler;

import com.vnator.turbinecraft.blocks.consumers.furnace.FrictionFurnaceContainer;
import com.vnator.turbinecraft.fluids.FluidRegistration;
import com.vnator.turbinecraft.util.FluidStackHandler;
import com.vnator.turbinecraft.util.MachineItemHandler;
import com.vnator.turbinecraft.blocks.MachineTileEntity;
import com.vnator.turbinecraft.capabilities.rotational_power.IRotationalAcceptor;
import com.vnator.turbinecraft.setup.Registration;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SolidBoilerTile extends MachineTileEntity implements INamedContainerProvider {

    private static final int STEAM_PER_CYCLE = 5; //mb steam produced per cycle
    private static final int WATER_PER_CYCLE = 2; //mb water consumed per cycle
    private static final int BURN_RATE = 2; //burn time quantity consumed per cycle
    private static final int CYCLE_RATE = 1; // ticks/cycle

    LazyOptional<FluidStackHandler> tanks;
    private int burnTime, maxBurnTime = 1, cycleCount;

    public SolidBoilerTile() {
        super(Registration.SOLID_BOILER_TILE);
        inventoryBase = NonNullList.withSize(1, ItemStack.EMPTY);
        initInventory(new MachineItemHandler(this){
            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                return ForgeHooks.getBurnTime(stack) > 0;
            }

            @Nonnull
            @Override
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
                if(ForgeHooks.getBurnTime(stack) <= 0)
                    return stack;
                return super.insertItem(slot, stack, simulate);
            }

            @Nonnull
            @Override
            public ItemStack extractItem(int slot, int amount, boolean simulate) {
                return ItemStack.EMPTY;
            }
        }, new MachineItemHandler(this){
            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                return ForgeHooks.getBurnTime(stack) > 0;
            }

            @Nonnull
            @Override
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
                if(ForgeHooks.getBurnTime(stack) <= 0)
                    return stack;
                return super.insertItem(slot, stack, simulate);
            }
        });
        initTanks();
    }

    @Override
    public void tick(){
        super.tick();
        cycleCount++;
        if(cycleCount < CYCLE_RATE)
            return;

        cycleCount = 0;
        if(burnTime <= 0){
            ItemStack burnable = inventoryBase.get(0);
            if(ForgeHooks.getBurnTime(burnable) > 0){
                burnTime = ForgeHooks.getBurnTime(burnable);
                maxBurnTime = burnTime;
                burnable.shrink(1);
            }
        }

        if(burnTime > 0){
            tanks.ifPresent(tank -> {
                if(tank.getFluidInTank(0).getAmount() >= WATER_PER_CYCLE &&
                        tank.getFluidInTank(1).getAmount() <= tank.getTankCapacity(1) - STEAM_PER_CYCLE){
                    burnTime -= BURN_RATE;
                    tank.getFluidInTank(0).setAmount(tank.getFluidInTank(0).getAmount() - WATER_PER_CYCLE);
                    tank.fill(new FluidStack(FluidRegistration.steam_fluid.get(), STEAM_PER_CYCLE), IFluidHandler.FluidAction.EXECUTE);
                }
            });
        }
    }

    private void initTanks(){
        tanks = LazyOptional.of(() -> new FluidStackHandler(2, 4000){
            @Override
            public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
                if(tank == 0) //Water Tank
                    return super.isFluidValid(tank, stack) && stack.getFluid().isEquivalentTo(Fluids.WATER);
                else if(tank == 1)
                    return super.isFluidValid(tank, stack) && stack.getFluid().isEquivalentTo(FluidRegistration.steam_fluid.get());
                else //Steam Tank
                    return false;
            }

            @Nonnull
            @Override
            public FluidStack drain(FluidStack resource, FluidAction action) {
                return super.drain(resource, action);
            }

            @Nonnull
            @Override
            public FluidStack drain(int maxDrain, FluidAction action) {
                return super.drain(maxDrain, action);
            }

            @Nonnull
            @Override
            public FluidStack drain(int tank, FluidStack resource, FluidAction action) {
                return super.drain(tank, resource, action);
            }

            @Nonnull
            @Override
            public FluidStack drain(int tank, int maxDrain, FluidAction action) {
                return super.drain(tank, maxDrain, action);
            }
        });
    }

    @Override
    protected IRotationalAcceptor createRotationAcceptor() {
        return null;
    }

    @Override
    public void read(CompoundNBT compound) {
        burnTime = compound.getInt("burnTime");
        maxBurnTime = compound.getInt("maxBurnTime");
        cycleCount = compound.getInt("cycleCount");
        tanks.ifPresent(tank -> tank.deserializeNBT(compound.getCompound("tank")));
        super.read(compound);
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        compound.putInt("burnTime", burnTime);
        compound.putInt("maxBurnTime", maxBurnTime);
        compound.putInt("cycleCount", cycleCount);
        tanks.ifPresent(tank -> compound.put("tank", tank.serializeNBT()));
        return super.write(compound);
    }

    public float getBurnRatio(){
        return 1.0f * burnTime / maxBurnTime;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if(cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            updateClient();
            return tanks.cast();
        }
        else if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return autoInventoryHandler.cast();
        return super.getCapability(cap, side);
    }

    @Override
    public ITextComponent getDisplayName() {
        return new StringTextComponent(getType().getRegistryName().getPath());
    }

    @Nullable
    @Override
    public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        return new SolidBoilerContainer(i, world, pos, playerInventory, playerEntity);
    }
}
