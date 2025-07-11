package com.hbm.entity.mob;

import com.hbm.items.ModItems;
import com.hbm.tileentity.machine.TileEntityTesla;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.item.Item;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class EntityTeslaCrab extends EntityCyberCrab {
	
	public List<double[]> targets = new ArrayList<double[]>();

	public EntityTeslaCrab(World p_i1733_1_) {
		super(p_i1733_1_);
        this.setSize(0.75F, 1.25F);
        this.ignoreFrustumCheck = true;
	}

    @Override
	protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.5F);
    }
    
    public void onLivingUpdate() {
    	
    	targets = TileEntityTesla.zap(world, posX, posY + 1, posZ, 3, this);
    	
        super.onLivingUpdate();
    }

    @Override
	protected Item getDropItem(){
        return ModItems.wire_fine;
    }

    protected void dropRareDrop(int p_70600_1_) {
    	this.dropItem(ModItems.coil_copper, 1);
    }

}