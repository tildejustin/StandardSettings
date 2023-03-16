package com.kingcontaria.standardsettings.mixins.accessors;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Minecraft.class)

public interface MinecraftClientAccessor {
    @Accessor
    void setOpenProfilerSection(String value);
    @Accessor
    String getOpenProfilerSection();
}
