import java.util.ArrayList;
import java.util.Random;

/**
 * Manages the in-game market where players buy ingredients or cauldrons
 * and sell ingredients from their inventory.
 */
public class Market {
    private static final int SLOT_COUNT = 8;
    private static final int CAULDRON_PRICE = 3000;

    private MarketSlot[] slots;
    private boolean needsRefresh;
    private Random random;
    private IngredientCatalog catalog;

    /**
     * Constructs the market and generates its initial stock.
     *
     * @param catalog the ingredient catalog used to stock the market's slots
     */
    public Market(IngredientCatalog catalog) {
        this.slots = new MarketSlot[SLOT_COUNT];
        this.needsRefresh = false;
        this.random = new Random();
        this.catalog = catalog;
        generateStock();
    }

    /**
     * Randomly fills all 8 slots with ingredients, with at most one slot
     * containing a Cauldron for purchase.
     */
    public void generateStock() {
        boolean includeCauldron = random.nextBoolean();
        int cauldronSlot = -1;
        if (includeCauldron == true) {
            cauldronSlot = random.nextInt(SLOT_COUNT);
        }

        // use Ingredient[] from IngredientCatalog
    ArrayList<Ingredient> allIngredients = catalog.getAll();

        for (int i = 0; i < SLOT_COUNT; i++) {
            if (i == cauldronSlot) {
                Ingredient cauldronItem = new Ingredient("Cauldron", CAULDRON_PRICE, 0, "CAULDRON");
                slots[i] = new MarketSlot();
                slots[i].restock(cauldronItem, 1);
            } else {
                int randIndex = random.nextInt(allIngredients.size());
                Ingredient ing = allIngredients.get(randIndex);
                int qty = 1 + random.nextInt(5);
                slots[i] = new MarketSlot();
                slots[i].restock(ing, qty);
            }
        }
        needsRefresh = false;
    }

    /**
     * Refreshes the market's stock and notifies the player via console output.
     */
    public void refresh() {
        System.out.println("  [Market] The market has been refreshed with new stock!");
        generateStock();
    }

    /**
     * Called when a player visits the market; refreshes stock if flagged or
     * if the player has brewed 3 or more concoctions since the last visit,
     * then resets that concoction counter.
     *
     * @param player the visiting player
     */
    public void onVisit(Player player) {
        if (needsRefresh == true) {
            refresh();
            needsRefresh = false;
        } else if (player.getConcoctionsBrewed() >= 3) {
            refresh();
        }
        player.resetConcoctionsBrewed();
    }

    /**
     * Flags the market so its stock will be regenerated on the next visit.
     */
    public void markForRefresh() {
        needsRefresh = true;
    }

    /**
     * Purchases chosen quantities from the given slot numbers on behalf of the player.
     *
     * @param slotIndices the 1-based slot numbers the player selected
     * @param quantities  the quantity to buy from each corresponding slot
     * @param player      the purchasing player
     */
    public void buySlots(int[] slotIndices, int[] quantities, Player player) {
    for (int i = 0; i < slotIndices.length; i++) {
        int slotNum = slotIndices[i];
        int idx = slotNum - 1;
        int requestedQty = quantities[i];

        if (idx < 0 || idx >= SLOT_COUNT) {
            System.out.println("  Invalid slot: " + slotNum);
            continue;
        }

        MarketSlot slot = slots[idx];
        if (slot.isEmpty() == true) {
            System.out.println("  Slot " + slotNum + " is already empty.");
            continue;
        }

        if (requestedQty <= 0 || requestedQty > slot.getQuantity()) {
            System.out.println("  Invalid quantity for slot " + slotNum + " (available: " + slot.getQuantity() + ").");
            continue;
        }

        Ingredient ing = slot.getIngredient();

        if (ing.getName().equals("Cauldron")) {
            boolean success = player.spendCrystals(CAULDRON_PRICE);
            if (success == false) continue;
            player.addCauldron(new Cauldron());
            slot.buy(requestedQty);
            System.out.println("  Cauldron purchased and added to your collection.");
        } else {
            int totalCost = ing.getBuyPrice() * requestedQty;
            boolean paid = player.spendCrystals(totalCost);
            if (paid == false) continue;
            player.getInventory().addItem(ing.getName(), requestedQty);
            slot.buy(requestedQty);
            System.out.printf("  Purchased %dx %s for %d crystals.%n", requestedQty, ing.getName(), totalCost);
        }
    }
}

    /**
     * Sells a given quantity of an ingredient from the player's inventory
     * for crystals.
     *
     * @param name   the ingredient name to sell
     * @param qty    the quantity to sell
     * @param player the selling player
     */
    public void sellIngredient(String name, int qty, Player player) {
        Ingredient ing = catalog.getByName(name);
        if (ing == null) {
            System.out.println("  Unknown ingredient: " + name);
            return;
        }

        boolean hasEnough = player.getInventory().hasItem(name, qty);
        if (hasEnough == false) {
            System.out.println("  Not enough " + name + " to sell.");
            return;
        }

        int earnings = ing.getSellPrice() * qty;
        player.getInventory().removeItem(name, qty);
        player.addCrystals(earnings);
        System.out.printf("  Sold %dx %s for %d crystals.%n", qty, name, earnings);
    }

    /**
     * Prints the current market stock (all 8 slots) to the console.
     */
    public void display() {
        System.out.println("================== MARKET ==================");
        for (int i = 0; i < SLOT_COUNT; i++) {
            System.out.println(slots[i].toDisplayString(i + 1));
        }
        System.out.println("============================================");
    }

    /**
     * @return the array of current market slots
     */
    public MarketSlot[] getSlots() {
        return slots;
    }
}
