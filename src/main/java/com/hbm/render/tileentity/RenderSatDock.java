package com.hbm.render.tileentity;

import com.hbm.blocks.ModBlocks;
import com.hbm.main.ResourceManager;
import com.hbm.render.item.ItemRenderBase;
import com.hbm.tileentity.machine.TileEntityMachineSatDock;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import org.lwjgl.opengl.GL11;

public class RenderSatDock extends TileEntitySpecialRenderer<TileEntityMachineSatDock> implements IItemRendererProvider {

	@Override
	public boolean isGlobalRenderer(TileEntityMachineSatDock te) {
		return true;
	}
	
	@Override
	public void render(TileEntityMachineSatDock te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		GL11.glPushMatrix();
		GL11.glTranslatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
		GL11.glRotatef(180, 0F, 0F, 1F);
		GlStateManager.enableLighting();
		
		GL11.glRotatef(180, 0F, 0F, 1F);
		GL11.glTranslatef(0, -1.5F, 0);
		
    	bindTexture(ResourceManager.satdock_tex);
    	ResourceManager.satDock.renderAll();
    	GL11.glPopMatrix();
	}

	@Override
	public Item getItemForRenderer() {
		return Item.getItemFromBlock(ModBlocks.sat_dock);
	}

	@Override
	public ItemRenderBase getRenderer(Item item) {
		return new ItemRenderBase() {
			public void renderInventory() {
				GlStateManager.scale(3, 3, 3);
			}

			public void renderCommon() {
				GlStateManager.rotate(90, 0, -1, 0);
				bindTexture(ResourceManager.satdock_tex);
				ResourceManager.satDock.renderAll();
			}
		};
	}
}
