/**
 * Represents a single purchasable slot in the market, holding one
 * ingredient (or cauldron) and its available quantity.
 */
public class MarketSlot {
    private Ingredient ingredient;
    private int quantity;
    private boolean empty;

    /**
     * Constructs a new, empty market slot.
     */
    public MarketSlot() {
        this.ingredient = null;
        this.quantity = 0;
        this.empty = true;
    }

    /**
     * @return true if this slot currently holds no stock
     */
    public boolean isEmpty() {
        return empty;
    }

    /**
     * @return the ingredient currently stocked in this slot
     */
    public Ingredient getIngredient() {
        return ingredient;
    }

    /**
     * @return the quantity currently available in this slot
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Attempts to purchase a given quantity from this slot's stock.
     *
     * @param quantity the amount to buy
     * @return true if the purchase succeeded, false if the slot is empty or has insufficient stock
     */
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

    /**
     * Empties this slot entirely, removing its ingredient and resetting its quantity.
     */
    public void clear() {
        empty = true;
        ingredient = null;
        quantity = 0;
    }

    /**
     * Restocks this slot with a new ingredient and quantity.
     *
     * @param i   the ingredient to stock
     * @param qty the quantity to stock
     */
    public void restock(Ingredient i, int qty) {
        ingredient = i;
        quantity = qty;
        empty = false;
    }

    /**
     * Builds a formatted string representing this slot for display in the market.
     *
     * @param slotNumber the slot's position number as shown to the player
     * @return the formatted display string, including price if the slot isn't empty
     */
    public String toDisplayString(int slotNumber) {
        if (empty) {
            return "Slot " + slotNumber + ": Empty";
        } else {
            return "Slot " + slotNumber + ": " + ingredient.getName() + " x" + quantity + " - " + ingredient.getBuyPrice() + " crystals each";
        }
    }
}
