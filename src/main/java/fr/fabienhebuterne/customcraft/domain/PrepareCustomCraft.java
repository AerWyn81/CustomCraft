package fr.fabienhebuterne.customcraft.domain;

import fr.fabienhebuterne.customcraft.domain.config.OptionItemStackConfig;
import lombok.Data;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

@Data
public class PrepareCustomCraft {
    private String craftName;
    private RecipeType recipeType;
    private ArrayList<ItemStack> craftCaseOrderRecipe;
    private ItemStack craftResult;
    // Only for craftResult for now
    private OptionItemStackConfig optionItemStackConfig = new OptionItemStackConfig();
    private Boolean highlightItem = false;
}
