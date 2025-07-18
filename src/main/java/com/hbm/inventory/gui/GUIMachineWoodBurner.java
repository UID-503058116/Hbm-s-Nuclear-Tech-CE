package com.hbm.inventory.gui;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.opengl.GL11;

import com.hbm.inventory.container.ContainerMachineWoodBurner;
import com.hbm.lib.RefStrings;
import com.hbm.packet.PacketDispatcher;
import com.hbm.packet.toserver.NBTControlPacket;
import com.hbm.tileentity.machine.TileEntityMachineWoodBurner;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class GUIMachineWoodBurner extends GuiInfoContainer {
	
	private TileEntityMachineWoodBurner burner;
	private final ResourceLocation texture = new ResourceLocation(RefStrings.MODID + ":textures/gui/generators/gui_wood_burner_alt.png");

	public GUIMachineWoodBurner(InventoryPlayer invPlayer, TileEntityMachineWoodBurner tedf) {
		super(new ContainerMachineWoodBurner(invPlayer, tedf));
		burner = tedf;
		
		this.xSize = 176;
		this.ySize = 186;
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float f) {
		super.drawScreen(mouseX, mouseY, f);
		this.drawElectricityInfo(this, mouseX, mouseY, guiLeft + 143, guiTop + 18, 16, 34, burner.power, burner.maxPower);

		if(this.mc.player.inventory.getItemStack() == ItemStack.EMPTY) {
			
			Slot slot = (Slot) this.inventorySlots.inventorySlots.get(0);
			if(this.isMouseOverSlot(slot, mouseX, mouseY) && !slot.getHasStack()) {
				List<String> bonuses = burner.burnModule.getDesc();
				if(!bonuses.isEmpty()) {
					this.drawHoveringText(bonuses, mouseX, mouseY);
				}
			}
		}
		
		if(burner.liquidBurn) burner.tank.renderTankInfo(this, mouseX, mouseY, guiLeft + 80, guiTop + 18, 16, 52);
		
		if(!burner.liquidBurn && guiLeft + 16 <= mouseX && guiLeft + 16 + 8 > mouseX && guiTop + 17 < mouseY && guiTop + 17 + 54 >= mouseY) {
			drawHoveringText(Arrays.asList(new String[] { (burner.burnTime / 20) + "s" }), mouseX, mouseY);
		}
		
		if(guiLeft + 53 <= mouseX && guiLeft + 53 + 16 > mouseX && guiTop + 17 < mouseY && guiTop + 17 + 15 >= mouseY) {
			drawHoveringText(Arrays.asList(new String[] { burner.isOn ? TextFormatting.GREEN + "ON" : TextFormatting.RED + "OFF" }), mouseX, mouseY);
		}
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);

		if(guiLeft + 53 <= mouseX && guiLeft + 53 + 16 > mouseX && guiTop + 17 < mouseY && guiTop + 17 + 15 >= mouseY) {
			mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
			NBTTagCompound data = new NBTTagCompound();
			data.setBoolean("toggle", false);
			PacketDispatcher.wrapper.sendToServer(new NBTControlPacket(data, burner.getPos()));
		}

		if(guiLeft + 46 <= mouseX && guiLeft + 46 + 30 > mouseX && guiTop + 37 < mouseY && guiTop + 37 + 14 >= mouseY) {
			mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
			NBTTagCompound data = new NBTTagCompound();
			data.setBoolean("switch", false);
			PacketDispatcher.wrapper.sendToServer(new NBTControlPacket(data, burner.getPos()));
		}
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int i, int j) {
		String name = this.burner.hasCustomInventoryName() ? this.burner.getInventoryName() : I18n.format(this.burner.getInventoryName());
		
		this.fontRenderer.drawString(name, 70 - this.fontRenderer.getStringWidth(name) / 2, 6, 0xffffff);
		this.fontRenderer.drawString(I18n.format("container.inventory"), 8, this.ySize - 96 + 2, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float interp, int x, int y) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		
		if(burner.liquidBurn) {
			drawTexturedModalRect(guiLeft + 16, guiTop + 17, 176, 52, 60, 54);
			drawTexturedModalRect(guiLeft + 79, guiTop + 17, 176, 106, 36, 54);
		}
		
		if(burner.isOn) {
			drawTexturedModalRect(guiLeft + 53, guiTop + 17, 196, 0, 16, 15);
		}
		
		int p = (int) (burner.power * 34 / burner.maxPower);
		drawTexturedModalRect(guiLeft + 143, guiTop + 52 - p, 176, 52 - p, 16, p);
		
		if(burner.maxBurnTime > 0 && !burner.liquidBurn) {
			int b = (int) (burner.burnTime * 52 / burner.maxBurnTime);
			drawTexturedModalRect(guiLeft + 17, guiTop + 70 - b, 192, 52 - b, 4, b);
		}
		
		if(burner.liquidBurn) burner.tank.renderTank(guiLeft + 80, guiTop + 70, this.zLevel, 16, 52);
	}
}
