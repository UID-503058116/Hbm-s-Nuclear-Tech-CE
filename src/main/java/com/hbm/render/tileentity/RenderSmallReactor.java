package com.hbm.render.tileentity;

import com.hbm.blocks.ModBlocks;
import com.hbm.main.ResourceManager;
import com.hbm.render.item.ItemRenderBase;
import com.hbm.tileentity.machine.TileEntityReactorResearch;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.Item;
import org.lwjgl.opengl.GL11;

public class RenderSmallReactor extends TileEntitySpecialRenderer<TileEntityReactorResearch>
    implements IItemRendererProvider {
  @Override
  public void render(
      TileEntityReactorResearch reactor,
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

    bindTexture(ResourceManager.reactor_small_base_tex);
    ResourceManager.reactor_small_base.renderAll();

    double level = (reactor.lastLevel + (reactor.level - reactor.lastLevel) * partialTicks);

    GL11.glPushMatrix();
    GL11.glTranslated(0.0D, level, 0.0D);
    bindTexture(ResourceManager.reactor_small_rods_tex);
    ResourceManager.reactor_small_rods.renderAll();
    GL11.glPopMatrix();

    if (reactor.totalFlux > 10 && reactor.isSubmerged()) {

      GlStateManager.disableTexture2D();
      GlStateManager.enableBlend();
      GlStateManager.disableLighting();
      GlStateManager.blendFunc(
          GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
      GlStateManager.disableAlpha();

      Tessellator tess = Tessellator.getInstance();
      BufferBuilder buf = tess.getBuffer();

      for (double d = 0.285; d < 0.7; d += 0.025) {

        buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        float intensity = 0.002F + (0.003F * reactor.totalFlux / 1000F);
        double top = 1.375;
        double bottom = 1.375;

        buf.pos(d, bottom - d, -d).color(0.4F, 0.9F, 1.0F, intensity).endVertex();
        buf.pos(d, top + d, -d).color(0.4F, 0.9F, 1.0F, intensity).endVertex();
        buf.pos(d, top + d, d).color(0.4F, 0.9F, 1.0F, intensity).endVertex();
        buf.pos(d, bottom - d, d).color(0.4F, 0.9F, 1.0F, intensity).endVertex();

        buf.pos(-d, bottom - d, -d).color(0.4F, 0.9F, 1.0F, intensity).endVertex();
        buf.pos(-d, top + d, -d).color(0.4F, 0.9F, 1.0F, intensity).endVertex();
        buf.pos(-d, top + d, d).color(0.4F, 0.9F, 1.0F, intensity).endVertex();
        buf.pos(-d, bottom - d, d).color(0.4F, 0.9F, 1.0F, intensity).endVertex();

        buf.pos(-d, bottom - d, d).color(0.4F, 0.9F, 1.0F, intensity).endVertex();
        buf.pos(-d, top + d, d).color(0.4F, 0.9F, 1.0F, intensity).endVertex();
        buf.pos(d, top + d, d).color(0.4F, 0.9F, 1.0F, intensity).endVertex();
        buf.pos(d, bottom - d, d).color(0.4F, 0.9F, 1.0F, intensity).endVertex();

        buf.pos(-d, bottom - d, -d).color(0.4F, 0.9F, 1.0F, intensity).endVertex();
        buf.pos(-d, top + d, -d).color(0.4F, 0.9F, 1.0F, intensity).endVertex();
        buf.pos(d, top + d, -d).color(0.4F, 0.9F, 1.0F, intensity).endVertex();
        buf.pos(d, bottom - d, -d).color(0.4F, 0.9F, 1.0F, intensity).endVertex();

        buf.pos(-d, top + d, -d).color(0.4F, 0.9F, 1.0F, intensity).endVertex();
        buf.pos(-d, top + d, d).color(0.4F, 0.9F, 1.0F, intensity).endVertex();
        buf.pos(d, top + d, d).color(0.4F, 0.9F, 1.0F, intensity).endVertex();
        buf.pos(d, top + d, -d).color(0.4F, 0.9F, 1.0F, intensity).endVertex();

        buf.pos(-d, bottom - d, -d).color(0.4F, 0.9F, 1.0F, intensity).endVertex();
        buf.pos(-d, bottom - d, d).color(0.4F, 0.9F, 1.0F, intensity).endVertex();
        buf.pos(d, bottom - d, d).color(0.4F, 0.9F, 1.0F, intensity).endVertex();
        buf.pos(d, bottom - d, -d).color(0.4F, 0.9F, 1.0F, intensity).endVertex();

        tess.draw();
      }

      GlStateManager.enableLighting();
      GlStateManager.disableBlend();
      GlStateManager.enableTexture2D();
    }

    GlStateManager.enableCull();

    GL11.glPopMatrix();
  }

  @Override
  public Item getItemForRenderer() {
    return Item.getItemFromBlock(ModBlocks.reactor_research);
  }

  @Override
  public ItemRenderBase getRenderer(Item item) {
    return new ItemRenderBase() {
      public void renderInventory() {
        GlStateManager.translate(0, -4, 0);
        GlStateManager.scale(4, 4, 4);
      }

      public void renderCommon() {
        bindTexture(ResourceManager.reactor_small_base_tex);
        ResourceManager.reactor_small_base.renderAll();
        bindTexture(ResourceManager.reactor_small_rods_tex);
        ResourceManager.reactor_small_rods.renderAll();
      }
    };
  }
}
