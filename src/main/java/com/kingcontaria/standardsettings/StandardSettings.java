package com.kingcontaria.standardsettings;

import com.kingcontaria.standardsettings.mixins.MinecraftClientAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.entity.player.PlayerEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.Scanner;

public class StandardSettings {

    public static final Logger LOGGER = LogManager.getLogger();
    private static final MinecraftClient client = MinecraftClient.getInstance();
    private static final GameOptions options = client.options;
    public static final File standardoptionsFile = new File("config/standardoptions.txt");
    public static final File optionsFile = new File("options.txt");
    public static boolean changeOnGainedFocus = false;
    private static int renderDistanceOnWorldJoin = 0;
    private static float fovOnWorldJoin = 0;

    public static void load() {

        fovOnWorldJoin = renderDistanceOnWorldJoin = 0;

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(standardoptionsFile))){
            if (!standardoptionsFile.exists()) {
                LOGGER.error("standardoptions.txt is missing");
                return;
            }
            String string;
            while ((string = bufferedReader.readLine()) != null) {
                String[] strings = string.split(":");
                String[] string0_split = strings[0].split("_");
                try {
                    switch (string0_split[0]){
                        case "mouseSensitivity" -> options.sensitivity = Float.parseFloat(strings[1]);
                        case "fov" -> options.fov = Float.parseFloat(strings[1]) * 40.0f + 70.0f;
                        case "gamma" -> options.gamma = Float.parseFloat(strings[1]);
                        case "invertYMouse" -> options.invertYMouse = Boolean.parseBoolean(strings[1]);
                        case "renderDistance" -> options.viewDistance = Integer.parseInt(strings[1]);
                        case "guiScale" -> options.guiScale = Integer.parseInt(strings[1]);
                        case "particles" -> options.particle = Integer.parseInt(strings[1]);
                        case "bobView" -> options.bobView = Boolean.parseBoolean(strings[1]);
                        case "maxFps" -> options.maxFramerate = Integer.parseInt(strings[1]);
                        case "fancyGraphics" -> options.fancyGraphics = Boolean.parseBoolean(strings[1]);
                        case "ao" -> options.ao = strings[1].equals("true") ? 2 : (strings[1].equals("false") ? 0 : Integer.parseInt(strings[1]));
                        case "renderClouds" -> options.cloudMode = strings[1].equals("true") ? 2 : strings[1].equals("false") ? 0 : 1;
                        case "chatVisibility" -> options.chatVisibilityType = PlayerEntity.ChatVisibilityType.getById(Integer.parseInt(strings[1]));
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
                        case "useVbo" -> options.vbo = Boolean.parseBoolean(strings[1]);
                        case "advancedItemTooltips" -> options.advancedItemTooltips = Boolean.parseBoolean(strings[1]);
                        case "pauseOnLostFocus" -> options.pauseOnLostFocus = Boolean.parseBoolean(strings[1]);
                        case "touchscreen" -> options.touchScreen = Boolean.parseBoolean(strings[1]);
                        case "chatHeightFocused" -> options.chatHeightFocused = Float.parseFloat(strings[1]);
                        case "chatHeightUnfocused" -> options.chatHeightUnfocused = Float.parseFloat(strings[1]);
                        case "chatScale" -> options.chatScale = Float.parseFloat(strings[1]);
                        case "chatWidth" -> options.chatWidth = Float.parseFloat(strings[1]);
                        case "forceUnicodeFont" -> client.textRenderer.method_960(options.forceUnicode = Boolean.parseBoolean(strings[1]));
                        case "allowBlockAlternatives" -> options.alternativeBlocks = Boolean.parseBoolean(strings[1]);
                        case "reducedDebugInfo" -> options.reducedDebugInfo = Boolean.parseBoolean(strings[1]);
                        case "entityShadows" -> options.entityShadows = Boolean.parseBoolean(strings[1]);
                        case "perspective" -> options.perspective = Integer.parseInt(strings[1]);
                        case "piedirectory" -> ((MinecraftClientAccessor)client).setOpenProfilerSection(strings[1]);
                        case "hitboxes" -> client.getEntityRenderManager().method_10205(Boolean.parseBoolean(strings[1]));
                        case "renderDistanceOnWorldJoin" -> {
                            try {
                                renderDistanceOnWorldJoin = Integer.parseInt(strings[1]);
                            } catch (NumberFormatException e) {
                                // empty catch block
                            }
                        }
                        case "fovOnWorldJoin" -> {
                            try {
                                fovOnWorldJoin = Float.parseFloat(strings[1]);
                            } catch (NumberFormatException e) {
                                // empty catch block
                            }
                        }
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
                        case "modelPart" -> {
                            for (PlayerModelPart playerModelPart : PlayerModelPart.values()) {
                                if (strings[0].equals("modelPart_" + playerModelPart.getName())) {
                                    options.setPlayerModelPart(playerModelPart, Boolean.parseBoolean(strings[1])); break;
                                }
                            }
                        }
                    }

                    // Excluded are 3D Anaglyph and Mipmap Levels.
                    // Additionally, options.txt settings which aren't accessible in vanilla Minecraft and some unnecessary settings (like Multiplayer and Streaming stuff) are not included.
                } catch (Exception exception) {
                    if(!string.equals("renderDistanceOnWorldJoin:") && !string.equals("fovOnWorldJoin:") && !string.equals("lastServer:")){
                        LOGGER.warn("Skipping bad StandardSetting: " + string);
                    }
                }
            }
            KeyBinding.updateKeysByCode();
            LOGGER.info("Finished loading StandardSettings");
        } catch (Exception exception2) {
            LOGGER.error("Failed to load StandardSettings", exception2);
        }
    }

    public static void changeSettingsOnJoin() {
        if (renderDistanceOnWorldJoin != 0) {
            options.viewDistance = renderDistanceOnWorldJoin;
        }
        if (fovOnWorldJoin != 0) {
            options.fov = fovOnWorldJoin;
        }
        if (fovOnWorldJoin != 0 || renderDistanceOnWorldJoin != 0) {
            options.save();
            LOGGER.info("Changed Settings on World Join");
        }
    }

    public static void checkSettings(){
        options.sensitivity = Check("Sensitivity",options.sensitivity,0,1);
        options.fov = Math.round(Check("FOV",options.fov,30,110));
        options.gamma = Check("Brightness",options.gamma,0,5);
        options.viewDistance = Check("Render Distance",options.viewDistance,2,32);
        options.guiScale = Check("GUI Scale",options.guiScale,0,4);
        //Because of DynamicFPS/SleepBackground I will not mess with adjusting FPS :)
        options.chatOpacity = Check("Chat Opacity",options.chatOpacity,0,1);
        options.chatHeightFocused = Check("(Chat) Focused Height",options.chatHeightFocused,0,1);
        options.chatHeightUnfocused = Check("(Chat) Unfocused Height",options.chatHeightUnfocused,0,1);
        options.chatScale = Check("Chat Text Size",options.chatScale,0,1);
        options.chatWidth = Check("ChatWidth",options.chatWidth,0,1);
        for(SoundCategory soundCategory : SoundCategory.values()){
            float i = Check(soundCategory.getName(),options.getSoundVolume(soundCategory),0,1);
            client.getSoundManager().updateSoundVolume(soundCategory, i);
            options.setSoundVolume(soundCategory, i);
        }
        if(options.mipmapLevels < 0){
            LOGGER.warn("Mipmap Levels was too low! (" + options.mipmapLevels + ")");
            LOGGER.error("Mipmap Levels can not be corrected!");
        }else {
            if (options.mipmapLevels > 4) {
                LOGGER.warn("Mipmap Levels was too high! (" + options.mipmapLevels + ")");
                LOGGER.error("Mipmap Levels can not be corrected!");
            }
        }

        if(renderDistanceOnWorldJoin != 0){
            renderDistanceOnWorldJoin = Check("Render Distance (On World Join)",renderDistanceOnWorldJoin,2,32);
        }
        if(fovOnWorldJoin != 0){
            fovOnWorldJoin = Math.round(Check("FOV (On World Join)",fovOnWorldJoin,30,110));
        }

        LOGGER.info("Finished checking Settings");
    }

    private static float Check(String settingName, float setting, float min, float max){
        if(setting<min){
            LOGGER.warn(settingName + " was too low! (" + setting + ")");
            return min;
        }
        if(setting>max){
            LOGGER.warn(settingName + " was too high! (" + setting + ")");
            return max;
        }
        return setting;
    }

    private static int Check(String settingName, int setting, int min, int max){
        if(setting<min){
            LOGGER.warn(settingName + " was too low! (" + setting + ")");
            return min;
        }
        if(setting>max){
            LOGGER.warn(settingName + " was too high! (" + setting + ")");
            return max;
        }
        return setting;
    }

    public static void save(File directory) {
        LOGGER.info("Saving StandardSettings...");

        if(!optionsFile.exists()) options.save();
        if(!directory.getParentFile().exists()) directory.getParentFile().mkdir();

        try{
            Files.copy(optionsFile.toPath(), directory.toPath(), StandardCopyOption.REPLACE_EXISTING);
            Files.write(directory.toPath(), ("perspective:" + options.perspective + System.lineSeparator()).getBytes(), StandardOpenOption.APPEND);
            Files.write(directory.toPath(), ("piedirectory:" + ((MinecraftClientAccessor)client).getOpenProfilerSection() + System.lineSeparator()).getBytes(), StandardOpenOption.APPEND);
            Files.write(directory.toPath(), ("hitboxes:" + client.getEntityRenderManager().method_10203() + System.lineSeparator()).getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if(directory == standardoptionsFile){
            String rd = "renderDistanceOnWorldJoin:";
            String fov = "fovOnWorldJoin:";

            try (Scanner standardoptionsTxt = new Scanner(directory)) {
                while (standardoptionsTxt.hasNextLine()) {
                    String line = standardoptionsTxt.nextLine();
                    switch (line.split(":")[0]) {
                        case "renderDistanceOnWorldJoin" -> rd = line;
                        case "fovOnWorldJoin" -> fov = line;
                    }
                    Files.write(directory.toPath(), (rd + System.lineSeparator() + fov).getBytes(), StandardOpenOption.APPEND);
                }
            } catch (IOException e) {
                // empty catch block
            }
            LOGGER.info("Finished saving StandardSettings");
        }
    }
}