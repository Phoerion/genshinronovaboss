package phoerion.genshinronovaboss.screen.abilities.ronova;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import phoerion.genshinronovaboss.GenshinRonovaBoss;
import phoerion.genshinronovaboss.ability.ronova.CurseOnDamageAbilityManager;
import phoerion.genshinronovaboss.ability.ronova.ExplodingAbilityManager;
import phoerion.genshinronovaboss.ability.ronova.SlashAbilityManager;


public class RonovaAbilityHud {

    private static final Identifier ICON = new Identifier(GenshinRonovaBoss.MOD_ID, "textures/gui/ronova_ability.png");
    private static final Identifier COOLDOWN_ICON = new Identifier(GenshinRonovaBoss.MOD_ID, "textures/gui/cooldown_texture.png");

    private static final Identifier ICON_2 = new Identifier(GenshinRonovaBoss.MOD_ID, "textures/gui/ronova_ability_2.png");
    private static final Identifier ACTIVETED_ICON = new Identifier(GenshinRonovaBoss.MOD_ID, "textures/gui/activated.png");

    private static final Identifier ICON_3 = new Identifier(GenshinRonovaBoss.MOD_ID, "textures/gui/ronova_ability_3.png");

    private static int cooldown = 0;
    private static int curse_cooldown = 0;
    private static int slash_cooldown = 0;
    private static boolean canCurse;

    public static void setCooldown(int value){
        cooldown = value;
    }

    public static void setCurseCooldown(int value){
        curse_cooldown = value;
    }
    public static void setCanCurse(boolean bool){
        canCurse = bool;
    }
    public static void setSlashCooldown(int value){
        slash_cooldown = value;
    }

    private static boolean hasRonovaTag = false;

    public static void setRonovaTag(boolean value) {
        hasRonovaTag = value;
    }

    public static void register() {
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            MinecraftClient client = MinecraftClient.getInstance();

            if (client.player == null || client.world == null || !hasRonovaTag) {
                return;
            }

            ClientPlayerEntity player = client.player;

            int screenWidth = client.getWindow().getScaledWidth();
            int screenHeight = client.getWindow().getScaledHeight();

            int size = 32;
            int x = 10;
            int y = screenHeight - size - 10;

            int cooldown_size;
            int curse_cooldown_size;
            int slash_cooldown_size;

            cooldown_size = 32 * cooldown / ExplodingAbilityManager.getMaxCooldown();
            curse_cooldown_size = 32 * curse_cooldown / CurseOnDamageAbilityManager.getMaxCooldown();
            slash_cooldown_size = 32 * slash_cooldown / SlashAbilityManager.getMaxCooldown();

            //exploding projectile
            drawContext.drawTexture(
                    ICON,
                    x, y,
                    0, 0,
                    size, size,
                    size, size
            );

            drawContext.drawTexture(
                    COOLDOWN_ICON,
                    x, y,
                    0, 0,
                    size, cooldown_size,
                    size, size
            );

            //curse ability
            drawContext.drawTexture(
                    ICON_2,
                    x, y  - size - 10,
                    0, 0,
                    size, size,
                    size, size
            );
             if(canCurse) {
                 drawContext.drawTexture(
                         ACTIVETED_ICON,
                         x, y - size - 10,
                         0, 0,
                         size, size,
                         size, size
                 );
             }

            drawContext.drawTexture(
                    COOLDOWN_ICON,
                    x, y - size - 10,
                    0, 0,
                    size, curse_cooldown_size,
                    size, size
            );

            //slash
            drawContext.drawTexture(
                    ICON_3,
                    x, y  - size - size - 20,
                    0, 0,
                    size, size,
                    size, size
            );

            drawContext.drawTexture(
                    COOLDOWN_ICON,
                    x, y  - size - size - 20,
                    0, 0,
                    size, slash_cooldown_size,
                    size, size
            );

        });
    }
}