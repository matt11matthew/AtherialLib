package spigui.menu;

import spigui.menu.SGMenu;
import org.bukkit.entity.Player;

/**
 * Used to refer to a player's "viewing session" of a given menu.
 */@Deprecated
public class SGOpenMenu {

    /** The {@link spigui.menu.SGMenu} that is currently open. */
    private final spigui.menu.SGMenu gui;
    /** The player viewing the menu. */
    private final Player player;

    /**
     * Pairs an {@link spigui.menu.SGMenu} instance with a player viewing that menu.
     * @param gui The {@link spigui.menu.SGMenu} that is open.
     * @param player The player viewing the menu.
     */
    public SGOpenMenu(spigui.menu.SGMenu gui, Player player) {
        this.gui = gui;
        this.player = player;
    }

    /**
     * Get the open {@link spigui.menu.SGMenu} instance.
     * @return The menu that is open.
     */
    public spigui.menu.SGMenu getMenu() {
        return this.gui;
    }

    /**
     * Get the player viewing the {@link SGMenu}.
     * @return The player viewing the menu.
     */
    public Player getPlayer() {
        return this.player;
    }

}
