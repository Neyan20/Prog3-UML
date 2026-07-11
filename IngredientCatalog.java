import java.util.ArrayList;
import java.util.Arrays;

/**
 * Holds the master list of every ingredient available in the game and
 * provides lookup, filtering, and randomization methods over that list.
 */
public class IngredientCatalog {
    private ArrayList<Ingredient> ALL_INGREDIENTS;

    /**
     * Constructs the catalog and populates it with all base game ingredients
     * (9 fruits and 5 bases).
     */
    public IngredientCatalog() {
        this.ALL_INGREDIENTS = new ArrayList<>(Arrays.asList(
            //fruits
            new Ingredient("Strawberry", 125, 25, "fruit"),
            new Ingredient("Orange", 80, 40, "fruit"),
            new Ingredient("Lemon", 50, 25, "fruit"),
            new Ingredient("Banana", 75, 50, "fruit"),
            new Ingredient("Mango", 90, 30, "fruit"),
            new Ingredient("Pineapple", 240, 120, "fruit"),
            new Ingredient("Kiwi", 200, 80, "fruit"),
            new Ingredient("Blueberry", 120, 20, "fruit"),
            new Ingredient("Coconut", 180, 90, "fruit"),

            //bases
            new Ingredient("Syrup Base", 50, 10, "base"),
            new Ingredient("Bubble Base", 80, 20, "base"),
            new Ingredient("Perfume Base", 250, 50, "base"),
            new Ingredient("Milk Base", 60, 15, "base"),
            new Ingredient("Lotion Base", 150, 25, "base")
        ));
    }

    /**
     * @return every ingredient in the catalog
     */
    public ArrayList<Ingredient> getAll() {
        return ALL_INGREDIENTS;
    }

    /**
     * @return only the ingredients whose type is "fruit"
     */
    public ArrayList<Ingredient> getFruits() {
        ArrayList<Ingredient> fruits = new ArrayList<>();
        for (Ingredient ingredient : ALL_INGREDIENTS) {
            if (ingredient.isFruit()) {
                fruits.add(ingredient);
            }
        }
        return fruits;
    }

    /**
     * @return only the ingredients whose type is "base"
     */
    public ArrayList<Ingredient> getBases() {
        ArrayList<Ingredient> bases = new ArrayList<>();
        for (Ingredient ingredient : ALL_INGREDIENTS) {
            if (ingredient.isBase()) {
                bases.add(ingredient);
            }
        }
        return bases;
    }

    /**
     * Looks up an ingredient by its exact name.
     *
     * @param name the ingredient name to search for
     * @return the matching Ingredient, or null if none is found
     */
    public Ingredient getByName(String name) {
        for (Ingredient ingredient : ALL_INGREDIENTS) {
            if (ingredient.getName().equals(name)) {
                return ingredient;
            }
        }
        return null;
    }

    /**
     * @return a randomly selected ingredient from the full catalog
     */
    public Ingredient getRandom() {
        int randomIndex = (int) (Math.random() * ALL_INGREDIENTS.size());
        return ALL_INGREDIENTS.get(randomIndex);
    }

    /**
     * @return the total number of ingredients in the catalog
     */
    public int getCount() {
        return ALL_INGREDIENTS.size();
    }
}
