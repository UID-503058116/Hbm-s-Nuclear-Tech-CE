package com.hbm.render.tileentity;

import com.hbm.blocks.BlockDummyable;
import com.hbm.blocks.ModBlocks;
import com.hbm.main.ResourceManager;
import com.hbm.render.item.ItemRenderBase;
import com.hbm.tileentity.machine.TileEntityXenonThruster;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import org.lwjgl.opengl.GL11;

public class RenderXenonThruster extends TileEntitySpecialRenderer<TileEntityXenonThruster>
    implements IItemRendererProvider {

  @Override
  public void render(
      TileEntityXenonThruster te,
      double x,
      double y,
      double z,
      float partialTicks,
      int destroyStage,
      float alpha) {

    GL11.glPushMatrix();
    {
      GL11.glTranslated(x + 0.5, y - 1.0, z + 0.5);

      GL11.glRotatef(-90, 0, 1, 0);

      switch (te.getBlockMetadata() - BlockDummyable.offset) {
        case 2:
          GL11.glRotatef(90, 0F, 1F, 0F);
          break;
        case 4:
          GL11.glRotatef(180, 0F, 1F, 0F);
          break;
        case 3:
          GL11.glRotatef(270, 0F, 1F, 0F);
          break;
        case 5:
          GL11.glRotatef(0, 0F, 1F, 0F);
          break;
      }

      GL11.glTranslated(0, 0, -0.5);

      float trailStretch = te.getWorld().rand.nextFloat();
      trailStretch = 1.2F - (trailStretch * trailStretch * 0.2F);
      trailStretch *= te.thrustAmount;

      GL11.glShadeModel(GL11.GL_SMOOTH);

      bindTexture(ResourceManager.xenon_thruster_tex);
      ResourceManager.xenon_thruster.renderPart("Thruster");

      if (trailStretch > 0) {
        GL11.glColor4d(1, 1, 1, te.thrustAmount);

        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.glBlendFunc(
            GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        GL11.glPushAttrib(GL11.GL_LIGHTING_BIT);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240F, 240F);
        GL11.glDepthMask(false);

        GL11.glTranslatef(0, 0, -1F);
        GL11.glScalef(1, 1, trailStretch);
        GL11.glTranslatef(0, 0, 1F);

        bindTexture(ResourceManager.xenon_exhaust_tex);
        ResourceManager.xenon_thruster.renderPart("Exhaust");

        GL11.glDepthMask(true);
        GL11.glPopAttrib();
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glDisable(GL11.GL_BLEND);

        GL11.glColor4d(1, 1, 1, 1);
      }

      GL11.glShadeModel(GL11.GL_FLAT);
    }
    GL11.glPopMatrix();
  }

  @Override
  public Item getItemForRenderer() {
    return Item.getItemFromBlock(ModBlocks.machine_xenon_thruster);
  }

  @Override
  public ItemRenderBase getRenderer(Item item) {
    return new ItemRenderBase() {
      public void renderInventory() {
        GlStateManager.translate(0, -4, 0);
        GlStateManager.scale(4, 4, 4);
      }

      public void renderCommon() {
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        bindTexture(ResourceManager.xenon_thruster_tex);
        ResourceManager.xenon_thruster.renderPart("Thruster");
        GlStateManager.shadeModel(GL11.GL_FLAT);
      }
    };
  }
}
