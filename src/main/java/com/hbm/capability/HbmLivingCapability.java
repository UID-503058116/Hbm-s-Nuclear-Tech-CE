package com.hbm.capability;

import com.hbm.capability.HbmLivingProps.ContaminationEffect;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class HbmLivingCapability {

	public interface IEntityHbmProps {
		float getRads();
		void setRads(float rads);
		void increaseRads(float rads);
		void decreaseRads(float rads);

		float getNeutrons();
		void setNeutrons(float rads);

		float getRadsEnv();
		void setRadsEnv(float rads);

		float getRadBuf();
		void setRadBuf(float buf);

		float getDigamma();
		void setDigamma(float dig);
		void increaseDigamma(float dig);
		void decreaseDigamma(float dig);

		int getAsbestos();
		void setAsbestos(int asbestos);

		int getBlacklung();
		void setBlacklung(int blacklung);

		int getBombTimer();
		void setBombTimer(int bombTimer);

		int getContagion();
		void setContagion(int cont);

		int getOil();
		void setOil(int time);

		List<HbmLivingProps.ContaminationEffect> getContaminationEffectList();

		void saveNBTData(NBTTagCompound tag);
		void loadNBTData(NBTTagCompound tag);
	}

	public static class EntityHbmProps implements IEntityHbmProps {

		public static final Callable<IEntityHbmProps> FACTORY = EntityHbmProps::new;

		private float rads = 0;
		private float neutrons = 0;
		private float envRads = 0;
		private float radBuf = 0;
		private float digamma = 0;
		private int asbestos = 0;
		public static final int maxAsbestos = 60 * 60 * 20;
		private int blacklung;
		public static final int maxBlacklung = 60 * 60 * 20;
		private int bombTimer;
		private int contagion;
		private int oil;
		private final List<HbmLivingProps.ContaminationEffect> contamination = new ArrayList<>();

		@Override
		public float getRads() {
			return rads;
		}

		@Override
		public void setRads(float rads) {
			this.rads = MathHelper.clamp(rads, 0, 2500);
		}

		@Override
		public float getNeutrons() {
			return neutrons;
		}

		@Override
		public void setNeutrons(float neutrons) {
			this.neutrons = Math.max(neutrons, 0);
		}

		@Override
		public void increaseRads(float rads){
			this.rads = MathHelper.clamp(this.rads + rads, 0, 2500);
		}

		@Override
		public void decreaseRads(float rads){
			this.rads = MathHelper.clamp(this.rads - rads, 0, 2500);
		}

		@Override
		public float getRadsEnv(){
			return envRads;
		}

		@Override
		public void setRadsEnv(float rads){
			envRads = rads;
		}

		@Override
		public float getRadBuf(){
			return radBuf;
		}

		@Override
		public void setRadBuf(float buf){
			radBuf = buf;
		}

		@Override
		public float getDigamma(){
			return digamma;
		}

		@Override
		public void setDigamma(float dig){
			digamma = dig;
		}

		@Override
		public void increaseDigamma(float dig){
			this.digamma = MathHelper.clamp(this.digamma + dig, 0, 1000);
		}

		@Override
		public void decreaseDigamma(float dig){
			this.digamma = MathHelper.clamp(this.digamma - dig, 0, 1000);
		}

		@Override
		public int getAsbestos(){
			return asbestos;
		}

		@Override
		public void setAsbestos(int asbestos){
			this.asbestos = asbestos;
		}

		@Override
		public int getBlacklung(){
			return blacklung;
		}

		@Override
		public void setBlacklung(int blacklung){
			this.blacklung = blacklung;
		}

		@Override
		public int getBombTimer(){
			return bombTimer;
		}

		@Override
		public void setBombTimer(int bombTimer){
			this.bombTimer = bombTimer;
		}

		@Override
		public int getContagion(){
			return contagion;
		}

		@Override
		public void setContagion(int cont){
			contagion = cont;
		}

		@Override
		public int getOil() { return oil; }

		@Override public void setOil(int time) { this.oil = time; }


		@Override
		public List<HbmLivingProps.ContaminationEffect> getContaminationEffectList(){
			return contamination;
		}

		@Override
		public void saveNBTData(NBTTagCompound tag){
			tag.setFloat("rads", getRads());
			tag.setFloat("neutrons", getNeutrons());
			tag.setFloat("envRads", getRadsEnv());
			tag.setFloat("radBuf", getRadBuf());
			tag.setFloat("digamma", getDigamma());
			tag.setInteger("asbestos", getAsbestos());
			tag.setInteger("blacklung", blacklung);
			tag.setInteger("bombtimer", bombTimer);
			tag.setInteger("contagion", contagion);
			tag.setInteger("oil", getOil());
			tag.setInteger("conteffectsize", contamination.size());
			for(int i = 0; i < contamination.size(); i ++){
				contamination.get(i).save(tag, i);
			}
		}

		@Override
		public void loadNBTData(NBTTagCompound tag){
			setRads(tag.getFloat("rads"));
			setNeutrons(tag.getFloat("neutrons"));
			setRadsEnv(tag.getFloat("envRads"));
			setRadBuf(tag.getFloat("radBuf"));
			setDigamma(tag.getFloat("digamma"));
			setAsbestos(tag.getInteger("asbestos"));
			setBlacklung(tag.getInteger("blacklung"));
			setBombTimer(tag.getInteger("bombtimer"));
			setContagion(tag.getInteger("contagion"));
			setOil(tag.getInteger("oil"));
			contamination.clear();
			for(int i = 0; i < tag.getInteger("conteffectsize"); i ++){
				contamination.add(HbmLivingProps.ContaminationEffect.load(tag, i));
			}
		}
	}

	public static class EntityHbmPropsStorage implements IStorage<IEntityHbmProps>{

		@Override
		public NBTBase writeNBT(Capability<IEntityHbmProps> capability, IEntityHbmProps instance, EnumFacing side) {
			NBTTagCompound tag = new NBTTagCompound();
			instance.saveNBTData(tag);
			return tag;
		}

		@Override
		public void readNBT(Capability<IEntityHbmProps> capability, IEntityHbmProps instance, EnumFacing side, NBTBase nbt) {
			if(nbt instanceof NBTTagCompound){
				instance.loadNBTData((NBTTagCompound)nbt);
			}
		}

	}

	public static class EntityHbmPropsProvider implements ICapabilitySerializable<NBTBase> {

		public static final IEntityHbmProps DUMMY = new IEntityHbmProps(){
			@Override
			public float getRads() {
				return 0;
			}
			@Override
			public void setRads(float rads) {
			}
			@Override
			public float getNeutrons() {
				return 0;
			}
			@Override
			public void setNeutrons(float neutrons) {
			}
			@Override
			public void increaseRads(float rads) {
			}
			@Override
			public void decreaseRads(float rads) {
			}
			@Override
			public float getRadsEnv(){
				return 0;
			}
			@Override
			public void setRadsEnv(float rads){
			}
			@Override
			public float getRadBuf(){
				return 0;
			}
			@Override
			public void setRadBuf(float buf){
			}
			@Override
			public float getDigamma(){
				return 0;
			}
			@Override
			public void setDigamma(float dig){
			}
			@Override
			public void increaseDigamma(float dig){
			}
			@Override
			public void decreaseDigamma(float dig){
			}
			@Override
			public int getAsbestos(){
				return 0;
			}
			@Override
			public void setAsbestos(int asbestos){
			}
			@Override
			public void saveNBTData(NBTTagCompound tag){
			}
			@Override
			public void loadNBTData(NBTTagCompound tag){
			}
			@Override
			public List<ContaminationEffect> getContaminationEffectList(){
				return new ArrayList<>(0);
			}
			@Override
			public int getBlacklung(){
				return 0;
			}
			@Override
			public void setBlacklung(int blacklung){
			}
			@Override
			public int getBombTimer(){
				return 0;
			}
			@Override
			public void setBombTimer(int bombTimer){
			}
			@Override
			public int getContagion(){
				return 0;
			}
			@Override
			public void setContagion(int cont){
			}
			@Override
			public int getOil(){ return 0; }
			@Override
			public void setOil(int cont){ }
		};
		
		@CapabilityInject(IEntityHbmProps.class)
		public static final Capability<IEntityHbmProps> ENT_HBM_PROPS_CAP = null;

		private final IEntityHbmProps instance = ENT_HBM_PROPS_CAP.getDefaultInstance();

		@Override
		public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
			return capability == ENT_HBM_PROPS_CAP;
		}

		@Override
		public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
			return capability == ENT_HBM_PROPS_CAP ? ENT_HBM_PROPS_CAP.cast(this.instance) : null;
		}

		@Override
		public NBTBase serializeNBT() {
			return ENT_HBM_PROPS_CAP.getStorage().writeNBT(ENT_HBM_PROPS_CAP, instance, null);
		}

		@Override
		public void deserializeNBT(NBTBase nbt) {
			ENT_HBM_PROPS_CAP.getStorage().readNBT(ENT_HBM_PROPS_CAP, instance, null, nbt);
		}
	}
}