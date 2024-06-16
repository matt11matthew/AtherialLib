package me.matthewedevelopment.atheriallib.minigame.dungeon.load.edit.listeners;

import me.matthewe.extraction.Extraction;
import me.matthewe.extraction.ExtractionConfig;
import me.matthewe.extraction.dungeon.extraction.ExtractionPoint;
import me.matthewe.extraction.dungeon.extraction.ExtractionPointCreationMenu;
import me.matthewe.extraction.dungeon.floor.Floor;
import me.matthewe.extraction.dungeon.floor.FloorRegistry;
import me.matthewe.extraction.dungeon.load.LoadedDungeon;
import me.matthewe.extraction.dungeon.load.edit.EditLoadedDungeon;
import me.matthewe.extraction.location.ExtractionPointLocation;
import me.matthewe.extraction.loot.LootChest;
import me.matthewe.extraction.loot.LootHandler;
import me.matthewe.extraction.spawner.Spawner;
import me.matthewe.extraction.spawner.SpawnerHandler;
import me.matthewe.extraction.spawner.SpawnerRegistry;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static me.matthewedevelopment.atheriallib.utilities.ChatUtils.colorize;

public class EditListener implements Listener {
    private Extraction extraction;
    private ExtractionConfig config = ExtractionConfig.get();

    public EditListener(Extraction extraction) {
        this.extraction = extraction;
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled()) return;

        if (!event.getPlayer().hasPermission(Extraction.getInstance().getExtractionConfig().D_BREAK_EXTRACTION_POINT)) {
            return;
        }
        Optional<LoadedDungeon> currentEditDungeon = EditLoadedDungeon.getCurrentEditDungeon(event.getPlayer());
        if (currentEditDungeon.isPresent()) {
            EditLoadedDungeon loadedDungeon = (EditLoadedDungeon) currentEditDungeon.get();
            if (loadedDungeon.hasFloor(event.getPlayer())) {

                UUID currentFloorId = loadedDungeon.getCurrentFloorId(event.getPlayer());
                Floor floor = FloorRegistry.get().getMap().get(currentFloorId);

                LootChest chest = null;
                for (LootChest lootChest : floor.getLootChests()) {
                    if (lootChest.matches(event.getBlock().getLocation())) {
                        chest = lootChest;
                        event.setCancelled(true);
                        event.getBlock().setType(Material.AIR);
                        break;
                    }
                }

                Spawner spawner = null;

                for (Spawner spawner1 : SpawnerRegistry.get().getMap().values().stream().filter(spawner1 -> spawner1.getFloorId().equals(floor.getUuid())).collect(Collectors.toList())) {
                    if (spawner1.matches(event.getBlock().getLocation())) {
                        spawner=spawner1;
                        event.setCancelled(true);
                        event.getBlock().setType(Material.AIR);
                        break;
                    }
                }
                if (spawner!=null){
                    Location location = event.getBlock().getLocation();
                    Spawner finalSpawner = spawner;
                    SpawnerRegistry.get().deleteAsync(spawner.getUuid(),() -> {
                        config.BREAK_SPAWNER_MSG.send(event.getPlayer(), s -> colorize(s)
                                .replace("%x%", location.getBlockX() + "")
                                .replace("%y%", location.getBlockY() + "")
                                .replace("%z%", location.getBlockZ() + "")
                                .replace("%id%", finalSpawner.getUuid().toString()));
                    });
                    return;
                }
                if (chest != null) {
                    floor.removeLootChest(chest);
                    Location location = event.getBlock().getLocation();
                    LootChest finalChest = chest;
                    config.BREAK_LT_PT_MSG.send(event.getPlayer(), s -> colorize(s)
                            .replace("%x%", location.getBlockX() + "")
                            .replace("%y%", location.getBlockY() + "")
                            .replace("%z%", location.getBlockZ() + "")
                            .replace("%id%", finalChest.getId().toString()));
                    FloorRegistry.get().updateAsync(floor, () -> {
                    });
                    return;
                }
                ExtractionPoint toRemove = null;
                for (ExtractionPoint extractionPoint : floor.getExtractionPoints().values()) {
                    if (extractionPoint.matches(event.getBlock().getLocation())) {
                        toRemove = extractionPoint;
                        event.setCancelled(true);
                        event.getBlock().setType(Material.AIR);
                        break;
                    }
                }
                if (toRemove != null) {
                    String id = toRemove.getUuid().toString();
                    floor.removeExtractionPoint(toRemove);
                    Location location = event.getBlock().getLocation();
                    config.BREAK_EXT_PT_MSG.send(event.getPlayer(), s -> colorize(s)
                            .replace("%x%", location.getBlockX() + "")
                            .replace("%y%", location.getBlockY() + "")
                            .replace("%z%", location.getBlockZ() + "")
                            .replace("%id%", id));


                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.isCancelled()) return;
        if (!event.getAction().toString().contains("RIGHT")) {
            return;
        }
        if (event.hasBlock() && event.getClickedBlock().getType() == Material.CHEST) {

            Optional<LoadedDungeon> currentEditDungeon = EditLoadedDungeon.getCurrentEditDungeon(event.getPlayer());
            if (currentEditDungeon.isPresent()) {
                EditLoadedDungeon loadedDungeon = (EditLoadedDungeon) currentEditDungeon.get();
                if (loadedDungeon.hasFloor(event.getPlayer())) {
                    UUID currentFloorId = loadedDungeon.getCurrentFloorId(event.getPlayer());

                    Floor floor = FloorRegistry.get().getMap().get(currentFloorId);
                    for (LootChest lootChest : floor.getLootChests()) {
                        if (lootChest.matches(event.getClickedBlock().getLocation())) {

                            event.setCancelled(true);
                            event.setUseInteractedBlock(Event.Result.DENY);
                            event.setUseItemInHand(Event.Result.DENY);
                            Extraction.getInstance().getHandler(LootHandler.class).startLootPlacement(event.getPlayer(), event.getClickedBlock().getLocation(), loadedDungeon, floor, lootChest);
                            return;
                        }
                    }
                }
            }
        } else if (event.hasBlock() && event.getClickedBlock().getType() == Material.REDSTONE_BLOCK) {

            Optional<LoadedDungeon> currentEditDungeon = EditLoadedDungeon.getCurrentEditDungeon(event.getPlayer());
            if (currentEditDungeon.isPresent()) {
                EditLoadedDungeon loadedDungeon = (EditLoadedDungeon) currentEditDungeon.get();
                if (loadedDungeon.hasFloor(event.getPlayer())) {
                    UUID currentFloorId = loadedDungeon.getCurrentFloorId(event.getPlayer());

                    Floor floor = FloorRegistry.get().getMap().get(currentFloorId);
                    for (ExtractionPoint value : floor.getExtractionPoints().values()) {
                        if (value.matches(event.getClickedBlock().getLocation())) {

                            event.setCancelled(true);
                            event.setUseInteractedBlock(Event.Result.DENY);
                            event.setUseItemInHand(Event.Result.DENY);
                            ExtractionPointCreationMenu extractionPointCreationMenu = new ExtractionPointCreationMenu(event.getPlayer(), floor.getUuid(), value.getExtractionPointLocation(), value, config, true);

                            extractionPointCreationMenu.create();
                            return;
                        }
                    }

                }
            }
        }
    }


    private final ItemStack EXTRACTION = config.EXTRACTION_ITEM.build();
    private final ItemStack LOOT = config.LOOT_CHEST_ITEM.build();
    private final ItemStack SPAWNER_ITEM = config.SPAWNER_ITEM.build();

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {

        if (event.getItemInHand() == null) return;
        boolean extraction = event.getItemInHand().isSimilar(EXTRACTION);
        boolean loot = event.getItemInHand().isSimilar(LOOT);
        boolean spawnerItem = event.getItemInHand().isSimilar(SPAWNER_ITEM);
//event.getPlayer().sendMessage(extraction+":"+loot+":"+spawnerItem);
        if (extraction || loot || spawnerItem) {


            Optional<LoadedDungeon> currentEditDungeon = EditLoadedDungeon.getCurrentEditDungeon(event.getPlayer());
            if (currentEditDungeon.isPresent()) {
                EditLoadedDungeon loadedDungeon = (EditLoadedDungeon) currentEditDungeon.get();
                if (loadedDungeon.hasFloor(event.getPlayer())) {
                    UUID currentFloorId = loadedDungeon.getCurrentFloorId(event.getPlayer());

                    if (extraction) {

                        ExtractionPointCreationMenu extractionPointCreationMenu = new ExtractionPointCreationMenu(event.getPlayer(), currentFloorId, new ExtractionPointLocation(event.getBlock().getLocation()), config, false);
                        extractionPointCreationMenu.setOnCancel(player -> {
                            event.getBlockPlaced().setType(Material.AIR);
                        });
                        extractionPointCreationMenu.create();
                    }
                    if (spawnerItem) {

                        SpawnerHandler.get().startCreation(event.getPlayer(), loadedDungeon, event.getBlockPlaced().getLocation(), FloorRegistry.get().getMap().get(currentFloorId), (player, mythicMob, spawner) -> {
                            player.closeInventory();
                            ExtractionConfig.get().SPAWNER_PLACED.send(player, s -> colorize(s).replace("%location%", spawner.getLocation().toFancyString()));

                        }, player -> {
                            ExtractionConfig.get().SPAWNER_PLACED_CANCELLED.send(player);

                        });

                    }
                    if (loot) {
                        Extraction.getInstance().getHandler(LootHandler.class).startLootPlacement(event.getPlayer(), event.getBlockPlaced().getLocation(), loadedDungeon, FloorRegistry.get().getMap().get(currentFloorId), null);
                    }
                }
            }
        }
    }
}
