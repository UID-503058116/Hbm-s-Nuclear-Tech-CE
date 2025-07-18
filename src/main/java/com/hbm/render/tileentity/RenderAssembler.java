package com.hbm.render.tileentity;

import com.hbm.blocks.ModBlocks;
import com.hbm.inventory.recipes.AssemblerRecipes;
import com.hbm.main.ResourceManager;
import com.hbm.render.item.ItemRenderBase;
import com.hbm.tileentity.machine.TileEntityMachineAssembler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.ForgeHooksClient;
import org.lwjgl.opengl.GL11;

public class RenderAssembler extends TileEntitySpecialRenderer<TileEntityMachineAssembler>
    implements IItemRendererProvider {

  @Override
  public boolean isGlobalRenderer(TileEntityMachineAssembler te) {
    return true;
  }

  @Override
  public void render(
      TileEntityMachineAssembler assembler,
      double x,
      double y,
      double z,
      float partialTicks,
      int destroyStage,
      float alpha) {
    // RenderHelper.renderFlashLight(start, start.add(-20, 0, 0), 20, 1, ResourceManager.fl_cookie,
    // partialTicks);
    // FlashlightRenderer.addFlashlight(start, start.add(-20, 0, 0), 20, 20,
    // ResourceManager.fl_cookie, true, true);
    // LightRenderer.addPointLight(start, new Vec3d(1, 0.4, 0.1), 10);
    GL11.glPushMatrix();
    GL11.glTranslated(x + 0.5D, y, z + 0.5D);
    GL11.glEnable(GL11.GL_LIGHTING);
    GL11.glDisable(GL11.GL_CULL_FACE);
    GL11.glRotatef(180, 0F, 1F, 0F);
    // GL11.glPushMatrix();
    // GL11.glTranslated(0, 5, 0);
    // GlStateManager.bindTexture(RenderHelper.deferredNormalTex);
    // ResourceManager.test.draw();
    // GL11.glPopMatrix();
    switch (assembler.getBlockMetadata()) {
      case 14:
        GL11.glRotatef(180, 0F, 1F, 0F);
        GL11.glTranslated(0.5D, 0.0D, -0.5D);
        break;
      case 13:
        GL11.glRotatef(270, 0F, 1F, 0F);
        GL11.glTranslated(0.5D, 0.0D, -0.5D);
        break;
      case 15:
        GL11.glRotatef(0, 0F, 1F, 0F);
        GL11.glTranslated(0.5D, 0.0D, -0.5D);
        break;
      case 12:
        GL11.glRotatef(90, 0F, 1F, 0F);
        GL11.glTranslated(0.5D, 0.0D, -0.5D);
        break;
    }

    bindTexture(ResourceManager.assembler_body_tex);
    ResourceManager.assembler_body.renderAll();

    if (assembler.recipe != -1) {
      GL11.glPushMatrix();
      GL11.glTranslated(-1, 0.875, 0);

      try {
        ItemStack stack = AssemblerRecipes.recipeList.get(assembler.recipe).toStack();

        GL11.glTranslated(1, 0, 1);
        if (!(stack.getItem() instanceof ItemBlock)) {
          GL11.glRotatef(-90, 1F, 0F, 0F);
        } else {
          GL11.glScaled(0.5, 0.5, 0.5);
          GL11.glTranslated(0, -0.875, -2);
        }

        IBakedModel model =
            Minecraft.getMinecraft()
                .getRenderItem()
                .getItemModelWithOverrides(stack, assembler.getWorld(), null);
        model = ForgeHooksClient.handleCameraTransforms(model, TransformType.FIXED, false);
        Minecraft.getMinecraft()
            .getTextureManager()
            .bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        GL11.glTranslatef(0.0F, 1.0F - 0.0625F * 165 / 100, 0.0F);
        Minecraft.getMinecraft().getRenderItem().renderItem(stack, model);
      } catch (Exception ex) {
      }

      GL11.glPopMatrix();
    }
    /*GL11.glTranslated(-0.5, 3.6, -0.5);
    bindTexture(ResourceManager.hatch_tex);
    AnimationWrapper w = new AnimationWrapper(0, ResourceManager.silo_hatch_open);
    ResourceManager.silo_hatch.controller.setAnim(w);
    GlStateManager.shadeModel(GL11.GL_SMOOTH);
    ResourceManager.silo_hatch.renderAnimated(5000);
    GlStateManager.shadeModel(GL11.GL_FLAT);*/

    GL11.glPopMatrix();

    renderSlider(assembler, x, y, z, partialTicks);
  }

  public void renderSlider(
      TileEntityMachineAssembler tileEntity, double x, double y, double z, float f) {
    GL11.glPushMatrix();
    GL11.glTranslated(x, y, z);
    GlStateManager.enableLighting();
    GlStateManager.disableCull();
    GL11.glRotatef(180, 0F, 1F, 0F);
    switch (tileEntity.getBlockMetadata()) {
      case 14:
        GL11.glTranslated(-1, 0, 0);
        GL11.glRotatef(180, 0F, 1F, 0F);
        break;
      case 13:
        GL11.glRotatef(270, 0F, 1F, 0F);
        break;
      case 15:
        GL11.glTranslated(0, 0, -1);
        GL11.glRotatef(0, 0F, 1F, 0F);
        break;
      case 12:
        GL11.glTranslated(-1, 0, -1);
        GL11.glRotatef(90, 0F, 1F, 0F);
        break;
    }

    bindTexture(ResourceManager.assembler_slider_tex);

    int offset = (int) (System.currentTimeMillis() % 5000) / 5;

    if (offset > 500) offset = 500 - (offset - 500);

    TileEntityMachineAssembler assembler = (TileEntityMachineAssembler) tileEntity;

    if (assembler.isProgressing) GL11.glTranslated(offset * 0.003 - 0.75, 0, 0);

    ResourceManager.assembler_slider.renderAll();

    bindTexture(ResourceManager.assembler_arm_tex);

    double sway = (System.currentTimeMillis() % 2000) / 2;

    sway = Math.sin(sway / Math.PI / 50);

    if (assembler.isProgressing) GL11.glTranslated(0, 0, sway * 0.3);
    ResourceManager.assembler_arm.renderAll();

    GL11.glPopMatrix();

    renderCogs(tileEntity, x, y, z, f);
  }

  public void renderCogs(
      TileEntityMachineAssembler tileEntity, double x, double y, double z, float f) {
    GL11.glPushMatrix();
    GL11.glTranslated(x, y, z);
    GlStateManager.enableLighting();
    GlStateManager.disableCull();
    GL11.glRotatef(180, 0F, 1F, 0F);
    switch (tileEntity.getBlockMetadata()) {
      case 14:
        GL11.glTranslated(-1, 0, 0);
        GL11.glRotatef(180, 0F, 1F, 0F);
        break;
      case 13:
        GL11.glRotatef(270, 0F, 1F, 0F);
        break;
      case 15:
        GL11.glTranslated(0, 0, -1);
        GL11.glRotatef(0, 0F, 1F, 0F);
        break;
      case 12:
        GL11.glTranslated(-1, 0, -1);
        GL11.glRotatef(90, 0F, 1F, 0F);
        break;
    }

    bindTexture(ResourceManager.assembler_cog_tex);

    int rotation = (int) (System.currentTimeMillis() % (360 * 5)) / 5;

    TileEntityMachineAssembler assembler = (TileEntityMachineAssembler) tileEntity;

    if (!assembler.isProgressing) rotation = 0;

    GL11.glPushMatrix();
    GL11.glTranslated(-0.6, 0.75, 1.0625);
    GL11.glRotatef(-rotation, 0F, 0F, 1F);
    ResourceManager.assembler_cog.renderAll();
    GL11.glPopMatrix();

    GL11.glPushMatrix();
    GL11.glTranslated(0.6, 0.75, 1.0625);
    GL11.glRotatef(rotation, 0F, 0F, 1F);
    ResourceManager.assembler_cog.renderAll();
    GL11.glPopMatrix();

    GL11.glPushMatrix();
    GL11.glTranslated(-0.6, 0.75, -1.0625);
    GL11.glRotatef(-rotation, 0F, 0F, 1F);
    ResourceManager.assembler_cog.renderAll();
    GL11.glPopMatrix();

    GL11.glPushMatrix();
    GL11.glTranslated(0.6, 0.75, -1.0625);
    GL11.glRotatef(rotation, 0F, 0F, 1F);
    ResourceManager.assembler_cog.renderAll();
    GL11.glPopMatrix();

    GL11.glPopMatrix();
  }

  @Override
  public Item getItemForRenderer() {
    return Item.getItemFromBlock(ModBlocks.machine_assembler);
  }

  @Override
  public ItemRenderBase getRenderer(Item item) {
    return new ItemRenderBase() {
      public void renderInventory() {
        GlStateManager.scale(3.5, 3.5, 3.5);
      }

      public void renderCommon() {
        bindTexture(ResourceManager.assembler_body_tex);
        ResourceManager.assembler_body.renderAll();
        bindTexture(ResourceManager.assembler_slider_tex);
        ResourceManager.assembler_slider.renderAll();
        bindTexture(ResourceManager.assembler_arm_tex);
        ResourceManager.assembler_arm.renderAll();
        bindTexture(ResourceManager.assembler_cog_tex);
        GlStateManager.pushMatrix();
        GlStateManager.translate(-0.6, 0.75, 1.0625);
        ResourceManager.assembler_cog.renderAll();
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.6, 0.75, 1.0625);
        ResourceManager.assembler_cog.renderAll();
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        GlStateManager.translate(-0.6, 0.75, -1.0625);
        ResourceManager.assembler_cog.renderAll();
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.6, 0.75, -1.0625);
        ResourceManager.assembler_cog.renderAll();
        GlStateManager.popMatrix();
      }
    };
  }
}
