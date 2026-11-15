package phoerion.genshinronovaboss.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import phoerion.genshinronovaboss.ability.ronova.SlashProjectileAbility;

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

        if (player.getCommandTags().contains("ronova") && !player.getWorld().isClient) {

            if (player.age % 10 == 0) {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 200, 1, false, false, false));
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 200, 0, false, false, false));
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

    @Inject(method = "travel", at = @At("HEAD"), cancellable = true)
    private void preventMovement(CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;

        if (player instanceof ServerPlayerEntity
                && SlashProjectileAbility.isParalyzed(player)) {
            player.setVelocity(0, 0, 0);
            player.velocityModified = true;
            ci.cancel();
        }
    }
}