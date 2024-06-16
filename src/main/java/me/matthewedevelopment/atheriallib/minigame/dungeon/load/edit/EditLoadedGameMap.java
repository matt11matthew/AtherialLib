package me.matthewedevelopment.atheriallib.minigame.dungeon.load.edit;

import me.matthewe.extraction.Extraction;
import me.matthewe.extraction.ExtractionConfig;
import me.matthewe.extraction.dungeon.Dungeon;
import me.matthewe.extraction.dungeon.DungeonRegistry;
import me.matthewe.extraction.dungeon.floor.Floor;
import me.matthewe.extraction.dungeon.floor.FloorRegistry;
import me.matthewe.extraction.dungeon.load.DungeonMode;
import me.matthewe.extraction.dungeon.load.LoadedDungeon;
import me.matthewe.extraction.lobby.LobbyHandler;
import me.matthewe.extraction.location.ExtractionPointLocation;
import me.matthewe.extraction.player.DungeonProfile;
import me.matthewedevelopment.atheriallib.config.yaml.AtherialLibItem;
import me.matthewedevelopment.atheriallib.menu.HotBarClickType;
import me.matthewedevelopment.atheriallib.menu.HotBarMenu;
import me.matthewedevelopment.atheriallib.message.message.ActionBarMessage;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static me.matthewedevelopment.atheriallib.utilities.ChatUtils.colorize;

public class EditLoadedGameMap extends LoadedDungeon {

    public EditLoadedGameMap(UUID dungeon, UUID sessionId) {
        super(dungeon, sessionId, DungeonMode.EDIT);
    }



    public static Optional<LoadedDungeon> getCurrentEditDungeon(Player player) {
       return DungeonRegistry.get().getLoadedDungeonMap().values()
                .stream()
                .filter(loadedDungeon -> loadedDungeon.getDungeonMode() == DungeonMode.EDIT)
                .filter(loadedDungeon -> loadedDungeon.getPlayers().contains(player)).findFirst();
    }

    @Override
    public void onWorldLoad(World world) {

    }

    @Override
    public void onSessionEnd(Player player) {
        if (  HotBarMenu.hasHotBar(player)) {
            HotBarMenu.destroy(player);
        }
    }

    @Override
    public void update() {
        ExtractionConfig config = Extraction.getInstance().getExtractionConfig();
        for (Player player : getPlayers()) {
            updateHotBar(player);

            if (hasFloor(player)) {
                Floor foundFloor = FloorRegistry.get().getMap().get(getCurrentFloorId(player));
                new ActionBarMessage(colorize(config.EDITING_ACTIONBAR_IN_FLOOR).replace("%floor%", foundFloor.getNumber()+"").replace("%name%", getDungeon().getName()), 1).send(player);

            } else {
                new ActionBarMessage(colorize(config.EDITING_ACTIONBAR).replace("%name%", getDungeon().getName()), 1).send(player);

            }

        }
    }

    @Override
    public void fastUpdate() {

    }

    private void updateHotBar(Player player) {

        HotBarMenu hotBarMenu;

        if (HotBarMenu.hasHotBar(player)){
            hotBarMenu = HotBarMenu.get(player);
            //UPDATE ITEMS HERE

            ExtractionConfig extractionConfig = Extraction.getInstance().getExtractionConfig();

            if (hasFloor(player)){
                hotBarMenu.show(extractionConfig.LOOT_CHEST_ITEM.getSlot());
                hotBarMenu.show(extractionConfig.EXTRACTION_ITEM.getSlot());
                hotBarMenu.show(extractionConfig.SPAWNER_ITEM.getSlot());

            } else {
                hotBarMenu.hide(extractionConfig.LOOT_CHEST_ITEM.getSlot());
                hotBarMenu.hide(extractionConfig.EXTRACTION_ITEM.getSlot());
                hotBarMenu.hide(extractionConfig.SPAWNER_ITEM.getSlot());
            }
            hotBarMenu.update();
//            player.sendMessage("HAS");

        } else {
            hotBarMenu = HotBarMenu.create("Edit", player);
            ExtractionConfig extractionConfig = Extraction.getInstance().getExtractionConfig();
            hotBarMenu.set(extractionConfig.EXTRACTION_ITEM.getSlot(), extractionConfig.EXTRACTION_ITEM.build(s -> colorize(s)), (player1, hotBarMenu1, i, hotBarClickType, block) -> {
                if (hotBarClickType==HotBarClickType.RIGHT&&block.isPresent()) {
                    return false;
                }
                return true;
            });
     hotBarMenu.set(extractionConfig.SPAWNER_ITEM.getSlot(), extractionConfig.SPAWNER_ITEM.build(s -> colorize(s)), (player1, hotBarMenu1, i, hotBarClickType, block) -> {
                if (hotBarClickType==HotBarClickType.RIGHT&&block.isPresent()) {
                    return false;
                }
                return true;
            });


            hotBarMenu.set(extractionConfig.LOOT_CHEST_ITEM.getSlot(), extractionConfig.LOOT_CHEST_ITEM.build(s -> colorize(s)), (player1, hotBarMenu1, i, hotBarClickType, block) -> {
                if (hotBarClickType==HotBarClickType.RIGHT&&block.isPresent()) {
                    return false;
                }
                return true;
            });

            ItemStack build = extractionConfig.SELECTION_STICK.build(s -> colorize(s));
            hotBarMenu.set(extractionConfig.SELECTION_STICK.getSlot(), build, (player1, hotBarMenu1, i, hotBarClickType, block) -> {
                if (hotBarClickType==HotBarClickType.RIGHT&&block.isPresent()){
                    DungeonProfile byPlayer = DungeonProfile.getByPlayer(player);
                    if (byPlayer==null)return true;
                    extractionConfig.SET_POS_2.send(player,s -> colorize(s)
                            .replace("%z%", block.get().getZ()+"")
                            .replace("%y%", block.get().getY()+"")
                            .replace("%x%", block.get().getX()+""));
                    byPlayer.setPos2(new ExtractionPointLocation(block.get().getLocation()));
                } else if (hotBarClickType== HotBarClickType.LEFT&&block.isPresent()){
                    DungeonProfile byPlayer = DungeonProfile.getByPlayer(player);
                    if (byPlayer==null)return true;
                    extractionConfig.SET_POS_1.send(player,s -> colorize(s)
                            .replace("%z%", block.get().getZ()+"")
                            .replace("%y%", block.get().getY()+"")
                            .replace("%x%", block.get().getX()+""));
                    byPlayer.setPos1(new ExtractionPointLocation(block.get().getLocation()));
                }
                return true;
            });


            AtherialLibItem exitEditModeItem = extractionConfig.EXIT_EDIT_MODE_ITEM;
            hotBarMenu.set(exitEditModeItem.getSlot(), exitEditModeItem.build(s -> colorize(s)), (player1, hotBarMenu1, i, hotBarClickType, block) -> {
                if (hotBarClickType == HotBarClickType.RIGHT) {
                    List<Player> players = getPlayers();
                    if (players.size()==1) {
                        //END EDIT MODE;
                        Dungeon dungeon1 = getDungeon();
                        if (dungeon1==null)return true;
                        Bukkit.dispatchCommand(player1, "dungeon save " + dungeon1.getName());
                    } else {
                        player1.teleport(Extraction.getInstance().getMainSpawn(), PlayerTeleportEvent.TeleportCause.PLUGIN);
                        LobbyHandler.get().onTeleportToSpawn(player1);
                    }
                }
                return true;
            });
            hotBarMenu.show();
        }



    }


    @Override
    public void onLoad() {
    }
}
