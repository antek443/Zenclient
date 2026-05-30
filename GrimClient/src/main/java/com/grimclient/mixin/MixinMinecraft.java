package com.grimclient.mixin;
import com.grimclient.GrimClientMod;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(MinecraftClient.class)
public class MixinMinecraft {
    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        if (GrimClientMod.core == null || GrimClientMod.core.moduleManager == null) return;
        var fb = GrimClientMod.core.moduleManager.getByName("FullBright");
        if (fb != null && fb.isEnabled()) {
            ((MinecraftClient)(Object)this).options.getGamma().setValue(100.0);
        }
    }
}
