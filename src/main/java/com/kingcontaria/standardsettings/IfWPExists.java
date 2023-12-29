package com.kingcontaria.standardsettings;

import me.voidxwalker.worldpreview.WorldPreview;
import net.minecraft.client.MinecraftClient;

public class IfWPExists {
    private static boolean changeNextRender = false;

    public static void handleLevelLoadScreenRender(MinecraftClient minecraft) {
        if (StandardSettings.f3PauseOnWorldLoad) {
            if (changeNextRender) {
                changeNextRender = false;
                WorldPreview.showMenu = false;
            }
            if (StandardSettings.hasWP && !minecraft.isWindowFocused()) {
                if (WorldPreview.showMenu) {
                    changeNextRender = true;
                }
            }
        }
    }
}
