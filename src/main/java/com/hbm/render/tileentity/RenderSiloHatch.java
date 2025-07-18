package com.hbm.render.tileentity;

import com.hbm.animloader.AnimationWrapper;
import com.hbm.animloader.AnimationWrapper.EndResult;
import com.hbm.animloader.AnimationWrapper.EndType;
import com.hbm.blocks.ModBlocks;
import com.hbm.interfaces.IDoor;
import com.hbm.main.ResourceManager;
import com.hbm.render.item.ItemRenderBase;
import com.hbm.tileentity.machine.TileEntitySiloHatch;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import org.lwjgl.opengl.GL11;

public class RenderSiloHatch extends TileEntitySpecialRenderer<TileEntitySiloHatch>
    implements IItemRendererProvider {

  @Override
  public boolean isGlobalRenderer(TileEntitySiloHatch te) {
    return true;
  }

  @Override
  public void render(
      TileEntitySiloHatch te,
      double x,
      double y,
      double z,
      float partialTicks,
      int destroyStage,
      float alpha) {
    GL11.glPushMatrix();
    GL11.glTranslated(x + 0.5, y + 0.595, z + 0.5);
    switch (te.getBlockMetadata() - 2) {
      case 0:
        GL11.glRotatef(270, 0F, 1F, 0F);
        break;
      case 1:
        GL11.glRotatef(90, 0F, 1F, 0F);
        break;
      case 2:
        GL11.glRotatef(0, 0F, 1F, 0F);
        break;
      case 3:
        GL11.glRotatef(180, 0F, 1F, 0F);
        break;
    }
    GL11.glTranslated(3, 0, 0);
    GlStateManager.enableLighting();
    GlStateManager.shadeModel(GL11.GL_SMOOTH);
    bindTexture(ResourceManager.hatch_tex);

    long time = System.currentTimeMillis();
    long startTime = te.state.isMovingState() ? te.sysTime : time;
    boolean reverse = te.state == IDoor.DoorState.OPEN || te.state == IDoor.DoorState.CLOSING;
    AnimationWrapper w = new AnimationWrapper(startTime, ResourceManager.silo_hatch_open);
    if (reverse) {
      w.reverse();
    }
    w.onEnd(new EndResult(EndType.STAY, null));
    bindTexture(ResourceManager.hatch_tex);
    ResourceManager.silo_hatch.controller.setAnim(w);
    ResourceManager.silo_hatch.renderAnimated(time);

    GlStateManager.shadeModel(GL11.GL_FLAT);
    GL11.glPopMatrix();
  }

  @Override
  public Item getItemForRenderer() {
    return Item.getItemFromBlock(ModBlocks.silo_hatch);
  }

  @Override
  public ItemRenderBase getRenderer(Item item) {
    return new ItemRenderBase() {
      @Override
      public void renderInventory() {
        GlStateManager.translate(15, -10, 10);
        GlStateManager.scale(2.5, 2.5, 2.5);
      }

      @Override
      public void renderCommon() {
        GlStateManager.translate(0.5F, 2, -2);
        GlStateManager.rotate(-120, 0, 1, 0);
        bindTexture(ResourceManager.hatch_tex);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        ResourceManager.silo_hatch.render();
        GlStateManager.shadeModel(GL11.GL_FLAT);
      }
    };
  }
}
