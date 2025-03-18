package redstonedev.neosinglemace;

import com.mojang.serialization.Codec;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.Objects;
import java.util.function.Supplier;

@Mod(NeoSingleMace.MOD_ID)
public class NeoSingleMace {
    public static final String MOD_ID = "neosinglemace";
    private static boolean isMaceCrafted = false;
    private static boolean needsToBroadcast = false;
    private static boolean dirty = false;

    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(
            NeoForgeRegistries.ATTACHMENT_TYPES,
            MOD_ID
    );

    private static final Supplier<AttachmentType<Boolean>> MACE_CRAFTED_DATA = ATTACHMENT_TYPES.register(
            "mace_crafted_data",
            () -> AttachmentType.builder(
                            () -> false)
                    .serialize(
                            Codec.BOOL)
                    .build()
    );

    public NeoSingleMace(IEventBus modEventBus, ModContainer modContainer) {
        ATTACHMENT_TYPES.register(modEventBus);
        NeoForge.EVENT_BUS.register(this);
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    @SubscribeEvent
    public void onCraft(PlayerEvent.ItemCraftedEvent event) {
        if (event.getCrafting().is(Items.MACE)) {
            isMaceCrafted = true;
            dirty = true;

            if (Config.broadcastMessage) needsToBroadcast = true;
        }
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        isMaceCrafted = Objects.requireNonNull(event.getServer().getLevel(Level.OVERWORLD))
                .getExistingData(MACE_CRAFTED_DATA)
                .orElse(false);
    }

    @SubscribeEvent
    public void onServerStopping(ServerStoppingEvent event) {
        Objects.requireNonNull(event.getServer().getLevel(Level.OVERWORLD)).setData(MACE_CRAFTED_DATA, isMaceCrafted);
    }

    @SubscribeEvent
    public void onServerTick(ServerTickEvent.Post event) {
        if (needsToBroadcast) {
            event.getServer()
                    .getPlayerList()
                    .broadcastSystemMessage(Component.literal(Config.messageToBroadcast), true);

            needsToBroadcast = false;
        }

        if (dirty) {
            Objects.requireNonNull(event.getServer().getLevel(Level.OVERWORLD))
                    .setData(MACE_CRAFTED_DATA, isMaceCrafted);

            dirty = false;
        }
    }

    public static boolean isMaceCrafted() {
        return NeoSingleMace.isMaceCrafted;
    }
}
