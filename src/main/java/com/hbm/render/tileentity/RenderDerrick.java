package com.hbm.render.tileentity;

import com.hbm.blocks.BlockDummyable;
import com.hbm.blocks.ModBlocks;
import com.hbm.main.ResourceManager;
import com.hbm.render.item.ItemRenderBase;
import com.hbm.tileentity.machine.oil.TileEntityMachineOilWell;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import org.lwjgl.opengl.GL11;

public class RenderDerrick extends TileEntitySpecialRenderer<TileEntityMachineOilWell>
    implements IItemRendererProvider {

  @Override
  public boolean isGlobalRenderer(TileEntityMachineOilWell te) {
    return true;
  }

  @Override
  public void render(
      TileEntityMachineOilWell te,
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
    GL11.glRotatef(90, 0F, 1F, 0F);

    switch (te.getBlockMetadata() - BlockDummyable.offset) {
      case 2 -> GL11.glRotatef(90, 0F, 1F, 0F);
      case 4 -> GL11.glRotatef(180, 0F, 1F, 0F);
      case 3 -> GL11.glRotatef(270, 0F, 1F, 0F);
      case 5 -> GL11.glRotatef(0, 0F, 1F, 0F);
    }

    GL11.glShadeModel(GL11.GL_SMOOTH);
    bindTexture(ResourceManager.derrick_tex);
    ResourceManager.derrick.renderAll();
    GL11.glShadeModel(GL11.GL_FLAT);

    GL11.glPopMatrix();
  }

  @Override
  public Item getItemForRenderer() {
    return Item.getItemFromBlock(ModBlocks.machine_well);
  }

  @Override
  public ItemRenderBase getRenderer(Item item) {
    return new ItemRenderBase() {
      public void renderInventory() {
        GlStateManager.translate(0, -4, 0);
        GlStateManager.scale(3, 3, 3);
      }

      public void renderCommon() {
        GlStateManager.rotate(90, 0F, 1F, 0F);
        GlStateManager.scale(0.5, 0.5, 0.5);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        bindTexture(ResourceManager.derrick_tex);
        ResourceManager.derrick.renderAll();
        GlStateManager.shadeModel(GL11.GL_FLAT);
      }
    };
  }
}
