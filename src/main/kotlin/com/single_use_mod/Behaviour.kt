package com.single_use_mod

import kotlinx.serialization.json.*
import net.minecraft.network.chat.TextComponent
import net.minecraft.world.entity.ExperienceOrb
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraftforge.event.entity.EntityEvent
import net.minecraftforge.event.entity.living.LivingDeathEvent
import net.minecraftforge.event.entity.living.LivingEvent
import net.minecraftforge.event.entity.player.AdvancementEvent
import net.minecraftforge.event.entity.player.AttackEntityEvent
import net.minecraftforge.event.entity.player.EntityItemPickupEvent
import net.minecraftforge.event.entity.player.PlayerEvent
import net.minecraftforge.event.entity.player.PlayerEvent.ItemCraftedEvent
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent
import net.minecraftforge.event.world.BlockEvent.BreakEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import org.apache.logging.log4j.LogManager
import java.io.File
import java.util.*
import kotlin.math.roundToInt

class Behaviour {

    private val multipliers = object : HashMap<UUID?, Float?>() {}
    private var LEVEL_ADVANCEMENT_ACHIVEMENT = 5
    private var KILL_EXPERIENCE_MULTIPLIER = 5f
    private var CRAFT_SMELT_EXPERIENCE_MULTIPLIER = 1.5f
    private var MAX_LEVEL_UNBREAKING_ITEMS = 1000
    private var ALWAYS_DROP_FULL_STACK = false
    private var HARVEST_BLOCK_XP = 1f
    private var PLAYER_KILL_XP = 100f
    private var SMELT_XP = 20f
    private var CRAFT_XP = 20f
    private var CHANGE_CHUNK_XP = 30f
    private var CHANGE_DIMENSION_XP = 50f
    private var STOP_TRACK_XP = 5f
    private var START_TRACK_XP = 15f
    private var JUMP_XP = 5f
    private var TWIST_XP = 100f

    private val propertiesFileName = "single_use_mod_properties.json"

    private val LOGGER = LogManager.getLogger()

    init {
        val jsonElement = Json.parseToJsonElement(File(propertiesFileName).readText())
        val json = jsonElement.jsonObject.toMap()

        LEVEL_ADVANCEMENT_ACHIVEMENT = json["LEVEL_ADVANCEMENT_ACHIVEMENT"]!!.jsonPrimitive.int
        KILL_EXPERIENCE_MULTIPLIER = json["KILL_EXPERIENCE_MULTIPLIER"]!!.jsonPrimitive.float
        CRAFT_SMELT_EXPERIENCE_MULTIPLIER = json["CRAFT_EXPERIENCE_MULTIPLIER"]!!.jsonPrimitive.float
        MAX_LEVEL_UNBREAKING_ITEMS = json["MAX_LEVEL_UNBREAKING_ITEMS"]!!.jsonPrimitive.int
        ALWAYS_DROP_FULL_STACK = json["ALWAYS_DROP_FULL_STACK"]!!.jsonPrimitive.boolean
        HARVEST_BLOCK_XP = json["HARVEST_BLOCK_XP"]!!.jsonPrimitive.float
        PLAYER_KILL_XP = json["PLAYER_KILL_XP"]!!.jsonPrimitive.float
        SMELT_XP = json["SMELT_XP"]!!.jsonPrimitive.float
        CRAFT_XP = json["CRAFT_XP"]!!.jsonPrimitive.float
        CHANGE_CHUNK_XP = json["CHANGE_CHUNK_XP"]!!.jsonPrimitive.float
        CHANGE_DIMENSION_XP = json["CHANGE_DIMENSION_XP"]!!.jsonPrimitive.float
        STOP_TRACK_XP = json["STOP_TRACK_XP"]!!.jsonPrimitive.float
        START_TRACK_XP = json["START_TRACK_XP"]!!.jsonPrimitive.float
        JUMP_XP = json["JUMP_XP"]!!.jsonPrimitive.float
        TWIST_XP = json["TWIST_XP"]!!.jsonPrimitive.float
    }


    @SubscribeEvent
    fun onPlayerConnected(event: PlayerLoggedInEvent) {
        val uuid = event.player.uuid
        if (multipliers[uuid] == null) {
            multipliers[uuid] = 1.0f
            LOGGER.info(event.javaClass.toString())
        }
    }

    @SubscribeEvent
    fun onBreakBlock(event: BreakEvent) {
        val player = event.player
        LOGGER.info(event.javaClass.toString())
        onTwist(player)
    }

    @SubscribeEvent
    fun onAttack(event: AttackEntityEvent) {
        val player = event.player
        LOGGER.info(event.javaClass.toString())
        onTwist(player)
    }

    @SubscribeEvent
    fun onPlayerKill(event: LivingDeathEvent) {
        val entity = event.entity
        val server = entity.server
        if (server != null && entity is Player && entity.killCredit is Player) {
            val killer = entity.killCredit as Player
            val killerName = killer.name.string
            val playerName = entity.name.string
            val points = entity.experienceProgress

            val playerList = server.playerList
            for (serverPlayer in playerList.players) {
                val message = "$killerName KILLED $playerName and stole levels: $points"
                server.sendMessage(TextComponent(message), serverPlayer.uuid)
            }

            increaseXP(killer, PLAYER_KILL_XP)
            increaseMultiplier(killer, KILL_EXPERIENCE_MULTIPLIER)
            LOGGER.info(event.javaClass.toString())
        }
    }

    @SubscribeEvent
    fun onEntityItemPickup(event: EntityItemPickupEvent) {
        val player = event.player
        if (player != null) {
            val entity = event.entity
            if (entity is ExperienceOrb) {
                val level = player.experienceLevel
                val xp = player.experienceProgress
                // Prevents player from leveling up more than MAX LEVEL
                if (level >= MAX_LEVEL_UNBREAKING_ITEMS && xp > 0.0) {
                    player.experienceProgress = 0f
                    player.experienceLevel = MAX_LEVEL_UNBREAKING_ITEMS
                }
            }
            LOGGER.info(event.javaClass.toString())
        }
    }

    @SubscribeEvent
    fun onHarvestBlock(event: PlayerEvent.HarvestCheck) {
        if (event.entity is Player) {
            increaseXP(event.entity as Player, HARVEST_BLOCK_XP)
            LOGGER.info(event.javaClass.toString())
        }
    }

    @SubscribeEvent
    fun onStopTracking(event: PlayerEvent.StopTracking) {
        if (event.entityLiving is Player && event.target is Player) {
            increaseXP(event.target as Player, STOP_TRACK_XP)
            LOGGER.info(event.javaClass.toString())
        }
    }

    @SubscribeEvent
    fun onStartTracking(event: PlayerEvent.StartTracking) {
        if (event.entity is Player && event.target is Player) {
            increaseXP(event.target as Player, START_TRACK_XP)
            LOGGER.info(event.javaClass.toString())
        }
    }


    @SubscribeEvent
    fun onChunk(event: EntityEvent.EnteringSection) {
        if (event.entity is Player && event.didChunkChange()) {
            increaseXP(event.entity as Player, CHANGE_CHUNK_XP)
            LOGGER.info(event.javaClass.toString())
        }
    }

    @SubscribeEvent
    fun onJump(event: LivingEvent.LivingJumpEvent) {
        if (event.entityLiving is Player) {
            increaseXP(event.entity as Player, JUMP_XP)
            LOGGER.info(event.javaClass.toString())
        }
    }

    @SubscribeEvent
    fun onAchievement(event: AdvancementEvent) {
        val player = event.player
        if (player != null) {
            increaseLevel(player, LEVEL_ADVANCEMENT_ACHIVEMENT)
            LOGGER.info(event.javaClass.toString())
        }
    }

    @SubscribeEvent
    fun onCraft(event: ItemCraftedEvent) {
        val player = event.player
        if (player != null) {
            increaseMultiplier(player, CRAFT_SMELT_EXPERIENCE_MULTIPLIER)
            increaseXP(player, CRAFT_XP)
            LOGGER.info(event.javaClass.toString())
        }
    }

    @SubscribeEvent
    fun onSmelt(event: PlayerEvent.ItemSmeltedEvent) {
        val player = event.player
        if (player != null) {
            increaseMultiplier(player, CRAFT_SMELT_EXPERIENCE_MULTIPLIER)
            increaseXP(player, SMELT_XP)
            LOGGER.info(event.javaClass.toString())
        }
    }

    @SubscribeEvent
    fun onChangedDimension(event: PlayerEvent.PlayerChangedDimensionEvent) {
        val player = event.player
        if (player != null) {
            increaseMultiplier(player, CRAFT_SMELT_EXPERIENCE_MULTIPLIER)
            increaseXP(player, CHANGE_DIMENSION_XP)
            LOGGER.info(event.javaClass.toString())
        }
    }

    private fun onTwist(player: Player) {
        if (player.experienceLevel < MAX_LEVEL_UNBREAKING_ITEMS) {
            val itemStack = player.mainHandItem
            val itemId = Item.getId(itemStack.item)
            val inventory = player.inventory
            for (mItemStack in inventory.items) {
                val mItem = mItemStack.item
                if (Item.getId(mItem) == itemId) {
                    decreaseItem(inventory, itemStack)
                    return
                }
            }
            increaseXP(player, TWIST_XP)
        }
    }

    private fun decreaseItem(inventory: Inventory, itemStack: ItemStack) {
        val count = itemStack.count
        if (count == 1 || ALWAYS_DROP_FULL_STACK) {
            inventory.removeItem(itemStack)
        } else {
            itemStack.count = count - 1
        }
    }

    private fun increaseMultiplier(player: Player, addMultiplier: Float) {
        val mulitplier = multipliers[player.uuid]
        val newMultiplier = mulitplier!! + addMultiplier
        multipliers[player.uuid] = newMultiplier

        val message = "${player.name.string} multiplier increased to: $newMultiplier"
        player.server?.sendMessage(TextComponent(message), player.uuid)
    }

    private fun increaseXP(player: Player, points: Float) {
        val multiplier = multipliers[player.uuid]
        if (multiplier != null) {
            player.giveExperiencePoints((points * multiplier).roundToInt())
        }
    }

    private fun increaseLevel(player: Player, levels: Int) {
        player.giveExperienceLevels(levels)
    }
}
