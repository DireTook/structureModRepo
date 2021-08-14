package com.diretook.structuremod.structures;

import java.util.List;

import org.apache.logging.log4j.Level;

import com.diretook.structuremod.StructureMod;
import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;

public class Example_Structures extends StructureFeature<NoneFeatureConfiguration> {

	public Example_Structures(Codec<NoneFeatureConfiguration> codec) 
	{
		super(codec);
	}

	@Override
	public StructureStartFactory<NoneFeatureConfiguration> getStartFactory() 
	{
		return Example_Structures.Start::new;
	}
	
	@Override
	public GenerationStep.Decoration step() 
	{
		return GenerationStep.Decoration.SURFACE_STRUCTURES;
	}
		
	private static final List<MobSpawnSettings.SpawnerData> STRUCTURE_MONSTERS = ImmutableList.of(
			new MobSpawnSettings.SpawnerData(EntityType.VINDICATOR, 100, 4, 9)
			);
	
	@Override
	public List<MobSpawnSettings.SpawnerData> getDefaultSpawnList() 
	{
		return STRUCTURE_MONSTERS;
	}
	
    private static final List<MobSpawnSettings.SpawnerData> STRUCTURE_CREATURES = ImmutableList.of(
            new MobSpawnSettings.SpawnerData(EntityType.SHEEP, 30, 10, 15),
            new MobSpawnSettings.SpawnerData(EntityType.RABBIT, 100, 1, 2)
    );
    
    @Override
    public List<MobSpawnSettings.SpawnerData> getDefaultCreatureSpawnList() {
        return STRUCTURE_CREATURES;
    }

	@Override
	protected boolean isFeatureChunk(ChunkGenerator chunkGenerator, BiomeSource biomeSource, long seed, WorldgenRandom chunkRandom, ChunkPos chunkPos, Biome biome, ChunkPos p_160461_,
			NoneFeatureConfiguration featureConfig, LevelHeightAccessor levelHeightAccessor) {
    
        int landHeight = chunkGenerator.getFirstOccupiedHeight(chunkPos.getMiddleBlockX(), chunkPos.getMiddleBlockZ(), Heightmap.Types.WORLD_SURFACE_WG, levelHeightAccessor);
        
        NoiseColumn columnOfBlocks = chunkGenerator.getBaseColumn(chunkPos.getMiddleBlockX(), chunkPos.getMiddleBlockZ(), levelHeightAccessor);
        
        BlockState topBlock = columnOfBlocks.getBlockState(chunkPos.getMiddleBlockPosition(landHeight).above(landHeight));
        
		return topBlock.getFluidState().isEmpty();
	}
    
	public static class Start extends StructureStart<NoneFeatureConfiguration> 
	{

		public Start(StructureFeature<NoneFeatureConfiguration> p_163595_, ChunkPos p_163596_, int p_163597_, long p_163598_) 
		{
			super(p_163595_, p_163596_, p_163597_, p_163598_);
		}

		@Override
		public void generatePieces(RegistryAccess p_163615_, ChunkGenerator p_163616_, StructureManager p_163617_,
				ChunkPos chunkPos, Biome p_163619_, NoneFeatureConfiguration p_163620_, LevelHeightAccessor p_163621_) 
		{
			
			Vec3i structureCenter = this.pieces.get(0).getBoundingBox().getCenter();
	            int xOffset = chunkPos.getMiddleBlockX() - structureCenter.getX();
	            int zOffset = chunkPos.getMiddleBlockZ() - structureCenter.getZ();
				for(StructurePiece structurePiece : this.pieces)
				{
					structurePiece.move(xOffset, 0, zOffset);
				}
	        this.createBoundingBox();
	        
            StructureMod.LOGGER.log(Level.DEBUG, "Example Structure at " +
                    this.pieces.get(0).getBoundingBox().minX() + " " +
                    this.pieces.get(0).getBoundingBox().minY() + " " +
                    this.pieces.get(0).getBoundingBox().minZ());
		}
	}
	
}
