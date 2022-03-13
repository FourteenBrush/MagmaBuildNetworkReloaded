package me.fourteendoggo.MagmaBuildNetworkReloaded.inventory;

import me.fourteendoggo.MagmaBuildNetworkReloaded.MBNPlugin;
import me.fourteendoggo.MagmaBuildNetworkReloaded.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.libs.it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import org.bukkit.craftbukkit.libs.it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryGui implements Listener {
    private static final ItemStack FILLER = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setDisplayName(" ").build();
    private final Inventory inventory;
    private final Int2ObjectMap<InventoryButton> items;
    private final boolean returnItems;

    public InventoryGui(MBNPlugin plugin, String name, int rows) {
        this(plugin, name, rows, false);
    }

    public InventoryGui(MBNPlugin plugin, String name, int rows, boolean returnItems) {
        inventory = Bukkit.createInventory(null, rows * 9, Utils.colorize(name));
        items = new Int2ObjectOpenHashMap<>();
        this.returnItems = returnItems;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void addItem(int slot, ItemStack item) {
        inventory.setItem(slot, item);
    }

    public void setItem(int slot, InventoryButton item) {
        addItem(slot, item.getItem());
        items.put(slot, item);
    }

    public void setItem(int slot, ItemStack item) {
        inventory.setItem(slot, item);
    }

    public void fill(int start, int end) {
        fill(start, end, FILLER);
    }

    public void fill(int start, int end, ItemStack item) {
        for (; start < end; start++) {
            inventory.setItem(start, item);
        }
    }

    public void open(Player player) {
        player.openInventory(inventory);
    }

    @EventHandler
    private void onClick(InventoryClickEvent event) {
        if (!inventory.equals(event.getView().getTopInventory())) return;
        InventoryButton item = items.get(event.getRawSlot());
        if (item != null) {
            item.onClick(event);
        }
    }

    @EventHandler
    private void onClose(InventoryCloseEvent event) {
        if (event.getInventory().equals(inventory) && event.getInventory().getViewers().size() < 2) {
            destroy(event.getPlayer());
        }
    }

    private void destroy() {
        destroy(null);
    }

    private void destroy(HumanEntity lastViewer) {
        HandlerList.unregisterAll(this);
        if (returnItems && lastViewer != null) {
            for (ItemStack item : inventory) {
                if (item == null) continue;
                lastViewer.getInventory().addItem(item).values().forEach(overflow -> lastViewer.getWorld().dropItem(lastViewer.getLocation(), overflow));
            }
        }
        reset();
    }

    public void reset() {
        items.clear();
        inventory.clear();
    }
}
