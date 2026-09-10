package phoerion.genshinronovaboss.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {

    // Сохраняем состояние проклятия в NBT игрока (чтобы оно оставалось после смерти и перезахода)
    @Inject(method = "writeCustomDataToNbt", at = @At("HEAD"))
    private void saveRonovaCurse(NbtCompound nbt, CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        boolean hasCurse = player.getCommandTags().contains("ronova_cursed");
        nbt.putBoolean("RonovaCursed", hasCurse);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("HEAD"))
    private void readRonovaCurse(NbtCompound nbt, CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        if (nbt.contains("RonovaCursed") && nbt.getBoolean("RonovaCursed")) {
            player.getCommandTags().add("ronova_cursed");
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void onPlayerTick(CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;

        // Если у игрока есть постоянный тег проклятия
        if (player.getCommandTags().contains("ronova_cursed") && !player.getCommandTags().contains("ronova")) {
            // Если эффект пропал (например, от молока), принудительно возвращаем его
            if (player.age % 10 == 0) {
                player.addStatusEffect(new net.minecraft.entity.effect.StatusEffectInstance(
                        phoerion.genshinronovaboss.effects.ModEffects.RONOVA_CURSE,
                        200,
                        0,
                        false,
                        true
                ));
            }
        }
    }

    @Inject(method = "onDeath", at = @At("HEAD"))
    private void onPlayerDeath(DamageSource source, CallbackInfo ci) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;

        Entity attacker = source.getAttacker();
        Entity directSource = source.getSource();

        // Проверяем, нанес ли удар сам босс, его миньон или их снаряды
        boolean isRonovaRelated =
                attacker instanceof phoerion.genshinronovaboss.entity.custom.RonovaEntity ||
                        attacker instanceof phoerion.genshinronovaboss.entity.custom.RonovaEyeEntity ||
                        directSource instanceof phoerion.genshinronovaboss.entity.custom.RonovaProjectileEntity;

        if (isRonovaRelated) {
            player.addCommandTag("ronova_cursed");
        }
    }
}