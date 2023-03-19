package com.kingcontaria.standardsettings;

import com.kingcontaria.standardsettings.mixins.accessors.MinecraftClientAccessor;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.Window;
import net.minecraft.util.Language;
import org.lwjgl.opengl.Display;
import org.spongepowered.include.com.google.common.io.Files;
import xyz.tildejustin.nopaus.NoPaus;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Logger;

public class StandardSettings {

    public static final int[] version = new int[]{1,2,2,0};
    public static final Logger LOGGER = Logger.getLogger("StandardSettings");
    private static final Minecraft client = Minecraft.getMinecraft();
    public static final GameOptions options = client.options;
    public static final File standardoptionsFile = new File(FabricLoader.getInstance().getConfigDir().resolve("standardoptions.txt").toUri());
    public static boolean changeOnWindowActivation = false;
    public static boolean changeOnResize = false;
    private static Optional<Integer> renderDistanceOnWorldJoin = Optional.empty();
    private static Optional<Float> fovOnWorldJoin = Optional.empty();
    private static Optional<Integer> guiScaleOnWorldJoin = Optional.empty();
    public static OptionsCache optionsCache = new OptionsCache(client);
    public static String lastWorld;
    public static String[] standardoptionsCache;
    public static Map<File, Long> filesLastModifiedMap;
    public static float defaultFOV;
    public static boolean HAS_NO_PAUS;

    public static void load() {
        long start = System.nanoTime();

        emptyOnWorldJoinOptions();

        try {
            if (!standardoptionsFile.exists()) {
                standardoptionsCache = null;
                LOGGER.warning("standardoptions.txt is missing");
                return;
            }

            // caches options for last world before applying standardoptions to reload later if necessary
            // allows for verifiability when rejoining a world after accidentally quitting with Atum
            if (lastWorld != null) {
                optionsCache.save(lastWorld);
                lastWorld = null;
            }

            // reload and cache standardoptions if necessary
            if (standardoptionsCache == null || wereFilesModified(filesLastModifiedMap)) {
                LOGGER.info("Reloading & caching StandardSettings...");
                List<String> lines = resolveGlobalFile(standardoptionsFile);
                if (lines == null) {
                    LOGGER.warning("standardoptions.txt is empty");
                    return;
                }
                standardoptionsCache = lines.toArray(new String[0]);
            }
            load(standardoptionsCache);
            LOGGER.info("Finished loading StandardSettings + (" + (System.nanoTime() - start) / 1000000.0f + " ms)");
        } catch (Exception e) {
            standardoptionsCache = null;
            LOGGER.warning("Failed to load StandardSettings");
            e.printStackTrace();
        }
    }

    // checks if standardoptions file chain has been modified
    private static boolean wereFilesModified(Map<File, Long> map) {
        if (map == null) {
            return true;
        }
        boolean wereFilesModified = false;
        for (Map.Entry<File, Long> entry : map.entrySet()) {
            wereFilesModified |= !entry.getKey().exists() || entry.getKey().lastModified() != entry.getValue();
        }
        return wereFilesModified;
    }

    // creates a standardoptions file chain by checking if the first line of a file points to another file directory
    public static List<String> resolveGlobalFile(File file) {
        filesLastModifiedMap = new HashMap<>();
        List<String> lines = null;
        do {
            // save the last modified time of each file to be checked later
            filesLastModifiedMap.put(file, file.lastModified());

            try {
                lines = Files.readLines(file, StandardCharsets.UTF_8);
            } catch (IOException e) {
                break;
            }
        } while (lines != null && lines.size() > 0 && (file = new File(lines.get(0))).exists() && !filesLastModifiedMap.containsKey(file));
        return lines;
    }

    // load standardoptions from cache, the heart of the mod if you will
    private static void load(String[] lines) {
        boolean reload = false;

        for (String line : lines) {
            try {
                String[] strings = line.split(":", 2);

                // skip line if value is empty
                if (strings.length < 2 || (strings[1] = strings[1].trim()).equals("")) {
                    continue;
                }
                String[] string0_split = strings[0].split("_", 2);

                switch (string0_split[0]) {
                    case "music" -> options.musicVolume = Float.parseFloat(strings[1]);
                    case "sound" -> options.soundVolume = Float.parseFloat(strings[1]);
                    case "invertYMouse" -> options.invertYMouse = Boolean.parseBoolean(strings[1]);
                    case "mouseSensitivity" -> options.sensitivity = Float.parseFloat(strings[1]);
                    case "fov" -> options.fov = Float.parseFloat(strings[1]);
                    case "gamma" -> options.gamma = Float.parseFloat(strings[1]);
                    case "viewDistance" -> options.renderDistance = Integer.parseInt(strings[1]);
                    case "guiScale" -> {
                        options.guiScale = Integer.parseInt(strings[1]);
                        Window window = new Window(Minecraft.getMinecraft().options, Minecraft.getMinecraft().width, Minecraft.getMinecraft().height);
                        int n2 = window.getWidth();
                        int n3 = window.getHeight();
                        Minecraft.getMinecraft().currentScreen.method_1028(Minecraft.getMinecraft(), n2, n3);
                    }
                    case "particles" -> options.particle = Integer.parseInt(strings[1]);
                    case "bobView" -> options.bobView = Boolean.parseBoolean(strings[1]);
                    case "anaglyph3d" -> {
                        options.anaglyph3d = Boolean.parseBoolean(strings[1]);
                        Minecraft.getMinecraft().textureManager.updateAnaglyph3D();
                    }
                    case "advancedOpengl" -> options.advancedOpengl = Boolean.parseBoolean(strings[1]);
                    case "fpsLimit" -> options.maxFramerate = Integer.parseInt(strings[1]);
                    case "difficulty" -> options.difficultyLevel = Integer.parseInt(strings[1]);
                    case "fancyGraphics" -> options.fancyGraphics = Boolean.parseBoolean(strings[1]);
                    case "ao" -> options.ambientOcculsion = Boolean.parseBoolean(strings[1]);
                    case "clouds" -> options.renderClouds = Boolean.parseBoolean(strings[1]);
                    case "skin" -> options.currentTexturePackName = strings[1];
                    case "lastServer" -> options.lastServer = strings[1];
                    case "lang" -> {
                        options.language = strings[1];
                        Language.getInstance().setCode(options.language);
                        Minecraft.getMinecraft().textRenderer.setUnicode(Language.getInstance().method_638());
                        Minecraft.getMinecraft().textRenderer.setRightToLeft(Language.hasSpecialCharacters(options.language));
                    }
                    case "chatVisibility" -> options.chatVisibility = Integer.parseInt(strings[1]);
                    case "chatColors" -> options.chatColor = Boolean.parseBoolean(strings[1]);
                    case "chatLinks" -> options.chatLink = Boolean.parseBoolean(strings[1]);
                    case "chatLinksPrompt" -> options.chatLinkPrompt = Boolean.parseBoolean(strings[1]);
                    case "chatOpacity" -> options.chatOpacity = Float.parseFloat(strings[1]);
                    case "serverTextures" -> options.useServerTextures = Boolean.parseBoolean(strings[1]);
                    case "snooperEnabled" -> options.snopperEnabled = Boolean.parseBoolean(strings[1]);
                    case "fullscreen" -> {
                        if (options.fullscreen != Boolean.parseBoolean(strings[1])) {
                            if (Display.isActive()) {
                                client.toggleFullscreen();
                                options.fullscreen = Boolean.parseBoolean(strings[1]);
                            } else {
                                LOGGER.warning("Could not reset fullscreen mode because window wasn't focused!");
                            }
                        }
                    }
                    case "enableVsync" -> Display.setVSyncEnabled(options.vsync = Boolean.parseBoolean(strings[1]));
                    case "key" -> {
                        for (KeyBinding keyBinding : options.allKeys) {
                            if (string0_split[1].equals(keyBinding.translationKey)) {
                                keyBinding.code = Integer.parseInt(strings[1]);
                                break;
                            }
                        }
                    }
                    case "perspective" -> options.perspective = Integer.parseInt(strings[1]) % 3;
                    case "piedirectory" -> {
                        if (!strings[1].split("\\.")[0].equals("root")) break;
                        ((MinecraftClientAccessor) client).setOpenProfilerSection(strings[1]);
                    }
                    case "f1" -> options.hudHidden = Boolean.parseBoolean(strings[1]);
                    case "fovOnWorldJoin" ->
                            fovOnWorldJoin = Optional.of(Float.parseFloat(strings[1]) < 5 ? Float.parseFloat(strings[1]) * (defaultFOV / 7 * 4) + defaultFOV : (Integer.parseInt(strings[1]) - (70.0f - defaultFOV)) / (40.0f - defaultFOV / 70.0f * 39.0f));
                    case "guiScaleOnWorldJoin" -> guiScaleOnWorldJoin = Optional.of(Integer.parseInt(strings[1]));
                    case "renderDistanceOnWorldJoin" ->
                            renderDistanceOnWorldJoin = Optional.of(Integer.parseInt(strings[1]));
                    case "changeOnResize" -> changeOnResize = Boolean.parseBoolean(strings[1]);
                    case "pauseOnLostFocus" -> {
                        if (HAS_NO_PAUS) NoPaus.pauseOnLostFocus = Boolean.parseBoolean(strings[1]);
                    }
                }
                // Some options.txt settings which aren't accessible in vanilla Minecraft and some unnecessary settings (like Multiplayer and Streaming stuff) are not included.
                // also has a few extra settings that can be reset that Minecraft doesn't save to options.txt, but are important in speedrunning
            } catch (Exception e) {
                LOGGER.warning("Skipping bad StandardSetting: " + line);
            }
        }
        KeyBinding.updateKeysByCode();
    }

    // load OnWorldJoin options if present
    public static void changeSettingsOnJoin() {
        long start = System.nanoTime();

        renderDistanceOnWorldJoin.ifPresent(renderDistance -> options.renderDistance = renderDistance);
        fovOnWorldJoin.ifPresent(fov -> options.fov = fov);
        guiScaleOnWorldJoin.ifPresent(guiScale -> {
            options.guiScale = guiScale;
            Window window = new Window(Minecraft.getMinecraft().options, Minecraft.getMinecraft().width, Minecraft.getMinecraft().height);
            int n2 = window.getWidth();
            int n3 = window.getHeight();
            Minecraft.getMinecraft().currentScreen.method_1028(Minecraft.getMinecraft(), n2, n3);
        });

        if (fovOnWorldJoin.isPresent() || guiScaleOnWorldJoin.isPresent() || renderDistanceOnWorldJoin.isPresent()) {
            emptyOnWorldJoinOptions();
            options.save();
            LOGGER.info("Changed Settings on World Join ("+ (System.nanoTime() - start) / 1000000.0f + " ms)");
        }
    }

    // resets OnWorldJoin options to their default (empty) state
    private static void emptyOnWorldJoinOptions() {
        fovOnWorldJoin = Optional.empty();
        guiScaleOnWorldJoin = Optional.empty();
        renderDistanceOnWorldJoin = Optional.empty();
        changeOnResize = false;
        changeOnWindowActivation = false;
    }

    // makes sure the values are within the boundaries of vanilla minecraft / src rule set
    public static void checkSettings() {
        long start = System.nanoTime();

        options.sensitivity = check("Sensitivity", options.sensitivity * 2, 0, 2, true) / 2;
        options.fov = check("FOV", options.fov, 0.0f, 1.0f, false);
//        gamma 1 is vanilla
        options.gamma = check("Brightness", options.gamma, 0, 1, true);
        options.renderDistance = check("Render Distance", options.renderDistance, 0, 3);
        options.guiScale = check("GUI Scale", options.guiScale, 0, 3);
        options.maxFramerate = check("Max Framerate", options.maxFramerate, 1, 260);
        options.chatOpacity = check("(Chat) Opacity", options.chatOpacity, 0, 1, true);
        options.soundVolume = check("Sound Volume", options.soundVolume, 0F, 1F, false);
        options.musicVolume = check("Music Volume", options.musicVolume, 0F, 1F, false);

        if (renderDistanceOnWorldJoin.isPresent()) {
            renderDistanceOnWorldJoin = Optional.of(check("Render Distance (On World Join)", renderDistanceOnWorldJoin.get(), 0, 3));
        }
        if (fovOnWorldJoin.isPresent()) {
            fovOnWorldJoin = Optional.of(check("FOV (On World Join)", fovOnWorldJoin.get(), defaultFOV / 7 * 3, defaultFOV / 70 * 109 + 1, false));
            if (defaultFOV == 70.0f) fovOnWorldJoin = Optional.of((float) fovOnWorldJoin.get().intValue());
        }
        if (guiScaleOnWorldJoin.isPresent()) {
            guiScaleOnWorldJoin = Optional.of(check("GUI Scale (On World Join)", guiScaleOnWorldJoin.get(), 0, 3));
        }

        options.save();

        LOGGER.info("Finished checking and saving Settings (" + (System.nanoTime() - start) / 1000000.0f + " ms)");
    }

    // check methods return the value of the setting, adjusted to be in the given bounds
    // if a setting is outside the bounds, it also gives a log output to signal the value has been corrected
    private static float check(String settingName, float setting, float min, float max, boolean percent) {
        if (setting < min) {
            LOGGER.warning(settingName + " was too low! (" + (percent ? asPercent(setting) : setting) + ")");
            return min;
        }
        if (setting > max) {
            LOGGER.warning(settingName + " was too high! (" + (percent ? asPercent(setting) : setting)+ ")");
            return max;
        }
        return setting;
    }

    private static int check(String settingName, int setting, int min, int max) {
        if (setting < min) {
            LOGGER.warning(settingName + " was too low! (" + setting + ")");
            return min;
        }
        if (setting > max) {
            LOGGER.warning(settingName + " was too high! (" + setting + ")");
            return max;
        }
        return setting;
    }

    private static String asPercent(double value) {
        return value * 100 == (int) (value * 100) ? (int) (value * 100) + "%" : value * 100 + "%";
    }

    // returns the contents for a new standardoptions.txt file
    public static String getStandardoptionsTxt() {
        String l = System.lineSeparator();
        StringBuilder string = new StringBuilder(
                "music:" + options.musicVolume + l +
                "sound:" + options.soundVolume + l +
                "invertYMouse:" + options.invertYMouse + l +
                "mouseSensitivity:" + options.sensitivity + l +
                "fov:" + (options.fov - 70.0f) / 40.0f + l +
                "gamma:" + options.gamma + l +
                "viewDistance:" + options.renderDistance + l +
                "guiScale:" + options.guiScale + l +
                "particles:" + options.particle + l +
                "bobView:" + options.bobView + l +
                "anaglyph3d:" + options.anaglyph3d + l +
                "advancedOpengl:" + options.advancedOpengl + l +
                "fpsLimit:" + options.maxFramerate + l +
                "difficulty:" + options.difficultyLevel + l +
                "fancyGraphics:" + options.fancyGraphics + l +
                "ao:" + options.ambientOcculsion + l +
                "clouds:" + options.renderClouds + l +
                "skin:" + options.currentTexturePackName + l +
                "lastServer:" + options.lastServer + l +
                "lang:" + options.language + l +
                "chatVisibility:" + options.chatVisibility + l +
                "chatColors:" + options.chatColor + l +
                "chatLinks:" + options.chatLink + l +
                "chatLinksPrompt:" + options.chatLinkPrompt + l +
                "chatOpacity:" + options.chatOpacity + l +
                "serverTextures:" + options.useServerTextures + l +
                "snooperEnabled:" + options.snopperEnabled + l +
                "fullscreen:" + options.fullscreen + l +
                "enableVsync:" + options.vsync + l +
                "hideServerAddress:" + options.hideServerAddress + l
        );

        if (HAS_NO_PAUS) {
            string.append("pauseOnLostFocus:").append(NoPaus.pauseOnLostFocus).append(l);
        }

        for (KeyBinding keyBinding : options.allKeys) {
            string.append("key_").append(keyBinding.translationKey).append(":").append(keyBinding.code).append(l);
        }

        string.append("perspective:").append(l).append("piedirectory:").append(l).append("f1:").append(l).append("fovOnWorldJoin:").append(l).append("guiScaleOnWorldJoin:").append(l).append("renderDistanceOnWorldJoin:").append(l).append("changeOnResize:false");

        return string.toString();
    }

    public static List<String> checkVersion(int[] fileVersion, List<String> existingLines) {
        if (compareVersions(fileVersion, version)) {
            LOGGER.warning("standardoptions.txt was marked with an outdated StandardSettings version ({}), updating now..." + String.join(".", Arrays.stream(fileVersion).mapToObj(String::valueOf).toArray(String[]::new)));
        } else {
            return null;
        }

        // remove the values from the lines
        if (existingLines != null) {
            existingLines.replaceAll(line -> line.split(":", 2)[0]);
        }

        List<String> lines = new ArrayList<>();

        checking:
        {
            // add lines added in the pre-releases of StandardSettings v1.2.1
            if (compareVersions(fileVersion, new int[]{1, 2, 1, -1000})) {
                if (existingLines != null && (existingLines.contains("entityCulling") || existingLines.contains("f1") || existingLines.contains("guiScaleOnWorldJoin") || existingLines.contains("changeOnResize"))) {
                    break checking;
                }
                lines.add("f1:");
                lines.add("guiScaleOnWorldJoin:");
                lines.add("changeOnResize:false");
            }
        }

        if (lines.size() == 0) {
            LOGGER.info("Didn't find anything to update, good luck on the runs!");
            return null;
        }
        return lines;
    }

    // returns true when versionToCheck is older than versionToCompareTo
    public static boolean compareVersions(int[] versionToCheck, int[] versionToCompareTo) {
        for (int i = 0; i < Math.max(versionToCheck.length, versionToCompareTo.length); i++) {
            int v1 = versionToCheck.length <= i ? 0 : versionToCheck[i];
            int v2 = versionToCompareTo.length <= i ? 0 : versionToCompareTo[i];
            if (v1 == v2) continue;
            return v1 < v2;
        }
        return false;
    }

    public static String getVersion() {
        return String.join(".", Arrays.stream(version).mapToObj(String::valueOf).toArray(String[]::new));
    }
}