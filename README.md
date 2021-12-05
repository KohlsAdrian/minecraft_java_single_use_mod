
# Minecraft Java Edition Single Use Mod

## Features

* Everytime you mine a block/item or attack a mob/player the item gets destroyed

* Everytime you kill a player, your XP multiplier is increased

* Everytime you smelft or craft, your XP multiplier is increased

* Everytime you die, the killer earns your total XP

* While you harvest a block you earn XP

* Once you reach MAX LEVEL defined by the mod, mod is disabled for player (unless you enchant something and loses XP)

* Many more...

## Configuration - single_use_mod_properties.json

<details>
   <summary>Default Values</summary>

   | Parameter                          | Default Values
   |---|---|
   | LEVEL_ADVANCEMENT_ACHIVEMENT       | 5                   
   | KILL_EXPERIENCE_MULTIPLIER         | 5.0            
   | CRAFT_SMELT_EXPERIENCE_MULTIPLIER  | 1.5            
   | MAX_LEVEL_UNBREAKING_ITEMS         | 1000           
   | ALWAYS_DROP_FULL_STACK             | false          
   | HARVEST_BLOCK_XP                   | 1.0            
   | PLAYER_KILL_XP                     | 100.0          
   | SMELT_XP                           | 50.0           
   | CRAFT_XP                           | 50.0           
   | CHANGE_CHUNK_XP                    | 20.0           
   | CHANGE_DIMENSION_XP                | 100.0          
   | STOP_TRACK_XP                      | 15.0           
   | START_TRACK_XP                     | 15.0           
   | JUMP_XP                            | 1.0            
   | TWIST_XP                           | 100.0          

</details>

<details>
   <summary>Description</summary>

   | Parameter                          | Description
   |---|---|
   | LEVEL_ADVANCEMENT_ACHIVEMENT       | Give player number of levels on new Achievement            
   | KILL_EXPERIENCE_MULTIPLIER         | Increase killer experience multiplier after killing a player     
   | CRAFT_SMELT_EXPERIENCE_MULTIPLIER  | Increase player experience multiplier after 
   | MAX_LEVEL_UNBREAKING_ITEMS         | Max level where mod is disabled to player (watch out on enchants!)
   | ALWAYS_DROP_FULL_STACK             | true: drop all stack, false: drop one item at a time
   | HARVEST_BLOCK_XP                   | While harvesting before breaking block, earns XP
   | PLAYER_KILL_XP                     | The name says it all!
   | SMELT_XP                           | The name says it all!      
   | CRAFT_XP                           | The name says it all!
   | CHANGE_CHUNK_XP                    | Earn XP when reach a new chunk     
   | CHANGE_DIMENSION_XP                | Earn XP when switch between Nether and Overworld
   | STOP_TRACK_XP                      | The name says it all!
   | START_TRACK_XP                     | The name says it all!     
   | JUMP_XP                            | The name says it all!
   | TWIST_XP                           | Earn XP when you break a block or attack a player/mob while holding any item        

</details>


---
---
---

# Developers

Source installation information for modders
-------------------------------------------
This code follows the Minecraft Forge installation methodology. It will apply
some small patches to the vanilla MCP source code, giving you and it access 
to some of the data and functions you need to build a successful mod.

Note also that the patches are built against "un-renamed" MCP source code (aka
SRG Names) - this means that you will not be able to read them directly against
normal code.

Setup Process:
==============================

Step 1: Open your command-line and browse to the folder where you extracted the zip file.

Step 2: You're left with a choice.
If you prefer to use Eclipse:
1. Run the following command: `gradlew genEclipseRuns` (`./gradlew genEclipseRuns` if you are on Mac/Linux)
2. Open Eclipse, Import > Existing Gradle Project > Select Folder 
   or run `gradlew eclipse` to generate the project.

If you prefer to use IntelliJ:
1. Open IDEA, and import project.
2. Select your build.gradle file and have it import.
3. Run the following command: `gradlew genIntellijRuns` (`./gradlew genIntellijRuns` if you are on Mac/Linux)
4. Refresh the Gradle Project in IDEA if required.

If at any point you are missing libraries in your IDE, or you've run into problems you can 
run `gradlew --refresh-dependencies` to refresh the local cache. `gradlew clean` to reset everything 
{this does not affect your code} and then start the process again.

Mapping Names:
=============================
By default, the MDK is configured to use the official mapping names from Mojang for methods and fields 
in the Minecraft codebase. These names are covered by a specific license. All modders should be aware of this
license, if you do not agree with it you can change your mapping names to other crowdsourced names in your 
build.gradle. For the latest license text, refer to the mapping file itself, or the reference copy here:
https://github.com/MinecraftForge/MCPConfig/blob/master/Mojang.md

Additional Resources: 
=========================
Community Documentation: http://mcforge.readthedocs.io/en/latest/gettingstarted/  
LexManos' Install Video: https://www.youtube.com/watch?v=8VEdtQLuLO0  
Forge Forum: https://forums.minecraftforge.net/  
Forge Discord: https://discord.gg/UvedJ9m  