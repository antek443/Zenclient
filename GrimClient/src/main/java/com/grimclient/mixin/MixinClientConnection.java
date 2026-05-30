package com.grimclient.mixin;
import com.grimclient.GrimClientMod;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.PacketCallbacks;
import net.minecraft.network.packet.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(ClientConnection.class)
public class MixinClientConnection {
    @Inject(method = "sendImmediately", at = @At("HEAD"), cancellable = true)
    private void onSend(Packet<?> packet, PacketCallbacks cb, CallbackInfo ci) {
        if (GrimClientMod.core == null) return;
        var fl = GrimClientMod.core.moduleManager.getByName("FakeLag");
        // FakeLag hook placeholder
    }
}
