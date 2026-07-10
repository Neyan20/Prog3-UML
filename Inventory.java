import java.util.HashMap;
import java.util.Map;

public class Inventory {
    Map<String, Integer> fruits = new HashMap<>();
    Map<String, Integer> bases = new HashMap<>();
    private IngredientCatalog catalog;

    public Inventory(IngredientCatalog catalog) {
        this.catalog = catalog;
    }

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

    public boolean hasItem(String name, int qty) {
        if (fruits.containsKey(name) && fruits.get(name) >= qty) {
            return true;
        } else if (bases.containsKey(name) && bases.get(name) >= qty) {
            return true;
        } else {
            return false;
        }
    }

    public int getQuantity(String name) {
        if (fruits.containsKey(name)) {
            return fruits.get(name);
        } else if (bases.containsKey(name)) {
            return bases.get(name);
        } else {
            return 0;
        }
    }

    public Map<String, Integer> getFruits() {
        return fruits;
    }

    public Map<String, Integer> getBases() {
        return bases;
    }

    public void display() {
        System.out.println("=== [ INVENTORY ] ===");
        System.out.println("Fruits:");

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
        System.out.println("\nBases:");
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
