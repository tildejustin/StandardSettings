package com.kingcontaria.standardsettings;

import com.kingcontaria.standardsettings.mixins.accessors.MinecraftClientAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.resource.language.LanguageDefinition;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Arm;
import org.lwjgl.opengl.Display;

import java.util.Set;

public class OptionsCache {

    private final MinecraftClient client;
    private final GameOptions options;
    private String levelName;
    private boolean autoJump;
    private boolean chatColors;
    private boolean chatLinks;
    private boolean chatLinksPrompt;
    private boolean enableVsync;
    private boolean vbo;
    private boolean entityShadows;
    private boolean forceUnicodeFont;
    private boolean invertYMouse;
    private boolean reducedDebugInfo;
    private boolean showSubtitles;
    private boolean touchscreen;
    private boolean fullscreen;
    private boolean bobView;
    private boolean anaglyph3d;
    private float mouseSensitivity;
    private float fov;
    private float gamma;
    private int viewDistance;
    private int guiScale;
    private int particles;
    private int maxFps;
    private boolean fancyGraphics;
    private int ao;
    private int cloudMode;
    private int attackIndicator;
    private LanguageDefinition language;
    private PlayerEntity.class_1659 chatVisibilityType;
    private float chatOpacity;
    private boolean advancedItemTooltips;
    private boolean pauseOnLostFocus;
    private float chatHeightFocused;
    private float chatHeightUnfocused;
    private float chatScale;
    private float chatWidth;
    private int mipmapLevels;
    private Arm mainArm;
    private int narrator;
    private boolean chunkborders;
    private boolean hitboxes;
    private int perspective;
    private String piedirectory;
    private boolean hudHidden;
    private final int[] keysAll;
    private final float[] soundCategories;
    private Set<PlayerModelPart> playerModelParts;

    public OptionsCache(MinecraftClient client) {
        this.client = client;
        this.options = client.options;
        keysAll = new int[options.allKeys.length];
        soundCategories = new float[SoundCategory.values().length];
    }

    public void save(String levelName) {
        autoJump = options.autoJump;
        chatColors = options.chatColors;
        chatLinks = options.chatLinks;
        chatLinksPrompt = options.chatLinksPrompt;
        enableVsync = options.enableVsync;
        vbo = options.field_1899;
        entityShadows = options.entityShadows;
        forceUnicodeFont = options.field_0_2663;
        invertYMouse = options.invertYMouse;
        reducedDebugInfo = options.reducedDebugInfo;
        showSubtitles = options.showSubtitles;
        touchscreen = options.touchscreen;
        fullscreen = options.fullscreen;
        bobView = options.bobView;
        anaglyph3d = options.field_1820;
        mouseSensitivity = options.field_1843;
        fov = options.field_1826;
        gamma = options.field_1840;
        viewDistance = options.viewDistance;
        guiScale = options.guiScale;
        particles = options.field_1882;
        maxFps = options.maxFps;
        fancyGraphics = options.fancyGraphics;
        ao = options.field_1841;
        cloudMode = options.field_1814;
        attackIndicator = options.field_1895;
        language = client.getLanguageManager().getLanguage();
        chatVisibilityType = options.field_1877;
        chatOpacity = options.field_0_2718;
        advancedItemTooltips = options.advancedItemTooltips;
        pauseOnLostFocus = options.pauseOnLostFocus;
        chatHeightFocused = options.field_1838;
        chatHeightUnfocused = options.field_1825;
        chatScale = options.field_1908;
        chatWidth = options.field_1915;
        mipmapLevels = options.mipmapLevels;
        mainArm = options.mainArm;
        narrator = options.field_1896;
        client.debugRenderer.toggleShowChunkBorder();
        chunkborders = client.debugRenderer.toggleShowChunkBorder();
        hitboxes = client.getEntityRenderDispatcher().shouldRenderHitboxes();
        perspective = options.perspective;
        piedirectory = ((MinecraftClientAccessor)client).getOpenProfilerSection();
        hudHidden = options.hudHidden;
        int i = 0;
        for (KeyBinding key : options.allKeys) {
            keysAll[i++] = key.method_1421();
        }
        i = 0;
        for (SoundCategory sound : SoundCategory.values()) {
            soundCategories[i++] = options.getSoundVolume(sound);
        }
        playerModelParts = options.getEnabledPlayerModelParts();

        StandardSettings.LOGGER.info("Cached options for '{}'" + (this.levelName != null ? " & abandoned old cache" : ""), this.levelName = levelName);
    }

    public void load(String levelName) {
        if (!levelName.equals(this.levelName)) {
            return;
        }
        options.autoJump = autoJump;
        options.chatColors = chatColors;
        options.chatLinks = chatLinks;
        options.chatLinksPrompt = chatLinksPrompt;
        Display.setVSyncEnabled(options.enableVsync = enableVsync);
        options.field_1899 = vbo;
        options.entityShadows = entityShadows;
        client.field_1772.method_0_2391(client.getLanguageManager().method_0_4393() || (options.field_0_2663 = forceUnicodeFont));
        options.invertYMouse = invertYMouse;
        options.reducedDebugInfo = reducedDebugInfo;
        options.showSubtitles = showSubtitles;
        options.touchscreen = touchscreen;
        if (options.fullscreen != fullscreen) {
            if (Display.isActive()) {
                client.method_0_2307();
            } else {
                StandardSettings.LOGGER.error("Could not reset fullscreen mode because window wasn't focused!");
            }
        }
        options.bobView = bobView;
        if (options.field_1820 != (options.field_1820 = anaglyph3d)) {
            client.getTextureManager().method_14491(client.getResourceManager());
        }
        options.field_1843 = mouseSensitivity;
        options.field_1826 = fov;
        options.field_1840 = gamma;
        options.viewDistance = viewDistance;
        options.guiScale = guiScale;
        options.field_1882 = particles;
        options.maxFps = maxFps;
        options.fancyGraphics = fancyGraphics;
        options.field_1841 = ao;
        options.field_1814 = cloudMode;
        options.field_1895 = attackIndicator;
        if (!options.language.equals(options.language = language.method_4671())) {
            client.getLanguageManager().setLanguage(language);
            client.getLanguageManager().method_14491(client.getResourceManager());
        }
        options.field_1877 = chatVisibilityType;
        options.field_0_2718 = chatOpacity;
        options.advancedItemTooltips = advancedItemTooltips;
        options.pauseOnLostFocus = pauseOnLostFocus;
        options.field_1838 = chatHeightFocused;
        options.field_1825 = chatHeightUnfocused;
        options.field_1908 = chatScale;
        options.field_1915 = chatWidth;
        if (options.mipmapLevels != mipmapLevels) {
            client.getSpriteAtlas().setMipLevel(options.mipmapLevels = mipmapLevels);
            client.getTextureManager().bindTextureInner(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);
            client.getSpriteAtlas().setFilter(false, options.mipmapLevels > 0);
            ((MinecraftClientAccessor) client).getModelManager().method_14491(client.getResourceManager());
        }
        options.mainArm = mainArm;
        options.field_1896 = narrator;
        if (client.debugRenderer.toggleShowChunkBorder() != chunkborders) {
            client.debugRenderer.toggleShowChunkBorder();
        }
        client.getEntityRenderDispatcher().setRenderHitboxes(hitboxes);
        options.perspective = perspective;
        ((MinecraftClientAccessor)client).setOpenProfilerSection(piedirectory);
        options.hudHidden = hudHidden;
        int i = 0;
        for (KeyBinding keyBinding : options.allKeys) {
            keyBinding.method_1422(keysAll[i++]);
        }
        KeyBinding.updateKeysByCode();
        i = 0;
        for (SoundCategory soundCategory : SoundCategory.values()) {
            options.setSoundVolume(soundCategory, soundCategories[i++]);
        }
        for (PlayerModelPart playerModelPart : PlayerModelPart.values()) {
            options.setPlayerModelPart(playerModelPart, playerModelParts.contains(playerModelPart));
        }

        StandardSettings.LOGGER.info("Restored cached options for '{}'", this.levelName);
        this.levelName = null;
    }

}