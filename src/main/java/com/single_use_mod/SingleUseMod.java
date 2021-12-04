package com.single_use_mod;

import java.util.HashMap;
import java.util.UUID;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("single_use_mod")
public class SingleUseMod {
    static HashMap<UUID, Float> multipliers = new HashMap<UUID, Float>() {
    };

    public SingleUseMod() {
        MinecraftForge.EVENT_BUS.register(new Behaviour());
    }
}
