import java.util.ArrayList;
import java.io.*;

/**
 * Handles loading recipe data and executing the brewing process, both for
 * known recipes (Recipe Mode) and experimental combinations (Creative Mode).
 */
public class BrewingManager {
    private ArrayList<Recipe> recipeData;
    private IngredientCatalog catalog;

    /**
     * Constructs the brewing manager.
     *
     * @param catalog the ingredient catalog used elsewhere in the brewing process
     */
    public BrewingManager(IngredientCatalog catalog) {
        this.recipeData = new ArrayList<Recipe>();
        this.catalog = catalog;
    }

    /**
     * Loads all valid recipes from a CSV file into memory, replacing any
     * previously loaded recipe data.
     *
     * @param filePath the path to the recipe data CSV file
     */
    public void loadRecipeData(String filePath) {
        recipeData.clear();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split(",", -1);
                if (parts.length < 5) continue;

                ArrayList<String> fruits = new ArrayList<String>();
                for (int i = 4; i < parts.length && i <= 6; i++) {
                    if (parts[i].trim().isEmpty() == false)
                        fruits.add(toTitleCase(parts[i].trim()));
                }
                if (fruits.size() == 0) continue;

                recipeData.add(new Recipe(
                    Integer.parseInt(parts[0].trim()),
                    toTitleCase(parts[2].trim()),
                    fruits,
                    toTitleCase(parts[1].trim()),
                    Integer.parseInt(parts[3].trim())
                ));
            }
            reader.close();
            System.out.println("  Loaded " + recipeData.size() + " recipes.");
        } catch (FileNotFoundException e) {
            System.out.println("  [Warning] Recipe file not found: " + filePath);
        } catch (Exception e) {
            System.out.println("  [Warning] Error reading recipes: " + e.getMessage());
        }
    }

    /**
     * @return the full list of loaded recipe data
     */
    public ArrayList<Recipe> getRecipeData() { return recipeData; }

    /**
     * Brews a known recipe from the spellbook using an available cauldron,
     * consuming ingredients and selling the result on success.
     *
     * @param player the brewing player
     * @param recipe the recipe to brew
     * @return true if the brew succeeded
     */
    public boolean brewRecipeMode(Player player, Recipe recipe) {
        Cauldron cauldron = player.getUsableCauldron();
        if (cauldron == null) { System.out.println("  No usable cauldrons! Bless one first."); return false; }
        if (hasIngredients(player, recipe) == false) { System.out.println("  Not enough ingredients to brew " + recipe.getResultName() + "."); return false; }

        consumeIngredients(player, recipe);
        System.out.println("  Successfully mixed " + recipe.getFruits() + " with " + recipe.getBase() + "!");
        System.out.println("  Brewed: " + recipe.getResultName());
        sellConcoction(player, recipe);
        player.incrementConcoctionsBrewed();
        return true;
    }

    /**
     * Attempts to brew an experimental base/fruit combination in Creative Mode.
     * On success, the discovered recipe is added to the spellbook and sold;
     * on failure, the cauldron used becomes damaged.
     *
     * @param player    the brewing player
     * @param base      the base ingredient used
     * @param fruits    the fruit ingredients used
     * @param spellbook the player's spellbook, updated if a new recipe is discovered
     * @return the discovered Recipe if the combination is valid, otherwise null
     */
    public Recipe brewCreativeMode(Player player, String base, ArrayList<String> fruits, Spellbook spellbook) {
        if (player.getUsableCauldronCount() <= 1) {
            System.out.println("  Only 1 usable cauldron left! Creative mode locked.");
            return null;
        }
        if (player.getInventory().hasItem(base, 1) == false) { System.out.println("  No " + base + " in inventory."); return null; }
        for (int i = 0; i < fruits.size(); i++) {
            if (player.getInventory().hasItem(fruits.get(i), 1) == false) { System.out.println("  No " + fruits.get(i) + " in inventory."); return null; }
        }

        Cauldron cauldron = player.getUsableCauldron();
        if (cauldron == null) { System.out.println("  No usable cauldrons!"); return null; }

        player.getInventory().removeItem(base, 1);
        for (int i = 0; i < fruits.size(); i++) player.getInventory().removeItem(fruits.get(i), 1);

        Recipe discovered = validateCombo(base, fruits);
        if (discovered != null) {
            System.out.println("  Successfully mixed " + fruits + " with " + base + "!");
            System.out.println("  You discovered: " + discovered.getResultName() + "!");
            if (spellbook.hasRecipe(discovered) == false) { spellbook.addRecipe(discovered); System.out.println("  New recipe added to Spellbook!"); }
            sellConcoction(player, discovered);
            player.incrementConcoctionsBrewed();
            return discovered;
        } else {
            String junk = base + " + " + fruits.toString();
            cauldron.setDamaged(junk);
            System.out.println("  Alchemy failed! Cauldron is now damaged.");
            System.out.println("  Junk in cauldron: " + junk);
            System.out.println("  Bless the cauldron to repair it (1000 crystals).");
            player.incrementConcoctionsBrewed();
            return null;
        }
    }

    /**
     * Checks a base/fruit combination against all known recipe data.
     *
     * @param base   the base ingredient to check
     * @param fruits the fruit ingredients to check
     * @return the matching Recipe, or null if the combination doesn't match any recipe
     */
    public Recipe validateCombo(String base, ArrayList<String> fruits) {
        for (int i = 0; i < recipeData.size(); i++)
            if (recipeData.get(i).matches(base, fruits) == true) return recipeData.get(i);
        return null;
    }

    /**
     * Checks whether the player has enough of each ingredient required by a recipe.
     *
     * @param player the player to check
     * @param recipe the recipe to check against
     * @return true if the player has at least 1 of the base and each required fruit
     */
    public boolean hasIngredients(Player player, Recipe recipe) {
        if (player.getInventory().hasItem(recipe.getBase(), 1) == false) return false;
        ArrayList<String> fruits = recipe.getFruits();
        for (int i = 0; i < fruits.size(); i++)
            if (player.getInventory().hasItem(fruits.get(i), 1) == false) return false;
        return true;
    }

    /**
     * Sells a brewed concoction, printing a confirmation and awarding the
     * player its crystal sell value.
     *
     * @param player the selling player
     * @param recipe the recipe representing the sold concoction
     */
    public void sellConcoction(Player player, Recipe recipe) {
        System.out.println("  Potion bottled and sold automatically.");
        player.addCrystals(recipe.getSellValue());
    }

    /**
     * Removes the base and fruit ingredients required by a recipe from the
     * player's inventory.
     *
     * @param player the player whose inventory is consumed
     * @param recipe the recipe whose ingredients are consumed
     */
    private void consumeIngredients(Player player, Recipe recipe) {
        player.getInventory().removeItem(recipe.getBase(), 1);
        ArrayList<String> fruits = recipe.getFruits();
        for (int i = 0; i < fruits.size(); i++) player.getInventory().removeItem(fruits.get(i), 1);
    }

    // converts all caps to title case e.g. SYRUP BASE -> Syrup Base
    private String toTitleCase(String input) {
        if (input == null || input.isEmpty()) return input;
        String[] words = input.split(" ");
        String result = "";
        for (int i = 0; i < words.length; i++) {
            if (words[i].isEmpty()) continue;
            result = result + words[i].substring(0, 1).toUpperCase() + words[i].substring(1).toLowerCase() + " ";
        }
        return result.trim();
    }
}
