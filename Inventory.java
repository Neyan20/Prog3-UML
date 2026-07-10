import java.util.HashMap;
import java.util.Map;

public class Inventory {
    Map<String, Integer> fruits = new HashMap<>();
    Map<String, Integer> bases = new HashMap<>();

    public void addItem(String name, int qty) {
        if (fruits.containsKey(name)) {
            fruits.put(name, fruits.get(name) + qty);
        } else if (bases.containsKey(name)) {
            bases.put(name, bases.get(name) + qty);
        } else {
            // If the item is not in either map, you can choose to add it to one of them
            // For example, let's add it to fruits by default
            fruits.put(name, qty);
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
        
    }

    public void setDefaults() {

    }
}
