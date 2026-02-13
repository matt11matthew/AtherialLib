# AtherialMenu Auto-Update Feature Documentation

## Overview
The `AtherialMenu` class now supports automatic periodic updates with the new `setAutoUpdateInterval(long intervalMs)` method. This feature provides a safe and efficient way to update menus without causing flashing, duplication, or menu breaks.

## Why Use Auto-Update Instead of forceUpdate()?
Calling `forceUpdate()` every tick (or too frequently) can cause:
- ✗ Screen flashing/flickering
- ✗ Item duplication glitches
- ✗ Menu corruption
- ✗ Poor performance
- ✗ Player experience issues

The auto-update feature solves these problems by:
- ✓ Controlling update frequency with time-based throttling
- ✓ Preventing overlapping updates
- ✓ Automatically cleaning up when menu closes
- ✓ Safely checking if menu is still open before updating
- ✓ Using Folia-compatible entity-bound schedulers

## Usage

### Basic Example
```java
public class MyMenu extends AtherialMenu<MyConfig> {
    
    public MyMenu(Player player, MyConfig config) {
        super(player, config);
    }
    
    @Override
    public void update() {
        // Your menu update logic here
        set(10, new ItemStack(Material.DIAMOND), event -> {
            player.sendMessage("Clicked!");
        });
    }
    
    public void open() {
        create();
        
        // Enable auto-update every 1 second (1000ms)
        setAutoUpdateInterval(1000);
    }
}
```

### Advanced Example with Dynamic Updates
```java
public class LiveStatsMenu extends AtherialMenu<StatsConfig> {
    
    private int refreshRate = 2000; // 2 seconds default
    
    public LiveStatsMenu(Player player, StatsConfig config) {
        super(player, config);
    }
    
    @Override
    public void update() {
        // Update player stats
        set(10, createStatsItem(player));
        
        // Update online players count
        set(11, createOnlinePlayersItem());
        
        // Update server TPS
        set(12, createTPSItem());
    }
    
    public void openWithRefreshRate(int milliseconds) {
        create();
        this.refreshRate = milliseconds;
        setAutoUpdateInterval(refreshRate);
    }
    
    @Override
    public void onRealClose() {
        super.onRealClose(); // Important! This cancels the auto-update
        // Your cleanup logic here
    }
}
```

## Method Reference

### `setAutoUpdateInterval(long intervalMs)`
Sets the auto-update interval for this menu.

**Parameters:**
- `intervalMs` - The interval in milliseconds between updates
  - Set to any positive value to enable auto-updates
  - Set to 0 or negative to disable auto-updates

**Behavior:**
- Minimum interval is 1 tick (50ms) - values lower than 50ms will round up to 50ms
- Automatically cancels any existing auto-update task before starting a new one
- Uses FoliaLib's entity-bound scheduler for Folia compatibility
- Updates only occur when:
  - The player is online
  - The menu is still open
  - The minimum interval time has elapsed
  - No update is currently in progress

**Example:**
```java
// Update every 500ms (half second)
setAutoUpdateInterval(500);

// Update every 5 seconds
setAutoUpdateInterval(5000);

// Disable auto-updates
setAutoUpdateInterval(-1);
```

## Best Practices

### 1. Choose Appropriate Update Intervals
```java
// ❌ TOO FAST - May cause lag
setAutoUpdateInterval(50);  // Every tick

// ✓ GOOD - For live data
setAutoUpdateInterval(500);  // Half second

// ✓ GOOD - For general updates
setAutoUpdateInterval(1000); // 1 second

// ✓ GOOD - For less critical data
setAutoUpdateInterval(2000); // 2 seconds
```

### 2. Always Call super.onRealClose()
```java
@Override
public void onRealClose() {
    super.onRealClose(); // Cancels auto-update automatically
    // Your cleanup code here
}
```

### 3. Use with needsUpdate Flag
```java
@Override
public void update() {
    // Only do expensive operations when needed
    if (needsUpdate()) {
        // Heavy calculations
        recalculateStats();
    }
    
    // Update menu items
    set(10, createItem());
}
```

### 4. Conditional Auto-Update
```java
public void openMenu(boolean liveUpdate) {
    create();
    
    if (liveUpdate) {
        // Enable auto-updates for live data
        setAutoUpdateInterval(1000);
    }
    // Otherwise, menu updates only when needsUpdate() is called
}
```

## Performance Tips

1. **Optimize your update() method**
   - Cache ItemStacks when possible
   - Only update slots that have changed
   - Avoid expensive calculations every update

2. **Use appropriate intervals**
   - Don't update faster than needed
   - Consider player count and server load
   - 1000ms (1 second) is a good default for most use cases

3. **Clean up properly**
   - Auto-update automatically stops when:
     - Player goes offline
     - Menu is closed
     - `onRealClose()` is called
     - `destroy()` is called

## Thread Safety

The auto-update system is thread-safe and uses:
- FoliaLib entity-bound schedulers (Folia-compatible)
- Atomic flags for cancellation
- Safe update guards to prevent overlapping updates

## Troubleshooting

### Menu not updating
- Verify `setAutoUpdateInterval()` is called after `create()`
- Check that interval is positive (> 0)
- Ensure `update()` method is implemented

### Updates too slow
- Check your interval value (in milliseconds)
- Verify no exceptions in `update()` method
- Check server TPS

### Items flickering
- Increase update interval
- Cache ItemStacks and only update when changed
- Use `needsUpdate()` flag appropriately

## Migration from forceUpdate()

### Before (Problems):
```java
// ❌ This causes flashing and lag
public void startUpdating() {
    Bukkit.getScheduler().runTaskTimer(plugin, () -> {
        if (menu != null) {
            forceUpdate(); // Called every tick - BAD!
        }
    }, 0, 1);
}
```

### After (Recommended):
```java
// ✓ Safe, efficient, and clean
public void open() {
    create();
    setAutoUpdateInterval(1000); // Update every second
}
```

## Example: Live Leaderboard Menu
```java
public class LeaderboardMenu extends AtherialMenu<LeaderboardConfig> {
    
    public LeaderboardMenu(Player player, LeaderboardConfig config) {
        super(player, config);
    }
    
    @Override
    public String getTitle() {
        return "&6&lTop Players";
    }
    
    @Override
    public int getRows() {
        return 6;
    }
    
    @Override
    public void update() {
        clearMenu();
        
        // Get top 10 players
        List<TopPlayer> topPlayers = getTopPlayers();
        
        for (int i = 0; i < Math.min(topPlayers.size(), 45); i++) {
            TopPlayer tp = topPlayers.get(i);
            ItemStack skull = createPlayerSkull(tp);
            set(i, skull);
        }
        
        // Add refresh indicator
        set(53, createRefreshItem());
    }
    
    public void open() {
        create();
        // Update leaderboard every 5 seconds
        setAutoUpdateInterval(5000);
    }
}
```

## Compatibility

- ✓ Works with Spigot 1.8+
- ✓ Works with Paper
- ✓ Works with Folia (uses FoliaLib)
- ✓ Compatible with all existing AtherialMenu features
- ✓ No breaking changes to existing code

## Summary

The auto-update feature provides a robust, efficient, and safe way to create dynamic menus that update automatically. By using time-based throttling and proper cleanup mechanisms, it prevents the common issues associated with frequent forced updates while maintaining a smooth player experience.

