package com.arcanatech;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.BlockPos;
public class ArcanaGeneratorBlockEntity extends BlockEntity{
 public static final BlockEntityType<ArcanaGeneratorBlockEntity> TYPE=
  BlockEntityType.Builder.create(ArcanaGeneratorBlockEntity::new,ArcanaTech.ARCANE_GENERATOR).build(null);
 private int energy=0;
 public ArcanaGeneratorBlockEntity(BlockPos p,BlockState s){super(TYPE,p,s);}
 public int getEnergy(){return energy;}
 public void addEnergy(int n){energy=Math.min(10000,energy+n);markDirty();}
 public CompoundTag toTag(CompoundTag t){super.toTag(t);t.putInt("ArcaneEnergy",energy);return t;}
 public void fromTag(BlockState s,CompoundTag t){super.fromTag(s,t);energy=Math.max(0,Math.min(10000,t.getInt("ArcaneEnergy")));}
}