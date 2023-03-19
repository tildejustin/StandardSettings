package com.kingcontaria.standardsettings;

import com.kingcontaria.standardsettings.mixins.accessors.MinecraftClientAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import xyz.tildejustin.nopaus.NoPaus;

public class OptionsCache {

    private final Minecraft client;
    private final GameOptions options;
    private String levelName;
    public float musicVolume;
    public float soundVolume;
    public boolean invertYMouse;
    public float sensitivity;
    public float fov;
    public float gamma;
    public int renderDistance;
    public int guiScale;
    public int particle;
    public boolean bobView;
    public boolean anaglyph3d;
    public boolean advancedOpengl;
    public int maxFramerate;
    public int difficultyLevel;
    public boolean fancyGraphics;
    public boolean ambientOcculsion;
    public boolean renderClouds;
    public String currentTexturePackName;
    public String lastServer;
    public String language;
    public int chatVisibility;
    public boolean chatColor;
    public boolean chatLink;
    public boolean chatLinkPrompt;
    public float chatOpacity;
    public boolean useServerTextures;
    public boolean snopperEnabled;
    public boolean fullscreen;
    public boolean vsync;
    public boolean hideServerAddress;
    public KeyBinding[] allKeys;
    public int perspective;
    public boolean hudHidden;
    public String piedirectory;
    public boolean pauseOnLostFocus;

    public OptionsCache(Minecraft client) {
        this.client = client;
        this.options = client.options;
        allKeys = new KeyBinding[options.allKeys.length];
    }

    public void save(String levelName) {
        musicVolume = options.musicVolume;
        soundVolume = options.soundVolume;
        invertYMouse = options.invertYMouse;
        sensitivity = options.sensitivity;
        fov = options.fov;
        gamma = options.gamma;
        renderDistance = options.renderDistance;
        guiScale = options.guiScale;
        particle = options.particle;
        bobView = options.bobView;
        anaglyph3d = options.anaglyph3d;
        advancedOpengl = options.advancedOpengl;
        maxFramerate = options.maxFramerate;
        difficultyLevel = options.difficultyLevel;
        fancyGraphics = options.fancyGraphics;
        ambientOcculsion = options.ambientOcculsion;
        renderClouds = options.renderClouds;
        currentTexturePackName = options.currentTexturePackName;
        lastServer = options.lastServer;
        language = options.language;
        chatVisibility = options.chatVisibility;
        chatColor = options.chatColor;
        chatLink = options.chatLink;
        chatLinkPrompt = options.chatLinkPrompt;
        chatOpacity = options.chatOpacity;
        useServerTextures = options.useServerTextures;
        snopperEnabled = options.snopperEnabled;
        fullscreen = options.fullscreen;
        vsync = options.vsync;
        hideServerAddress = options.hideServerAddress;
        int i = 0;
        for (KeyBinding key : options.allKeys) {
            allKeys[i++] = key;
        }
        perspective = options.perspective;
        hudHidden = options.hudHidden;
        piedirectory = ((MinecraftClientAccessor)client).getOpenProfilerSection();
        if (StandardSettings.HAS_NO_PAUS) {
            pauseOnLostFocus = NoPaus.pauseOnLostFocus;
        }
        this.levelName = levelName;
        StandardSettings.LOGGER.info("Cached options for '" + this.levelName + "'" + (this.levelName != null ? " & abandoned old cache" : ""));
    }

    public void load(String levelName) {
        if (!levelName.equals(this.levelName)) {
            return;
        }
        options.musicVolume = musicVolume;
        options.soundVolume = soundVolume;
        options.invertYMouse = invertYMouse;
        options.sensitivity = sensitivity;
        options.fov = fov;
        options.gamma = gamma;
        options.renderDistance = renderDistance;
        options.guiScale = guiScale;
        options.particle = particle;
        options.bobView = bobView;
        options.anaglyph3d = anaglyph3d;
        options.advancedOpengl = advancedOpengl;
        options.maxFramerate = maxFramerate;
        options.difficultyLevel = difficultyLevel;
        options.fancyGraphics = fancyGraphics;
        options.ambientOcculsion = ambientOcculsion;
        options.renderClouds = renderClouds;
        options.currentTexturePackName = currentTexturePackName;
        options.lastServer = lastServer;
        options.language = language;
        options.chatVisibility = chatVisibility;
        options.chatColor = chatColor;
        options.chatLink = chatLink;
        options.chatLinkPrompt = chatLinkPrompt;
        options.chatOpacity = chatOpacity;
        options.useServerTextures = useServerTextures;
        options.snopperEnabled = snopperEnabled;
        options.fullscreen = fullscreen;
        options.vsync = vsync;
        options.hideServerAddress = hideServerAddress;
        int i = 0;
        for (KeyBinding keyBinding : options.allKeys) {
            keyBinding.code = allKeys[i++].code;
        }
        options.perspective = perspective;
        options.hudHidden = hudHidden;
        ((MinecraftClientAccessor)client).setOpenProfilerSection(piedirectory);
        if (StandardSettings.HAS_NO_PAUS) {
            NoPaus.pauseOnLostFocus = pauseOnLostFocus;
        }
        StandardSettings.LOGGER.info("Restored cached options for '" + this.levelName + "'");
        this.levelName = null;
    }

}