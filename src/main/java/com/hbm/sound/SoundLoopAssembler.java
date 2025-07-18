package com.hbm.sound;

import com.hbm.tileentity.machine.TileEntityMachineAssembler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundEvent;

import java.util.ArrayList;
import java.util.List;

public class SoundLoopAssembler extends SoundLoopMachine {
	
	public static List<SoundLoopAssembler> list = new ArrayList<SoundLoopAssembler>();

	public SoundLoopAssembler(SoundEvent path, TileEntity te) {
		super(path, te);
		list.add(this);
		
	}

	@Override
	public void update() {
		super.update();
		
		if(te instanceof TileEntityMachineAssembler) {
			TileEntityMachineAssembler drill = (TileEntityMachineAssembler)te;
			
			if(this.volume != 3)
				volume = 3;
			
			if(!drill.isProgressing)
				this.donePlaying = true;
		}
	}
}