# AtherialMenu Auto-Update - Quick Reference

## 🚀 Quick Start

```java
// 1. Create your menu class
public class MyMenu extends AtherialMenu<MyConfig> {
    
    @Override
    public void update() {
        // Your update logic
        set(10, new ItemStack(Material.DIAMOND));
    }
}

// 2. Open with auto-update
MyMenu menu = new MyMenu(player, config);
menu.create();
menu.setAutoUpdateInterval(1000); // Updates every second
```

## 📝 Method Signature

```java
public void setAutoUpdateInterval(long intervalMs)
```

**Parameters:**
- `intervalMs` - Milliseconds between updates
  - Positive value = Enable auto-update
  - Zero or negative = Disable auto-update

## ⏱️ Common Intervals

```java
setAutoUpdateInterval(500);   // 0.5 seconds - Fast updates
setAutoUpdateInterval(1000);  // 1 second - Default recommended
setAutoUpdateInterval(2000);  // 2 seconds - Moderate updates
setAutoUpdateInterval(5000);  // 5 seconds - Slow updates
setAutoUpdateInterval(-1);    // Disable auto-update
```

## ✅ Do's

```java
// ✅ Enable after create()
menu.create();
menu.setAutoUpdateInterval(1000);

// ✅ Call super in onRealClose()
@Override
public void onRealClose() {
    super.onRealClose(); // Important!
}

// ✅ Cache expensive operations
private ItemStack cachedItem;

@Override
public void update() {
    if (cachedItem == null) {
        cachedItem = createExpensiveItem();
    }
    set(10, cachedItem);
}
```

## ❌ Don'ts

```java
// ❌ Don't enable before create()
menu.setAutoUpdateInterval(1000);
menu.create(); // Won't work properly

// ❌ Don't use extremely fast intervals
menu.setAutoUpdateInterval(1); // Too fast, causes lag

// ❌ Don't forget super.onRealClose()
@Override
public void onRealClose() {
    // Missing super.onRealClose() - Task will leak!
    cleanup();
}

// ❌ Don't call forceUpdate() with auto-update enabled
menu.setAutoUpdateInterval(1000);
menu.forceUpdate(); // Redundant and can cause issues
```

## 🎯 Use Cases

| Use Case | Interval | Example |
|----------|----------|---------|
| Live stats | 500-1000ms | Player health, location |
| Server info | 1000-2000ms | TPS, player count |
| Leaderboards | 2000-5000ms | Top players, rankings |
| Economy data | 1000-3000ms | Balances, prices |
| Game timers | 500-1000ms | Countdown, match time |

## 🔄 Lifecycle

```
create() → setAutoUpdateInterval() → [Auto-updates] → onRealClose()
   ↓              ↓                         ↓               ↓
Opens menu    Starts task           Updates every N ms   Stops task
```

## 🛠️ Troubleshooting

| Problem | Solution |
|---------|----------|
| Menu not updating | Call `setAutoUpdateInterval()` after `create()` |
| Updates too slow | Decrease interval value |
| Menu flickering | Increase interval value |
| Task not stopping | Add `super.onRealClose()` |

## 📦 Complete Example

```java
public class LiveStatsMenu extends AtherialMenu<StatsConfig> {
    
    public LiveStatsMenu(Player player, StatsConfig config) {
        super(player, config);
    }
    
    @Override
    public String getTitle() {
        return "&a&lLive Stats";
    }
    
    @Override
    public int getRows() {
        return 3;
    }
    
    @Override
    public void update() {
        // Update every second
        set(10, createHealthItem());
        set(11, createLocationItem());
        set(12, createTimeItem());
    }
    
    private ItemStack createHealthItem() {
        ItemStack item = new ItemStack(Material.GOLDEN_APPLE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§c§lHealth");
        meta.setLore(Arrays.asList(
            "§7HP: §f" + player.getHealth()
        ));
        item.setItemMeta(meta);
        return item;
    }
    
    private ItemStack createLocationItem() {
        ItemStack item = new ItemStack(Material.COMPASS);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§b§lLocation");
        meta.setLore(Arrays.asList(
            "§7X: §f" + (int) player.getLocation().getX(),
            "§7Y: §f" + (int) player.getLocation().getY(),
            "§7Z: §f" + (int) player.getLocation().getZ()
        ));
        item.setItemMeta(meta);
        return item;
    }
    
    private ItemStack createTimeItem() {
        ItemStack item = new ItemStack(Material.CLOCK);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§e§lTime");
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        meta.setLore(Arrays.asList(
            "§7" + sdf.format(new Date())
        ));
        item.setItemMeta(meta);
        return item;
    }
    
    public void open() {
        create();
        setAutoUpdateInterval(1000); // Update every second
    }
    
    @Override
    public void onRealClose() {
        super.onRealClose(); // Stops auto-update
    }
}

// Usage
LiveStatsMenu menu = new LiveStatsMenu(player, config);
menu.open();
```

## 📚 See Also

- **Full Documentation:** `AUTO_UPDATE_DOCUMENTATION.md`
- **Implementation Details:** `IMPLEMENTATION_SUMMARY.md`
- **Working Example:** `ExampleAutoUpdateMenu.java`

## 💬 Quick Tips

1. **1000ms (1 second)** is the sweet spot for most use cases
2. Always call `super.onRealClose()` to prevent task leaks
3. Cache expensive ItemStack creation in fields
4. Use `needsUpdate()` flag for conditional updates
5. Test different intervals to find optimal performance

## 🎓 Remember

**Before:** `forceUpdate()` every tick = BAD ❌  
**After:** `setAutoUpdateInterval(1000)` = GOOD ✅

The auto-update feature handles all the complexity for you:
- ✅ Automatic scheduling
- ✅ Safe cleanup
- ✅ Flashing prevention
- ✅ Folia compatibility
- ✅ Simple API

