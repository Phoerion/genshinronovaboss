package phoerion.genshinronovaboss.effects.custom;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class RonovaCurse extends StatusEffect {
    private static final UUID HEALTH_MODIFIER_ID = UUID.fromString("15d80b60-7052-47c0-a7d5-d5e865768e14");

    public RonovaCurse(StatusEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        super.applyUpdateEffect(entity, amplifier);
    }

    @Override
    public void onApplied(LivingEntity entity, AttributeContainer attributes, int amplifier) {
        super.onApplied(entity, attributes, amplifier);
        setCustomMaxHealth(entity, -6);
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
                    "Ronova health modifier",
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
