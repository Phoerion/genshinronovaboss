package phoerion.genshinronovaboss.effects;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import phoerion.genshinronovaboss.GenshinRonovaBoss;
import phoerion.genshinronovaboss.effects.custom.RonovaCurse;

public class ModEffects {
    public static StatusEffect RONOVA_CURSE = Registry.register(Registries.STATUS_EFFECT, new Identifier(GenshinRonovaBoss.MOD_ID, "ronova_curse"), new RonovaCurse(StatusEffectCategory.HARMFUL, 0xFF0000));

    public static void registerModEffects(){
        GenshinRonovaBoss.LOGGER.info("Registering Effects for " + GenshinRonovaBoss.MOD_ID);
    }
}
