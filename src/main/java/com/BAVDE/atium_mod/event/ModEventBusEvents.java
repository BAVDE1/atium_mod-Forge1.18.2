package com.BAVDE.atium_mod.event;

import com.BAVDE.atium_mod.AtiumMod;
import com.BAVDE.atium_mod.entity.ModEntityTypes;
import com.BAVDE.atium_mod.event.loot.DrownedDropLeather;
import com.BAVDE.atium_mod.event.loot.HuskDropLeather;
import com.BAVDE.atium_mod.event.loot.ZombieDropLeather;
import com.BAVDE.atium_mod.event.loot.ZombieVillagerDropLeather;
import com.BAVDE.atium_mod.particle.ModParticles;
import com.BAVDE.atium_mod.particle.custom.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

@Mod.EventBusSubscriber(modid = AtiumMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEventBusEvents {

    @SubscribeEvent
    public static void registerModifierSerializers(@NotNull final RegistryEvent.Register<GlobalLootModifierSerializer<?>> event) {
        event.getRegistry().registerAll(
                new ZombieDropLeather.Serializer().setRegistryName
                        (new ResourceLocation(AtiumMod.MOD_ID,"leather_from_zombie")),
                new HuskDropLeather.Serializer().setRegistryName
                        (new ResourceLocation(AtiumMod.MOD_ID,"leather_from_husk")),
                new DrownedDropLeather.Serializer().setRegistryName
                        (new ResourceLocation(AtiumMod.MOD_ID,"leather_from_drowned")),
                new ZombieVillagerDropLeather.Serializer().setRegistryName
                        (new ResourceLocation(AtiumMod.MOD_ID,"leather_from_zombie_villager"))
        );
    }

    @SubscribeEvent
    public static void registerRecipeTypes(final RegistryEvent.Register<RecipeSerializer<?>> event){
        //Registry.register(Registry.RECIPE_TYPE, InfusingTableRecipe.Type.ID, InfusingTableRecipe.Type.INSTANCE);
    }

    //register particles
    @SubscribeEvent
    public static void registerParticleFactories(final ParticleFactoryRegisterEvent event) {
        //register particles here
        Minecraft.getInstance().particleEngine.register(ModParticles.SNOWFLAKE_PARTICLES.get(),
                ModSnowflakeParticle.Provider::new);
        Minecraft.getInstance().particleEngine.register(ModParticles.BLINDNESS_PARTICLES.get(),
                BlindnessParticle.Provider::new);
        Minecraft.getInstance().particleEngine.register(ModParticles.DISORIENTED_PARTICLES.get(),
                DisorientedParticle.Provider::new);
        Minecraft.getInstance().particleEngine.register(ModParticles.FALLING_SMOKE_PARTICLES.get(),
                FallingSmokeParticle.Provider::new);
        Minecraft.getInstance().particleEngine.register(ModParticles.MOD_FLAME_PARTICLES.get(),
                ModFlameParticle.Provider::new);
        Minecraft.getInstance().particleEngine.register(ModParticles.FORCE_FIELD_PARTICLES.get(),
                ForceFieldParticle.Provider::new);
        Minecraft.getInstance().particleEngine.register(ModParticles.MOB_DETECTION_PARTICLES.get(),
                MobDetectionParticle.Provider::new);
        Minecraft.getInstance().particleEngine.register(ModParticles.ORE_DETECTION_PARTICLES.get(),
                OreDetectionParticle.Provider::new);
        Minecraft.getInstance().particleEngine.register(ModParticles.INFUSION_FLAME_PARTICLES.get(),
                InfusionFlameParticle.FlameProvider::new);
        Minecraft.getInstance().particleEngine.register(ModParticles.INFUSION_CRAFT_PARTICLES.get(),
                InfusionFlameParticle.CraftProvider::new);
    }

    //register entity renderers
    @SubscribeEvent
    public static void registryRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntityTypes.IRON_COIN_PROJECTILE.get(), ThrownItemRenderer::new);
        //event.registerEntityRenderer(ModEntityTypes.STEEL_COIN_PROJECTILE.get(), ThrownItemRenderer::new);
    }
}
