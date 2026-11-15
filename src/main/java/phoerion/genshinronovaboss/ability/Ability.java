package phoerion.genshinronovaboss.ability;

import net.minecraft.server.network.ServerPlayerEntity;

public abstract class Ability {

    private final String id;
    private final int cooldown;

    public Ability(String id, int cooldown){
        this.id = id;
        this.cooldown = cooldown;
    }

    public int getCooldown(){
        return cooldown;
    }

    public String getId(){
        return id;
    }

    public void activate(ServerPlayerEntity player){

    }

}
