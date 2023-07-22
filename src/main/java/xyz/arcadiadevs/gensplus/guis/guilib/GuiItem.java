package xyz.arcadiadevs.gensplus.guis.guilib;

import org.bukkit.inventory.ItemStack;

public record GuiItem(GuiItemType type, ItemStack item, Runnable action) {
}
