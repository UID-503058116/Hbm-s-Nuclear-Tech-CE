package com.hbm.blocks.machine;

import com.hbm.blocks.BlockDummyable;
import com.hbm.blocks.ITooltipProvider;
import com.hbm.lib.ForgeDirection;
import com.hbm.tileentity.TileEntityProxyCombo;
import com.hbm.tileentity.machine.TileEntityChimneyBrick;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.List;

public class MachineChimneyBrick extends BlockDummyable implements ITooltipProvider {
    public MachineChimneyBrick(String s) {
        super(Material.IRON, s);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {

        if(meta >= 12) return new TileEntityChimneyBrick();
        if(meta >= 6) return new TileEntityProxyCombo().fluid();
        return null;
    }

    @Override
    public int[] getDimensions() {
        return new int[] {12, 0, 1, 1, 1, 1};
    }

    @Override
    public int getOffset() {
        return 1;
    }

    @Override
    public void fillSpace(World world, int x, int y, int z, ForgeDirection dir, int o) {
        super.fillSpace(world, x, y, z, dir, o);
        this.makeExtra(world, x + dir.offsetX * o + 1, y, z + dir.offsetZ * o);
        this.makeExtra(world, x + dir.offsetX * o - 1, y, z + dir.offsetZ * o);
        this.makeExtra(world, x + dir.offsetX * o, y, z + dir.offsetZ * o + 1);
        this.makeExtra(world, x + dir.offsetX * o, y, z + dir.offsetZ * o - 1);
    }

    @Override
    public void addInformation(ItemStack stack, World player, List<String> tooltip, ITooltipFlag advanced) {
        this.addStandardInfo(tooltip);
    }
}
