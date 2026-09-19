package phoerion.genshinronovaboss.worldgen.structures;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import phoerion.genshinronovaboss.worldgen.structures.CelestiaPlacer;

public class CelestiaCommand {

    public static void register(
            CommandDispatcher<ServerCommandSource> dispatcher
    ) {

        dispatcher.register(
                CommandManager.literal("celestia")
                        .requires(source -> source.hasPermissionLevel(2))
                        .executes(context -> {

                            ServerCommandSource source = context.getSource();

                            BlockPos pos = source.getEntity().getBlockPos();

                            CelestiaPlacer.place(source.getWorld(), pos);

                            source.sendFeedback(() -> Text.literal("Celestia placed!"), true);

                            return 1;
                        })
        );
    }
}