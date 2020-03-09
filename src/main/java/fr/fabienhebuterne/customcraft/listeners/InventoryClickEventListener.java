package fr.fabienhebuterne.customcraft.listeners;

import fr.fabienhebuterne.customcraft.CustomCraft;
import fr.fabienhebuterne.customcraft.domain.RecipeInventoryService;
import fr.fabienhebuterne.customcraft.domain.RecipeService;
import fr.fabienhebuterne.customcraft.domain.RecipeType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import static fr.fabienhebuterne.customcraft.commands.CommandCreate.*;

public class InventoryClickEventListener implements Listener {

    private CustomCraft customCraft;

    public InventoryClickEventListener(CustomCraft customCraft) {
        this.customCraft = customCraft;
    }

    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent e) {
        this.chooseCraftType(e);
        this.createCraftFromShapedOrShapelessRecipe(e);
    }

    private void chooseCraftType(InventoryClickEvent e) {
        if (!e.getView().getTitle().contentEquals("CustomCraft - Recipe type")) {
            return;
        }

        boolean invClick = e.getClickedInventory() != null && e.getClickedInventory().getType() == InventoryType.CHEST;

        if (invClick) {
            e.setCancelled(true);
        }

        Player player = (Player) e.getView().getPlayer();

        if (e.getSlot() == RecipeType.SHAPED_RECIPE.getInvIndex()) {
            new RecipeInventoryService(customCraft).openCreateCraftShapedOrShapelessRecipeInventory(
                    player,
                    RecipeType.SHAPED_RECIPE
            );
        }

        if (e.getSlot() == RecipeType.SHAPELESS_RECIPE.getInvIndex()) {
            new RecipeInventoryService(customCraft).openCreateCraftShapedOrShapelessRecipeInventory(
                    player,
                    RecipeType.SHAPELESS_RECIPE
            );
        }
    }

    private void createCraftFromShapedOrShapelessRecipe(InventoryClickEvent e) {
        if (!e.getView().getTitle().contains("CustomCraft - ") ||
                e.getView().getTitle().contentEquals("CustomCraft - Recipe type")) {
            return;
        }

        // TODO : Refactoring with tmpData variable and stop to use title to keep data ...
        RecipeType recipeType = RecipeType.valueOf(e.getView().getTitle().split("CustomCraft - ")[1]);

        boolean invCases = !CRAFT_CASES.contains(e.getSlot()) && RESULT_CRAFT_CASE != e.getSlot();
        boolean invClick = e.getClickedInventory() != null && e.getClickedInventory().getType() == InventoryType.CHEST;

        if (invCases && invClick) {
            e.setCancelled(true);
        }

        if (e.getSlot() == QUIT_INVENTORY_CASE) {
            e.getView().close();
        }

        if (e.getSlot() == VALID_INVENTORY_CASE) {
            // Craft order
            ArrayList<ItemStack> craftCaseRecipe = new ArrayList<>();

            // Give for each diffrent itemstack an unique id to set in grid
            HashMap<Integer, ItemStack> idCraftCaseRecipse = new HashMap<>();

            CRAFT_CASES.forEach(integer -> craftCaseRecipe.add(e.getInventory().getItem(integer)));

            craftCaseRecipe.forEach(itemStack -> {
                if (!idCraftCaseRecipse.containsValue(itemStack)) {
                    idCraftCaseRecipse.put(craftCaseRecipe.indexOf(itemStack), itemStack);
                }
            });

            ItemStack resultCraft = e.getInventory().getItem(RESULT_CRAFT_CASE);

            if (resultCraft == null) {
                ItemStack itemStack = e.getInventory().getItem(VALID_INVENTORY_CASE);
                ItemMeta itemMeta = itemStack.getItemMeta();
                itemMeta.setLore(Collections.singletonList("§cErreur : Vous n'avez pas mis d'item craftable..."));
                itemStack.setItemMeta(itemMeta);
                e.getInventory().setItem(VALID_INVENTORY_CASE, itemStack);
                return;
            }

            Player player = (Player) e.getView().getPlayer();

            String craftName = customCraft.getTmpData().get(player.getUniqueId());

            if (recipeType == RecipeType.SHAPED_RECIPE) {
                new RecipeService(this.customCraft).addShapedRecipe(
                        player,
                        craftCaseRecipe,
                        idCraftCaseRecipse,
                        resultCraft,
                        craftName,
                        recipeType
                );
            } else {
                new RecipeService(this.customCraft).addShapelessRecipe(
                        player,
                        craftCaseRecipe,
                        idCraftCaseRecipse,
                        resultCraft,
                        craftName,
                        recipeType
                );
            }

        }
    }

}
