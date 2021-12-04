package com.single_use_mod;

import java.util.UUID;

import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.ItemCraftedEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class Behaviour {
    private static int LEVEL_ADVANCEMENT_ACHIVEMENT = 5;
    private static float KILL_EXPERIENCE_MULTIPLIER = 5f;
    private static float CRAFT_EXPERIENCE_MULTIPLIER = 1.5f;
    private static int MAX_LEVEL_UNBREAKING_ITEMS = 1000;

    @SubscribeEvent
    public void onPlayerConnected(final PlayerLoggedInEvent event) {
        UUID uuid = event.getPlayer().getUUID();
        if (SingleUseMod.multipliers.get(uuid) == null) {
            SingleUseMod.multipliers.put(uuid, 1.0f);
        }
    }

    @SubscribeEvent
    public void onBreakItem(final BreakEvent event) {
        Player player = event.getPlayer();
        mouse1Event(player);
    }

    @SubscribeEvent
    public void onBreakItem(final AttackEntityEvent event) {
        Player player = event.getPlayer();
        mouse1Event(player);
    }

    @SubscribeEvent
    public void onPlayerKill(final LivingDeathEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Player) {

            Player player = (Player) entity;

            if (player.getKillCredit() instanceof Player) {

                Player killer = (Player) player.getKillCredit();

                MinecraftServer server = player.getServer();

                PlayerList playerList = server.getPlayerList();

                String killerName = killer.getName().getString();
                String playerName = player.getName().getString();
                float points = player.experienceProgress;

                for (ServerPlayer serverPlayer : playerList.getPlayers()) {
                    String message = killerName + " KILLED " + playerName + " and stole levels: " + points;
                    server.sendMessage(new TextComponent(message), serverPlayer.getUUID());
                }

                increaseMultiplier(killer.getUUID(), KILL_EXPERIENCE_MULTIPLIER);
                increaseXP(killer, points);
            }
        }
    }

    @SubscribeEvent
    public void onAchievement(AdvancementEvent event) {
        Player player = event.getPlayer();
        if (player != null) {
            increaseLevel(player, LEVEL_ADVANCEMENT_ACHIVEMENT);
        }
    }

    @SubscribeEvent
    public void EntityItemPickup(EntityItemPickupEvent event) {
        Player player = event.getPlayer();
        if (player != null) {
            Entity entity = event.getEntity();
            if (entity instanceof ExperienceOrb) {
                if (player.experienceLevel >= MAX_LEVEL_UNBREAKING_ITEMS && player.experienceProgress > 0.0) {
                    player.experienceProgress = 0;
                    player.experienceLevel = MAX_LEVEL_UNBREAKING_ITEMS;
                }
            }
        }
    }

    @SubscribeEvent
    public void onCraft(ItemCraftedEvent event) {
        Player player = event.getPlayer();
        if (player != null) {
            UUID uuid = player.getUUID();
            increaseMultiplier(uuid, CRAFT_EXPERIENCE_MULTIPLIER);
            increaseXP(player, 10);
        }
    }

    private void mouse1Event(Player player) {
        if (player != null && player.experienceLevel < MAX_LEVEL_UNBREAKING_ITEMS) {
            ItemStack itemStack = player.getMainHandItem();
            int itemId = Item.getId(itemStack.getItem());

            Inventory inventory = player.getInventory();

            for (ItemStack mItemStack : inventory.items) {
                Item mItem = mItemStack.getItem();
                if (Item.getId(mItem) == itemId) {
                    decreaseItem(inventory, itemStack);
                    increaseXP(player, 1);
                    return;
                }
            }
        }
    }

    private void decreaseItem(Inventory inventory, ItemStack itemStack) {
        int count = itemStack.getCount();
        if (count == 1) {
            inventory.removeItem(itemStack);
        } else {
            itemStack.setCount(count - 1);
        }
    }

    private void increaseMultiplier(UUID uuid, float addMultiplier) {
        Float mulitplier = SingleUseMod.multipliers.get(uuid);
        Float newMultiplier = mulitplier + addMultiplier;
        SingleUseMod.multipliers.put((uuid), newMultiplier);
    }

    private void increaseXP(Player player, float points) {
        Float multiplier = SingleUseMod.multipliers.get(player.getUUID());
        player.giveExperiencePoints(Math.round(points * multiplier));
    }

    private void increaseLevel(Player player, int levels) {
        player.giveExperienceLevels(levels);
    }
}
