package phoerion.genshinronovaboss.item.custom;

//import com.lowdragmc.photon.client.fx.FX;
//import com.lowdragmc.photon.client.fx.FXHelper;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import phoerion.genshinronovaboss.GenshinRonovaBoss;
import net.minecraft.client.MinecraftClient;

import java.util.Stack;

import static net.minecraft.util.ActionResult.PASS;

public class IceSwordItem extends SwordItem {
    public IceSwordItem(ToolMaterial toolMaterial, int attackDamage, float attackSpeed, Settings settings) {
        super(toolMaterial, attackDamage, attackSpeed, settings);
    }

//    @Override
//    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
////        ItemStack stack = user.getStackInHand(hand);
////
////        if(world.isClient){
////            Identifier effectId = new Identifier(GenshinRonovaBoss.MOD_ID, "ice_shield");
////            FX fx = FXHelper.getFX(effectId);
////
////            if(MinecraftClient.getInstance().getNetworkHandler() != null){
////                MinecraftClient.getInstance().getNetworkHandler().sendCommand("photon fx photon:ice_shield entity @s ^ ^1 ^1");
////                return TypedActionResult.success(stack, true);
////            }
////
////        return TypedActionResult.pass(stack);
//    }


    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if(!world.isClient){

        }
        return TypedActionResult.success(stack, true);
    }
}
