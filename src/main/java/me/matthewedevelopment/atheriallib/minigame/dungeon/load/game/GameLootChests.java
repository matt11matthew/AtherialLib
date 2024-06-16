package me.matthewedevelopment.atheriallib.minigame.dungeon.load.game;

import me.matthewe.extraction.dungeon.floor.Floor;
import me.matthewe.extraction.dungeon.floor.FloorRegistry;
import me.matthewe.extraction.loot.LootChest;
import me.matthewe.extraction.loot.SubLoot;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.HumanEntity;

import java.util.List;
import java.util.Random;
import java.util.UUID;

public class GameLootChests {
    private GameLoadedGameMap game;

    public GameLootChests(GameLoadedGameMap game) {
        this.game = game;
    }

    public void spawn() {
        for (UUID uuid : FloorRegistry.get().getFloorIdsByDungeon(game.getDungeonID())) {
            Floor floor = FloorRegistry.get().getMap().get(uuid);

            for (LootChest lootChest : floor.getLootChests()) {
                List<UUID> lootUUID = lootChest.getLootUUID();
                if (lootUUID.isEmpty())continue;

                if (new Random().nextDouble() * 100 <= lootChest.getSpawnChance()) {


                    Location location = lootChest.getLoc().toLocation(game.getWorldName());
                    if (location != null) {
                        location.getBlock().setType(Material.CHEST);
                        Chest chest = (Chest) location.getBlock().getState();


                        List<SubLoot> randomLoot = lootChest.getRandomLoot();

                        for (SubLoot subLoot : randomLoot) {
                            chest.getInventory().addItem(subLoot.getItemStack());
                        }

                    }
                }

            }
        }
    }


    public void checkChests() {
        for (UUID uuid : FloorRegistry.get().getFloorIdsByDungeon(game.getDungeonID())) {
            Floor floor = FloorRegistry.get().getMap().get(uuid);

            for (LootChest lootChest : floor.getLootChests()) {

                Location location = lootChest.getLoc().toLocation(game.getWorldName());
                if (location!=null){
                    Block block = location.getBlock();
                    if (block!=null&&block.getType()==Material.CHEST){
                        Chest chest = (Chest) block.getState();
                        List<HumanEntity> viewers = chest.getInventory().getViewers();
                        if (!viewers.isEmpty()){
                            continue; // PREVENTS BULLSHIT
                        }

                        if (chest.getInventory().isEmpty()) {
                            block.setType(Material.AIR);
                            World world = block.getWorld();
                            world.spawnParticle(Particle.BLOCK_CRACK, block.getLocation().add(0.5, 0.5, 0.5), 30, Material.CHEST.createBlockData());

                        }

                    }
                }
            }
        }

    }
}
