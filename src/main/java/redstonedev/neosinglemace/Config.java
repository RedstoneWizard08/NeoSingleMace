package redstonedev.neosinglemace;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@EventBusSubscriber(modid = NeoSingleMace.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.BooleanValue BROADCAST_MESSAGE = BUILDER.comment(
            "Whether to broadcast a message once the mace is crafted.").define("broadcastMessage", true);

    public static final ModConfigSpec.ConfigValue<String> MESSAGE_TO_BROADCAST = BUILDER.comment(
                    "The message to broadcast.")
            .define("messageToBroadcast", "First mace crafted. All mace recipes are now removed.");

    static final ModConfigSpec SPEC = BUILDER.build();

    public static boolean broadcastMessage;
    public static String messageToBroadcast;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        broadcastMessage = BROADCAST_MESSAGE.get();
        messageToBroadcast = MESSAGE_TO_BROADCAST.get();
    }
}
