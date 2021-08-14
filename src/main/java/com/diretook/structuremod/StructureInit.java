package com.diretook.structuremod;

import java.util.HashMap;
import java.util.Map;

import com.diretook.structuremod.structures.Example_Structures;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.StructureSettings;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.StructureFeatureConfiguration;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class StructureInit 
{

	public static final DeferredRegister<StructureFeature<?>> STRUCTURES = DeferredRegister.create(ForgeRegistries.STRUCTURE_FEATURES, StructureMod.MOD_ID);

	public static final RegistryObject<StructureFeature<NoneFeatureConfiguration>> EXAMPLE_STRUCTURE = STRUCTURES.register("example_structure", 
			() -> (new Example_Structures(NoneFeatureConfiguration.CODEC)));
	

	public static void setupStructures() {
		
		setupMapSpacingAndLand(StructureInit.EXAMPLE_STRUCTURE.get(), new StructureFeatureConfiguration(10, 5, 4867246), true);
	}
	
	public static <F extends StructureFeature<?>> void setupMapSpacingAndLand(F structure, StructureFeatureConfiguration structureFeatureConfiguration, boolean transformSurroundingLand)
	{
		StructureFeature.STRUCTURES_REGISTRY.put(structure.getRegistryName().toString(), structure);
		
		if(transformSurroundingLand)
		{
			StructureFeature.NOISE_AFFECTING_FEATURES = 
					ImmutableList.<StructureFeature<?>>builder().addAll(StructureFeature.NOISE_AFFECTING_FEATURES).add(structure).build();
		}
		
		StructureSettings.DEFAULTS = ImmutableMap.<StructureFeature<?>, StructureFeatureConfiguration>builder()
				.putAll(StructureSettings.DEFAULTS).put(structure, structureFeatureConfiguration).build();
		
		BuiltinRegistries.NOISE_GENERATOR_SETTINGS.entrySet().forEach(settings -> {
			Map<StructureFeature<?>, StructureFeatureConfiguration> structureMap = settings.getValue().structureSettings().structureConfig();
			
			
            // structureConfig requires AccessTransformer  (See resources/META-INF/accesstransformer.cfg)
			if(structureMap instanceof ImmutableMap) 
			{
				Map<StructureFeature<?>, StructureFeatureConfiguration> tempMap = new HashMap<>(structureMap);
				tempMap.put(structure, structureFeatureConfiguration);
				settings.getValue().structureSettings().structureConfig = tempMap;
			}
			else
			{
				structureMap.put(structure, structureFeatureConfiguration);
			}
		});
	}
	
	// ConfiguredStructures
	
	public static ConfiguredStructureFeature<?, ?> CONFIGURED_EXAMPLE_STRUCTURE = StructureInit.EXAMPLE_STRUCTURE.get().configured(FeatureConfiguration.NONE);
		
	
	public static void registerConfiguredStructures() {
		System.out.println("REACHED HERE");
		Registry<ConfiguredStructureFeature<?, ?>> registry = BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE;
		Registry.register(registry, new ResourceLocation(StructureMod.MOD_ID, "configured_example_structure"), CONFIGURED_EXAMPLE_STRUCTURE);
		
		FlatLevelGeneratorSettings.STRUCTURE_FEATURES.put(StructureInit.EXAMPLE_STRUCTURE.get(), CONFIGURED_EXAMPLE_STRUCTURE);
	}
}