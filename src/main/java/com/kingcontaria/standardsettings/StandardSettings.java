package com.kingcontaria.standardsettings;

import com.kingcontaria.standardsettings.mixins.BakedModelManagerAccessor;
import com.kingcontaria.standardsettings.mixins.MinecraftClientAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.*;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.Window;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Arm;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.Optional;

@Environment(value= EnvType.CLIENT)
public class StandardSettings {

    public static final Logger LOGGER = LogManager.getLogger();
    public static final MinecraftClient client = MinecraftClient.getInstance();
    public static final GameOptions options = client.options;
    private static final Window window = client.getWindow();
    public static final File standardoptionsFile = new File("config/standardoptions.txt");
    public static File lastUsedFile;
    public static long fileLastModified;
    public static final File optionsFile = options.getOptionsFile();
    public static boolean changeOnGainedFocus = false;
    private static int renderDistanceOnWorldJoin;
    private static int simulationDistanceOnWorldJoin;
    private static double entityDistanceScalingOnWorldJoin;
    private static int fovOnWorldJoin;

    public static void load() {
        long start = System.nanoTime();

        entityDistanceScalingOnWorldJoin = fovOnWorldJoin = renderDistanceOnWorldJoin = simulationDistanceOnWorldJoin = 0;

        try {
            if (!standardoptionsFile.exists()) {
                LOGGER.error("standardoptions.txt is missing");
                return;
            }

            BufferedReader bufferedReader = new BufferedReader(new FileReader(standardoptionsFile));

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
                String[] strings = string.split(":", 2);
                String[] string0_split = strings[0].split("_", 2);
                try {
                    switch (string0_split[0]) {
                        case "autoJump" -> options.getAutoJump().setValue(Boolean.parseBoolean(strings[1]));
                        case "autoSuggestions" -> options.getAutoSuggestions().setValue(Boolean.parseBoolean(strings[1]));
                        case "chatColors" -> options.getChatColors().setValue(Boolean.parseBoolean(strings[1]));
                        case "chatLinks" -> options.getChatLinks().setValue(Boolean.parseBoolean(strings[1]));
                        case "chatLinksPrompt" -> options.getChatLinksPrompt().setValue(Boolean.parseBoolean(strings[1]));
                        case "enableVsync" -> options.getEnableVsync().setValue(Boolean.parseBoolean(strings[1]));
                        case "entityShadows" -> options.getEntityShadows().setValue(Boolean.parseBoolean(strings[1]));
                        case "forceUnicodeFont" -> options.getForceUnicodeFont().setValue(Boolean.parseBoolean(strings[1]));
                        case "discrete" -> options.getDiscreteMouseScroll().setValue(Boolean.parseBoolean(strings[1]));
                        case "invertYMouse" -> options.getInvertYMouse().setValue(Boolean.parseBoolean(strings[1]));
                        case "reducedDebugInfo" -> options.getReducedDebugInfo().setValue(Boolean.parseBoolean(strings[1]));
                        case "showSubtitles" -> options.getShowSubtitles().setValue(Boolean.parseBoolean(strings[1]));
                        case "directionalAudio" -> options.getDirectionalAudio().setValue(Boolean.parseBoolean(strings[1]));
                        case "touchscreen" -> options.getTouchscreen().setValue(Boolean.parseBoolean(strings[1]));
                        case "fullscreen" -> {
                            if (window.isFullscreen() != Boolean.parseBoolean(strings[1])) {
                                if (client.isWindowFocused()) {
                                    window.toggleFullscreen();
                                    options.getFullscreen().setValue(window.isFullscreen());
                                } else {
                                    LOGGER.error("Could not reset fullscreen mode because window wasn't focused!");
                                }
                            }
                        }
                        case "bobView" -> options.getBobView().setValue(Boolean.parseBoolean(strings[1]));
                        case "toggleCrouch" -> options.getSneakToggled().setValue(Boolean.parseBoolean(strings[1]));
                        case "toggleSprint" -> options.getSprintToggled().setValue(Boolean.parseBoolean(strings[1]));
                        case "darkMojangStudiosBackground" -> options.getMonochromeLogo().setValue(Boolean.parseBoolean(strings[1]));
                        case "hideLightningFlashes" -> options.getHideLightningFlashes().setValue(Boolean.parseBoolean(strings[1]));
                        case "mouseSensitivity" -> options.getMouseSensitivity().setValue(Double.parseDouble(strings[1]));
                        case "fov" -> options.getFov().setValue((int) (Double.parseDouble(strings[1]) * 40.0f + 70.0f));
                        case "screenEffectScale" -> options.getDistortionEffectScale().setValue(Double.parseDouble(strings[1]));
                        case "fovEffectScale" -> options.getFovEffectScale().setValue(Double.parseDouble(strings[1]));
                        case "darknessEffectScale" -> options.getDarknessEffectScale().setValue(Double.parseDouble(strings[1]));
                        case "gamma" -> options.getGamma().setValue(Double.parseDouble(strings[1]));
                        case "renderDistance" -> options.getViewDistance().setValue(Integer.parseInt(strings[1]));
                        case "entityDistanceScaling" -> options.getEntityDistanceScaling().setValue(Double.parseDouble(strings[1]));
                        case "guiScale" -> options.getGuiScale().setValue(Integer.parseInt(strings[1]));
                        case "particles" -> options.getParticles().setValue(ParticlesMode.byId(Integer.parseInt(strings[1])));
                        case "maxFps" -> options.getMaxFps().setValue(Integer.parseInt(strings[1]));
                        case "graphicsMode" -> options.getGraphicsMode().setValue(GraphicsMode.byId(Integer.parseInt(strings[1])));
                        case "ao" -> options.getAo().setValue(AoMode.byId(Integer.parseInt(strings[1])));
                        case "renderClouds" -> options.getCloudRenderMod().setValue(strings[1].equals("true") ? CloudRenderMode.FANCY : strings[1].equals("false") ? CloudRenderMode.OFF : CloudRenderMode.FAST);
                        case "attackIndicator" -> options.getAttackIndicator().setValue(AttackIndicator.byId(Integer.parseInt(strings[1])));
                        case "lang" -> {
                            client.getLanguageManager().setLanguage(client.getLanguageManager().getLanguage(strings[1]));
                            client.getLanguageManager().reload(client.getResourceManager());
                            options.language = client.getLanguageManager().getLanguage().getCode();
                        }
                        case "chatVisibility" -> options.getChatVisibility().setValue(ChatVisibility.byId(Integer.parseInt(strings[1])));
                        case "chatOpacity" -> options.getChtOpacity().setValue(Double.parseDouble(strings[1]));
                        case "chatLineSpacing" -> options.getChatLineSpacing().setValue(Double.parseDouble(strings[1]));
                        case "textBackgroundOpacity" -> options.getTextBackgroundOpacity().setValue(Double.parseDouble(strings[1]));
                        case "backgroundForChatOnly" -> options.getBackgroundForChatOnly().setValue(Boolean.parseBoolean(strings[1]));
                        case "fullscreenResolution" -> {
                            if (!strings[1].equals(options.fullscreenResolution)) {
                                if (strings[1].equals("")) {
                                    window.setVideoMode(Optional.empty());
                                    window.applyVideoMode(); break;
                                }
                                for (int i = 0; i < window.getMonitor().getVideoModeCount(); i++) {
                                    if (window.getMonitor().getVideoMode(i).asString().equals(strings[1])) {
                                        window.setVideoMode(Optional.ofNullable(window.getMonitor().getVideoMode(i)));
                                        window.applyVideoMode(); break;
                                    }
                                }
                                LOGGER.warn("Could not resolve Fullscreen Resolution: " + strings[1]);
                            }
                        }
                        case "advancedItemTooltips" -> options.advancedItemTooltips = Boolean.parseBoolean(strings[1]);
                        case "pauseOnLostFocus" -> options.pauseOnLostFocus = Boolean.parseBoolean(strings[1]);
                        case "chatHeightFocused" -> options.getChatHeightFocused().setValue(Double.parseDouble(strings[1]));
                        case "chatDelay" -> options.getChatDelay().setValue(Double.parseDouble(strings[1]));
                        case "chatHeightUnfocused" -> options.getChatHeightUnfocused().setValue(Double.parseDouble(strings[1]));
                        case "chatScale" -> options.getChatScale().setValue(Double.parseDouble(strings[1]));
                        case "chatWidth" -> options.getChatWidth().setValue(Double.parseDouble(strings[1]));
                        case "mipmapLevels" -> {
                            if (options.getMipmapLevels().getValue() != Integer.parseInt(strings[1])) {
                                options.getMipmapLevels().setValue(Integer.parseInt(strings[1]));
                                client.setMipmapLevels(options.getMipmapLevels().getValue());
                                ((BakedModelManagerAccessor)client.getBakedModelManager()).callApply(((BakedModelManagerAccessor)client.getBakedModelManager()).callPrepare(client.getResourceManager(), client.getProfiler()), client.getResourceManager(), client.getProfiler());
                            }
                        }
                        case "mainHand" -> options.getMainArm().setValue("\"left\"".equalsIgnoreCase(strings[1]) ? Arm.LEFT : Arm.RIGHT);
                        case "narrator" -> options.getNarrator().setValue(NarratorMode.byId(Integer.parseInt(strings[1])));
                        case "biomeBlendRadius" -> options.getBiomeBlendRadius().setValue(Integer.parseInt(strings[1]));
                        case "mouseWheelSensitivity" -> options.getMouseWheelSensitivity().setValue(Double.parseDouble(strings[1]));
                        case "rawMouseInput" -> options.getRawMouseInput().setValue(Boolean.parseBoolean(strings[1]));
                        case "showAutosaveIndicator" -> options.getShowAutosaveIndicator().setValue(Boolean.parseBoolean(strings[1]));
                        case "chatPreview" -> options.getChatPreview().setValue(Boolean.parseBoolean(strings[1]));
                        case "onlyShowSecureChat" -> options.getOnlyShowSecureChat().setValue(Boolean.parseBoolean(strings[1]));
                        case "sneaking" -> options.sneakKey.setPressed(options.getSneakToggled().getValue() && Boolean.parseBoolean(strings[1]));
                        case "sprinting" -> options.sprintKey.setPressed(options.getSprintToggled().getValue() && Boolean.parseBoolean(strings[1]));
                        case "chunkborders" -> {
                            if (client.debugRenderer.toggleShowChunkBorder() != Boolean.parseBoolean(strings[1])) {
                                client.debugRenderer.toggleShowChunkBorder();
                            }
                        }
                        case "hitboxes" -> client.getEntityRenderDispatcher().setRenderHitboxes(Boolean.parseBoolean(strings[1]));
                        case "perspective" -> options.setPerspective(Perspective.values()[Integer.parseInt(strings[1]) % 3]);
                        case "piedirectory" -> {
                            if (!strings[1].split("\\.")[0].equals("root")) break;
                            ((MinecraftClientAccessor)client).setOpenProfilerSection(strings[1].replace('.','\u001e'));
                        }
                        case "fovOnWorldJoin" -> fovOnWorldJoin = Integer.parseInt(strings[1]);
                        case "renderDistanceOnWorldJoin" -> renderDistanceOnWorldJoin = Integer.parseInt(strings[1]);
                        case "simulationDistanceOnWorldJoin" -> simulationDistanceOnWorldJoin = Integer.parseInt(strings[1]);
                        case "entityDistanceScalingOnWorldJoin" -> entityDistanceScalingOnWorldJoin = Double.parseDouble(strings[1]);
                        case "key" -> {
                            for (KeyBinding keyBinding : options.allKeys) {
                                if (string0_split[1].equals(keyBinding.getTranslationKey())) {
                                    keyBinding.setBoundKey(InputUtil.fromTranslationKey(strings[1])); break;
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
                        case "modelPart" -> {
                            for (PlayerModelPart playerModelPart : PlayerModelPart.values()) {
                                if (string0_split[1].equals(playerModelPart.getName())) {
                                    options.togglePlayerModelPart(playerModelPart, Boolean.parseBoolean(strings[1])); break;
                                }
                            }
                        }
                    }
                    // Some options.txt settings which aren't accessible in vanilla Minecraft and some unnecessary settings (like Multiplayer stuff) are not included.
                } catch (Exception exception) {
                    if (!string.equals("sneaking:") && !string.equals("sprinting:") && !string.equals("chunkborders:") && !string.equals("hitboxes:") && !string.equals("renderDistanceOnWorldJoin:") && !string.equals("simulationDistanceOnWorldJoin:") && !string.equals("entityDistanceScalingOnWorldJoin:") && !string.equals("fovOnWorldJoin:") && !string.equals("lastServer:")) {
                        LOGGER.warn("Skipping bad StandardSetting: " + string);
                    }
                }
            } while ((string = bufferedReader.readLine()) != null);
            KeyBinding.updateKeysByCode();
            bufferedReader.close();
            LOGGER.info("Finished loading StandardSettings ({} ms)", (System.nanoTime() - start) / 1000000.0f);
        }
        catch (Exception exception2) {
            LOGGER.error("Failed to load StandardSettings", exception2);
        }
    }

    public static void changeSettingsOnJoin() {
        long start = System.nanoTime();

        if (renderDistanceOnWorldJoin != 0) {
            options.getViewDistance().setValue(renderDistanceOnWorldJoin);
        }
        if (simulationDistanceOnWorldJoin != 0) {
            options.getSimulationDistance().setValue(simulationDistanceOnWorldJoin);
        }
        if (entityDistanceScalingOnWorldJoin != 0) {
            options.getEntityDistanceScaling().setValue(entityDistanceScalingOnWorldJoin);
        }
        if (fovOnWorldJoin != 0) {
            options.getFov().setValue(fovOnWorldJoin);
        }
        if (fovOnWorldJoin != 0 || renderDistanceOnWorldJoin != 0 || simulationDistanceOnWorldJoin != 0 || entityDistanceScalingOnWorldJoin != 0) {
            entityDistanceScalingOnWorldJoin = renderDistanceOnWorldJoin = simulationDistanceOnWorldJoin = fovOnWorldJoin = 0;
            options.write();
            LOGGER.info("Changed Settings on World Join ({} ms)", (System.nanoTime() - start) / 1000000.0f);
        }
    }

    public static void checkSettings() {
        long start = System.nanoTime();

        options.getMouseSensitivity().setValue(check("Sensitivity", options.getMouseSensitivity().getValue(), 0, 1));
        options.getFov().setValue(Math.round(check("FOV", options.getFov().getValue(), 30, 110)));
        options.getDistortionEffectScale().setValue(check("Distortion Effects", options.getDistortionEffectScale().getValue(), 0, 1));
        options.getFovEffectScale().setValue(check("FOV Effects", options.getFovEffectScale().getValue(),0,1));
        options.getGamma().setValue(check("Brightness", options.getGamma().getValue(), 0, 1));
        options.getViewDistance().setValue(check("Render Distance", options.getViewDistance().getValue(), 2, 32));
        options.getSimulationDistance().setValue(check("Simulation Distance", options.getSimulationDistance().getValue(), 5, 32));
        options.getEntityDistanceScaling().setValue((double) Math.round(check("Entity Distance", options.getEntityDistanceScaling().getValue(), 0.5f, 5) * 4) / 4);
        options.getGuiScale().setValue(check("GUI Scale", options.getGuiScale().getValue(), 0, 4));
        options.getMaxFps().setValue(check("Max FPS", options.getMaxFps().getValue(), 1, 260));
        options.getBiomeBlendRadius().setValue(check("Biome Blend Radius", options.getBiomeBlendRadius().getValue(), 0, 7));
        options.getChtOpacity().setValue(check("Chat Opacity", options.getChtOpacity().getValue(), 0, 1));
        options.getChatLineSpacing().setValue(check("Line Spacing", options.getChatLineSpacing().getValue(), 0, 1));
        options.getTextBackgroundOpacity().setValue(check("Text Background Opacity", options.getTextBackgroundOpacity().getValue(), 0, 1));
        options.getChatHeightFocused().setValue(check("(Chat) Focused Height", options.getChatHeightFocused().getValue(), 0, 1));
        options.getChatDelay().setValue(check("Chat Delay", options.getChatDelay().getValue(), 0, 6));
        options.getChatHeightUnfocused().setValue(check("(Chat) Unfocused Height", options.getChatHeightUnfocused().getValue(), 0, 1));
        options.getChatScale().setValue(check("Chat Text Size", options.getChatScale().getValue(), 0, 1));
        options.getChatWidth().setValue(check("Chat Width", options.getChatWidth().getValue(), 0, 1));
        if (options.getMipmapLevels().getValue() < 0 || options.getMipmapLevels().getValue() > 4) {
            options.getMipmapLevels().setValue(check("Mipmap Levels", options.getMipmapLevels().getValue(), 0, 4));
            client.setMipmapLevels(options.getMipmapLevels().getValue());
            ((BakedModelManagerAccessor)client.getBakedModelManager()).callApply(((BakedModelManagerAccessor)client.getBakedModelManager()).callPrepare(client.getResourceManager(), client.getProfiler()), client.getResourceManager(), client.getProfiler());
        }
        options.getMouseWheelSensitivity().setValue(check("Scroll Sensitivity", options.getMouseWheelSensitivity().getValue(), 0.01, 10));
        for (SoundCategory soundCategory : SoundCategory.values()) {
            options.setSoundVolume(soundCategory, check("(Music & Sounds) " + soundCategory.name(), options.getSoundVolume(soundCategory)));
        }

        if (renderDistanceOnWorldJoin != 0) {
            renderDistanceOnWorldJoin = check("Render Distance (On World Join)", renderDistanceOnWorldJoin, 2, 32);
        }
        if (simulationDistanceOnWorldJoin != 0) {
            simulationDistanceOnWorldJoin = check("Simulation Distance (On World Join)", simulationDistanceOnWorldJoin, 5, 32);
        }
        if (entityDistanceScalingOnWorldJoin != 0) {
            entityDistanceScalingOnWorldJoin = (double) Math.round(check("Entity Distance (On World Join)", entityDistanceScalingOnWorldJoin, 0.5f, 5) * 4) / 4;
        }
        if (fovOnWorldJoin != 0) {
            fovOnWorldJoin = Math.round(check("FOV (On World Join)", fovOnWorldJoin, 30, 110));
        }

        window.setScaleFactor(window.calculateScaleFactor(options.getGuiScale().getValue(), options.getForceUnicodeFont().getValue()));
        LOGGER.info("Finished checking Settings ({} ms)", (System.nanoTime() - start) / 1000000.0f);
    }

    private static double check(String settingName, double setting, double min, double max) {
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

    private static float check(String settingName, float setting) {
        if (setting < 0) {
            LOGGER.warn(settingName + " was too low! ({})", setting);
            return 0;
        }
        if (setting > 1) {
            LOGGER.warn(settingName + " was too high! ({})", setting);
            return 1;
        }
        return setting;
    }

    private static int check(String settingName, int setting, int min, int max) {
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