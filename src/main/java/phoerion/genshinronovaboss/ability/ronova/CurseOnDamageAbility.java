package phoerion.genshinronovaboss.ability.ronova;

import net.minecraft.server.network.ServerPlayerEntity;
import phoerion.genshinronovaboss.ability.Ability;
import phoerion.genshinronovaboss.screen.abilities.ronova.RonovaAbilityHud;

public class  CurseOnDamageAbility extends Ability { ;
    public CurseOnDamageAbility(String id, int cooldown) {
        super(id, cooldown);
    }

    @Override
    public void activate(ServerPlayerEntity player) {
        boolean activated = player.getCommandTags().contains("canCurse");
        if(activated){
            player.getCommandTags().remove("canCurse");
            RonovaAbilityHud.setCanCurse(false);
        } else{
            player.addCommandTag("canCurse");
            RonovaAbilityHud.setCanCurse(true);
        }
    }
}
