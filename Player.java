public class Player {
    private String name;
    private int crystals;
    private Inventory inventory;
    private Cauldron[] cauldrons;
    private boolean loginBonusClaimed;
    private int concoctionsBrewed;
    private IngredientCatalog catalog;

    // constructor for new game
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

        this.inventory = new Inventory();
        this.inventory.setDefaults();
    }

    // constructor for loading from save file
    public Player(String name, int crystals, Inventory inventory, Cauldron[] cauldrons) {
        this.name = name;
        this.crystals = crystals;
        this.inventory = inventory;
        this.cauldrons = cauldrons;
        this.loginBonusClaimed = false;
        this.concoctionsBrewed = 0;
        this.catalog = null;
    }

    public String getName() {
        return name;
    }

    public int getCrystals() {
        return crystals;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public Cauldron[] getCauldrons() {
        return cauldrons;
    }

    public boolean isLoginBonusClaimed() {
        return loginBonusClaimed;
    }

    public int getConcoctionsBrewed() {
        return concoctionsBrewed;
    }

    public void addCrystals(int amount) {
        if (amount <= 0) {
            return;
        }
        crystals = crystals + amount;
        System.out.printf("  +%d crystals! Balance: %d crystals%n", amount, crystals);
    }

    public boolean spendCrystals(int amount) {
        if (crystals < amount) {
            System.out.println("  Not enough crystals.");
            return false;
        }
        crystals = crystals - amount;
        System.out.printf("  -%d crystals. Balance: %d crystals%n", amount, crystals);
        return true;
    }

    // returns the first usable cauldron it finds
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

    public int getUsableCauldronCount() {
        int count = 0;
        for (int i = 0; i < cauldrons.length; i++) {
            if (cauldrons[i].isUsable() == true) {
                count++;
            }
        }
        return count;
    }

    public int getDamagedCauldronCount() {
        int count = 0;
        for (int i = 0; i < cauldrons.length; i++) {
            if (cauldrons[i].isUsable() == false) {
                count++;
            }
        }
        return count;
    }

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

    // expands the cauldron array to add a new one
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

    public Ingredient claimLoginBonus() {
        loginBonusClaimed = true;
        Ingredient bonus = IngredientCatalog.getRandom();
        inventory.addItem(bonus.getName(), 1);
        return bonus;
    }

    public void incrementConcoctionsBrewed() {
        concoctionsBrewed = concoctionsBrewed + 1;
    }

    public void resetConcoctionsBrewed() {
        concoctionsBrewed = 0;
    }

    public void displayStatus() {
        System.out.printf("Player: %s | Crystals: %d%n", name, crystals);
    }

    public void displayInventory() {
        System.out.println("============== INVENTORY ==============");
        inventory.display();
        System.out.println("=== Cauldrons ===");
        int usable = getUsableCauldronCount();
        int damaged = getDamagedCauldronCount();
        System.out.printf("  Usable: %d  |  Damaged: %d%n", usable, damaged);
        for (int i = 0; i < cauldrons.length; i++) {
            System.out.printf("  Cauldron %d: %s%n", i + 1, cauldrons[i]);
        }
        System.out.println("=======================================");
    }
}
