/**
 * Represents the player character, including their crystal balance,
 * inventory, cauldrons, and brewing progress.
 */
public class Player {
    private String name;
    private int crystals;
    private Inventory inventory;
    private Cauldron[] cauldrons;
    private boolean loginBonusClaimed;
    private int concoctionsBrewed;
    private IngredientCatalog catalog;

    /**
     * Constructs a new player with default starting crystals, cauldrons,
     * and inventory.
     *
     * @param name    the player's chosen name
     * @param catalog the ingredient catalog used to set up starting inventory
     */
    public Player(String name, IngredientCatalog catalog) {
        this.name = name;
        this.crystals = 5000;
        this.loginBonusClaimed = false;
        this.concoctionsBrewed = 0;
        this.catalog = catalog;

        // start with 3 usable cauldrons
        this.cauldrons = new Cauldron[3];
        for (int i = 0; i < 3; i++) {
            cauldrons[i] = new Cauldron();
        }

        this.inventory = new Inventory(catalog);
        this.inventory.setDefaults();
    }

    /**
     * Constructs a player from existing save data.
     *
     * @param name      the player's name
     * @param crystals  the player's current crystal balance
     * @param inventory the player's loaded inventory
     * @param cauldrons the player's loaded cauldrons
     * @param catalog   the ingredient catalog, needed for features like the login bonus
     */
    public Player(String name, int crystals, Inventory inventory, Cauldron[] cauldrons, IngredientCatalog catalog) {
        this.name = name;
        this.crystals = crystals;
        this.inventory = inventory;
        this.cauldrons = cauldrons;
        this.loginBonusClaimed = false;
        this.concoctionsBrewed = 0;
        this.catalog = catalog;
    }

    /**
     * @return the player's name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the player's current crystal balance
     */
    public int getCrystals() {
        return crystals;
    }

    /**
     * @return the player's inventory
     */
    public Inventory getInventory() {
        return inventory;
    }

    /**
     * @return the player's array of cauldrons
     */
    public Cauldron[] getCauldrons() {
        return cauldrons;
    }

    /**
     * @return true if the player has already claimed the login bonus this session
     */
    public boolean isLoginBonusClaimed() {
        return loginBonusClaimed;
    }

    /**
     * @return the number of concoctions the player has brewed since the last market visit
     */
    public int getConcoctionsBrewed() {
        return concoctionsBrewed;
    }

    /**
     * Adds crystals to the player's balance. Amounts of 0 or less are ignored.
     *
     * @param amount the number of crystals to add
     */
    public void addCrystals(int amount) {
        if (amount <= 0) {
            return;
        }
        crystals = crystals + amount;
        System.out.printf("  +%d crystals! Balance: %d crystals%n", amount, crystals);
    }

    /**
     * Attempts to deduct crystals from the player's balance.
     *
     * @param amount the number of crystals to spend
     * @return true if the player had enough crystals and the spend succeeded
     */
    public boolean spendCrystals(int amount) {
        if (crystals < amount) {
            System.out.println("  Not enough crystals.");
            return false;
        }
        crystals = crystals - amount;
        System.out.printf("  -%d crystals. Balance: %d crystals%n", amount, crystals);
        return true;
    }

    /**
     * @return the first usable (non-damaged) cauldron found, or null if none are usable
     */
    public Cauldron getUsableCauldron() {
        Cauldron result = null;
        for (int i = 0; i < cauldrons.length; i++) {
            if (cauldrons[i].isUsable() == true) {
                result = cauldrons[i];
                break;
            }
        }
        return result;
    }

    /**
     * @return the number of currently usable cauldrons
     */
    public int getUsableCauldronCount() {
        int count = 0;
        for (int i = 0; i < cauldrons.length; i++) {
            if (cauldrons[i].isUsable() == true) {
                count++;
            }
        }
        return count;
    }

    /**
     * @return the number of currently damaged cauldrons
     */
    public int getDamagedCauldronCount() {
        int count = 0;
        for (int i = 0; i < cauldrons.length; i++) {
            if (cauldrons[i].isUsable() == false) {
                count++;
            }
        }
        return count;
    }

    /**
     * @return the first damaged cauldron found, or null if none are damaged
     */
    public Cauldron getDamagedCauldron() {
        Cauldron result = null;
        for (int i = 0; i < cauldrons.length; i++) {
            if (cauldrons[i].isUsable() == false) {
                result = cauldrons[i];
                break;
            }
        }
        return result;
    }

    /**
     * Adds a new cauldron to the player's collection, expanding the cauldron array.
     *
     * @param newCauldron the cauldron to add
     */
    public void addCauldron(Cauldron newCauldron) {
        int newSize = cauldrons.length + 1;
        Cauldron[] expanded = new Cauldron[newSize];
        for (int i = 0; i < cauldrons.length; i++) {
            expanded[i] = cauldrons[i];
        }
        expanded[cauldrons.length] = newCauldron;
        cauldrons = expanded;
        System.out.println("  New cauldron added! Total: " + cauldrons.length);
    }

    /**
     * Claims the one-time login bonus, granting a random ingredient and
     * adding it to the player's inventory.
     *
     * @return the ingredient granted as the bonus
     */
    public Ingredient claimLoginBonus() {
        loginBonusClaimed = true;
        Ingredient bonus = catalog.getRandom();
        inventory.addItem(bonus.getName(), 1);
        return bonus;
    }

    /**
     * Increments the player's count of concoctions brewed since the last market visit.
     */
    public void incrementConcoctionsBrewed() {
        concoctionsBrewed = concoctionsBrewed + 1;
    }

    /**
     * Resets the player's brewed-concoctions counter back to zero.
     */
    public void resetConcoctionsBrewed() {
        concoctionsBrewed = 0;
    }

    /**
     * Prints the player's current name and crystal balance to the console.
     */
    public void displayStatus() {
        System.out.printf("Player: %s | Crystals: %d%n", name, crystals);
    }

    /**
     * Prints the player's full inventory and cauldron status to the console.
     */
    public void displayInventory() {
        System.out.println("==================== INVENTORY ====================");
        inventory.display();
        System.out.println("=== Cauldrons ===");
        int usable = getUsableCauldronCount();
        int damaged = getDamagedCauldronCount();
        System.out.printf("  Usable: %d  |  Damaged: %d%n", usable, damaged);
        for (int i = 0; i < cauldrons.length; i++) {
            System.out.printf("  Cauldron %d: %s%n", i + 1, cauldrons[i]);
        }
        System.out.println("====================================================");
    }
}
