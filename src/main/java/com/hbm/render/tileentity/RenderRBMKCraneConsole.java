package com.hbm.render.tileentity;

import com.hbm.blocks.BlockDummyable;
import com.hbm.blocks.ModBlocks;
import com.hbm.main.ResourceManager;
import com.hbm.render.item.ItemRenderBase;
import com.hbm.tileentity.machine.rbmk.TileEntityRBMKCraneConsole;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import org.lwjgl.opengl.GL11;

public class RenderRBMKCraneConsole extends TileEntitySpecialRenderer<TileEntityRBMKCraneConsole>
    implements IItemRendererProvider {

  @Override
  public boolean isGlobalRenderer(TileEntityRBMKCraneConsole te) {
    return true;
  }

  @Override
  public void render(
      TileEntityRBMKCraneConsole console,
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

    switch (console.getBlockMetadata() - BlockDummyable.offset) {
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

    GL11.glTranslated(0.5, 0, 0);

    GlStateManager.shadeModel(GL11.GL_SMOOTH);
    bindTexture(ResourceManager.rbmk_crane_console_tex);
    ResourceManager.rbmk_crane_console.renderPart("Console_Coonsole");

    GL11.glPushMatrix();
    GL11.glTranslated(0.75, 1, 0);
    GL11.glRotated(
        console.lastTiltFront + (console.tiltFront - console.lastTiltFront) * partialTicks,
        0,
        0,
        1);
    GL11.glRotated(
        console.lastTiltLeft + (console.tiltLeft - console.lastTiltLeft) * partialTicks, 1, 0, 0);
    GL11.glTranslated(-0.75, -1.015, 0);
    ResourceManager.rbmk_crane_console.renderPart("JoyStick");
    GL11.glPopMatrix();

    GL11.glPushMatrix();
    GL11.glTranslated(0, 1.25, 0.75);
    double heat = console.loadedHeat;
    GL11.glRotated(
        Math.sin(System.currentTimeMillis() * 0.01 % 360) * 180 / Math.PI * 0.05 + 135 - 270 * heat,
        1,
        0,
        0);
    GL11.glTranslated(0, -1.25, -0.75);
    ResourceManager.rbmk_crane_console.renderPart("Meter1");
    GL11.glPopMatrix();

    GL11.glPushMatrix();
    GL11.glTranslated(0, 1.25, 0.25);
    double enrichment = console.loadedEnrichment;
    GL11.glRotated(
        Math.sin(System.currentTimeMillis() * 0.01 % 360) * 180 / Math.PI * 0.05
            + 135
            - 270 * enrichment,
        1,
        0,
        0);
    GL11.glTranslated(0, -1.25, -0.25);
    ResourceManager.rbmk_crane_console.renderPart("Meter2");
    GL11.glPopMatrix();

    bindTexture(ResourceManager.ks23_tex);
    ResourceManager.rbmk_crane_console.renderPart("Shotgun");
    bindTexture(ResourceManager.mini_nuke_tex);
    ResourceManager.rbmk_crane_console.renderPart("MiniNuke");

    bindTexture(ResourceManager.chemplant_fluid_tex); // Light Texture

    // loading indicator
    if (console.isCraneLoading()) GL11.glColor3f(0.8F, 0.8F, 0F); // is the crane loading? yellow
    else if (console.hasItemLoaded()) GL11.glColor3f(0F, 1F, 0F); // is the crane loaded? green
    else GL11.glColor3f(0F, 0.1F, 0F); // is the crane unloaded? off
    ResourceManager.rbmk_crane_console.renderPart("Lamp1");

    // target indicator
    if (console.isAboveValidTarget()) GL11.glColor3f(0F, 1F, 0F); // valid? green
    else GL11.glColor3f(1F, 0F, 0F); // not valid? red
    ResourceManager.rbmk_crane_console.renderPart("Lamp2");

    GL11.glColor3f(1F, 1F, 1F);

    GL11.glPopMatrix();
    GL11.glPushMatrix();

    if (console.setUpCrane) {
      GL11.glTranslated(x + 0.5, y - 1, z + 0.5);
      bindTexture(ResourceManager.rbmk_crane_tex);

      int height = console.height - 6;
      double cranePosX = (console.centerX - console.getPos().getX());
      double cranePosY = (console.centerY - console.getPos().getY()) + 1;
      double cranePosZ = (console.centerZ - console.getPos().getZ());

      GL11.glTranslated(cranePosX, cranePosY, cranePosZ);
      switch (console.getBlockMetadata() - BlockDummyable.offset) {
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

      double posX =
          (console.lastPosFront + (console.posFront - console.lastPosFront) * partialTicks);
      double posZ = (console.lastPosLeft + (console.posLeft - console.lastPosLeft) * partialTicks);
      GL11.glTranslated(0, 0, posZ);

      GL11.glPushMatrix();
      GL11.glTranslated(-console.spanL, height - 1, 0);

      for (int i = -console.spanL; i <= console.spanR; i++) {
        ResourceManager.rbmk_crane.renderPart("Girder");
        GL11.glTranslated(1, 0, 0);
      }
      GL11.glPopMatrix();

      GL11.glTranslated(-posX, 0, 0);
      ResourceManager.rbmk_crane.renderPart("Main");

      GL11.glPushMatrix();

      for (int i = 0; i < height; i++) {
        ResourceManager.rbmk_crane.renderPart("Tube");
        GL11.glTranslated(0, 1, 0);
      }
      GL11.glTranslated(0, -1, 0);
      ResourceManager.rbmk_crane.renderPart("Carriage");
      GL11.glPopMatrix();

      GL11.glTranslated(
          0,
          -3.25
              * (1
                  - (console.lastProgress
                      + (console.progress - console.lastProgress) * partialTicks)),
          0);
      ResourceManager.rbmk_crane.renderPart("Lift");
    }

    GlStateManager.shadeModel(GL11.GL_FLAT);
    GlStateManager.enableCull();
    GL11.glPopMatrix();
  }

  @Override
  public Item getItemForRenderer() {
    return Item.getItemFromBlock(ModBlocks.rbmk_crane_console);
  }

  @Override
  public ItemRenderBase getRenderer(Item item) {
    return new ItemRenderBase() {
      public void renderInventory() {
        GlStateManager.translate(0, -3, 0);
        GlStateManager.scale(3.5, 3.5, 3.5);
      }

      public void renderCommon() {
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        bindTexture(ResourceManager.rbmk_crane_console_tex);
        ResourceManager.rbmk_crane_console.renderPart("Console_Coonsole");
        ResourceManager.rbmk_crane_console.renderPart("JoyStick");
        ResourceManager.rbmk_crane_console.renderPart("Meter1");
        ResourceManager.rbmk_crane_console.renderPart("Meter2");
        bindTexture(ResourceManager.ks23_tex);
        ResourceManager.rbmk_crane_console.renderPart("Shotgun");
        bindTexture(ResourceManager.mini_nuke_tex);
        ResourceManager.rbmk_crane_console.renderPart("MiniNuke");
        GlStateManager.shadeModel(GL11.GL_FLAT);
      }
    };
  }
}
