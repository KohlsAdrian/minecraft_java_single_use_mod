package com.single_use_mod

import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.Mod

// The value here should match an entry in the META-INF/mods.toml file
@Mod("single_use_mod")
class SingleUseMod {
    init {
        MinecraftForge.EVENT_BUS.register(Behaviour())
    }
}