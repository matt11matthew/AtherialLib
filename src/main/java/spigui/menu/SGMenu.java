package spigui.menu;

import me.matthewedevelopment.atheriallib.menu.gui.AtherialMenu;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.java.JavaPlugin;
import spigui.SpiGUI;
import spigui.buttons.SGButton;
import spigui.toolbar.SGToolbarBuilder;
import spigui.toolbar.SGToolbarButtonType;

import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Consumer;

/**
 * SGMenu with Adventure Component title support (via reflection) across 1.8.9–1.21+.
 */
@Deprecated
public class SGMenu implements InventoryHolder {

    private AtherialMenu link;
    /** The plugin (owner of the SpiGUI instance) that created this inventory. */
    private final JavaPlugin owner;
    /** The SpiGUI instance that created this inventory. */
    private final SpiGUI spiGUI;

    /** The title of the inventory (string fallback). */
    private String name;
    /** A tag that may be used to identify the type of inventory. */
    private String tag;
    /** The number of rows to display per page. */
    private int rowsPerPage;

    /** The map of items in the inventory. */
    private final Map<Integer, SGButton> items;
    /** The set of sticky slots (that should remain when the page is changed). */
    private final HashSet<Integer> stickiedSlots;

    /** The currently selected page of the inventory. */
    private int currentPage;

    /**
     * Whether the "default" behaviors and interactions should be permitted or
     * blocked. (True prevents default behaviors such as moving items in the
     * inventory, false allows them).
     */
    private boolean blockDefaultInteractions;
    /**
     * Whether the pagination functionality should be enabled. (True adds
     * pagination buttons when they're needed, false does not).
     */
    private boolean enableAutomaticPagination;

    /** The toolbar builder used to render this GUI's toolbar. */
    private SGToolbarBuilder toolbarBuilder;
    /** The action to be performed on close. */
    private Consumer<SGMenu> onClose;
    /** The action to be performed on page change. */
    private Consumer<SGMenu> onPageChange;

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SGMenu) {
            SGMenu menu = (SGMenu) obj;
            for (int i : items.keySet()) {
                if (!menu.items.containsKey(i)) return false;
                if (!items.get(i).equals(menu.items.get(i))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Used by the library internally to construct an SGMenu.
     * The name parameter is color code translated.
     */
    public SGMenu(AtherialMenu link, JavaPlugin owner, SpiGUI spiGUI, String name, int rowsPerPage, String tag) {
        this.link = link;
        this.owner = owner;
        this.spiGUI = spiGUI;
        this.name = ChatColor.translateAlternateColorCodes('&', name);
        this.rowsPerPage = rowsPerPage;
        this.tag = tag;

        this.items = new HashMap<>();
        this.stickiedSlots = new HashSet<>();

        this.currentPage = 0;
    }

    /// INVENTORY SETTINGS ///

    public void setBlockDefaultInteractions(boolean blockDefaultInteractions) {
        this.blockDefaultInteractions = blockDefaultInteractions;
    }

    public Boolean areDefaultInteractionsBlocked() {
        return blockDefaultInteractions;
    }

    public void setAutomaticPaginationEnabled(boolean enableAutomaticPagination) {
        this.enableAutomaticPagination = enableAutomaticPagination;
    }

    public Boolean isAutomaticPaginationEnabled() {
        return enableAutomaticPagination;
    }

    public void setToolbarBuilder(SGToolbarBuilder toolbarBuilder) {
        this.toolbarBuilder = toolbarBuilder;
    }

    public SGToolbarBuilder getToolbarBuilder() {
        return this.toolbarBuilder;
    }

    /// INVENTORY OWNER ///

    public JavaPlugin getOwner() {
        return owner;
    }

    /// INVENTORY SIZE ///

    public int getRowsPerPage() {
        return rowsPerPage;
    }

    public int getPageSize() {
        return rowsPerPage * 9;
    }

    public void setRowsPerPage(int rowsPerPage) {
        this.rowsPerPage = rowsPerPage;
    }

    /// INVENTORY TAG ///

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    /// INVENTORY NAME ///

    public void setName(String name) {
        this.name = ChatColor.translateAlternateColorCodes('&', name);
    }

    public void setRawName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    /// BUTTONS ///

    public void addButton(SGButton button) {
        // Edge case for empty inv
        if (getHighestFilledSlot() == 0 && getButton(0) == null) {
            setButton(0, button);
            return;
        }
        setButton(getHighestFilledSlot() + 1, button);
    }

    public void addButtons(SGButton... buttons) {
        for (SGButton button : buttons) addButton(button);
    }

    public void setButton(int slot, SGButton button) {
        items.put(slot, button);
        if (link != null) link.setNeedsUpdate(true);
    }

    public void setButton(int page, int slot, SGButton button) {
        if (slot < 0 || slot > getPageSize()) return;
        setButton((page * getPageSize()) + slot, button);
    }

    public void removeButton(int slot) {
        items.remove(slot);
    }

    public void removeButton(int page, int slot) {
        if (slot < 0 || slot > getPageSize()) return;
        removeButton((page * getPageSize()) + slot);
    }

    public SGButton getButton(int slot) {
        if (slot < 0 || slot > getHighestFilledSlot()) return null;
        return items.get(slot);
    }

    public SGButton getButton(int page, int slot) {
        if (slot < 0 || slot > getPageSize()) return null;
        return getButton((page * getPageSize()) + slot);
    }

    /// PAGINATION ///

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage (int page) {
        this.currentPage = page;
        if (this.onPageChange != null) this.onPageChange.accept(this);
    }

    public int getMaxPage() {
        return (int) Math.ceil(((double) getHighestFilledSlot() + 1) / ((double) getPageSize()));
    }

    public int getHighestFilledSlot() {
        int slot = 0;
        for (int nextSlot : items.keySet()) {
            if (items.get(nextSlot) != null && nextSlot > slot) slot = nextSlot;
        }
        return slot;
    }

    public boolean nextPage(HumanEntity viewer) {
        if (currentPage < getMaxPage() - 1) {
            currentPage++;
            refreshInventory(viewer);
            if (this.onPageChange != null) this.onPageChange.accept(this);
            return true;
        } else {
            return false;
        }
    }

    public boolean previousPage(HumanEntity viewer) {
        if (currentPage > 0) {
            currentPage--;
            refreshInventory(viewer);
            if (this.onPageChange != null) this.onPageChange.accept(this);
            return true;
        } else {
            return false;
        }
    }

    /// STICKY SLOTS ///

    public void stickSlot(int slot) {
        if (slot < 0 || slot >= getPageSize()) return;
        this.stickiedSlots.add(slot);
    }

    public void unstickSlot(int slot) {
        this.stickiedSlots.remove(slot);
    }

    public void clearStickiedSlots() {
        this.stickiedSlots.clear();
    }

    public boolean isStickiedSlot(int slot) {
        if (slot < 0 || slot >= getPageSize()) return false;
        return this.stickiedSlots.contains(slot);
    }

    public void clearAllButStickiedSlots() {
        this.currentPage = 0;
        items.entrySet().removeIf(item -> !isStickiedSlot(item.getKey()));
    }

    /// EVENTS ///

    public Consumer<SGMenu> getOnClose() {
        return this.onClose;
    }

    public void setOnClose(Consumer<SGMenu> onClose) {
        this.onClose = onClose;
    }

    public Consumer<SGMenu> getOnPageChange() {
        return this.onPageChange;
    }

    public void setOnPageChange(Consumer<SGMenu> onPageChange) {
        this.onPageChange = onPageChange;
    }

    /// COMPONENT TITLE SUPPORT (REFLECTION) ///

    /**
     * Builds the desired String (legacy) title with page placeholders resolved.
     */
    private String computeTitleString() {
        return name
                .replace("{currentPage}", String.valueOf(currentPage + 1))
                .replace("{maxPage}", String.valueOf(getMaxPage()));
    }

    /**
     * Returns the Component title from the linked AtherialMenu (may be null).
     * If you need page numbers in the Component, build them in AtherialMenu#getTitleComponent().
     */
    private Component computeTitleComponent() {
        return (link != null) ? link.getTitleComponent() : null;
    }

    /**
     * Try Paper's static Bukkit.createInventory(InventoryHolder, int, Component).
     */
    private static Inventory tryCreateInventoryWithComponent(InventoryHolder holder, int size, Component title) {
        if (title == null) return null;
        try {
            Method m = Bukkit.class.getMethod("createInventory", InventoryHolder.class, int.class, Component.class);
            Object inv = m.invoke(null, holder, size, title);
            return (Inventory) inv;
        } catch (NoSuchMethodException e) {
            return null; // running on an API without the Component overload
        } catch (Throwable t) {
            return null;
        }
    }

    /**
     * Try Paper's HumanEntity/Player#openInventory(Inventory, Component).
     */
    private static boolean tryOpenWithComponentTitle(HumanEntity viewer, Inventory inv, Component title) {
        if (viewer == null || inv == null || title == null) return false;
        try {
            Method m = viewer.getClass().getMethod("openInventory", Inventory.class, Component.class);
            m.invoke(viewer, inv, title);
            return true;
        } catch (NoSuchMethodException e) {
            return false; // older APIS: no component title support
        } catch (Throwable t) {
            return false;
        }
    }

    /**
     * Resolve whether automatic pagination is in effect for this menu.
     */
    private boolean effectivePaginationEnabled() {
        boolean isAutomatic = spiGUI.isAutomaticPaginationEnabled();
        if (isAutomaticPaginationEnabled() != null) {
            isAutomatic = isAutomaticPaginationEnabled();
        }
        return isAutomatic;
    }

    /// INVENTORY API ///

    /**
     * Refresh an inventory that is currently open for a given viewer.
     * Re-applies Component title after size/title changes or content refresh.
     */
    public void refreshInventory(HumanEntity viewer) {
        // If the open inventory isn't an SGMenu - or if it isn't this inventory, do nothing.
        if (!(viewer.getOpenInventory().getTopInventory().getHolder() instanceof SGMenu)
                || viewer.getOpenInventory().getTopInventory().getHolder() != this) {
            return;
        }

        final boolean needsPagination = getMaxPage() > 0 && effectivePaginationEnabled();
        final int desiredSize = needsPagination ? getPageSize() + 9 : getPageSize();
        final String desiredName = computeTitleString();

        final boolean sizeDiffers = viewer.getOpenInventory().getTopInventory().getSize() != desiredSize;
        final boolean titleDiffers = !Objects.equals(viewer.getOpenInventory().getTitle(), desiredName);

        if (sizeDiffers || titleDiffers) {
            // Rebuild & open new inventory (prefer Component if available)
            Inventory inv = buildInventoryInternal();
            viewer.openInventory(inv); // fallback open
            Component comp = computeTitleComponent();
            if (comp != null) {
                tryOpenWithComponentTitle(viewer, inv, comp);
            }
            return;
        }

        // Otherwise, just refresh contents in-place
        viewer.getOpenInventory().getTopInventory().setContents(getInventory().getContents());

        // And still re-assert the Component title to be safe (if supported)
        Component comp = computeTitleComponent();
        if (comp != null) {
            tryOpenWithComponentTitle(viewer, viewer.getOpenInventory().getTopInventory(), comp);
        }
    }

    /**
     * Returns the Bukkit/Spigot Inventory that represents the GUI.
     * Prefers creating with a Component title (Paper) via reflection, falls back to String.
     */
    @Override
    public Inventory getInventory() {
        return buildInventoryInternal();
    }

    /**
     * Internal builder that renders items, toolbar, and title (Component via reflection if available).
     */
    private Inventory buildInventoryInternal() {
        boolean needsPagination = getMaxPage() > 0 && effectivePaginationEnabled();
        int size = needsPagination ? getPageSize() + 9 : getPageSize();

        // Try to create with Component title first
        Component comp = computeTitleComponent();
        Inventory inventory = tryCreateInventoryWithComponent(this, size, comp);

        // Fallback to legacy String title
        if (inventory == null) {
            String legacyTitle = computeTitleString();
            inventory = Bukkit.createInventory(this, size, legacyTitle);
        }

        // Add main page items
        for (int key = currentPage * getPageSize(); key < (currentPage + 1) * getPageSize(); key++) {
            if (key > getHighestFilledSlot()) break;
            if (items.containsKey(key)) {
                inventory.setItem(key - (currentPage * getPageSize()), items.get(key).getIcon());
            }
        }

        // Stickied slots
        for (int stickiedSlot : stickiedSlots) {
            SGButton b = items.get(stickiedSlot);
            if (b != null) {
                inventory.setItem(stickiedSlot, b.getIcon());
            }
        }

        // Pagination toolbar
        if (needsPagination) {
            SGToolbarBuilder toolbarButtonBuilder = spiGUI.getDefaultToolbarBuilder();
            if (getToolbarBuilder() != null) {
                toolbarButtonBuilder = getToolbarBuilder();
            }

            int pageSize = getPageSize();
            for (int i = pageSize; i < pageSize + 9; i++) {
                int offset = i - pageSize;

                SGButton paginationButton = toolbarButtonBuilder.buildToolbarButton(
                        offset, getCurrentPage(), SGToolbarButtonType.getDefaultForSlot(offset), this
                );
                inventory.setItem(i, paginationButton != null ? paginationButton.getIcon() : null);
            }
        }

        return inventory;
    }
}
