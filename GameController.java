import java.util.ArrayList;
import javax.swing.SwingUtilities;

/**
 *
 * 
 * Note: 
 * 
 */
public class GameController {

    
    private static final int BLESS_COST = 1000;
    private static final String RECIPE_DATA_PATH = "data/POTION_COMPENDIUM.csv";

   //models
    private Player player;
    private Market market;
    private Spellbook spellbook;
    private BrewingManager brewingManager;
    private SaveManager saveManager;
    private IngredientCatalog catalog;

   //views
    private MainMenuView mainMenuView;

    //controller constructor
    public GameController() {
        catalog = new IngredientCatalog();
        saveManager = new SaveManager(catalog);
        brewingManager = new BrewingManager(catalog);
        market = new Market(catalog);
        brewingManager.loadRecipeData(RECIPE_DATA_PATH);  
    }

   
    public void start() {
        OpeningView openingView = new OpeningView(this);
        openingView.display();
    }

    //run this file instead of Game.java to start the game
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GameController().start());
    }

   
    public boolean saveExists(String name) {
        return saveManager.fileExists(name);
    }

    public void startNewGame(String name) {
        if (saveManager.fileExists(name)) {
            saveManager.deleteFile(name); // wipe old save so it wont get mixed with the new one
        }
        player = new Player(name, catalog);
        spellbook = new Spellbook();

        //defaults
        int[] startingIds = {1, 2, 16, 17, 36, 37, 55, 56};
        for (int i = 0; i < startingIds.length; i++) {
            ArrayList<Recipe> allRecipes = brewingManager.getRecipeData();
            boolean found = false;
            for (int j = 0; j < allRecipes.size() && found == false; j++) {
                if (allRecipes.get(j).getConcoctionId() == startingIds[i]) {
                    spellbook.addRecipe(allRecipes.get(j));
                    found = true; // stop checking further recipes for this id
                }
            }
        }
        showMainMenu(); 
    }

   
    public boolean loadGame(String name) {
        player = saveManager.load(name);
        if (player == null) {
            return false; // no save found, OpeningView show error message
        }
        spellbook = saveManager.loadSpellbook(name, brewingManager.getRecipeData());
        market.markForRefresh(); 
        showMainMenu();
        return true;
    }

   //MainMenuView
    public void showMainMenu() {
        if (mainMenuView == null) {
            mainMenuView = new MainMenuView(this);
        }
        mainMenuView.refresh();
        mainMenuView.display();
    }

    //RecipeModeView is called from BrewView
    public String brewRecipe(int concoctionId) {
        if (player.getUsableCauldronCount() == 0) {
            return "No usable cauldrons! Bless one first.";
        }
        Recipe recipe = spellbook.getRecipeById(concoctionId);
        if (recipe == null) {
            return "Recipe not found in spellbook.";
        }
        if (brewingManager.hasIngredients(player, recipe) == false) {
            return "Not enough ingredients to brew " + recipe.getResultName() + ".";
        }
        boolean success = brewingManager.brewRecipeMode(player, recipe);
        if (success) {
            return "Successfully brewed " + recipe.getResultName() + "! Sold for " + recipe.getSellValue() + " crystals.";
        }
        return "Brewing failed.";
    }

    //CreativeModeView is also called from BrewView
    public String brewCreative(String base, ArrayList<String> fruits) {
        if (player.getUsableCauldronCount() <= 1) {
            return "Only 1 usable cauldron left! Creative mode is locked.";
        }
        Recipe result = brewingManager.brewCreativeMode(player, base, fruits, spellbook);
        if (result != null) {
            return "You discovered: " + result.getResultName() + "! Sold for " + result.getSellValue() + " crystals.";
        }
        return "Alchemy failed! Your cauldron is now damaged. Visit Bless Cauldron to repair it.";
    }

    //MarketView is called from MainMenuView
    public void visitMarket() {
        market.onVisit(player);
    }

    //Market actions
    public String buyFromMarket(int[] slotIndices) {
        StringBuilder sb = new StringBuilder();
        MarketSlot[] slots = market.getSlots();
        for (int i = 0; i < slotIndices.length; i++) {
            int idx = slotIndices[i] - 1; // convert from 1-based (shown to player) to 0-based array index
            if (idx < 0 || idx >= slots.length) { sb.append("Invalid slot: ").append(slotIndices[i]).append("\n"); continue; }
            MarketSlot slot = slots[idx];
            if (slot.isEmpty()) { sb.append("Slot ").append(slotIndices[i]).append(" is empty.\n"); continue; }
            Ingredient ing = slot.getIngredient();
            int qty = slot.getQuantity(); // always buy the whole slot, no partial quantity allowed
            if (ing.getName().equals("Cauldron")) {
                // cauldrons are bought individually with a flat price, not per-unit like ingredients
                if (player.spendCrystals(3000)) {
                    player.addCauldron(new Cauldron());
                    slot.buy(qty);
                    sb.append("Cauldron purchased!\n");
                } else {
                    sb.append("Not enough crystals for cauldron.\n");
                }
            } else {
                int cost = ing.getBuyPrice() * qty;
                if (player.spendCrystals(cost)) {
                    player.getInventory().addItem(ing.getName(), qty);
                    slot.buy(qty);
                    sb.append("Purchased ").append(qty).append("x ").append(ing.getName()).append(" for ").append(cost).append(" crystals.\n");
                } else {
                    sb.append("Not enough crystals for ").append(ing.getName()).append(".\n");
                }
            }
        }
        return sb.toString().trim();
    }


    public String sellIngredient(String ingredientName, int qty) {
        Ingredient ing = catalog.getByName(ingredientName);
        if (ing == null) return "Unknown ingredient: " + ingredientName;
        if (player.getInventory().hasItem(ingredientName, qty) == false) return "Not enough " + ingredientName + " to sell.";
        int earnings = ing.getSellPrice() * qty;
        player.getInventory().removeItem(ingredientName, qty);
        player.addCrystals(earnings);
        return "Sold " + qty + "x " + ingredientName + " for " + earnings + " crystals.";
    }

    //BlessCauldronView is called from MainMenuView
    public String blessCauldron() {
        if (player.getDamagedCauldronCount() == 0) {
            return "All cauldrons are in good condition!";
        }
        if (player.spendCrystals(BLESS_COST) == false) {
            return "Not enough crystals. Blessing costs " + BLESS_COST + " crystals.";
        }
        player.getDamagedCauldron().bless();
        return "Cauldron blessed and usable again!";
    }

    //Login bonus is called directly from MainMenuView
    public String claimLoginBonus() {
        if (player.isLoginBonusClaimed()) {
            return "Already claimed! Exit and reenter to claim again.";
        }
        Ingredient bonus = player.claimLoginBonus();
        return "Login bonus: 1x " + bonus.getName() + " added to your inventory!";
    }

    //Load and save are called from MainMenuView
    public void saveAndExit() {
        market.markForRefresh(); // so the market restocks on next login regardless of concoction count
        saveManager.save(player, spellbook, brewingManager.getRecipeData());
        System.exit(0);
    }

   
    public Player getPlayer() { return player; }

    public Market getMarket() { return market; }

    public Spellbook getSpellbook() { return spellbook; }

    public IngredientCatalog getCatalog() { return catalog; }

    public int getBlessCost() { return BLESS_COST; }
}
