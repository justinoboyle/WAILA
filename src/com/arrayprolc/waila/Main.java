package com.arrayprolc.waila;

import java.util.ArrayList;
import java.util.logging.Logger;

import me.confuser.barapi.BarAPI;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {

	private final Logger logger = Logger.getLogger("Minecraft");
	private static Main plugin;
	private final EventListener bl = new EventListener(this);
	private final Boolean b = false;
	private final String s = "§e§l{DISPLAY}";
	private final String id = " §8(§9{ID}§8)";
	private final String[] wool = { ChatColor.WHITE + "White",
			ChatColor.GOLD + "Orange", "§d" + "Magenta",
			ChatColor.BLUE + "Light-Blue", ChatColor.YELLOW + "Yellow",
			"§aLime", "§dPink", "§7Gray", "§7Light-Gray", "§3Cyan", "§5Purple",
			ChatColor.BLUE + "Blue", ChatColor.DARK_GRAY + "Brown",
			ChatColor.GREEN + "Green", ChatColor.RED + "Red",
			ChatColor.BLACK + "Black" };
	private final Material[] noid = { Material.FIRE, Material.LAVA,
			Material.WATER };
	private final ArrayList<Player> disabled = new ArrayList<Player>();

	@Override
	public void onDisable() {
		PluginDescriptionFile pdfFile = this.getDescription();
		this.logger.info(pdfFile.getName() + " plugin disabled!");
	}

	@Override
	public void onEnable() {
		Bukkit.getServer().getPluginManager().registerEvents(this, this);
		PluginManager pm = this.getServer().getPluginManager();
		pm.registerEvents(bl, this);
		getServer().getScheduler().scheduleSyncRepeatingTask(this,
				new Runnable() {
					@Override
					public void run() {
						for (Player p : Bukkit.getOnlinePlayers()) {
							show(p);
						}
					}
				}, 0, 1);
	}

	public boolean isAllowed(Player p) {
		return !disabled.contains(p);
	}

	public void setIsAllowed(Player p, Boolean b) {
		if (b) {
			if (disabled.contains(p)) {
				disabled.remove(p);
			}
		} else {
			disabled.add(p);
		}
	}

	@EventHandler
	private void onPlayerMove(PlayerMoveEvent e) {
		try {
			show(e.getPlayer());
		} catch (Exception ex) {

		}
	}

	@SuppressWarnings("deprecation")
	private boolean show(Player p) {
		if (disabled.contains(p)) {
			return false;
		}
		String MSG = "";
		Block b = p.getTargetBlock(null, 7);
		if (b.getType() == Material.AIR) {
			BarAPI.removeBar(p);
			return false;
		}
		String string = b.getType().toString().replace("_", " ").toLowerCase();
		MSG = s.replace("{DISPLAY}", string);
		MSG = ids(b, MSG);
		BarAPI.setMessage(p, MSG);
		return true;
	}

	@SuppressWarnings("deprecation")
	private String ids(Block b, String s) {
		for (Material m : noid) {
			if (b.getType() == m) {
				return s;
			}
		}
		if (b.getData() != 0) {

			if (b.getType() == Material.WOOL) {
				s = s + id.replace("{ID}", wool[b.getData()] + "");
			} else {
				s = s + id.replace("{ID}", b.getData() + "");
			}
		}

		return s;
	}

}
