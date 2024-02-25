package com.kingcontaria.standardsettings;

import com.google.common.io.Files;
import com.kingcontaria.standardsettings.mixins.accessors.LanguageManagerAccessor;
import com.kingcontaria.standardsettings.mixins.accessors.MinecraftClientAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.class_0_686;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Arm;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.Display;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Environment(value= EnvType.CLIENT)
public class StandardSettings {

    public static final int[] version = new int[]{1,2,2,0};
    public static final Logger LOGGER = LogManager.getLogger();
    public static final MinecraftClient client = MinecraftClient.getInstance();
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

    public static void load() {
        long start = System.nanoTime();

        emptyOnWorldJoinOptions();

        try {
            if (!standardoptionsFile.exists()) {
                standardoptionsCache = null;
                LOGGER.error("standardoptions.txt is missing");
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
                    LOGGER.error("standardoptions.txt is empty");
                    return;
                }
                standardoptionsCache = lines.toArray(new String[0]);
            }
            load(standardoptionsCache);
            LOGGER.info("Finished loading StandardSettings ({} ms)", (System.nanoTime() - start) / 1000000.0f);
        } catch (Exception e) {
            standardoptionsCache = null;
            LOGGER.error("Failed to load StandardSettings", e);
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
        for (String line : lines) {
            try {
                String[] strings = line.split(":", 2);

                // skip line if value is empty
                if (strings.length < 2 || (strings[1] = strings[1].trim()).equals("")) {
                    continue;
                }
                String[] string0_split = strings[0].split("_", 2);

                switch (string0_split[0]) {
                    case "autoJump": options.autoJump = Boolean.parseBoolean(strings[1]); break;
                    case "chatColors": options.chatColors = Boolean.parseBoolean(strings[1]); break;
                    case "chatLinks": options.chatLinks = Boolean.parseBoolean(strings[1]); break;
                    case "chatLinksPrompt": options.chatLinksPrompt = Boolean.parseBoolean(strings[1]); break;
                    case "enableVsync": Display.setVSyncEnabled(options.enableVsync = Boolean.parseBoolean(strings[1])); break;
                    case "useVbo": options.field_1899 = Boolean.parseBoolean(strings[1]); break;
                    case "entityShadows": options.entityShadows = Boolean.parseBoolean(strings[1]); break;
                    case "forceUnicodeFont": client.field_1772.method_0_2391(client.getLanguageManager().method_0_4393() || (options.field_0_2663 = Boolean.parseBoolean(strings[1]))); break;
                    case "invertYMouse": options.invertYMouse = Boolean.parseBoolean(strings[1]); break;
                    case "reducedDebugInfo": options.reducedDebugInfo = Boolean.parseBoolean(strings[1]); break;
                    case "showSubtitles": options.showSubtitles = Boolean.parseBoolean(strings[1]); break;
                    case "touchscreen": options.touchscreen = Boolean.parseBoolean(strings[1]); break;
                    case "fullscreen":
                        if (options.fullscreen != Boolean.parseBoolean(strings[1])) {
                            if (Display.isActive()) {
                                client.method_0_2307();
                            } else {
                                LOGGER.error("Could not reset fullscreen mode because window wasn't focused!");
                            }
                        } break;
                    case "bobView": options.bobView = Boolean.parseBoolean(strings[1]); break;
                    case "anaglyph3d":
                        if (options.field_1820 != (options.field_1820 = Boolean.parseBoolean(strings[1]))) {
                            client.getTextureManager().method_14491(client.getResourceManager());
                        } break;
                    case "mouseSensitivity": options.field_1843 = Float.parseFloat(strings[1]); break;
                    case "fov": options.field_1826 = Float.parseFloat(strings[1]) < 5 ? Float.parseFloat(strings[1]) * 40.0f + 70.0f : Integer.parseInt(strings[1]); break;
                    case "gamma": options.field_1840 = Float.parseFloat(strings[1]); break;
                    case "renderDistance": options.viewDistance = Integer.parseInt(strings[1]); break;
                    case "guiScale": options.guiScale = Integer.parseInt(strings[1]); break;
                    case "particles": options.field_1882 = Integer.parseInt(strings[1]); break;
                    case "maxFps": options.maxFps = Integer.parseInt(strings[1]); break;
                    case "fancyGraphics": options.fancyGraphics = Boolean.parseBoolean(strings[1]); break;
                    case "ao": options.field_1841 = Integer.parseInt(strings[1]); break;
                    case "renderClouds": options.field_1814 = strings[1].equals("true") ? 2 : strings[1].equals("false") ? 0 : 1; break;
                    case "attackIndicator": options.field_1895 = Integer.parseInt(strings[1]); break;
                    case "lang":
                        if (!options.language.equals(strings[1]) && ((LanguageManagerAccessor)client.getLanguageManager()).getLanguageDefs().containsKey(strings[1])) {
                            client.getLanguageManager().setLanguage(((LanguageManagerAccessor)client.getLanguageManager()).getLanguageDefs().get(options.language = strings[1]));
                            client.getLanguageManager().method_14491(client.getResourceManager());
                        } break;
                    case "chatVisibility": options.field_1877 = PlayerEntity.class_1659.method_7360(Integer.parseInt(strings[1])); break;
                    case "chatOpacity": options.field_0_2718 = Float.parseFloat(strings[1]); break;
                    case "advancedItemTooltips": options.advancedItemTooltips = Boolean.parseBoolean(strings[1]); break;
                    case "pauseOnLostFocus": options.pauseOnLostFocus = Boolean.parseBoolean(strings[1]); break;
                    case "chatHeightFocused": options.field_1838 = Float.parseFloat(strings[1]); break;
                    case "chatHeightUnfocused": options.field_1825 = Float.parseFloat(strings[1]); break;
                    case "chatScale": options.field_1908 = Float.parseFloat(strings[1]); break;
                    case "chatWidth": options.field_1915 = Float.parseFloat(strings[1]); break;
                    case "mipmapLevels":
                        if (options.mipmapLevels != Integer.parseInt(strings[1])) {
                            client.getSpriteAtlas().setMipLevel(options.mipmapLevels = Integer.parseInt(strings[1]));
                            client.getTextureManager().bindTextureInner(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);
                            client.getSpriteAtlas().setFilter(false, options.mipmapLevels > 0);
                            ((MinecraftClientAccessor) client).getModelManager().method_14491(client.getResourceManager());
                        } break;
                    case "mainHand": options.mainArm = "left".equalsIgnoreCase(strings[1]) ? Arm.LEFT : Arm.RIGHT; break;
                    case "narrator": options.field_1896 = Integer.parseInt(strings[1]); break;
                    case "key":
                        for (KeyBinding keyBinding : options.allKeys) {
                            if (string0_split[1].equals(keyBinding.getTranslationKey())) {
                                keyBinding.method_1422(Integer.parseInt(strings[1])); break;
                            }
                        } break;
                    case "soundCategory":
                        for (SoundCategory soundCategory : SoundCategory.values()) {
                            if (string0_split[1].equals(soundCategory.getName())) {
                                options.setSoundVolume(soundCategory, Float.parseFloat(strings[1])); break;
                            }
                        } break;
                    case "modelPart":
                        for (PlayerModelPart playerModelPart : PlayerModelPart.values()) {
                            if (string0_split[1].equals(playerModelPart.getName())) {
                                options.setPlayerModelPart(playerModelPart, Boolean.parseBoolean(strings[1])); break;
                            }
                        } break;
                    case "chunkborders":
                        if (client.debugRenderer.toggleShowChunkBorder() != Boolean.parseBoolean(strings[1])) {
                            client.debugRenderer.toggleShowChunkBorder();
                        } break;
                    case "hitboxes": client.getEntityRenderDispatcher().setRenderHitboxes(Boolean.parseBoolean(strings[1])); break;
                    case "perspective": options.perspective = Integer.parseInt(strings[1]) % 3; break;
                    case "piedirectory":
                        if (!strings[1].split("\\.")[0].equals("root")) break;
                        ((MinecraftClientAccessor)client).setOpenProfilerSection(strings[1]); break;
                    case "f1": options.hudHidden = Boolean.parseBoolean(strings[1]); break;
                    case "fovOnWorldJoin": fovOnWorldJoin = Optional.of(Float.parseFloat(strings[1]) < 5 ? Float.parseFloat(strings[1]) * 40.0f + 70.0f : Integer.parseInt(strings[1])); break;
                    case "guiScaleOnWorldJoin": guiScaleOnWorldJoin = Optional.of(Integer.parseInt(strings[1])); break;
                    case "renderDistanceOnWorldJoin": renderDistanceOnWorldJoin = Optional.of(Integer.parseInt(strings[1])); break;
                    case "changeOnResize": changeOnResize = Boolean.parseBoolean(strings[1]); break;
                    // Some options.txt settings which aren't accessible in vanilla Minecraft and some unnecessary settings (like Multiplayer stuff) are not included.
                    // also has a few extra settings that can be reset that Minecraft doesn't save to options.txt, but are important in speedrunning
                }
            } catch (Exception e) {
                LOGGER.warn("Skipping bad StandardSetting: " + line);
            }
        }
        KeyBinding.updateKeysByCode();
    }

    // load OnWorldJoin options if present
    public static void changeSettingsOnJoin() {
        long start = System.nanoTime();

        renderDistanceOnWorldJoin.ifPresent(viewDistance -> {
            options.viewDistance = viewDistance;
            client.worldRenderer.scheduleTerrainUpdate();
        });
        fovOnWorldJoin.ifPresent(fov -> options.field_1826 = fov);
        guiScaleOnWorldJoin.ifPresent(guiScale -> {
            options.guiScale = guiScale;
            if (client.currentScreen != null) {
                class_0_686 window = new class_0_686(client);
                client.currentScreen.method_2233(client, window.method_0_2459(), window.method_0_2460());
            }
        });

        if (fovOnWorldJoin.isPresent() || guiScaleOnWorldJoin.isPresent() || renderDistanceOnWorldJoin.isPresent()) {
            emptyOnWorldJoinOptions();
            options.write();
            LOGGER.info("Changed Settings on World Join ({} ms)", (System.nanoTime() - start) / 1000000.0f);
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

    // makes sure the values are within the boundaries of vanilla minecraft / the speedrun.com rule set
    public static void checkSettings() {
        long start = System.nanoTime();

        options.field_1843 = check("Sensitivity", options.field_1843 * 2, 0, 2, true) / 2;
        options.field_1826 = (int) (check("FOV", options.field_1826, 30, 110, false));
        options.field_1840 = check("Brightness", options.field_1840, 0, 5, true);
        options.viewDistance = check("Render Distance", options.viewDistance, 2, 32);
        options.guiScale = check("GUI Scale", options.guiScale, 0, Integer.MAX_VALUE);
        options.maxFps = check("Max Framerate", options.maxFps, 1, 260);
        options.field_0_2718 = check("Chat Text Opacity", options.field_0_2718, 0, 1, true);
        options.field_1838 = check("(Chat) Focused Height", options.field_1838, 0, 1, false);
        options.field_1825 = check("(Chat) Unfocused Height", options.field_1825, 0, 1, false);
        options.field_1908 = check("(Chat) Scale", options.field_1908, 0, 1, true);
        options.field_1915 = check("(Chat) Width", options.field_1915, 0, 1, false);
        if (options.mipmapLevels != (options.mipmapLevels = check("Mipmap Levels", options.mipmapLevels, 0, 4))) {
            client.getSpriteAtlas().setMipLevel(options.mipmapLevels);
            client.getTextureManager().bindTextureInner(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);
            client.getSpriteAtlas().setFilter(false, options.mipmapLevels > 0);
            ((MinecraftClientAccessor) client).getModelManager().method_14491(client.getResourceManager());
        }
        for (SoundCategory soundCategory : SoundCategory.values()) {
            options.setSoundVolume(soundCategory, check("(Music & Sounds) " + SoundCategoryName.valueOf(soundCategory.name()).assignedName, options.getSoundVolume(soundCategory), 0, 1, true));
        }

        if (renderDistanceOnWorldJoin.isPresent()) {
            renderDistanceOnWorldJoin = Optional.of(check("Render Distance (On World Join)", renderDistanceOnWorldJoin.get(), 2, 32));
        }
        if (fovOnWorldJoin.isPresent()) {
            fovOnWorldJoin = Optional.of((float) (int) check("FOV (On World Join)", fovOnWorldJoin.get(), 30, 110, false));
        }
        if (guiScaleOnWorldJoin.isPresent()) {
            guiScaleOnWorldJoin = Optional.of(check("GUI Scale (On World Join)", guiScaleOnWorldJoin.get(), 0, 3));
        }

        options.write();

        LOGGER.info("Finished checking and saving Settings ({} ms)", (System.nanoTime() - start) / 1000000.0f);
    }

    // check methods return the value of the setting, adjusted to be in the given bounds
    // if a setting is outside the bounds, it also gives a log output to signal the value has been corrected
    private static float check(String settingName, float setting, float min, float max, boolean percent) {
        if (setting < min) {
            LOGGER.warn(settingName + " was too low! ({})", percent ? asPercent(setting) : setting);
            return min;
        }
        if (setting > max) {
            LOGGER.warn(settingName + " was too high! ({})", percent ? asPercent(setting) : setting);
            return max;
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

    private static String asPercent(double value) {
        return value * 100 == (int) (value * 100) ? (int) (value * 100) + "%" : value * 100 + "%";
    }

    private enum SoundCategoryName {
        MASTER("Master Volume"),
        MUSIC("Music"),
        RECORDS("Jukebox/Note Blocks"),
        WEATHER("Weather"),
        BLOCKS("Blocks"),
        HOSTILE("Hostile Creatures"),
        NEUTRAL("Friendly Creatures"),
        PLAYERS("Players"),
        AMBIENT("Ambient/Environment"),
        VOICE("Voice/Speech");

        private final String assignedName;
        SoundCategoryName(String name) {
            this.assignedName = name;
        }
    }

    // returns the contents for a new standardoptions.txt file
    public static String getStandardoptionsTxt() {
        String l = System.lineSeparator();
        StringBuilder string = new StringBuilder("autoJump:" + options.autoJump + l +
                "chatColors:" + options.chatColors + l +
                "chatLinks:" + options.chatLinks + l +
                "chatLinksPrompt:" + options.chatLinksPrompt + l +
                "enableVsync:" + options.enableVsync + l +
                "useVbo:" + options.field_1899 + l +
                "entityShadows:" + options.entityShadows + l +
                "forceUnicodeFont:" + options.field_0_2663 + l +
                "invertYMouse:" + options.invertYMouse + l +
                "reducedDebugInfo:" + options.reducedDebugInfo + l +
                "showSubtitles:" + options.showSubtitles + l +
                "touchscreen:" + options.touchscreen + l +
                "fullscreen:" + options.fullscreen + l +
                "bobView:" + options.bobView + l +
                "anaglyph3d:" + options.field_1820 + l +
                "mouseSensitivity:" + options.field_1843 + l +
                "fov:" + (options.field_1826 - 70.0f) / 40.0f + l +
                "gamma:" + options.field_1840 + l +
                "renderDistance:" + options.viewDistance + l +
                "guiScale:" + options.guiScale + l +
                "particles:" + options.field_1882 + l +
                "maxFps:" + options.maxFps + l +
                "fancyGraphics:" + options.fancyGraphics + l +
                "ao:" + options.field_1841 + l +
                "renderClouds:" + (options.field_1814 == 1 ? "fast" : options.field_1814 == 2) + l +
                "attackIndicator:" + options.field_1895 + l +
                "lang:" + options.language + l +
                "chatVisibility:" + options.field_1877.method_7362() + l +
                "chatOpacity:" + options.field_0_2718 + l +
                "advancedItemTooltips:" + options.advancedItemTooltips + l +
                "pauseOnLostFocus:" + options.pauseOnLostFocus + l +
                "chatHeightFocused:" + options.field_1838 + l +
                "chatHeightUnfocused:" + options.field_1825 + l +
                "chatScale:" + options.field_1908 + l +
                "chatWidth:" + options.field_1915 + l +
                "mipmapLevels:" + options.mipmapLevels + l +
                "mainHand:" + (options.mainArm == Arm.LEFT ? "left" : "right") + l +
                "narrator:" + options.field_1896 + l);
        for (KeyBinding keyBinding : options.allKeys) {
            string.append("key_").append(keyBinding.getTranslationKey()).append(":").append(keyBinding.method_1421()).append(l);
        }
        for (SoundCategory soundCategory : SoundCategory.values()) {
            string.append("soundCategory_").append(soundCategory.getName()).append(":").append(options.getSoundVolume(soundCategory)).append(l);
        }
        for (PlayerModelPart playerModelPart : PlayerModelPart.values()) {
            string.append("modelPart_").append(playerModelPart.getName()).append(":").append(options.getEnabledPlayerModelParts().contains(playerModelPart)).append(l);
        }
        string.append("chunkborders:").append(l).append("hitboxes:").append(l).append("perspective:").append(l).append("piedirectory:").append(l).append("f1:").append(l).append("fovOnWorldJoin:").append(l).append("guiScaleOnWorldJoin:").append(l).append("renderDistanceOnWorldJoin:").append(l).append("changeOnResize:false");

        return string.toString();
    }

    public static List<String> checkVersion(int[] fileVersion, List<String> existingLines) {
        if (compareVersions(fileVersion, version)) {
            LOGGER.warn("standardoptions.txt was marked with an outdated StandardSettings version ({}), updating now...", String.join(".", Arrays.stream(fileVersion).mapToObj(String::valueOf).toArray(String[]::new)));
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