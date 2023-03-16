package com.kingcontaria.standardsettings.mixins.accessors;

import net.minecraft.util.Language;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.TreeMap;

@Mixin(Language.class)
public interface LanguageManagerAccessor {
    @Accessor
    TreeMap<String, String> getField_619();
}
