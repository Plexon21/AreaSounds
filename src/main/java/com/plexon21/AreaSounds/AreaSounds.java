package com.plexon21.AreaSounds;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class AreaSounds extends JavaPlugin {
	@Override
	public void onEnable() {
		AreaSounds();
		System.out.println("InfiniteBucket enabled");
	}

	@Override
	public void onDisable() {

	}

	public static ShapedRecipe infiniteBucketRecipe() {
		ItemStack infiniteBucket = new ItemStack(Material.WATER_BUCKET);
		ItemMeta meta = infiniteBucket.getItemMeta();
		meta.setDisplayName("Infinite Bucket");
		ArrayList<String> lore = new ArrayList<String>();
		lore.add("This is an endless ");
		lore.add("bucket of water.");
		meta.setLore(lore);
		infiniteBucket.setItemMeta(meta);
		ShapedRecipe recipe = new ShapedRecipe(infiniteBucket);
		recipe.shape(" E ", " W ", " B ");
		recipe.setIngredient('E', Material.EYE_OF_ENDER);
		recipe.setIngredient('W', Material.POTION);
		recipe.setIngredient('B', Material.BUCKET);
		return recipe;
	}

	public void AreaSounds() {
		getServer().addRecipe(infiniteBucketRecipe());
		getServer().getPluginManager().registerEvents(new AreaSoundsListener(getServer()),this);
	}
}
