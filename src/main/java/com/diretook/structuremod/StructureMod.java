package com.diretook.structuremod;



import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mojang.serialization.Codec;

import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.levelgen.StructureSettings;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.StructureFeatureConfiguration;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(StructureMod.MOD_ID)
public class StructureMod
{
    // Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "structuremod";

    public StructureMod() {
    	// Register the setup method for modloading
    	IEventBus bus =  FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(EventPriority.HIGH, this::setup);
        
        IEventBus forgeBus = MinecraftForge.EVENT_BUS;
        
        forgeBus.addListener(EventPriority.NORMAL, this::addDimensionalSpacing);
        forgeBus.addListener(EventPriority.HIGH, this::biomeModification);
        
        // Register ourselves for server and other game events we are interested in
        forgeBus.register(this);
        
    }
    

    public void setup(final FMLCommonSetupEvent event) {
        
    	event.enqueueWork(() -> {
    		StructureInit.setupStructures();
    		StructureInit.registerConfiguredStructures();
    	});
    	
    	
    }
    
    public void biomeModification(final BiomeLoadingEvent event) 
    {
        
    	event.getGeneration().getStructures().add(() -> StructureInit.CONFIGURED_EXAMPLE_STRUCTURE);
        
    }
 
    
        
    private static Method GETCODEC_METHOD;
    public void addDimensionalSpacing(final WorldEvent.Load event) {
        
    	if(event.getWorld() instanceof ServerLevel) 
    	{
    		ServerLevel serverLevel = (ServerLevel)event.getWorld();
    		
    		try
    		{
    			if(GETCODEC_METHOD == null) GETCODEC_METHOD = ObfuscationReflectionHelper.findMethod(ChunkGenerator.class, "f_62136_");
    			ResourceLocation cgRL = Registry.CHUNK_GENERATOR.getKey((Codec<? extends ChunkGenerator>) GETCODEC_METHOD.invoke(serverLevel.getChunkSource().generator));
    			if(cgRL != null && cgRL.getNamespace().equals("terraforged")) return;
    		}
    		catch(Exception e) 
    		{
    			StructureMod.LOGGER.error("Was unable to check if " + serverLevel.dimension().location() + " is using Terraforged's ChunkGenerator.");
    		}
    		
            if(serverLevel.getChunkSource().getGenerator() instanceof FlatLevelSource &&
            		serverLevel.dimension().equals(Level.OVERWORLD)){
                    return;
                }
            Map<StructureFeature<?>, StructureFeatureConfiguration> tempMap = new HashMap<>(serverLevel.getChunkSource().generator.getSettings().structureConfig());
            tempMap.putIfAbsent(StructureInit.EXAMPLE_STRUCTURE.get(), StructureSettings.DEFAULTS.get(StructureInit.EXAMPLE_STRUCTURE.get()));
            serverLevel.getChunkSource().generator.getSettings().structureConfig = tempMap;
    	}
    }
    

}