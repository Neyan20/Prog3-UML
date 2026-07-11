import java.util.HashMap;
import java.util.Map;

/**
 * Tracks the quantities of fruit and base ingredients a player currently holds.
 */
public class Inventory {
    Map<String, Integer> fruits = new HashMap<>();
    Map<String, Integer> bases = new HashMap<>();
    private IngredientCatalog catalog;

    /**
     * Constructs an empty inventory.
     *
     * @param catalog the ingredient catalog used to validate items being added
     */
    public Inventory(IngredientCatalog catalog) {
        this.catalog = catalog;
    }

    /**
     * Adds a given quantity of an ingredient to the inventory, sorting it
     * into the fruits or bases map based on its catalog type.
     *
     * @param name the ingredient name to add
     * @param qty  the quantity to add
     */
    public void addItem(String name, int qty) {
        Ingredient ingredient = catalog.getByName(name);

        if (ingredient != null) {
            if (ingredient .isFruit()) {
                fruits.put(name, fruits.getOrDefault(name, 0) + qty);
            }
            else if (ingredient.isBase()) {
                bases.put(name, bases.getOrDefault(name, 0) + qty);
            }
        }
        else {
            System.out.println("Cannot add item: " + name + " is not a valid ingredient.");
        }
    }

    /**
     * Removes a given quantity of an ingredient from the inventory.
     *
     * @param name the ingredient name to remove
     * @param qty  the quantity to remove
     * @return true if the removal succeeded, false if not enough was held or the item wasn't found
     */
    public boolean removeItem(String name, int qty) {
        if (fruits.containsKey(name)) {
            int currentQty = fruits.get(name);
            if (currentQty >= qty) {
                fruits.put(name, currentQty - qty);
                return true;
            } else {
                return false;
            }
        } else if (bases.containsKey(name)) {
            int currentQty = bases.get(name);
            if (currentQty >= qty) {
                bases.put(name, currentQty - qty);
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * Checks whether the inventory holds at least the given quantity of an item.
     *
     * @param name the ingredient name to check
     * @param qty  the required quantity
     * @return true if enough of the item is held
     */
    public boolean hasItem(String name, int qty) {
        if (fruits.containsKey(name) && fruits.get(name) >= qty) {
            return true;
        } else if (bases.containsKey(name) && bases.get(name) >= qty) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Gets the quantity currently held of a given ingredient.
     *
     * @param name the ingredient name to look up
     * @return the quantity held, or 0 if the item isn't in the inventory
     */
    public int getQuantity(String name) {
        if (fruits.containsKey(name)) {
            return fruits.get(name);
        } else if (bases.containsKey(name)) {
            return bases.get(name);
        } else {
            return 0;
        }
    }

    /**
     * @return a map of held fruit names to their quantities
     */
    public Map<String, Integer> getFruits() {
        return fruits;
    }

    /**
     * @return a map of held base names to their quantities
     */
    public Map<String, Integer> getBases() {
        return bases;
    }

    /**
     * Prints the current contents of the inventory (fruits and bases) to the console.
     */
    public void display() {
        System.out.println("=== Fruits ===");

        if (fruits.isEmpty()) {
            System.out.println("Currently no fruits in inventory.");
        }
        else {
            for (Map.Entry<String, Integer> entry : fruits.entrySet()) {
                String name = entry.getKey();
                int qty = entry.getValue();
                System.out.println(name + " = " + qty);
            }
        }
        System.out.println("\n=== Bases ===");
        if (bases.isEmpty()) {
            System.out.println("Currently no bases in inventory.");
        }
        else {
            for (Map.Entry<String, Integer> entry : bases.entrySet()) {
                String name = entry.getKey();
                int qty = entry.getValue();
                System.out.println(name + " = " + qty);
            }
        }
    }

    /**
     * Populates the inventory with the default starting ingredients for a new game.
     */
    public void setDefaults() {
        addItem("Strawberry", 3);
        addItem("Orange", 2);
        addItem("Lemon", 2);
        addItem("Banana", 3);
        addItem("Mango", 1);
        addItem("Pineapple", 0);
        addItem("Kiwi", 1);
        addItem("Blueberry", 3);
        addItem("Coconut", 0);
        addItem("Syrup Base", 3);
        addItem("Bubble Base", 3);
        addItem("Perfume Base", 1);
        addItem("Milk Base", 2);
        addItem("Lotion Base", 2);
    }
}
