package com.kingcontaria.standardsettings;

import com.kingcontaria.standardsettings.mixins.MinecraftClientAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatVisibility;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.world.Difficulty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;

public class StandardSettings {

    public static final Logger LOGGER = LogManager.getLogger();
    private static final MinecraftClient client = MinecraftClient.getInstance();
    public static final GameOptions options = client.options;
    public static final File standardoptionsFile = new File("config/standardoptions.txt");
    public static File lastUsedFile;
    public static long fileLastModified;
    public static final File optionsFile = new File("options.txt");
    public static boolean changeOnGainedFocus = false;
    private static int renderDistanceOnWorldJoin;
    private static float fovOnWorldJoin;

    public static void load() {
        long start = System.nanoTime();

        fovOnWorldJoin = renderDistanceOnWorldJoin = 0;

        try {
            if (!standardoptionsFile.exists()) {
                LOGGER.error("standardoptions.txt is missing");
                return;
            }

            BufferedReader bufferedReader = new BufferedReader(new FileReader(standardoptionsFile));

            boolean reload = false;
            String string = bufferedReader.readLine();

            if (new File(string).exists()) {
                LOGGER.info("Using global standardoptions file");
                bufferedReader.close();
                bufferedReader = new BufferedReader(new FileReader(lastUsedFile = new File(string)));
                string = bufferedReader.readLine();
            } else {
                lastUsedFile = standardoptionsFile;
            }
            fileLastModified = lastUsedFile.lastModified();

            do {
                String[] strings = string.split(":");
                String[] string0_split = strings[0].split("_");
                try {
                    switch (string0_split[0]) {
                        case "mouseSensitivity" -> options.sensitivity = Float.parseFloat(strings[1]);
                        case "fov" -> options.fov = Float.parseFloat(strings[1]) * 40.0f + 70.0f;
                        case "gamma" -> options.gamma = Float.parseFloat(strings[1]);
                        case "invertYMouse" -> options.invertYMouse = Boolean.parseBoolean(strings[1]);
                        case "renderDistance" -> options.viewDistance = Integer.parseInt(strings[1]);
                        case "guiScale" -> options.guiScale = Integer.parseInt(strings[1]);
                        case "particles" -> options.particle = Integer.parseInt(strings[1]);
                        case "bobView" -> options.bobView = Boolean.parseBoolean(strings[1]);
                        case "anaglyph3d" -> {
                            if (options.anaglyph3d != Boolean.parseBoolean(strings[1])) {
                                options.anaglyph3d = Boolean.parseBoolean(strings[1]);
                                client.stitchTextures();
                            }
                        }
                        case "maxFps" -> options.maxFramerate = Integer.parseInt(strings[1]);
                        case "difficulty" -> options.difficulty = Difficulty.byOrdinal(Integer.parseInt(strings[1]));
                        case "fancyGraphics" -> options.fancyGraphics = Boolean.parseBoolean(strings[1]);
                        case "ao" -> options.ao = strings[1].equals("true") ? 2 : (strings[1].equals("false") ? 0 : Integer.parseInt(strings[1]));
                        case "chatVisibility" -> options.field_7671 = ChatVisibility.get(Integer.parseInt(strings[1]));
                        case "chatColors" -> options.chatColor = Boolean.parseBoolean(strings[1]);
                        case "chatLinks" -> options.chatLink = Boolean.parseBoolean(strings[1]);
                        case "chatLinksPrompt" -> options.chatLinkPrompt = Boolean.parseBoolean(strings[1]);
                        case "chatOpacity" -> options.chatOpacity = Float.parseFloat(strings[1]);
                        case "fullscreen" -> {
                            if (options.fullscreen != Boolean.parseBoolean(strings[1])) {
                                if (client.isWindowFocused()) {
                                    client.toggleFullscreen();
                                    options.fullscreen = Boolean.parseBoolean(strings[1]);
                                } else {
                                    LOGGER.error("Could not reset fullscreen mode because window wasn't focused!");
                                }
                            }
                        }
                        case "enableVsync" -> options.vsync = Boolean.parseBoolean(strings[1]);
                        case "advancedItemTooltips" -> options.advancedItemTooltips = Boolean.parseBoolean(strings[1]);
                        case "pauseOnLostFocus" -> options.pauseOnLostFocus = Boolean.parseBoolean(strings[1]);
                        case "showCape" -> options.field_5053 = Boolean.parseBoolean(strings[1]);
                        case "touchscreen" -> options.touchScreen = Boolean.parseBoolean(strings[1]);
                        case "chatHeightFocused" -> options.chatHeightFocused = Float.parseFloat(strings[1]);
                        case "chatHeightUnfocused" -> options.chatHeightUnfocused = Float.parseFloat(strings[1]);
                        case "chatScale" -> options.chatScale = Float.parseFloat(strings[1]);
                        case "chatWidth" -> options.chatWidth = Float.parseFloat(strings[1]);
                        case "mipmapLevels" -> {
                            if (options.mipmapLevels != Integer.parseInt(strings[1])) {
                                client.getSpriteAtlasTexture().setMaxTextureSize(options.mipmapLevels = Integer.parseInt(strings[1]));
                                reload = true;
                            }
                        }
                        case "anisotropicFiltering" -> {
                            if (options.field_7638 != Integer.parseInt(strings[1])) {
                                client.getSpriteAtlasTexture().method_7004(options.field_7638);
                                reload = true;
                            }
                        }
                        case "forceUnicodeFont" -> client.textRenderer.method_960(options.forceUnicode = Boolean.parseBoolean(strings[1]));
                        case "clouds" -> options.renderClouds = Boolean.parseBoolean(strings[1]);
                        case "hitboxes" -> EntityRenderDispatcher.field_5192 = Boolean.parseBoolean(strings[1]);
                        case "perspective" -> options.perspective = Integer.parseInt(strings[1]);
                        case "piedirectory" -> ((MinecraftClientAccessor)client).setOpenProfilerSection(strings[1]);
                        case "fovOnWorldJoin" -> fovOnWorldJoin = Float.parseFloat(strings[1]);
                        case "renderDistanceOnWorldJoin" -> renderDistanceOnWorldJoin = Integer.parseInt(strings[1]);
                        case "key" -> {
                            for (KeyBinding keyBinding : options.keysAll) {
                                if (string0_split[1].equals(keyBinding.getTranslationKey())) {
                                    keyBinding.setCode(Integer.parseInt(strings[1])); break;
                                }
                            }
                        }
                        case "soundCategory" -> {
                            for (SoundCategory soundCategory : SoundCategory.values()) {
                                if (string0_split[1].equals(soundCategory.getName())) {
                                    options.setSoundVolume(soundCategory, Float.parseFloat(strings[1])); break;
                                }
                            }
                        }
                    }
                    // Some options.txt settings which aren't accessible in vanilla Minecraft and some unnecessary settings (like Multiplayer and Streaming stuff) are not included.
                } catch (Exception exception) {
                    if (!string.equals("hitboxes:") && !string.equals("perspective:") && !string.equals("piedirectory:") && !string.equals("renderDistanceOnWorldJoin:") && !string.equals("fovOnWorldJoin:") && !string.equals("lastServer:")) {
                        LOGGER.warn("Skipping bad StandardSetting: " + string);
                    }
                }
            } while ((string = bufferedReader.readLine()) != null);
            KeyBinding.updateKeysByCode();
            bufferedReader.close();
            if (reload) {
                client.getSpriteAtlasTexture().load(client.getResourceManager());
            }
            LOGGER.info("Finished loading StandardSettings ({} ms)", (System.nanoTime() - start) / 1000000.0f);
        } catch (Exception exception2) {
            LOGGER.error("Failed to load StandardSettings", exception2);
        }
    }

    public static void changeSettingsOnJoin() {
        long start = System.nanoTime();

        if (renderDistanceOnWorldJoin != 0) {
            options.viewDistance = renderDistanceOnWorldJoin;
        }
        if (fovOnWorldJoin != 0) {
            options.fov = fovOnWorldJoin;
        }
        if (fovOnWorldJoin != 0 || renderDistanceOnWorldJoin != 0) {
            fovOnWorldJoin = renderDistanceOnWorldJoin = 0;
            options.save();
            LOGGER.info("Changed Settings on World Join ({} ms)", (System.nanoTime() - start) / 1000000.0f);
        }
    }

    public static void checkSettings() {
        long start = System.nanoTime();

        options.sensitivity = Check("Sensitivity", options.sensitivity, 0, 1);
        options.fov = Math.round(Check("FOV", options.fov, 30, 110));
        options.gamma = Check("Brightness", options.gamma, 0, 5);
        options.viewDistance = Check("Render Distance", options.viewDistance, 2, 32);
        options.guiScale = Check("GUI Scale", options.guiScale, 0, 4);
        // Because of DynamicFPS/SleepBackground I will not mess with adjusting FPS :)
        options.chatOpacity = Check("Chat Opacity", options.chatOpacity, 0, 1);
        options.chatHeightFocused = Check("(Chat) Focused Height", options.chatHeightFocused, 0, 1);
        options.chatHeightUnfocused = Check("(Chat) Unfocused Height", options.chatHeightUnfocused, 0, 1);
        options.chatScale = Check("Chat Text Size", options.chatScale, 0, 1);
        options.chatWidth = Check("Chat Width", options.chatWidth, 0, 1);
        if (options.mipmapLevels != (options.mipmapLevels = Check("Mipmap Levels", options.mipmapLevels, 0, 4)) || options.field_7638 != (options.field_7638 = Check("Anisotropic Filtering", options.field_7638, 0, 16))) {
            client.getSpriteAtlasTexture().method_7004(options.field_7638);
            client.getSpriteAtlasTexture().setMaxTextureSize(options.mipmapLevels);
            client.getSpriteAtlasTexture().load(client.getResourceManager());
        }
        for (SoundCategory soundCategory : SoundCategory.values()) {
            options.setSoundVolume(soundCategory, Check(soundCategory.getName(), options.getSoundVolume(soundCategory), 1, 1));
        }

        if (renderDistanceOnWorldJoin != 0) {
            renderDistanceOnWorldJoin = Check("Render Distance (On World Join)", renderDistanceOnWorldJoin, 2, 32);
        }
        if (fovOnWorldJoin != 0) {
            fovOnWorldJoin = Math.round(Check("FOV (On World Join)", fovOnWorldJoin, 30, 110));
        }

        LOGGER.info("Finished checking Settings ({} ms)", (System.nanoTime() - start) / 1000000.0f);
    }

    private static float Check(String settingName, float setting, float min, float max) {
        if (setting < min) {
            LOGGER.warn(settingName + " was too low! ({})", setting);
            return min;
        }
        if (setting > max) {
            LOGGER.warn(settingName + " was too high! ({})", setting);
            return max;
        }
        return setting;
    }

    private static int Check(String settingName, int setting, int min, int max) {
        if (setting < min) {
            LOGGER.warn(settingName + " was too low! ({})", setting);
            return min;
        }
        if (setting > max) {
            LOGGER.warn(settingName + " was too high! ({})", setting);
            return max;
        }
        return setting;
    }
}