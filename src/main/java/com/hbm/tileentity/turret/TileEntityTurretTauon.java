package com.hbm.tileentity.turret;

import com.hbm.handler.BulletConfigSyncingUtil;
import com.hbm.handler.BulletConfiguration;
import com.hbm.handler.threading.PacketThreading;
import com.hbm.inventory.container.ContainerTurretBase;
import com.hbm.inventory.gui.GUITurretTauon;
import com.hbm.lib.HBMSoundHandler;
import com.hbm.lib.ModDamageSource;
import com.hbm.packet.toclient.AuxParticlePacketNT;
import com.hbm.render.amlfrom1710.Vec3;
import com.hbm.tileentity.IGUIProvider;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

public class TileEntityTurretTauon extends TileEntityTurretBaseNT implements IGUIProvider {
	
	static List<Integer> configs = new ArrayList<>();

	static {
		configs.add(BulletConfigSyncingUtil.SPECIAL_GAUSS);
	}

	@Override
	protected List<Integer> getAmmoList(){
		return configs;
	}

	@Override
	public String getName(){
		return "container.turretTauon";
	}

	@Override
	public double getDecetorGrace(){
		return 3D;
	}

	@Override
	public double getTurretYawSpeed(){
		return 9D;
	}

	@Override
	public double getTurretPitchSpeed(){
		return 6D;
	}

	@Override
	public double getTurretElevation(){
		return 35D;
	}

	@Override
	public double getTurretDepression(){
		return 35D;
	}

	@Override
	public double getDecetorRange(){
		return 128D;
	}

	@Override
	public double getBarrelLength(){
		return 2.0D - 0.0625D;
	}

	@Override
	public long getMaxPower(){
		return 100000;
	}

	@Override
	public long getConsumption(){
		return 1000;
	}

	int timer;
	public int beam;
	public float spin;
	public float lastSpin;
	public double lastDist;

	@Override
	public void update(){
		if(world.isRemote) {
			
			if(this.tPos != null) {
				Vec3d pos = this.getTurretPos();
				double length = new Vec3d(tPos.x - pos.x, tPos.y - pos.y, tPos.z - pos.z).length();
				this.lastDist = length;
			}
			
			if(beam > 0)
				beam--;
			
			this.lastSpin = this.spin;
			
			if(this.tPos != null) {
				this.spin += 45;
			}
			
			if(this.spin >= 360F) {
				this.spin -= 360F;
				this.lastSpin -= 360F;
			}
		}
		super.update();
	}

	@Override
	public void updateFiringTick(){
		timer++;
		
		if(timer % 5 == 0) {
			
			BulletConfiguration conf = this.getFirstConfigLoaded();
			
			if(conf != null && this.target != null) {
				this.target.attackEntityFrom(ModDamageSource.electricity, 30F + world.rand.nextInt(11));
				this.consumeAmmo(conf.ammo);
				this.world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), HBMSoundHandler.tauShoot, SoundCategory.BLOCKS, 4.0F, 0.9F + world.rand.nextFloat() * 0.3F);

				networkPackNT(250);
				
				Vec3 pos = new Vec3(this.getTurretPos());
				Vec3 vec = Vec3.createVectorHelper(this.getBarrelLength(), 0, 0);
				vec.rotateAroundZ((float) -this.rotationPitch);
				vec.rotateAroundY((float) -(this.rotationYaw + Math.PI * 0.5));
				
				NBTTagCompound dPart = new NBTTagCompound();
				dPart.setString("type", "tau");
				dPart.setByte("count", (byte)5);
				PacketThreading.createAllAroundThreadedPacket(new AuxParticlePacketNT(dPart, pos.xCoord + vec.xCoord, pos.yCoord + vec.yCoord, pos.zCoord + vec.zCoord), new TargetPoint(world.provider.getDimension(), this.pos.getX(), this.pos.getY(), this.pos.getZ(), 50));
			}
		}
	}

	@Override
	public void serialize(ByteBuf buf) {
		super.serialize(buf);
		buf.writeBoolean(true);
	}

	@Override
	public void deserialize(ByteBuf buf) {
		super.deserialize(buf);
		if(buf.readBoolean())
			beam = 3;
	}

	@Override
	public Container provideContainer(int ID, EntityPlayer player, World world, int x, int y, int z) {
		return new ContainerTurretBase(player.inventory, this);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public GuiScreen provideGUI(int ID, EntityPlayer player, World world, int x, int y, int z) {
		return new GUITurretTauon(player.inventory, this);
	}
}
