package phoerion.genshinronovaboss.network;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import phoerion.genshinronovaboss.GenshinRonovaBoss;
import phoerion.genshinronovaboss.item.ModItemMaterials;
import phoerion.genshinronovaboss.item.ModItems;
import phoerion.genshinronovaboss.item.custom.StellarVisionItem;

public class ModNetworking {
    public static final Identifier FIRST_VISION_ABILITY_PACKET_ID =  new Identifier(GenshinRonovaBoss.MOD_ID, "first_vision_ability");

    public static void registerC2SPackets(){
        ServerPlayNetworking.registerGlobalReceiver(FIRST_VISION_ABILITY_PACKET_ID, ((server, player, handler, buf, responseSender) -> {
            server.execute(() -> {
                if(player.getInventory().contains(new ItemStack(ModItems.STELLAR_VISION))){
                    StellarVisionItem.giveIceSword(player.getWorld(), player);
                }
            });
        }));
    }
}
