package com.hbm.render.tileentity;

import com.hbm.render.NTMRenderHelper;
import com.hbm.tileentity.machine.TileEntityStructureMarker;
import com.hbm.world.FactoryTitanium;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

public class RenderStructureMarker extends TileEntitySpecialRenderer<TileEntityStructureMarker> {

	final float pixel = 1F / 16F;
	public static final TextureAtlasSprite[][] fac_ti = 
		{ 
		{ null, null }, 
		{ null, null }, 
		{ null, null } 
		};
	public static final TextureAtlasSprite[][] fusion =
		{ 
		{ null, null }, 
		{ null, null }, 
		{ null, null }, 
		{ null, null }, 
		{ null, null }, 
		{ null, null }, 
		{ null, null }, 
		};
	public static final TextureAtlasSprite[][] watz = 
		{ 
		{ null, null }, 
		{ null, null }, 
		{ null, null }, 
		{ null, null }, 
		{ null, null }, 
		{ null, null }, 
		{ null, null }, 
		{ null, null }, 
		};
	public static final TextureAtlasSprite[][] fwatz = 
		{ 
		{ null, null }, 
		{ null, null }, 
		{ null, null }, 
		{ null, null }, 
		{ null, null }, 
		{ null, null }, 
		{ null, null }, 
		};

	@Override
	public boolean isGlobalRenderer(TileEntityStructureMarker te) {
		return true;
	}
	
	@Override
	public void render(TileEntityStructureMarker te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		GL11.glPushMatrix();
		GL11.glTranslatef((float) x, (float) y, (float) z);
		GL11.glRotatef(180, 0F, 0F, 1F);

		GlStateManager.disableLighting();
		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
		GlStateManager.glBlendEquation(GL14.GL_FUNC_ADD);
		GlStateManager.color(0.5f, 0.25f, 1.0f, 1f);
		NTMRenderHelper.startDrawingTexturedQuads();
		this.renderBlocks((int) x, (int) y, (int) z, te.type, te.getBlockMetadata());
		NTMRenderHelper.draw();
		GlStateManager.enableLighting();
		GlStateManager.disableBlend();
		GL11.glPopMatrix();
	}

	public void renderBlocks(int x, int y, int z, int type, int meta) {
		bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		int offsetX = 0;
		int offsetZ = 0;
		if(type == 0) {
			//System.out.println(meta);
			if(meta == 1) {
				offsetZ = -1;
				offsetX = 3;
			}
			if(meta == 2) {
				offsetZ = -1;
				offsetX = -1;
			}
			if(meta == 3) {
				offsetX = 1;
				offsetZ = -3;
			}
			if(meta == 4) {
				offsetZ = 1;
				offsetX = 1;
			}
			

			GL11.glTranslatef(offsetX, 0, offsetZ);
			for(int a = 0; a < 3; a++) {
				for(int b = 0; b < 3; b++) {
					for(int c = 0; c < 3; c++) {

						int texture = -1;
						if(FactoryTitanium.array[b][a].substring(c, c + 1).equals("H")) {
							texture = 0;
						} else if(FactoryTitanium.array[b][a].substring(c, c + 1).equals("F")) {
							texture = 1;
						} else if(FactoryTitanium.array[b][a].substring(c, c + 1).equals("C")) {
							texture = 2;
						}
						if(texture >= 0) {
							renderSmolBlockAt(fac_ti[texture][0], fac_ti[texture][1], a, b, c);
						}
					}
				}
			}
		}
	}

	//TODO: remove that
	public void renderSmolBlockAt(TextureAtlasSprite loc1, TextureAtlasSprite loc2, int x, int y, int z) {
		// GL11.glTranslatef(x, y, z);
		GL11.glRotatef(180, 0F, 0F, 1F);
		NTMRenderHelper.addVertexWithUV(x + 1 - 11 * pixel / 2, y + 1 - 11 * pixel / 2, z + 1 - 11 * pixel / 2, loc2.getMaxU(), loc2.getMinV());
		NTMRenderHelper.addVertexWithUV(x + 11 * pixel / 2, y + 1 - 11 * pixel / 2, z + 1 - 11 * pixel / 2, loc2.getMinU(), loc2.getMinV());
		NTMRenderHelper.addVertexWithUV(x + 11 * pixel / 2, y + 11 * pixel / 2, z + 1 - 11 * pixel / 2, loc2.getMinU(), loc2.getMaxV());
		NTMRenderHelper.addVertexWithUV(x + 1 - 11 * pixel / 2, y + 11 * pixel / 2, z + 1 - 11 * pixel / 2, loc2.getMaxU(), loc2.getMaxV());

		NTMRenderHelper.addVertexWithUV(x + 1 - 11 * pixel / 2, y + 1 - 11 * pixel / 2, z + 11 * pixel / 2, loc2.getMaxU(), loc2.getMinV());
		NTMRenderHelper.addVertexWithUV(x + 1 - 11 * pixel / 2, y + 1 - 11 * pixel / 2, z + 1 - 11 * pixel / 2, loc2.getMinU(), loc2.getMinV());
		NTMRenderHelper.addVertexWithUV(x + 1 - 11 * pixel / 2, y + 11 * pixel / 2, z + 1 - 11 * pixel / 2, loc2.getMinU(), loc2.getMaxV());
		NTMRenderHelper.addVertexWithUV(x + 1 - 11 * pixel / 2, y + 11 * pixel / 2, z + 11 * pixel / 2, loc2.getMaxU(), loc2.getMaxV());

		NTMRenderHelper.addVertexWithUV(x + 11 * pixel / 2, y + 1 - 11 * pixel / 2, z + 11 * pixel / 2, loc2.getMaxU(), loc2.getMinV());
		NTMRenderHelper.addVertexWithUV(x + 1 - 11 * pixel / 2, y + 1 - 11 * pixel / 2, z + 11 * pixel / 2, loc2.getMinU(), loc2.getMinV());
		NTMRenderHelper.addVertexWithUV(x + 1 - 11 * pixel / 2, y + 11 * pixel / 2, z + 11 * pixel / 2, loc2.getMinU(), loc2.getMaxV());
		NTMRenderHelper.addVertexWithUV(x + 11 * pixel / 2, y + 11 * pixel / 2, z + 11 * pixel / 2, loc2.getMaxU(), loc2.getMaxV());

		NTMRenderHelper.addVertexWithUV(x + 11 * pixel / 2, y + 1 - 11 * pixel / 2, z + 1 - 11 * pixel / 2, loc2.getMaxU(), loc2.getMinV());
		NTMRenderHelper.addVertexWithUV(x + 11 * pixel / 2, y + 1 - 11 * pixel / 2, z + 11 * pixel / 2, loc2.getMinU(), loc2.getMinV());
		NTMRenderHelper.addVertexWithUV(x + 11 * pixel / 2, y + 11 * pixel / 2, z + 11 * pixel / 2, loc2.getMinU(), loc2.getMaxV());
		NTMRenderHelper.addVertexWithUV(x + 11 * pixel / 2, y + 11 * pixel / 2, z + 1 - 11 * pixel / 2, loc2.getMaxU(), loc2.getMaxV());

		NTMRenderHelper.addVertexWithUV(x + 1 - 11 * pixel / 2, y + 1 - 11 * pixel / 2, z + 11 * pixel / 2, loc1.getMaxU(), loc1.getMinV());
		NTMRenderHelper.addVertexWithUV(x + 11 * pixel / 2, y + 1 - 11 * pixel / 2, z + 11 * pixel / 2, loc1.getMinU(), loc1.getMinV());
		NTMRenderHelper.addVertexWithUV(x + 11 * pixel / 2, y + 1 - 11 * pixel / 2, z + 1 - 11 * pixel / 2, loc1.getMinU(), loc1.getMaxV());
		NTMRenderHelper.addVertexWithUV(x + 1 - 11 * pixel / 2, y + 1 - 11 * pixel / 2, z + 1 - 11 * pixel / 2, loc1.getMaxU(), loc1.getMaxV());

		NTMRenderHelper.addVertexWithUV(x + 11 * pixel / 2, y + 11 * pixel / 2, z + 11 * pixel / 2, loc1.getMaxU(), loc1.getMinV());
		NTMRenderHelper.addVertexWithUV(x + 1 - 11 * pixel / 2, y + 11 * pixel / 2, z + 11 * pixel / 2, loc1.getMinU(), loc1.getMinV());
		NTMRenderHelper.addVertexWithUV(x + 1 - 11 * pixel / 2, y + 11 * pixel / 2, z + 1 - 11 * pixel / 2, loc1.getMinU(), loc1.getMaxV());
		NTMRenderHelper.addVertexWithUV(x + 11 * pixel / 2, y + 11 * pixel / 2, z + 1 - 11 * pixel / 2, loc1.getMaxU(), loc1.getMaxV());

	}
}
