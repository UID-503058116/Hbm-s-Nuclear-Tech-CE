package com.hbm.render.tileentity;

import com.hbm.blocks.ModBlocks;
import com.hbm.main.ResourceManager;
import com.hbm.render.item.ItemRenderBase;
import com.hbm.tileentity.machine.TileEntityMachineCyclotron;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import org.lwjgl.opengl.GL11;

public class RenderCyclotron extends TileEntitySpecialRenderer<TileEntityMachineCyclotron>
    implements IItemRendererProvider {

  @Override
  public void render(
      TileEntityMachineCyclotron cyc,
      double x,
      double y,
      double z,
      float partialTicks,
      int destroyStage,
      float alpha) {
    GL11.glPushMatrix();
    GL11.glTranslated(x + 0.5D, y, z + 0.5D);

    switch (cyc.getBlockMetadata()) {
      case 14:
        GL11.glRotatef(0, 0F, 1F, 0F);
        break;
      case 12:
        GL11.glRotatef(270, 0F, 1F, 0F);
        break;
      case 15:
        GL11.glRotatef(180, 0F, 1F, 0F);
        break;
      case 13:
        GL11.glRotatef(90, 0F, 1F, 0F);
        break;
    }

    GlStateManager.enableLighting();
    GL11.glEnable(GL11.GL_LIGHTING);
    GlStateManager.disableCull();
    GlStateManager.shadeModel(GL11.GL_SMOOTH);

    bindTexture(ResourceManager.cyclotron_tex);
    ResourceManager.cyclotron.renderPart("Body");

    GL11.glShadeModel(GL11.GL_FLAT);

    boolean plugged = true;

    if (cyc.getPlug(0)) {
      bindTexture(ResourceManager.cyclotron_ashes_filled);
    } else {
      bindTexture(ResourceManager.cyclotron_ashes);
      plugged = false;
    }
    ResourceManager.cyclotron.renderPart("B1");
    if (cyc.getPlug(1)) {
      bindTexture(ResourceManager.cyclotron_book_filled);
    } else {
      bindTexture(ResourceManager.cyclotron_book);
      plugged = false;
    }
    ResourceManager.cyclotron.renderPart("B2");
    if (cyc.getPlug(2)) {
      bindTexture(ResourceManager.cyclotron_gavel_filled);
    } else {
      bindTexture(ResourceManager.cyclotron_gavel);
      plugged = false;
    }
    ResourceManager.cyclotron.renderPart("B3");
    if (cyc.getPlug(3)) {
      bindTexture(ResourceManager.cyclotron_coin_filled);
    } else {
      bindTexture(ResourceManager.cyclotron_coin);
      plugged = false;
    }
    ResourceManager.cyclotron.renderPart("B4");

    if (plugged) {

      GL11.glPushMatrix();
      RenderHelper.enableStandardItemLighting();
      GL11.glRotated(System.currentTimeMillis() * 0.025 % 360, 0, 1, 0);

      GlStateManager.enableBlend();
      GlStateManager.disableLighting();
      GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE);
      GlStateManager.disableAlpha();

      String msg = "plures necat crapula quam gladius";

      GL11.glTranslated(0, 2, 0);
      GL11.glRotated(180, 1, 0, 0);

      float rot = 0F;

      // looks dumb but we'll use this technology for the cyclotron
      for (char c : msg.toCharArray()) {

        GL11.glPushMatrix();

        GL11.glRotatef(rot, 0, 1, 0);

        rot -= Minecraft.getMinecraft().fontRenderer.getCharWidth(c) * 2F;

        GL11.glTranslated(2.75, 0, 0);

        GL11.glRotatef(-90, 0, 1, 0);

        float scale = 0.1F;
        GL11.glScalef(scale, scale, scale);
        GlStateManager.disableCull();
        Minecraft.getMinecraft()
            .standardGalacticFontRenderer
            .drawString(String.valueOf(c), 0, 0, 0x600060);
        GlStateManager.enableCull();
        GL11.glPopMatrix();
      }

      GlStateManager.enableLighting();
      GlStateManager.disableBlend();
      GlStateManager.enableTexture2D();

      GL11.glPopMatrix();

      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      RenderHelper.enableStandardItemLighting();
    }

    GlStateManager.shadeModel(GL11.GL_FLAT);
    GlStateManager.enableCull();
    GL11.glPopMatrix();
  }

  @Override
  public Item getItemForRenderer() {
    return Item.getItemFromBlock(ModBlocks.machine_cyclotron);
  }

  @Override
  public ItemRenderBase getRenderer(Item item) {
    return new ItemRenderBase() {
      public void renderInventory() {
        GlStateManager.scale(2.25, 2.25, 2.25);
      }

      public void renderCommon() {
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        bindTexture(ResourceManager.cyclotron_tex);
        ResourceManager.cyclotron.renderPart("Body");
        bindTexture(ResourceManager.cyclotron_ashes);
        ResourceManager.cyclotron.renderPart("B1");
        bindTexture(ResourceManager.cyclotron_book);
        ResourceManager.cyclotron.renderPart("B2");
        bindTexture(ResourceManager.cyclotron_gavel);
        ResourceManager.cyclotron.renderPart("B3");
        bindTexture(ResourceManager.cyclotron_coin);
        ResourceManager.cyclotron.renderPart("B4");
        GlStateManager.shadeModel(GL11.GL_FLAT);
      }
    };
  }
}
