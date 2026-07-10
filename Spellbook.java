import java.util.ArrayList;


public class Spellbook {
    private ArrayList<Recipe> recipes;

    public Spellbook() {
        recipes = new ArrayList<Recipe>();
    }

    // adds a recipe to the spellbook if its not there yet
    public void addRecipe(Recipe recipe) {
        boolean alreadyHave = hasRecipe(recipe);
        if (alreadyHave == false) {
            recipes.add(recipe);
            sortById();
        }
    }

    public boolean hasRecipe(Recipe recipe) {
        boolean found = false;
        for (int i = 0; i < recipes.size(); i++) {
            if (recipes.get(i).getConcoctionId() == recipe.getConcoctionId()) {
                found = true;
            }
        }
        return found;
    }

    public boolean hasRecipeById(int id) {
        for (int i = 0; i < recipes.size(); i++) {
            if (recipes.get(i).getConcoctionId() == id) {
                return true;
            }
        }
        return false;
    }

    public Recipe getRecipeById(int id) {
        Recipe result = null;
        for (int i = 0; i < recipes.size(); i++) {
            if (recipes.get(i).getConcoctionId() == id) {
                result = recipes.get(i);
            }
        }
        return result;
    }

    public ArrayList<Recipe> getRecipes() {
        return recipes;
    }

    public int getCount() {
        return recipes.size();
    }

    // sort recipes by concoction id ascending
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
