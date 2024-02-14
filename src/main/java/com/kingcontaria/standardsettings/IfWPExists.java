package com.kingcontaria.standardsettings;

import me.voidxwalker.worldpreview.WorldPreview;
import net.minecraft.client.MinecraftClient;

public class IfWPExists {
    public static boolean handleLevelLoadScreenRender(MinecraftClient minecraft) {
        if (StandardSettings.f3PauseOnWorldLoad && StandardSettings.hasWP) {
            if (!minecraft.isWindowFocused() && WorldPreview.renderingPreview) {
                WorldPreview.showMenu = false;
                return true;
            } else return WorldPreview.renderingPreview;
        }
        return true;
    }
}
