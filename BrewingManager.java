import java.util.ArrayList;
import java.io.*;

/**
 * Handles all the brewing stuff.
 * Recipe mode, creative mode, and loading the recipe data file.
 *
 * @author [Your Names Here]
 * @version 1.0
 */
public class BrewingManager {
    private ArrayList<Recipe> recipeData;
    private IngredientCatalog catalog;

    public BrewingManager(IngredientCatalog catalog) {
        this.recipeData = new ArrayList<Recipe>();
        this.catalog = catalog;
    }

    // loads all valid recipes from the csv file
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

    public ArrayList<Recipe> getRecipeData() { return recipeData; }

    // brews using a recipe from the spellbook
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

    // brews in creative mode, success adds to spellbook, fail damages cauldron
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

    public Recipe validateCombo(String base, ArrayList<String> fruits) {
        for (int i = 0; i < recipeData.size(); i++)
            if (recipeData.get(i).matches(base, fruits) == true) return recipeData.get(i);
        return null;
    }

    public boolean hasIngredients(Player player, Recipe recipe) {
        if (player.getInventory().hasItem(recipe.getBase(), 1) == false) return false;
        ArrayList<String> fruits = recipe.getFruits();
        for (int i = 0; i < fruits.size(); i++)
            if (player.getInventory().hasItem(fruits.get(i), 1) == false) return false;
        return true;
    }

    public void sellConcoction(Player player, Recipe recipe) {
        System.out.println("  Potion bottled and sold automatically.");
        player.addCrystals(recipe.getSellValue());
    }

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
