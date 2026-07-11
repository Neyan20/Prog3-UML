import java.util.ArrayList;


/**
 * Stores the collection of recipes a player has discovered or unlocked.
 */
public class Spellbook {
    private ArrayList<Recipe> recipes;

    /**
     * Constructs an empty spellbook.
     */
    public Spellbook() {
        recipes = new ArrayList<Recipe>();
    }

    /**
     * Adds a recipe to the spellbook if it isn't already known, keeping
     * the list sorted by concoction ID.
     *
     * @param recipe the recipe to add
     */
    public void addRecipe(Recipe recipe) {
        boolean alreadyHave = hasRecipe(recipe);
        if (alreadyHave == false) {
            recipes.add(recipe);
            sortById();
        }
    }

    /**
     * Checks whether a given recipe (by concoction ID) is already known.
     *
     * @param recipe the recipe to check for
     * @return true if a recipe with the same concoction ID is already known
     */
    public boolean hasRecipe(Recipe recipe) {
        boolean found = false;
        for (int i = 0; i < recipes.size(); i++) {
            if (recipes.get(i).getConcoctionId() == recipe.getConcoctionId()) {
                found = true;
            }
        }
        return found;
    }

    /**
     * Checks whether a recipe with the given ID is already known.
     *
     * @param id the concoction ID to check for
     * @return true if a recipe with that ID is known
     */
    public boolean hasRecipeById(int id) {
        for (int i = 0; i < recipes.size(); i++) {
            if (recipes.get(i).getConcoctionId() == id) {
                return true;
            }
        }
        return false;
    }

    /**
     * Retrieves a known recipe by its concoction ID.
     *
     * @param id the concoction ID to look up
     * @return the matching recipe, or null if not found
     */
    public Recipe getRecipeById(int id) {
        Recipe result = null;
        for (int i = 0; i < recipes.size(); i++) {
            if (recipes.get(i).getConcoctionId() == id) {
                result = recipes.get(i);
            }
        }
        return result;
    }

    /**
     * @return the full list of known recipes
     */
    public ArrayList<Recipe> getRecipes() {
        return recipes;
    }

    /**
     * @return the number of recipes currently known
     */
    public int getCount() {
        return recipes.size();
    }

    /**
     * Sorts the known recipes in ascending order by concoction ID, using
     * an insertion sort.
     */
    public void sortById() {
        for (int i = 1; i < recipes.size(); i++) {
            Recipe key = recipes.get(i);
            int j = i - 1;
            while (j >= 0 && recipes.get(j).getConcoctionId() > key.getConcoctionId()) {
                recipes.set(j + 1, recipes.get(j));
                j = j - 1;
            }
            recipes.set(j + 1, key);
        }
    }

    /**
     * Prints every known recipe in the spellbook to the console, or a
     * placeholder message if none have been discovered yet.
     */
    public void displayAll() {
        System.out.println("==================== SPELLBOOK ====================");
        if (recipes.size() == 0) {
            System.out.println("  Your spellbook is empty. Discover recipes in creative mode!");
        } else {
            for (int i = 0; i < recipes.size(); i++) {
                System.out.println("  " + recipes.get(i).toString());
            }
        }
        System.out.println("====================================================");
    }
}
