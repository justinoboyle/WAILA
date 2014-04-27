package com.arrayprolc.waila;

import java.util.HashMap;

import net.minecraft.server.v1_7_R1.ContainerAnvil;
import net.minecraft.server.v1_7_R1.EntityHuman;
import net.minecraft.server.v1_7_R1.EntityPlayer;
import net.minecraft.server.v1_7_R1.PacketPlayOutOpenWindow;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class AnvilGUI {
	public static Main plugin;

	private class AnvilContainer extends ContainerAnvil {
		public AnvilContainer(EntityHuman entity) {
			super(entity.inventory, entity.world, 0, 0, 0, entity);
		}

		@Override
		public boolean a(EntityHuman entityhuman) {
			return true;
		}
	}

	public enum AnvilSlot {
		INPUT_LEFT(0), INPUT_RIGHT(1), OUTPUT(2);

		private int slot;

		private AnvilSlot(int slot) {
			this.slot = slot;
		}

		public int getSlot() {
			return slot;
		}

		public static AnvilSlot bySlot(int slot) {
			for (AnvilSlot anvilSlot : values()) {
				if (anvilSlot.getSlot() == slot) {
					return anvilSlot;
				}
			}

			return null;
		}
	}

	public class AnvilClickEvent {
		private final AnvilSlot slot;

		private final String name;

		private boolean close = true;
		private boolean destroy = true;

		public AnvilClickEvent(AnvilSlot slot, String name) {
			this.slot = slot;
			this.name = name;
		}

		public AnvilSlot getSlot() {
			return slot;
		}

		public String getName() {
			return name;
		}

		public boolean getWillClose() {
			return close;
		}

		public void setWillClose(boolean close) {
			this.close = close;
		}

		public boolean getWillDestroy() {
			return destroy;
		}

		public void setWillDestroy(boolean destroy) {
			this.destroy = destroy;
		}
	}

	public interface AnvilClickEventHandler {
		public void onAnvilClick(AnvilClickEvent event);
	}

	private Player player;

	@SuppressWarnings("unused")
	private AnvilClickEventHandler handler;

	private HashMap<AnvilSlot, ItemStack> items = new HashMap<AnvilSlot, ItemStack>();

	private Inventory inv;

	private Listener listener;

	public AnvilGUI(Player player, final AnvilClickEventHandler handler) {
		this.player = player;
		this.handler = handler;

		this.listener = new Listener() {
			@EventHandler
			public void onInventoryClick(InventoryClickEvent event) {
				if (event.getWhoClicked() instanceof Player) {
					@SuppressWarnings("unused")
					Player clicker = (Player) event.getWhoClicked();

					if (event.getInventory().equals(inv)) {
						event.setCancelled(true);

						ItemStack item = event.getCurrentItem();
						int slot = event.getRawSlot();
						String name = "";

						if (item != null) {
							if (item.hasItemMeta()) {
								ItemMeta meta = item.getItemMeta();

								if (meta.hasDisplayName()) {
									name = meta.getDisplayName();
								}
							}
						}

						AnvilClickEvent clickEvent = new AnvilClickEvent(
								AnvilSlot.bySlot(slot), name);

						handler.onAnvilClick(clickEvent);

						if (clickEvent.getWillClose()) {
							event.getWhoClicked().closeInventory();
						}

						if (clickEvent.getWillDestroy()) {
							destroy();
						}
					}
				}
			}

			@SuppressWarnings("unused")
			@EventHandler
			public void onInventoryClose(InventoryCloseEvent event) {
				if (event.getPlayer() instanceof Player) {
					Player player = (Player) event.getPlayer();
					Inventory inv = event.getInventory();

					if (inv.equals(AnvilGUI.this.inv)) {
						inv.clear();
						destroy();
					}
				}
			}

			@EventHandler
			public void onPlayerQuit(PlayerQuitEvent event) {
				if (event.getPlayer().equals(getPlayer())) {
					destroy();
				}
			}
		};

		Bukkit.getPluginManager().registerEvents(listener, plugin); // Replace
																	// with
																	// instance
																	// of
																	// main
																	// class
	}

	public Player getPlayer() {
		return player;
	}

	public void setSlot(AnvilSlot slot, ItemStack item) {
		items.put(slot, item);
	}

	public void open() {
		EntityPlayer p = ((CraftPlayer) player).getHandle();

		AnvilContainer container = new AnvilContainer(p);

		// Set the items to the items from the inventory given
		inv = container.getBukkitView().getTopInventory();

		for (AnvilSlot slot : items.keySet()) {
			inv.setItem(slot.getSlot(), items.get(slot));
		}

		// Counter stuff that the game uses to keep track of inventories
		int c = p.nextContainerCounter();

		// Send the packet
		p.playerConnection.sendPacket(new PacketPlayOutOpenWindow(c, 8,
				"Repairing", 9, true));

		// Set their active container to the container
		p.activeContainer = container;

		// Set their active container window id to that counter stuff
		p.activeContainer.windowId = c;

		// Add the slot listener
		p.activeContainer.addSlotListener(p);
	}

	public void destroy() {
		player = null;
		handler = null;
		items = null;

		HandlerList.unregisterAll(listener);

		listener = null;
	}
}