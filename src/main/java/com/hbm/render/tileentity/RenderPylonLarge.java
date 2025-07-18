package com.hbm.render.tileentity;

import com.hbm.blocks.BlockDummyable;
import com.hbm.blocks.ModBlocks;
import com.hbm.main.ResourceManager;
import com.hbm.render.item.ItemRenderBase;
import com.hbm.tileentity.network.energy.TileEntityPylonLarge;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import org.lwjgl.opengl.GL11;

public class RenderPylonLarge extends TileEntitySpecialRenderer<TileEntityPylonLarge>
    implements IItemRendererProvider {

  @Override
  public boolean isGlobalRenderer(TileEntityPylonLarge te) {
    return true;
  }

  @Override
  public void render(
      TileEntityPylonLarge pyl,
      double x,
      double y,
      double z,
      float partialTicks,
      int destroyStage,
      float alpha) {
    GL11.glPushMatrix();
    GL11.glTranslatef((float) x + 0.5F, (float) y, (float) z + 0.5F);
    switch (pyl.getBlockMetadata() - BlockDummyable.offset) {
      case 2:
        GL11.glRotatef(90, 0F, 1F, 0F);
        break;
      case 4:
        GL11.glRotatef(135, 0F, 1F, 0F);
        break;
      case 3:
        GL11.glRotatef(0, 0F, 1F, 0F);
        break;
      case 5:
        GL11.glRotatef(45, 0F, 1F, 0F);
        break;
    }
    bindTexture(ResourceManager.pylon_large_tex);
    ResourceManager.pylon_large.renderAll();
    GL11.glPopMatrix();

    RenderPylon.renderPowerLines(pyl, x, y, z);
  }

  @Override
  public Item getItemForRenderer() {
    return Item.getItemFromBlock(ModBlocks.red_pylon_large);
  }

  @Override
  public ItemRenderBase getRenderer(Item item) {
    return new ItemRenderBase() {
      public void renderInventory() {
        GlStateManager.translate(0, -5, 0);
        GlStateManager.scale(2.25, 2.25, 2.25);
      }

      public void renderCommon() {
        GlStateManager.scale(0.5, 0.5, 0.5);
        bindTexture(ResourceManager.pylon_large_tex);
        ResourceManager.pylon_large.renderAll();
      }
    };
  }
}
