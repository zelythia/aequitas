package net.zelythia.aequitas;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.resource.ResourceType;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.zelythia.aequitas.block.CraftingPedestalBlock;
import net.zelythia.aequitas.block.PedestalBlock;
import net.zelythia.aequitas.block.SamplingPedestalBlock;
import net.zelythia.aequitas.block.entity.CraftingPedestalBlockEntity;
import net.zelythia.aequitas.block.entity.SamplingPedestalBlockEntity;
import net.zelythia.aequitas.networking.EssencePacket;
import net.zelythia.aequitas.networking.NetworkingHandler;
import net.zelythia.aequitas.screen.CraftingPedestalScreenHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;

public class Aequitas implements ModInitializer {

    public static final String MOD_ID = "aequitas";
    public static final Logger LOGGER = LogManager.getLogger("Aequitas");

    public static final ItemGroup ITEM_GROUP;


    public static final Identifier PEDESTAL = new Identifier(MOD_ID, "pedestal");
    public static final Block PEDESTAL_BLOCK;
    public static final BlockItem PEDESTAL_BLOCK_ITEM;

    public static final Identifier CRAFTING_PEDESTAL = new Identifier(MOD_ID, "crafting_pedestal");
    public static Block CRAFTING_PEDESTAL_BLOCK;
    public static final BlockItem CRAFTING_PEDESTAL_BLOCK_ITEM;
    public static final BlockEntityType<CraftingPedestalBlockEntity> CRAFTING_PEDESTAL_BLOCK_ENTITY;
    public static final ScreenHandlerType<CraftingPedestalScreenHandler> CRAFTING_PEDESTAL_SCREEN_HANDLER;

    public static final Identifier SAMPLING_PEDESTAL = new Identifier(MOD_ID, "sampling_pedestal");
    public static final Block SAMPLING_PEDESTAL_BLOCK;
    public static final BlockItem SAMPLING_PEDESTAL_BLOCK_ITEM;
    public static final BlockEntityType<SamplingPedestalBlockEntity> SAMPLING_PEDESTAL_BLOCK_ENTITY;


    public static final DefaultParticleType CRAFTING_PARTICLE = FabricParticleTypes.simple();

    public static MinecraftServer server;

    static {
        ITEM_GROUP = FabricItemGroupBuilder.build(new Identifier(MOD_ID, "aequitas_group"), () -> new ItemStack(CRAFTING_PEDESTAL_BLOCK));

        //Blocks
        PEDESTAL_BLOCK = Registry.register(Registry.BLOCK, PEDESTAL, new PedestalBlock());
        CRAFTING_PEDESTAL_BLOCK = Registry.register(Registry.BLOCK, CRAFTING_PEDESTAL, new CraftingPedestalBlock(AbstractBlock.Settings.of(Material.STONE)));
        SAMPLING_PEDESTAL_BLOCK = Registry.register(Registry.BLOCK, SAMPLING_PEDESTAL, new SamplingPedestalBlock(AbstractBlock.Settings.of(Material.STONE)));

        //Items
        PEDESTAL_BLOCK_ITEM = Registry.register(Registry.ITEM, PEDESTAL, new BlockItem(PEDESTAL_BLOCK, new Item.Settings().group(ITEM_GROUP)));
        CRAFTING_PEDESTAL_BLOCK_ITEM = Registry.register(Registry.ITEM, CRAFTING_PEDESTAL, new BlockItem(CRAFTING_PEDESTAL_BLOCK, new Item.Settings().group(ITEM_GROUP)));
        SAMPLING_PEDESTAL_BLOCK_ITEM = Registry.register(Registry.ITEM, SAMPLING_PEDESTAL, new BlockItem(SAMPLING_PEDESTAL_BLOCK, new Item.Settings().group(ITEM_GROUP)));

        //Entities
        CRAFTING_PEDESTAL_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, CRAFTING_PEDESTAL, BlockEntityType.Builder.create(CraftingPedestalBlockEntity::new, CRAFTING_PEDESTAL_BLOCK).build(null));
        SAMPLING_PEDESTAL_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, SAMPLING_PEDESTAL, BlockEntityType.Builder.create(SamplingPedestalBlockEntity::new, SAMPLING_PEDESTAL_BLOCK).build(null));

        //Screens
        CRAFTING_PEDESTAL_SCREEN_HANDLER = ScreenHandlerRegistry.registerSimple(CRAFTING_PEDESTAL, CraftingPedestalScreenHandler::new);

    }

    @Override
    public void onInitialize() {
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new ResourceLoader());

        Registry.register(Registry.PARTICLE_TYPE, new Identifier(MOD_ID, "crafting_particle"), CRAFTING_PARTICLE);

        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            Aequitas.server = server;
            EssenceHandler.registerRecipeManager(server.getRecipeManager());
            EssenceHandler.reloadEssenceValues(new HashMap<>());
        });

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            PacketByteBuf buf = PacketByteBufs.create();
            EssencePacket.encode(buf);

            sender.sendPacket(NetworkingHandler.ESSENCE_UPDATE, buf);
        });
    }




}
