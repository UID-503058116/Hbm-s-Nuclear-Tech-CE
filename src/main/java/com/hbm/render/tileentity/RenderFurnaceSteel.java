package com.hbm.render.tileentity;

import com.hbm.blocks.BlockDummyable;
import com.hbm.blocks.ModBlocks;
import com.hbm.main.ResourceManager;
import com.hbm.render.item.ItemRenderBase;
import com.hbm.tileentity.machine.TileEntityFurnaceSteel;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import org.lwjgl.opengl.GL11;

public class RenderFurnaceSteel extends TileEntitySpecialRenderer<TileEntityFurnaceSteel>
    implements IItemRendererProvider {

  @Override
  public void render(
      TileEntityFurnaceSteel tileEntity,
      double x,
      double y,
      double z,
      float partialTicks,
      int destroyStage,
      float alpha) {
    GL11.glPushMatrix();
    GL11.glTranslated(x + 0.5D, y, z + 0.5D);
    GL11.glEnable(GL11.GL_LIGHTING);
    GL11.glEnable(GL11.GL_CULL_FACE);

    switch (tileEntity.getBlockMetadata() - BlockDummyable.offset) {
      case 3:
        GL11.glRotatef(0, 0F, 1F, 0F);
        break;
      case 5:
        GL11.glRotatef(90, 0F, 1F, 0F);
        break;
      case 2:
        GL11.glRotatef(180, 0F, 1F, 0F);
        break;
      case 4:
        GL11.glRotatef(270, 0F, 1F, 0F);
        break;
    }

    GL11.glRotatef(-90, 0F, 1F, 0F);

    bindTexture(ResourceManager.furnace_steel_tex);
    ResourceManager.furnace_steel.renderAll();
    // for now no anim, while I figure out the 1.12 tesselator
    GL11.glPopMatrix();
  }

  @Override
  public Item getItemForRenderer() {
    return Item.getItemFromBlock(ModBlocks.furnace_steel);
  }

  @Override
  public ItemRenderBase getRenderer(Item item) {
    return new ItemRenderBase() {
      public void renderInventory() {
        GlStateManager.translate(0, -1, 0);
        GlStateManager.scale(1.9, 1.9, 1.9);
      }

      public void renderCommon() {
        GlStateManager.rotate(180, 0, 1, 0);
        GlStateManager.scale(1.9, 1.9, 1.9);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        bindTexture(ResourceManager.furnace_steel_tex);
        ResourceManager.furnace_steel.renderAll();
        GlStateManager.shadeModel(GL11.GL_FLAT);
      }
    };
  }
}
