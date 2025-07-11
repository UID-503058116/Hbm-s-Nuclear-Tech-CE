package com.hbm.explosion.vanillant.standard;

import com.hbm.explosion.vanillant.ExplosionVNT;
import com.hbm.explosion.vanillant.interfaces.ICustomDamageHandler;
import com.hbm.explosion.vanillant.interfaces.IEntityProcessor;
import com.hbm.explosion.vanillant.interfaces.IEntityRangeMutator;
import com.hbm.lib.ForgeDirection;
import net.minecraft.enchantment.EnchantmentProtection;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;

import java.util.HashMap;
import java.util.List;

/** The amount of good decisions in NTM is few and far between, but the VNT explosion surely is one of them. */
public class EntityProcessorCross implements IEntityProcessor {

	protected double nodeDist = 2D;
	protected IEntityRangeMutator range;
	protected ICustomDamageHandler damage;
	protected boolean allowSelfDamage = false;

	public EntityProcessorCross(double nodeDist) {
		this.nodeDist = nodeDist;
	}

	@Override
	public HashMap<EntityPlayer, Vec3d> process(ExplosionVNT explosion, World world, double x, double y, double z, float size) {

		HashMap<EntityPlayer, Vec3d> affectedPlayers = new HashMap();

		size *= 2.0F;
		
		if(range != null) {
			size = range.mutateRange(explosion, size);
		}
		
		double minX = x - (double) size - 1.0D;
		double maxX = x + (double) size + 1.0D;
		double minY = y - (double) size - 1.0D;
		double maxY = y + (double) size + 1.0D;
		double minZ = z - (double) size - 1.0D;
		double maxZ = z + (double) size + 1.0D;
		
		List list = world.getEntitiesWithinAABBExcludingEntity(explosion.exploder, new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ));
		
		ForgeEventFactory.onExplosionDetonate(world, explosion.compat, list, size);
		
		Vec3d[] nodes = new Vec3d[7];
		
		for(int i = 0; i < 7; i++) {
			ForgeDirection dir = ForgeDirection.getOrientation(i);
			nodes[i] = new Vec3d(x + dir.offsetX * nodeDist, y + dir.offsetY * nodeDist, z + dir.offsetZ * nodeDist);
		}

		for(int index = 0; index < list.size(); ++index) {
			
			Entity entity = (Entity) list.get(index);
			double distanceScaled = entity.getDistance(x, y, z) / size;

			if(distanceScaled <= 1.0D) {
				
				double deltaX = entity.posX - x;
				double deltaY = entity.posY + entity.getEyeHeight() - y;
				double deltaZ = entity.posZ - z;
				double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);

				if(distance != 0.0D) {
					
					deltaX /= distance;
					deltaY /= distance;
					deltaZ /= distance;
					
					double density = 0;
					
					for(Vec3d vec : nodes) {
						double d = world.getBlockDensity(vec, entity.getEntityBoundingBox());
						if(d > density) {
							density = d;
						}
					}
					
					double knockback = (1.0D - distanceScaled) * density;
					double enchKnockback = knockback;
					
					entity.attackEntityFrom(DamageSource.causeExplosionDamage(explosion.compat), (float) ((int) ((knockback * knockback + knockback) / 2.0D * 8.0D * size + 1.0D)));
					if (entity instanceof EntityLivingBase) {
						enchKnockback = EnchantmentProtection.getBlastDamageReduction((EntityLivingBase) entity, knockback);
					} else if (entity instanceof EntityItem) {
					}
					
					entity.motionX += deltaX * enchKnockback;
					entity.motionY += deltaY * enchKnockback;
					entity.motionZ += deltaZ * enchKnockback;

					if(entity instanceof EntityPlayer) {
						affectedPlayers.put((EntityPlayer) entity, new Vec3d(deltaX * knockback, deltaY * knockback, deltaZ * knockback));
					}
					
					if(damage != null) {
						damage.handleAttack(explosion, entity, distanceScaled);
					}
				}
			}
		}
		
		return affectedPlayers;
	}
	public EntityProcessorCross setAllowSelfDamage() {
		this.allowSelfDamage = true;
		return this;
	}


	public EntityProcessorCross withRangeMod(float mod) {
		range = new IEntityRangeMutator() {
			@Override
			public float mutateRange(ExplosionVNT explosion, float range) {
				return range * mod;
			}
		};
		return this;
	}
	
	public EntityProcessorCross withDamageMod(ICustomDamageHandler damage) {
		this.damage = damage;
		return this;
	}
}
