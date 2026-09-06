package com.arcanatech.client;

import com.arcanatech.ArcanaTech;
import com.arcanatech.CustomNPCModel;
import com.arcanatech.StoryNPCRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.util.Identifier;

public class ArcanaTechClient implements ClientModInitializer {
    @Override public void onInitializeClient() {
        EntityRendererRegistry.INSTANCE.register(StoryNPCRegistry.STORY_NPC,
            (dispatcher, context) -> new BipedEntityRenderer<>(
                dispatcher, new CustomNPCModel<>(0.0F), 0.5F) {
                @Override protected Identifier getTexture(net.minecraft.entity.Entity entity) {
                    return ArcanaTech.id("textures/entity/story_npc.png");
                }
            });
    }
}