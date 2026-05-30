package com.grimclient.mixin;
import com.grimclient.GrimClientMod;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
@Mixin(EntityRenderer.class)
public class MixinEntityRenderer {
    @Inject(method = "hasLabel(Lnet/minecraft/entity/Entity;Z)Z", at = @At("HEAD"), cancellable = true)
    private void onHasLabel(Entity e, boolean b, CallbackInfoReturnable<Boolean> cir) {
        if (GrimClientMod.core == null) return;
        var esp = GrimClientMod.core.moduleManager.getByName("ESP");
        if (esp != null && esp.isEnabled()) cir.setReturnValue(true);
    }
}
