package com.grimclient;

import com.grimclient.client.GrimClientCore;
import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GrimClientMod implements ClientModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger("grimclient");
    public static GrimClientCore core;

    @Override
    public void onInitializeClient() {
        LOGGER.info("[GrimClient] Loading Grim Client...");
        core = new GrimClientCore();
        core.init();
        LOGGER.info("[GrimClient] Ready! Press P to open GUI.");
    }
}
