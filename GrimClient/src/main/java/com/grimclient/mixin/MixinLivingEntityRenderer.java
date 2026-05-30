package com.grimclient.mixin;
import com.grimclient.GrimClientMod;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
@Mixin(LivingEntityRenderer.class)
public class MixinLivingEntityRenderer {
    @Inject(method = "isVisible", at = @At("HEAD"), cancellable = true)
    private void onIsVisible(LivingEntity e, CallbackInfoReturnable<Boolean> cir) {
        if (GrimClientMod.core == null) return;
        var esp = GrimClientMod.core.moduleManager.getByName("ESP");
        if (esp != null && esp.isEnabled()) cir.setReturnValue(true);
    }
}
