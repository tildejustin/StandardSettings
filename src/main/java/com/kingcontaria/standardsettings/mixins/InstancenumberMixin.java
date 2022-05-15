package com.kingcontaria.standardsettings.mixins;

import com.kingcontaria.standardsettings.StandardSettings;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class InstancenumberMixin {

    @Inject(at = @At("TAIL"), method = "updateWindowTitle")
    public void setInstancenumber(CallbackInfo ci) {
        if (StandardSettings.instancenumber != null) {
            MinecraftClient.getInstance().getWindow().setTitle("Instance " + StandardSettings.instancenumber);
        }
    }
}