package com.cuboidlabs.soulscythe.util;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HammerOfJusticeCooldownHandler {
    private static int ticksDone = 0;
    public static Map<UUID, Integer> serverPlayerEntityMap = new HashMap<>();
    public static void tick() {
        if (ticksDone < 20) {
            ticksDone++;
            return;
        }
        for (UUID uuid : serverPlayerEntityMap.keySet()) {
            int currentVal = serverPlayerEntityMap.get(uuid);
            serverPlayerEntityMap.replace(uuid, currentVal, currentVal - 1);
            if (serverPlayerEntityMap.get(uuid) <= 0) {
                serverPlayerEntityMap.remove(uuid);
            }
        }
        ticksDone = 0;
    }
}
