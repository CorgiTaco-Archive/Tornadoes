package dev.corgitaco.examplemod;

import dev.corgitaco.examplemod.core.ModRegistries;

public class CommonClass {

    public static void init() {
        ModRegistries.registerAll();
    }
}