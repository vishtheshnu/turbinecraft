package com.vnator.turbinecraft.blocks.generators.t1_steam_generator;

import com.vnator.turbinecraft.blocks.GeneratorTileEntity;
import com.vnator.turbinecraft.capabilities.rotational_power.IRotationalAcceptor;
import com.vnator.turbinecraft.capabilities.rotational_power.RotationProvider;
import com.vnator.turbinecraft.capabilities.rotational_power.RotationalAcceptor;
import com.vnator.turbinecraft.setup.Registration;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.atomic.AtomicBoolean;

public class SteamGeneratorTile extends GeneratorTileEntity implements INamedContainerProvider {

    private static final long SPEED = 2, FORCE = 2;
    private static int DRAIN_AMOUNT = 2;

    LazyOptional<IFluidHandler> tank = LazyOptional.of(this::createTank);
    LazyOptional<IItemHandler> inventory = LazyOptional.of(this::createInventory);

    private int burnTime = 0;
    private int burnTotal = 0;

    public SteamGeneratorTile() {
        super(Registration.BASIC_STEAM_GENERATOR_TILE);
    }

    @Override
    public void tick() {
        super.tick();

        AtomicBoolean canDrain = new AtomicBoolean(false);
        tank.ifPresent(tank -> {
            FluidStack drained = tank.drain(DRAIN_AMOUNT, IFluidHandler.FluidAction.SIMULATE);
            canDrain.set(!drained.isEmpty() && drained.getAmount() == DRAIN_AMOUNT);
        });

        if(burnTime == 0 && canDrain.get()) {
            inventory.ifPresent(inv -> {
                ItemStack burnable = inv.extractItem(0, 1, false);
                if (burnable != null && !burnable.isEmpty()){
                    burnTime = ForgeHooks.getBurnTime(burnable);
                    burnTotal = burnTime;
                    markDirty();
                }
            });
        }

        if(canDrain.get() && burnTime > 0){
            tank.ifPresent(tank -> {
                tank.drain(DRAIN_AMOUNT, IFluidHandler.FluidAction.EXECUTE);
                burnTime--;
                transferRotation(SPEED, FORCE);
                markDirty();
                setBlockState(BlockStateProperties.POWERED, true);
            });
        }else{
            transferRotation(0, 0);
            setBlockState(BlockStateProperties.POWERED, false);
            //world.setBlockState(getPos(), getBlockState().with(BlockStateProperties.POWERED, false));
        }
    }

    private IFluidHandler createTank(){
        return new FluidTank(4000){
            @Override
            protected void onContentsChanged() {
                markDirty();
                super.onContentsChanged();
            }

            @Override
            public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
                if(stack.getFluid() == Fluids.WATER)
                    return super.isFluidValid(tank, stack);
                return false;
            }
        };
    }

    private IItemHandler createInventory(){
        return new ItemStackHandler(1){
            @Override
            protected void onContentsChanged(int slot) {
                markDirty();
            }

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
        };
    }

    protected IRotationalAcceptor createRotationAcceptor(){
        return new RotationalAcceptor();
    }

    @Override
    public void read(CompoundNBT compound) {
        CompoundNBT invTag = compound.getCompound("inventory");
        inventory.ifPresent(inv -> ((INBTSerializable<CompoundNBT>) inv).deserializeNBT(invTag));
        //CompoundNBT tankTag = compound.getCompound("tank");
        tank.ifPresent(inv -> ((FluidTank) inv).readFromNBT(compound));
        burnTime = compound.getInt("burnTimeLeft");
        burnTotal = compound.getInt("burnTimeTotal");
        super.read(compound);
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        inventory.ifPresent(inv -> {
            CompoundNBT nbt = ((INBTSerializable<CompoundNBT>) inv).serializeNBT();
            compound.put("inventory", nbt);
        });
        tank.ifPresent(inv -> {
            ((FluidTank) inv).writeToNBT(compound);
        });

        compound.putInt("burnTimeLeft", burnTime);
        compound.putInt("burnTimeTotal", burnTotal);
        return super.write(compound);
    }

    protected CompoundNBT getMinimalUpdateNbt(){
        CompoundNBT compound = new CompoundNBT();
        tank.ifPresent(inv -> {
            ((FluidTank) inv).writeToNBT(compound);
        });
        rotation.ifPresent(rot -> compound.put("rotation", rot.serializeNBT()));
        compound.putInt("burnTimeLeft", burnTime);
        compound.putInt("burnTimeTotal", burnTotal);
        return compound;
    }

    protected void setMinimalUpdateNbt(CompoundNBT nbt){
        tank.ifPresent(inv -> ((FluidTank) inv).readFromNBT(nbt));
        rotation.ifPresent(rot -> rot.deserializeNBT(nbt.getCompound("rotation")));
        burnTime = nbt.getInt("burnTimeLeft");
        burnTotal = nbt.getInt("burnTimeTotal");
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
            return inventory.cast();
        }else if(cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY){
            updateClient();
            return tank.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public ITextComponent getDisplayName() {
        return new StringTextComponent(getType().getRegistryName().getPath());
    }

    @Nullable
    @Override
    public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        return new SteamGeneratorContainer(i, world, pos, playerInventory, playerEntity);
    }

    public float getBurnRatio(){
        return 1.0f*burnTime/burnTotal;
    }
}
