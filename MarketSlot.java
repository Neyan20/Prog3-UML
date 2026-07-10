public class MarketSlot {
    private Ingredient ingredient;
    private int quantity;
    private boolean empty;

    //initialize an empty market slot
    public MarketSlot() {
        this.ingredient = null;
        this.quantity = 0;
        this.empty = true;
    }

    public boolean isEmpty() {
        return empty;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public int getQuantity() {
        return quantity;
    }

    public boolean buy(int quantity) {
        if (empty || quantity > this.quantity) {
            return false;
        }
        this.quantity -= quantity;
        if (this.quantity == 0) {
            empty = true;
            ingredient = null;
        }
        return true;
    }

    public void clear() {
        empty = true;
        ingredient = null;
        quantity = 0;
    }

    public void restock(Ingredient i, int qty) {
        ingredient = i;
        quantity = qty;
        empty = false;
    }

    public String toDisplayString(int slotNumber) {
        if (empty) {
            return "Slot " + slotNumber + ": Empty";
        } else {
            return "Slot " + slotNumber + ": " + ingredient.getName() + " x" + quantity + " - " + ingredient.getBuyPrice() + " crystals each";
        }
    }
}
