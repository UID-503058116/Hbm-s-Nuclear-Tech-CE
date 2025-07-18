package com.hbm.handler;

import com.hbm.items.armor.ItemArmorMod;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.UUID;

public class ArmorModHandler {

	public static final int helmet_only = 0;
	public static final int plate_only = 1;
	public static final int legs_only = 2;
	public static final int boots_only = 3;
	public static final int servos = 4;
	public static final int cladding = 5;
	public static final int kevlar = 6;
	public static final int extra = 7;
	public static final int battery = 8;

	public static final int MOD_SLOTS = 9;
	
	public static final UUID[] UUIDs = new UUID[] {
			UUID.fromString("8d6e5c77-133e-4056-9c80-a9e42a1a0b65"),
			UUID.fromString("b1b7ee0e-1d14-4400-8037-f7f2e02f21ca"),
			UUID.fromString("30b50d2a-4858-4e5b-88d4-3e3612224238"),
			UUID.fromString("426ee0d0-7587-4697-aaef-4772ab202e78")
	};
	
	public static final UUID[] fixedUUIDs = new UUID[] {
			UUID.fromString("e572caf4-3e65-4152-bc79-c4d4048cbd29"),
			UUID.fromString("bed30902-8a6a-4769-9f65-2a9b67469fff"),
			UUID.fromString("baebf7b3-1eda-4a14-b233-068e2493e9a2"),
			UUID.fromString("28016c1b-d992-4324-9409-a9f9f0ffb85c")
	};
	
	//The key for the NBTTagCompound that holds the armor mods
	public static final String MOD_COMPOUND_KEY = "ntm_armor_mods";
	//The key for the specific slot inside the armor mod NBT Tag
	public static final String MOD_SLOT_KEY = "mod_slot_";

	/**
	 * Checks if a mod can be applied to an armor piece
	 * Needs to be used to prevent people from inserting invalid items into the armor table
	 * @param armor
	 * @param mod
	 * @return
	 */
	public static boolean isApplicable(@NotNull ItemStack armor, @NotNull ItemStack mod) {
		
		if(!(armor.getItem() instanceof ItemArmor))
			return false;
		
		if(!(mod.getItem() instanceof ItemArmorMod aMod))
			return false;
		
		EntityEquipmentSlot type = ((ItemArmor)armor.getItem()).armorType;

        return (type == EntityEquipmentSlot.HEAD && aMod.helmet) || (type == EntityEquipmentSlot.CHEST && aMod.chestplate) || (type == EntityEquipmentSlot.LEGS && aMod.leggings) || (type == EntityEquipmentSlot.FEET && aMod.boots);
	}
	
	/**
	 * Applies an mod to the given armor piece
	 * Make sure to check for applicability first
	 * Will override present mods so make sure to only use unmodded armor pieces
	 * @param armor
	 * @param mod
	 */
	public static void applyMod(ItemStack armor, ItemStack mod) {

		if(mod == null || mod.isEmpty() || armor == null || armor.isEmpty()) return;
		
		if(!armor.hasTagCompound())
			armor.setTagCompound(new NBTTagCompound());
		
		NBTTagCompound nbt = armor.getTagCompound();
		
		if(!nbt.hasKey(MOD_COMPOUND_KEY))
			nbt.setTag(MOD_COMPOUND_KEY, new NBTTagCompound());
		
		NBTTagCompound mods = nbt.getCompoundTag(MOD_COMPOUND_KEY);
		
		ItemArmorMod aMod = (ItemArmorMod)mod.getItem();
		int slot = aMod.type;
		
		NBTTagCompound cmp = new NBTTagCompound();
		mod.writeToNBT(cmp);
		
		mods.setTag(MOD_SLOT_KEY + slot, cmp);
	}
	
	/**
	 * Removes the mod from the given slot
	 * @param armor
	 * @param slot
	 */
	public static void removeMod(ItemStack armor, int slot) {
		
		if(armor == null)
			return;
		
		if(!armor.hasTagCompound())
			armor.setTagCompound(new NBTTagCompound());
		
		NBTTagCompound nbt = armor.getTagCompound();
		
		if(!nbt.hasKey(MOD_COMPOUND_KEY))
			nbt.setTag(MOD_COMPOUND_KEY, new NBTTagCompound());
		
		NBTTagCompound mods = nbt.getCompoundTag(MOD_COMPOUND_KEY);
		mods.removeTag(MOD_SLOT_KEY + slot);
		
		if(mods.isEmpty())
			clearMods(armor);
	}
	
	/**
	 * Removes ALL mods
	 * Should be used when the armor piece is put in the armor table slot AFTER the armor pieces have been separated
	 * @param armor
	 */
	public static void clearMods(ItemStack armor) {
		
		if(!armor.hasTagCompound())
			return;
		
		NBTTagCompound nbt = armor.getTagCompound();
		nbt.removeTag(MOD_COMPOUND_KEY);
		if(nbt.isEmpty())
			armor.setTagCompound(null);
	}
	
	/**
	 * Does what the name implies
	 * @param armor
	 * @return
	 */
	public static boolean hasMods(ItemStack armor) {
		
		if(!armor.hasTagCompound())
			return false;
		
		return armor.getTagCompound().hasKey(MOD_COMPOUND_KEY);
	}
	
	public static ItemStack[] pryMods(ItemStack armor) {
		
		ItemStack[] slots = new ItemStack[MOD_SLOTS];

		if(!hasMods(armor)){
			Arrays.fill(slots, ItemStack.EMPTY);
			return slots;
		}
		
		NBTTagCompound nbt = armor.getTagCompound();
		NBTTagCompound mods = nbt.getCompoundTag(MOD_COMPOUND_KEY);
		
		for(int i = 0; i < MOD_SLOTS; i++) {
			
			NBTTagCompound cmp = mods.getCompoundTag(MOD_SLOT_KEY + i);
			
			ItemStack stack = new ItemStack(cmp);
			
			slots[i] = stack;
		}
		
		return slots;
	}
	
	public static ItemStack pryMod(ItemStack armor, int slot){
		if(!hasMods(armor))
			return ItemStack.EMPTY;
		NBTTagCompound nbt = armor.getTagCompound();
		NBTTagCompound mods = nbt.getCompoundTag(MOD_COMPOUND_KEY);
		NBTTagCompound cmp = mods.getCompoundTag(MOD_SLOT_KEY + slot);

        return new ItemStack(cmp);
	}
}