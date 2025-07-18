package com.hbm.render.tileentity;

import com.hbm.blocks.ModBlocks;
import com.hbm.main.ResourceManager;
import com.hbm.render.item.ItemRenderBase;
import com.hbm.tileentity.machine.TileEntityMachineRadarNT;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import org.lwjgl.opengl.GL11;

public class RenderRadar extends TileEntitySpecialRenderer<TileEntityMachineRadarNT>
    implements IItemRendererProvider {

  @Override
  public boolean isGlobalRenderer(TileEntityMachineRadarNT te) {
    return true;
  }

  @Override
  public void render(
      TileEntityMachineRadarNT radar,
      double x,
      double y,
      double z,
      float partialTicks,
      int destroyStage,
      float alpha) {
    GL11.glPushMatrix();
    GL11.glTranslated(x + 0.5D, y, z + 0.5D);
    GlStateManager.enableLighting();
    GlStateManager.disableCull();
    GL11.glRotatef(180, 0F, 1F, 0F);

    bindTexture(ResourceManager.radar_base_tex);
    ResourceManager.radar.renderPart("Base");

    if (radar.power > 0)
      GL11.glRotatef(((float) -System.currentTimeMillis() / 10) % 360, 0F, 1F, 0F);
    GL11.glTranslated(-0.125D, 0, 0);

    bindTexture(ResourceManager.radar_dish_tex);
    ResourceManager.radar.renderPart("Dish");

    GL11.glPopMatrix();

    GlStateManager.enableCull();
  }

  @Override
  public Item getItemForRenderer() {
    return Item.getItemFromBlock(ModBlocks.machine_radar);
  }

  @Override
  public ItemRenderBase getRenderer(Item item) {
    return new ItemRenderBase() {
      public void renderInventory() {
        GlStateManager.translate(0, -4, 0);
        GlStateManager.scale(6, 6, 6);
      }

      public void renderCommon() {
        GlStateManager.disableCull();
        bindTexture(ResourceManager.radar_base_tex);
        ResourceManager.radar.renderPart("Base");
        GL11.glTranslated(-0.125, 0, 0);
        bindTexture(ResourceManager.radar_dish_tex);
        ResourceManager.radar.renderPart("Dish");
        GlStateManager.enableCull();
      }
    };
  }
}
