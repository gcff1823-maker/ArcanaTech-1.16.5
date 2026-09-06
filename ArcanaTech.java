package com.arcanatech;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.item.*;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.*;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import java.util.*;

public class ArcanaTech implements ModInitializer {
 public static final String MOD_ID="arcanatech";
 public static final int MAX_MANA=100;
 private static final Map<UUID,Integer> MANA=new HashMap<>();

 public static final ItemGroup GROUP=new ItemGroup("arcanatech") { @Override public ItemStack createIcon(){ return new ItemStack(ARCANE_STAFF); } };

 public static final Item MANA_CRYSTAL=new Item(new FabricItemSettings().group(GROUP).maxCount(64).rarity(Rarity.UNCOMMON));
 public static final Item ARCANE_STAFF=new ArcaneStaff(new FabricItemSettings().group(GROUP).maxCount(1).rarity(Rarity.RARE));
 public static final Item AWAKENING_SCROLL=new AwakeningScroll(new FabricItemSettings().group(GROUP).maxCount(16).rarity(Rarity.RARE));

 public static final Block ARCANE_ORE=new Block(AbstractBlock.Settings.of(Material.STONE).strength(3.0f,3.0f));
 public static final Block ARCANE_GENERATOR=new ArcaneGeneratorBlock(AbstractBlock.Settings.of(Material.STONE).strength(4.0f,4.0f));
 public static final Block ORE_PURIFIER=new OrePurifierBlock(AbstractBlock.Settings.of(Material.STONE).strength(3.5f,3.5f));

 @Override public void onInitialize(){
  item("mana_crystal",MANA_CRYSTAL); item("arcane_staff",ARCANE_STAFF); item("awakening_scroll",AWAKENING_SCROLL);
  block("arcane_ore",ARCANE_ORE); block("arcane_generator",ARCANE_GENERATOR); block("ore_purifier",ORE_PURIFIER);
  Registry.register(Registry.BLOCK_ENTITY_TYPE,id("arcane_generator"),ArcaneGeneratorBlockEntity.TYPE);

  ServerPlayConnectionEvents.JOIN.register((h,s,server)->{
   ServerPlayerEntity p=h.getPlayer(); MANA.putIfAbsent(p.getUuid(),MAX_MANA); StorySystem.start(p);
  });
  ServerPlayConnectionEvents.DISCONNECT.register((h,server)->MANA.remove(h.getPlayer().getUuid()));

  ServerTickEvents.END_SERVER_TICK.register(server->{
   if(server.getTicks()%20!=0)return;
   for(ServerPlayerEntity p:server.getPlayerManager().getPlayerList()){
    int m=MANA.getOrDefault(p.getUuid(),MAX_MANA);
    if(m<MAX_MANA)MANA.put(p.getUuid(),m+1);
   }
  });
  WorldGen.init();
  StoryNPCRegistry.init();
  StoryNPCSpawner.init();
 }
 static void item(String n,Item i){Registry.register(Registry.ITEM,id(n),i);}
 static void block(String n,Block b){Registry.register(Registry.BLOCK,id(n),b);Registry.register(Registry.ITEM,id(n),new BlockItem(b,new FabricItemSettings().group(GROUP)));}
 public static Identifier id(String p){return new Identifier(MOD_ID,p);}
 public static int mana(ServerPlayerEntity p){return MANA.getOrDefault(p.getUuid(),MAX_MANA);}
 public static boolean spend(ServerPlayerEntity p,int n){int m=mana(p);if(m<n)return false;MANA.put(p.getUuid(),m-n);return true;}

 public static class AwakeningScroll extends Item{
  public AwakeningScroll(Settings s){super(s);}
  public TypedActionResult<ItemStack> use(World w,net.minecraft.entity.player.PlayerEntity p,Hand h){
   if(!w.isClient&&p instanceof ServerPlayerEntity)StorySystem.useScroll((ServerPlayerEntity)p);
   return TypedActionResult.success(p.getStackInHand(h));
  }
 }
 public static class ArcaneStaff extends Item{
  public ArcaneStaff(Settings s){super(s);}
  public TypedActionResult<ItemStack> use(World w,net.minecraft.entity.player.PlayerEntity p,Hand h){
   if(!w.isClient&&p instanceof ServerPlayerEntity){
    ServerPlayerEntity q=(ServerPlayerEntity)p;
    if(!spend(q,20)){q.sendMessage(new LiteralText("§5[Магия] §fНедостаточно маны."),true);}
    else{
     for(net.minecraft.entity.Entity e:w.getOtherEntities(q,q.getBoundingBox().expand(4.0)))
      if(e instanceof net.minecraft.entity.LivingEntity)e.takeKnockback(.8,q.getX()-e.getX(),q.getZ()-e.getZ());
     q.sendMessage(new LiteralText("§5[Магия] §dИмпульс §7(-20 маны)"),true);
     if(StorySystem.getChapter(q)==2)StorySystem.advance(q);
    }
   }
   return TypedActionResult.success(p.getStackInHand(h));
  }
 }
}