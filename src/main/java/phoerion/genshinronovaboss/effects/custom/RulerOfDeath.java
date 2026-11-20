package phoerion.genshinronovaboss.effects.custom;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.world.ServerWorld;
import phoerion.genshinronovaboss.particle.ModParticles;

import java.util.UUID;

public class RulerOfDeath extends StatusEffect {
    private static final UUID HEALTH_MODIFIER_ID = UUID.fromString("a7c5f120-6b3e-4d91-9f82-1c6a0e5b7432");

    public RulerOfDeath(StatusEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        if(entity.age % 40 == 0 && entity.getHealth() < entity.getMaxHealth()){
            entity.setHealth(entity.getHealth() + 1);
        }
        entity.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 40, 1, true, false, false));
    }

    @Override
    public void onApplied(LivingEntity entity, AttributeContainer attributes, int amplifier) {
        super.onApplied(entity, attributes, amplifier);
        setCustomMaxHealth(entity, +10);
    }

    @Override
    public void onRemoved(LivingEntity entity, AttributeContainer attributes, int amplifier) {
        super.onRemoved(entity, attributes, amplifier);
        EntityAttributeInstance healthAttribute = entity.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);
        if (healthAttribute != null) {
            healthAttribute.removeModifier(HEALTH_MODIFIER_ID);
        }
    }



    private void setCustomMaxHealth(LivingEntity entity, double healthAmount) {
        EntityAttributeInstance healthAttribute = entity.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);

        if (healthAttribute != null) {
            healthAttribute.removeModifier(HEALTH_MODIFIER_ID);

            EntityAttributeModifier modifier = new EntityAttributeModifier(
                    HEALTH_MODIFIER_ID,
                    "Ruler Of Death health modifier",
                    healthAmount,
                    EntityAttributeModifier.Operation.ADDITION
            );

            healthAttribute.addPersistentModifier(modifier);

            if (entity.getHealth() > entity.getMaxHealth()) {
                entity.setHealth(entity.getMaxHealth());
            }
        }
    }


}
