package phoerion.genshinronovaboss.client;

import phoerion.genshinronovaboss.GenshinRonovaBoss;

public class ClientBossData {
    public static boolean isRonovaFightActive = false;

    public static void registerClientBossData(){
        GenshinRonovaBoss.LOGGER.info("registering client boss data for " + GenshinRonovaBoss.MOD_ID);
    }
}
