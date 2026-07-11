import java.util.ArrayList;

/**
 * Represents a discovered or predefined potion recipe, combining a base
 * and one or more fruits into a named, sellable concoction.
 */
public class Recipe {
    private int concoctionId;
    private String base;
    private ArrayList<String> fruits = new ArrayList<>();
    private String resultName;
    private int sellValue;

    /**
     * Constructs a new Recipe.
     *
     * @param concoctionId the recipe's unique ID
     * @param base         the required base ingredient name
     * @param fruits       the list of required fruit ingredient names
     * @param resultName   the display name of the resulting potion
     * @param sellValue    the crystal value the resulting potion sells for
     */
    public Recipe(int concoctionId, String base, ArrayList<String> fruits, String resultName, int sellValue) {
        this.concoctionId = concoctionId;
        this.base = base;
        this.fruits = fruits;
        this.resultName = resultName;
        this.sellValue = sellValue;
    }

    /**
     * @return the recipe's unique ID
     */
    public int getConcoctionId() {
        return concoctionId;
    }

    /**
     * @return the required base ingredient name
     */
    public String getBase() {
        return base;
    }

    /**
     * @return the list of required fruit ingredient names
     */
    public ArrayList<String> getFruits() {
        return fruits;
    }

    /**
     * @return the display name of the resulting potion
     */
    public String getResultName() {
        return resultName;
    }

    /**
     * @return the crystal sell value of the resulting potion
     */
    public int getSellValue() {
        return sellValue;
    }

    /**
     * Checks whether a given base and set of fruits matches this recipe,
     * regardless of the order the fruits are given in.
     *
     * @param base   the base ingredient to check
     * @param fruits the fruit ingredients to check
     * @return true if the combination matches this recipe exactly
     */
    public boolean matches(String base, ArrayList<String> fruits) {
        if (!this.base.equals(base)) {
            return false;
        }
        if (this.fruits.size() != fruits.size()) {
            return false;
        }
        for (String fruit : fruits) {
            if (!this.fruits.contains(fruit)) {
                return false;
            }
        }
        return true;
    }

    /**
     * @return a readable summary of this recipe's ID, base, fruits, and sell value
     */
    @Override
    public String toString() {
        return "[" + concoctionId + "] " + resultName + " (Base: " + base + ", Fruits: " + fruits + ", Sells for: " + sellValue + ")";

    }
}
