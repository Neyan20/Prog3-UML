import java.util.Random;

public class Market {
    private static final int SLOT_COUNT = 8;
    private static final int CAULDRON_PRICE = 3000;

    private MarketSlot[] slots;
    private boolean needsRefresh;
    private Random random;
    private IngredientCatalog catalog;

    public Market(IngredientCatalog catalog) {
        this.slots = new MarketSlot[SLOT_COUNT];
        this.needsRefresh = false;
        this.random = new Random();
        this.catalog = catalog;
        generateStock();
    }

    // randomly fills the 8 slots with ingredients
    public void generateStock() {
        boolean includeCauldron = random.nextBoolean();
        int cauldronSlot = -1;
        if (includeCauldron == true) {
            cauldronSlot = random.nextInt(SLOT_COUNT);
        }

        // use Ingredient[] from IngredientCatalog
        Ingredient[] allIngredients = catalog.getAll();

        for (int i = 0; i < SLOT_COUNT; i++) {
            if (i == cauldronSlot) {
                Ingredient cauldronItem = new Ingredient("Cauldron", CAULDRON_PRICE, 0, "CAULDRON");
                slots[i] = new MarketSlot();
                slots[i].restock(cauldronItem, 1);
            } else {
                int randIndex = random.nextInt(allIngredients.length);
                Ingredient ing = allIngredients[randIndex];
                int qty = 1 + random.nextInt(5);
                slots[i] = new MarketSlot();
                slots[i].restock(ing, qty);
            }
        }
        needsRefresh = false;
    }

    public void refresh() {
        System.out.println("  [Market] The market has been refreshed with new stock!");
        generateStock();
    }

    // called when player visits the market
    public void onVisit(Player player) {
        if (needsRefresh == true) {
            refresh();
            needsRefresh = false;
        } else if (player.getConcoctionsBrewed() >= 3) {
            refresh();
        }
        player.resetConcoctionsBrewed();
    }

    public void markForRefresh() {
        needsRefresh = true;
    }

    public void buySlots(int[] slotIndices, Player player) {
        for (int i = 0; i < slotIndices.length; i++) {
            int slotNum = slotIndices[i];
            int idx = slotNum - 1;

            if (idx < 0 || idx >= SLOT_COUNT) {
                System.out.println("  Invalid slot: " + slotNum);
                continue;
            }

            MarketSlot slot = slots[idx];
            if (slot.isEmpty() == true) {
                System.out.println("  Slot " + slotNum + " is already empty.");
                continue;
            }

            Ingredient ing = slot.getIngredient();
            int qty = slot.getQuantity();

            if (ing.getName().equals("Cauldron")) {
                boolean success = player.spendCrystals(CAULDRON_PRICE);
                if (success == false) continue;
                player.addCauldron(new Cauldron());
                slot.clear();
                System.out.println("  Cauldron purchased and added to your collection.");
            } else {
                int totalCost = ing.getBuyPrice() * qty;
                boolean paid = player.spendCrystals(totalCost);
                if (paid == false) continue;
                player.getInventory().addItem(ing.getName(), qty);
                slot.clear();
                System.out.printf("  Purchased %dx %s for %d crystals.%n", qty, ing.getName(), totalCost);
            }
        }
    }

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

    public void display() {
        System.out.println("================== MARKET ==================");
        for (int i = 0; i < SLOT_COUNT; i++) {
            System.out.println(slots[i].toDisplayString(i + 1));
        }
        System.out.println("============================================");
    }

    public MarketSlot[] getSlots() {
        return slots;
    }
}
