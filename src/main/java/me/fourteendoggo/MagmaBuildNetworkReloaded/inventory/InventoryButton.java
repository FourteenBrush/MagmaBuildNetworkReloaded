package me.fourteendoggo.MagmaBuildNetworkReloaded.inventory;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public abstract class InventoryButton {
    private final ItemStack item;

    private InventoryButton(ItemStack item) {
        this.item = item;
    }

    public static InventoryButton from(ItemStack item, Consumer<InventoryClickEvent> onClick) {
        return from(item, true, onClick);
    }

    public static InventoryButton from(ItemStack item, boolean cancelClicks, Consumer<InventoryClickEvent> onClick) {
        return new InventoryButton(item) {
            @Override
            public void onClick(InventoryClickEvent event) {
                onClick.accept(event);
                if (cancelClicks) {
                    event.setCancelled(true);
                }
            }
        };
    }

    public ItemStack getItem() {
        return item;
    }

    public abstract void onClick(InventoryClickEvent event);
}
