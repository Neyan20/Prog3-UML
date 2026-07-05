import java.util.ArrayList;

public class IngredientCatalog {
    private ArrayList<Ingredient> ALL_INGREDIENTS;

    public ArrayList<Ingredient> getAll() {
        return ALL_INGREDIENTS;
    }

    public ArrayList<Ingredient> getFruits() {
        ArrayList<Ingredient> fruits = new ArrayList<>();
        for (Ingredient ingredient : ALL_INGREDIENTS) {
            if (ingredient.isFruit()) {
                fruits.add(ingredient);
            }
        }
        return fruits;
    }

    public ArrayList<Ingredient> getBases() {
        ArrayList<Ingredient> bases = new ArrayList<>();
        for (Ingredient ingredient : ALL_INGREDIENTS) {
            if (ingredient.isBase()) {
                bases.add(ingredient);
            }
        }
        return bases;
    }

    public Ingredient getByName(String name) {
        for (Ingredient ingredient : ALL_INGREDIENTS) {
            if (ingredient.getName().equals(name)) {
                return ingredient;
            }
        }
        return null;
    }

    public Ingredient getRandom() {
        int randomIndex = (int) (Math.random() * ALL_INGREDIENTS.size());
        return ALL_INGREDIENTS.get(randomIndex);
    }

    public int getCount() {
        return ALL_INGREDIENTS.size();
    }
}
