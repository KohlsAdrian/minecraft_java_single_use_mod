package com.single_use_mod

import java.util.*
import net.minecraft.network.chat.TextComponent
import net.minecraft.world.entity.ExperienceOrb
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraftforge.event.entity.living.LivingDeathEvent
import net.minecraftforge.event.entity.player.AdvancementEvent
import net.minecraftforge.event.entity.player.AttackEntityEvent
import net.minecraftforge.event.entity.player.EntityItemPickupEvent
import net.minecraftforge.event.entity.player.PlayerEvent.ItemCraftedEvent
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent
import net.minecraftforge.event.world.BlockEvent.BreakEvent
import net.minecraftforge.eventbus.api.SubscribeEvent

class Behaviour {

    companion object {
        private val multipliers = object : HashMap<UUID?, Float?>() {}
        private const val LEVEL_ADVANCEMENT_ACHIVEMENT = 5
        private const val KILL_EXPERIENCE_MULTIPLIER = 5f
        private const val CRAFT_EXPERIENCE_MULTIPLIER = 1.5f
        private const val MAX_LEVEL_UNBREAKING_ITEMS = 1000
    }

    @SubscribeEvent
    fun onPlayerConnected(event: PlayerLoggedInEvent) {
        val uuid = event.player.uuid
        if (multipliers[uuid] == null) {
            multipliers[uuid] = 1.0f
        }
    }

    @SubscribeEvent
    fun onBreakItem(event: BreakEvent) {
        val player = event.player
        mouse1Event(player)
    }

    @SubscribeEvent
    fun onBreakItem(event: AttackEntityEvent) {
        val player = event.player
        mouse1Event(player)
    }

    @SubscribeEvent
    fun onPlayerKill(event: LivingDeathEvent) {
        val entity = event.entity
        if (entity is Player) {
            val player = entity
            if (player.killCredit is Player) {
                val killer = player.killCredit as Player
                val server = player.server
                val playerList = server!!.playerList
                val killerName = killer.name.string
                val playerName = player.name.string
                val points = player.experienceProgress
                for (serverPlayer in playerList.players) {
                    val message = "$killerName KILLED $playerName and stole levels: $points"
                    server.sendMessage(TextComponent(message), serverPlayer.uuid)
                }
                increaseMultiplier(killer.uuid, KILL_EXPERIENCE_MULTIPLIER)
                increaseXP(killer, points)
            }
        }
    }

    @SubscribeEvent
    fun onAchievement(event: AdvancementEvent) {
        val player = event.player
        if (player != null) {
            increaseLevel(player, LEVEL_ADVANCEMENT_ACHIVEMENT)
        }
    }

    @SubscribeEvent
    fun onEntityItemPickup(event: EntityItemPickupEvent) {
        val player = event.player
        if (player != null) {
            val entity = event.entity
            if (entity is ExperienceOrb) {
                if (player.experienceLevel >= MAX_LEVEL_UNBREAKING_ITEMS &&
                                player.experienceProgress > 0.0
                ) {
                    player.experienceProgress = 0f
                    player.experienceLevel = MAX_LEVEL_UNBREAKING_ITEMS
                }
            }
        }
    }

    @SubscribeEvent
    fun onCraft(event: ItemCraftedEvent) {
        val player = event.player
        if (player != null) {
            val uuid = player.uuid
            increaseMultiplier(uuid, CRAFT_EXPERIENCE_MULTIPLIER)
            increaseXP(player, 10f)
        }
    }

    private fun mouse1Event(player: Player) {
        if (player.experienceLevel < MAX_LEVEL_UNBREAKING_ITEMS) {
            val itemStack = player.mainHandItem
            val itemId = Item.getId(itemStack.item)
            val inventory = player.inventory
            for (mItemStack in inventory.items) {
                val mItem = mItemStack.item
                if (Item.getId(mItem) == itemId) {
                    decreaseItem(inventory, itemStack)
                    increaseXP(player, 1f)
                    return
                }
            }
        }
    }

    private fun decreaseItem(inventory: Inventory, itemStack: ItemStack) {
        val count = itemStack.count
        if (count == 1) {
            inventory.removeItem(itemStack)
        } else {
            itemStack.count = count - 1
        }
    }

    private fun increaseMultiplier(uuid: UUID, addMultiplier: Float) {
        val mulitplier = multipliers[uuid]
        val newMultiplier = mulitplier!! + addMultiplier
        multipliers[uuid] = newMultiplier
    }

    private fun increaseXP(player: Player?, points: Float) {
        val multiplier = multipliers[player!!.uuid]
        player.giveExperiencePoints(Math.round(points * multiplier!!))
    }

    private fun increaseLevel(player: Player, levels: Int) {
        player.giveExperienceLevels(levels)
    }
}
