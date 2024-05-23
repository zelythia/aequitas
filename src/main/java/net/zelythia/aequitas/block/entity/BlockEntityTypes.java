package net.zelythia.aequitas.block.entity;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.zelythia.aequitas.Aequitas;
import net.zelythia.aequitas.block.AequitasBlocks;

public class BlockEntityTypes {


    public static final BlockEntityType<CraftingPedestalBlockEntity> CRAFTING_PEDESTAL_BLOCK_ENTITY;
    public static final BlockEntityType<SamplingPedestalBlockEntity> SAMPLING_PEDESTAL_BLOCK_ENTITY;


    public static final BlockEntityType<CollectionBowlBlockEntity> COLLECTION_BOWL_BLOCK_ENTITY_I;
    public static final BlockEntityType<CollectionBowlBlockEntity> COLLECTION_BOWL_BLOCK_ENTITY_II;
    public static final BlockEntityType<CollectionBowlBlockEntity> COLLECTION_BOWL_BLOCK_ENTITY_III;


    static {
        CRAFTING_PEDESTAL_BLOCK_ENTITY = register("crafting_pedestal", FabricBlockEntityTypeBuilder.create(CraftingPedestalBlockEntity::new, AequitasBlocks.CRAFTING_PEDESTAL).build());
        SAMPLING_PEDESTAL_BLOCK_ENTITY = register("sampling_pedestal", FabricBlockEntityTypeBuilder.create(SamplingPedestalBlockEntity::new, AequitasBlocks.SAMPLING_PEDESTAL).build());

        COLLECTION_BOWL_BLOCK_ENTITY_I = register("collection_bowl_1", FabricBlockEntityTypeBuilder.create((pos, state) -> new CollectionBowlBlockEntity(pos, state, 1), AequitasBlocks.COLLECTION_BOWL_I).build());
        COLLECTION_BOWL_BLOCK_ENTITY_II = register("collection_bowl_2", FabricBlockEntityTypeBuilder.create((pos, state) -> new CollectionBowlBlockEntity(pos, state, 9), AequitasBlocks.COLLECTION_BOWL_II).build());
        COLLECTION_BOWL_BLOCK_ENTITY_III = register("collection_bowl_3", FabricBlockEntityTypeBuilder.create((pos, state) -> new CollectionBowlBlockEntity(pos, state, 15), AequitasBlocks.COLLECTION_BOWL_III).build());
    }


    public static <T extends BlockEntity> BlockEntityType<T> register(String id, BlockEntityType<T> item) {
        return Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(Aequitas.MOD_ID, id), item);
    }

    public BlockEntityTypes() {
    }
}
