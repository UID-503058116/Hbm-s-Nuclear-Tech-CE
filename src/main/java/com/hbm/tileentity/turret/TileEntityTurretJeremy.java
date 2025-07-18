package com.hbm.tileentity.turret;

import com.hbm.handler.BulletConfigSyncingUtil;
import com.hbm.handler.BulletConfiguration;
import com.hbm.handler.threading.PacketThreading;
import com.hbm.inventory.container.ContainerTurretBase;
import com.hbm.inventory.gui.GUITurretJeremy;
import com.hbm.lib.HBMSoundHandler;
import com.hbm.packet.toclient.AuxParticlePacketNT;
import com.hbm.render.amlfrom1710.Vec3;
import com.hbm.tileentity.IGUIProvider;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

public class TileEntityTurretJeremy extends TileEntityTurretBaseNT implements IGUIProvider {

	static List<Integer> configs = new ArrayList<>();

	static {
		configs.add(BulletConfigSyncingUtil.SHELL_NORMAL);
		configs.add(BulletConfigSyncingUtil.SHELL_EXPLOSIVE);
		configs.add(BulletConfigSyncingUtil.SHELL_AP);
		configs.add(BulletConfigSyncingUtil.SHELL_DU);
		configs.add(BulletConfigSyncingUtil.SHELL_W9);
	}

	@Override
	protected List<Integer> getAmmoList(){
		return configs;
	}

	@Override
	public String getName(){
		return "container.turretJeremy";
	}

	@Override
	public double getDecetorGrace(){
		return 16D;
	}

	@Override
	public double getTurretDepression(){
		return 45D;
	}

	@Override
	public long getMaxPower(){
		return 10000;
	}

	@Override
	public double getBarrelLength(){
		return 4.25D;
	}

	@Override
	public double getDecetorRange(){
		return 80D;
	}

	public int timer;
	public int reload;

	@Override
	public void update(){
		if(reload > 0)
			reload--;
		
		if(reload == 1)
			this.world.playSound(null, pos, HBMSoundHandler.jeremy_reload, SoundCategory.BLOCKS, 2.0F, 1.0F);
		
		super.update();
	}

	@Override
	public void updateFiringTick(){
		timer++;

		if(timer % 40 == 0) {

			BulletConfiguration conf = this.getFirstConfigLoaded();

			if(conf != null) {
				this.spawnBullet(conf);
				this.consumeAmmo(conf.ammo);
				this.world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), HBMSoundHandler.jeremy_fire, SoundCategory.BLOCKS, 4.0F, 1.0F);
				
				Vec3 pos = new Vec3(this.getTurretPos());
				Vec3 vec = Vec3.createVectorHelper(this.getBarrelLength(), 0, 0);
				vec.rotateAroundZ((float) -this.rotationPitch);
				vec.rotateAroundY((float) -(this.rotationYaw + Math.PI * 0.5));

				reload = 20;

				NBTTagCompound data = new NBTTagCompound();
				data.setString("type", "vanillaExt");
				data.setString("mode", "largeexplode");
				data.setFloat("size", 0F);
				data.setByte("count", (byte)5);
				PacketThreading.createAllAroundThreadedPacket(new AuxParticlePacketNT(data, pos.xCoord + vec.xCoord, pos.yCoord + vec.yCoord, pos.zCoord + vec.zCoord), new TargetPoint(world.provider.getDimension(), this.pos.getX(), this.pos.getY(), this.pos.getZ(), 50));
			}
		}
	}

	@Override
	public Container provideContainer(int ID, EntityPlayer player, World world, int x, int y, int z) {
		return new ContainerTurretBase(player.inventory, this);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public GuiScreen provideGUI(int ID, EntityPlayer player, World world, int x, int y, int z) {
		return new GUITurretJeremy(player.inventory, this);
	}
}
