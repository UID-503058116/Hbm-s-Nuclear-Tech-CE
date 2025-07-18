package com.hbm.tileentity.machine;

import com.hbm.api.energymk2.IEnergyReceiverMK2;
import com.hbm.blocks.BlockDummyable;
import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.machine.MachineSILEX;
import com.hbm.capability.NTMEnergyCapabilityWrapper;
import com.hbm.inventory.container.ContainerFEL;
import com.hbm.inventory.gui.GUIFEL;
import com.hbm.items.machine.ItemFELCrystal;
import com.hbm.items.machine.ItemFELCrystal.EnumWavelengths;
import com.hbm.lib.ForgeDirection;
import com.hbm.lib.Library;
import com.hbm.main.MainRegistry;
import com.hbm.packet.toclient.LoopedSoundPacket;
import com.hbm.packet.PacketDispatcher;
import com.hbm.tileentity.IGUIProvider;
import com.hbm.tileentity.TileEntityMachineBase;
import com.hbm.util.BufferUtil;
import com.hbm.util.ContaminationUtil;
import com.hbm.util.ContaminationUtil.ContaminationType;
import com.hbm.util.ContaminationUtil.HazardType;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class TileEntityFEL extends TileEntityMachineBase implements ITickable, IEnergyReceiverMK2, IGUIProvider {
	
	public long power;
	public static final long maxPower = 2000000000;
	public static final int powerReq = 1000;
	public EnumWavelengths mode = EnumWavelengths.NULL;
	public boolean isOn;
	public boolean missingValidSilex = true	;
	public int distance;
	public List<EntityLivingBase> entities = new ArrayList();
	
	
	public TileEntityFEL() {
		super(2);
	}

	@Override
	public String getName() {
		return "container.machineFEL";
	}

	@Override
	public void update() {
		
		if(!world.isRemote) {
			
			ForgeDirection dir = ForgeDirection.getOrientation(this.getBlockMetadata() - BlockDummyable.offset);
			this.trySubscribe(world, pos.getX() +dir.offsetX * -5, pos.getY() + 1, pos.getZ() + dir.offsetZ  * -5, dir);
			this.power = Library.chargeTEFromItems(inventory, 0, power, maxPower);
			
			if(this.isOn && !(inventory.getStackInSlot(1).getCount() == 0)) {
				
				if(inventory.getStackInSlot(1).getItem() instanceof ItemFELCrystal) {
					
					ItemFELCrystal crystal = (ItemFELCrystal) inventory.getStackInSlot(1).getItem();
					this.mode = crystal.wavelength;
					
				} else { this.mode = EnumWavelengths.NULL; }
				
			} else { this.mode = EnumWavelengths.NULL; }
			
			int range = 24;
			boolean silexSpacing = false;
			double xCoord = pos.getX();
			double yCoord = pos.getY();
			double zCoord = pos.getZ();
			if(this.isOn &&  this.mode != EnumWavelengths.NULL) {
				if(this.power < powerReq * Math.pow(4, mode.ordinal())){
					this.power = 0;
				}else{
					int distance = this.distance-1;
					double blx = Math.min(xCoord, xCoord + (double)dir.offsetX * distance) + 0.2;
					double bux = Math.max(xCoord, xCoord + (double)dir.offsetX * distance) + 0.8;
					double bly = Math.min(yCoord, 1 + yCoord + (double)dir.offsetY * distance) + 0.2;
					double buy = Math.max(yCoord, 1 + yCoord + (double)dir.offsetY * distance) + 0.8;
					double blz = Math.min(zCoord, zCoord + (double)dir.offsetZ * distance) + 0.2;
					double buz = Math.max(zCoord, zCoord + (double)dir.offsetZ * distance) + 0.8;
					
					List<EntityLivingBase> list = world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(blx, bly, blz, bux, buy, buz));
					
					for(EntityLivingBase entity : list) {
						switch(this.mode) {
						case RADIO: break;
						case MICRO: entity.setFire(2); break;
						case IR: entity.setFire(4); break;
						case VISIBLE: entity.addPotionEffect(new PotionEffect(Potion.getPotionById(15), 60 * 60 * 65536, 0)); break;
						case UV: entity.setFire(10); ContaminationUtil.contaminate(entity, HazardType.RADIATION, ContaminationType.CREATIVE, 0.025F); break;
						case XRAY: ContaminationUtil.contaminate(entity, HazardType.RADIATION, ContaminationType.CREATIVE, 1F); break;
						case GAMMA: 
							ContaminationUtil.contaminate(entity, HazardType.RADIATION, ContaminationType.CREATIVE, 1000);
							if(Math.random() < 0.01){
								entity.addPotionEffect(new PotionEffect(Potion.getPotionById(3), 1800, 4, false, false));
								entity.addPotionEffect(new PotionEffect(Potion.getPotionById(5), 1800, 6, false, false));
								entity.addPotionEffect(new PotionEffect(Potion.getPotionById(10), 1800, 6, false, false));
								entity.addPotionEffect(new PotionEffect(Potion.getPotionById(11), 1800, 6, false, false));
								entity.addPotionEffect(new PotionEffect(Potion.getPotionById(21), 1800, 4, false, false));
							}
							break;
						case DRX: ContaminationUtil.contaminate(entity, HazardType.RADIATION, ContaminationType.CREATIVE, 21000); ContaminationUtil.applyDigammaData(entity, 0.1F); break;
						}
					}
					
					this.power -= powerReq * ((mode.ordinal() == 0) ? 0 : Math.pow(4, mode.ordinal()));
					for(int i = 3; i < range; i++) {
					
						double x = xCoord + dir.offsetX * i;
						double y = yCoord + 1;
						double z = zCoord + dir.offsetZ * i;
						
						IBlockState b = world.getBlockState(new BlockPos(x, y, z));
						
						if(!(b.getMaterial().isOpaque()) && b != Blocks.TNT) {
							this.distance = range;
							silexSpacing = false;
							continue;
						}
						
						if(b.getBlock() == ModBlocks.machine_silex) {
							BlockPos silex_pos = new BlockPos(x + dir.offsetX, yCoord, z + dir.offsetZ);
							TileEntity te = world.getTileEntity(silex_pos);
						
							if(te instanceof TileEntitySILEX) {
								TileEntitySILEX silex = (TileEntitySILEX) te;
								int meta = silex.getBlockMetadata() - BlockDummyable.offset;
								if(rotationIsValid(meta, this.getBlockMetadata() - BlockDummyable.offset) && i >= 5 && silexSpacing == false	) {
									if(silex.mode != this.mode) {
										silex.mode = this.mode;
										this.missingValidSilex = false;
										silexSpacing = true;
										continue;
									} 
								} else {
									MachineSILEX silexBlock = (MachineSILEX)silex.getBlockType();
									world.setBlockToAir(silex_pos);
									world.spawnEntity(new EntityItem(world, x + 0.5, y + 0.5, z + 0.5, new ItemStack(Item.getItemFromBlock(ModBlocks.machine_silex))));
								} 
							}
							
						} else if(b.getBlock() != Blocks.AIR){
							this.distance = i;
							float hardness = b.getBlock().getExplosionResistance(null);
							boolean blocked = false;
							switch(this.mode) {
								case RADIO: blocked = true;break;
								case MICRO:
									if(b.getMaterial().isLiquid()){
										world.playSound(null, x + 0.5, y + 0.5, z + 0.5, SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS,  1.0F, 1.0F);
										world.setBlockToAir(new BlockPos(x, y, z));
									} else if(b.getMaterial() == Material.GLASS || b.getMaterial().isOpaque())
										blocked = true;
									break;
								case IR:
									if(b.getMaterial().isOpaque() || b.getMaterial() == Material.GLASS)
										blocked = true;
									break;
								case VISIBLE:
									if(b.getMaterial().isOpaque()){
										if(hardness < 10 && world.rand.nextInt(40) == 0){
											world.playSound(null, x + 0.5, y + 0.5, z + 0.5, SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS,  1.0F, 1.0F);
											world.setBlockState(new BlockPos(x, y, z), Blocks.FIRE.getDefaultState());
										}else{
											blocked = true;
										}
									}
									break;
								case UV: 
									if(b.getMaterial().isOpaque()){
										if(hardness < 100 && world.rand.nextInt(20) == 0){
											world.playSound(null, x + 0.5, y + 0.5, z + 0.5, SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS,  1.0F, 1.0F);
											world.setBlockState(new BlockPos(x, y, z), Blocks.FIRE.getDefaultState());
										}else{
											blocked = true;
										}
									}
									break;
								case XRAY: 
									if(b.getMaterial().isOpaque()){
										if(hardness < 1000 && world.rand.nextInt(10) == 0){
											world.playSound(null, x + 0.5, y + 0.5, z + 0.5, SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS,  1.0F, 1.0F);
											world.setBlockState(new BlockPos(x, y, z), Blocks.FIRE.getDefaultState());
										}else{
											blocked = true;
										}
									}
									break;
								case GAMMA: 
									if(b.getMaterial().isOpaque()){
										if(hardness < 3000 && world.rand.nextInt(5) == 0){
											world.playSound(null, x + 0.5, y + 0.5, z + 0.5, SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS,  1.0F, 1.0F);
											world.setBlockState(new BlockPos(x, y, z), ModBlocks.balefire.getDefaultState());
										}else{
											blocked = true;
										}
									}
									break;
								case DRX: 
									world.playSound(null, x + 0.5, y + 0.5, z + 0.5, SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS,  1.0F, 1.0F);
									world.setBlockState(new BlockPos(x, y, z), ((MainRegistry.polaroidID == 11) ? ModBlocks.digamma_matter : ModBlocks.fire_digamma).getDefaultState());
									world.setBlockState(new BlockPos(x, y-1, z), ModBlocks.ash_digamma.getDefaultState());
									break;
							}
							if(blocked)
								break;
						}
					}
				}
			}

			PacketDispatcher.wrapper.sendToAll(new LoopedSoundPacket(pos.getX(), pos.getY(), pos.getZ()));
			networkPackNT(250);
		}
	}
	
	public boolean rotationIsValid(int silexMeta, int felMeta) {
		ForgeDirection silexDir = ForgeDirection.getOrientation(silexMeta);
		ForgeDirection felDir = ForgeDirection.getOrientation(felMeta);
		if(silexDir == felDir || silexDir == felDir.getOpposite()) {
			return true;
		}
		 
		return false;
	}
	
	@Override
	public void serialize(ByteBuf buf) {
		buf.writeLong(power);
		BufferUtil.writeString(buf, mode.toString());
		buf.writeBoolean(isOn);
		buf.writeBoolean(missingValidSilex);
		buf.writeInt(distance);
	}

	@Override
	public void deserialize(ByteBuf buf) {
		this.power = buf.readLong();
		this.mode = EnumWavelengths.valueOf(BufferUtil.readString(buf));
		this.isOn = buf.readBoolean();
		this.distance = buf.readInt();
		this.missingValidSilex = buf.readBoolean();
	}

	@Override
	public void handleButtonPacket(int value, int meta) {
		
		if(meta == 2){
			this.isOn = !this.isOn;
		}
	}
	
	public long getPowerScaled(long i) {
		return (power * i) / maxPower;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		
		this.power = nbt.getLong("power");
		this.mode = EnumWavelengths.valueOf(nbt.getString("mode"));
		this.isOn = nbt.getBoolean("isOn");
		this.missingValidSilex = nbt.getBoolean("valid");
		this.distance = nbt.getInteger("distance");
	}
	
	@Override
	public @NotNull NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		
		nbt.setLong("power", this.power);
		nbt.setString("mode", this.mode.name());
		nbt.setBoolean("isOn", this.isOn);
		nbt.setBoolean("valid", this.missingValidSilex);
		nbt.setInteger("distance", this.distance);
		return nbt;
	}
	
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return INFINITE_EXTENT_AABB;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public double getMaxRenderDistanceSquared() {
		return 65536.0D;
	}

	@Override
	public void setPower(long i) {
		power = i;
	}

	@Override
	public long getPower() {
		return power;
	}

	@Override
	public long getMaxPower() {
		return maxPower;
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (capability == CapabilityEnergy.ENERGY) {
			return true;
		}
		return super.hasCapability(capability, facing);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (capability == CapabilityEnergy.ENERGY) {
			return CapabilityEnergy.ENERGY.cast(
					new NTMEnergyCapabilityWrapper(this)
			);
		}
		return super.getCapability(capability, facing);
	}
	@Override
	public Container provideContainer(int ID, EntityPlayer player, World world, int x, int y, int z) {
		return new ContainerFEL(player.inventory, this);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public GuiScreen provideGUI(int ID, EntityPlayer player, World world, int x, int y, int z) {
		return new GUIFEL(player.inventory, this);
	}
}