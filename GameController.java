import java.util.ArrayList;
import javax.swing.SwingUtilities;

/**
 * The main controller for Potion Prodigy MCO2.
 * Owns all model objects and coordinates between them and the views.
 * All game logic is delegated here from the views.
 *
 * QUICK REFERENCE FOR REWRITING THE SCREENS - which method each screen needs:
 *   OpeningView        -> saveExists(), startNewGame(), loadGame()
 *   MainMenuView       -> getPlayer(), claimLoginBonus(), saveAndExit()
 *   BrewView           -> getPlayer().getUsableCauldronCount() (to lock Creative Mode)
 *   RecipeModeView     -> getSpellbook(), brewRecipe()
 *   CreativeModeView   -> getCatalog(), getPlayer(), brewCreative()
 *   InventoryView      -> getPlayer(), getCatalog()
 *   SpellbookView      -> getSpellbook()
 *   MarketView         -> visitMarket(), getMarket(), buyFromMarket(), sellIngredient(), getCatalog()
 *   BlessCauldronView  -> getPlayer(), getBlessCost(), blessCauldron()
 *
 * @author [Your Names Here]
 * @version 2.0
 */
public class GameController {

    // -------------------------------------------------------------------------
    // Constants
    // -------------------------------------------------------------------------
    private static final int BLESS_COST = 1000;
    private static final String RECIPE_DATA_PATH = "data/POTION_COMPENDIUM.csv";

    // -------------------------------------------------------------------------
    // Model objects - the "M" in MVC. Views never touch these directly.
    // -------------------------------------------------------------------------
    private Player player;
    private Market market;
    private Spellbook spellbook;
    private BrewingManager brewingManager;
    private SaveManager saveManager;
    private IngredientCatalog catalog;

    // -------------------------------------------------------------------------
    // Views - only MainMenuView is kept as a field, since it's reused every
    // time the player returns to it. Every other screen is just a local
    // variable created fresh wherever it's opened from.
    // -------------------------------------------------------------------------
    private MainMenuView mainMenuView;

    /**
     * Constructs the controller and initializes all model objects.
     * Called once when the game starts, before any screen exists.
     */
    public GameController() {
        catalog = new IngredientCatalog();
        saveManager = new SaveManager(catalog);
        brewingManager = new BrewingManager(catalog);
        market = new Market(catalog);
        brewingManager.loadRecipeData(RECIPE_DATA_PATH); // reads the recipe list from file, must happen before any brewing
    }

    /**
     * Launches the game by showing the opening screen.
     * This is the only place OpeningView gets created.
     */
    public void start() {
        OpeningView openingView = new OpeningView(this);
        openingView.display();
    }

    /**
     * Program entry point. Creates the controller and starts the GUI.
     * SwingUtilities.invokeLater keeps all GUI work on Swing's own thread.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GameController().start());
    }

    // -------------------------------------------------------------------------
    // New Game / Load Game  -->  called from OpeningView
    // -------------------------------------------------------------------------

    /**
     * Checks if a save file exists for the given name.
     * OpeningView calls this before New Game to ask "overwrite?" if true.
     *
     * @param name the player name to check
     * @return true if a save file exists
     */
    public boolean saveExists(String name) {
        return saveManager.fileExists(name);
    }

    /**
     * Starts a new game with the given player name.
     * Call this from OpeningView's New Game button (after the overwrite
     * confirm, if saveExists() was true).
     *
     * @param name the player name
     */
    public void startNewGame(String name) {
        if (saveManager.fileExists(name)) {
            saveManager.deleteFile(name); // wipe old save so it doesn't get mixed with the new one
        }
        player = new Player(name, catalog);
        spellbook = new Spellbook();

        // load the 8 default starting recipes per Sam-Paul.txt
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
        showMainMenu(); // hands off to the main menu once setup is done
    }

    /**
     * Loads an existing game by player name.
     * Call this from OpeningView's Load Game button.
     *
     * @param name the player name to load
     * @return true if load was successful, false if save not found (show an error in that case)
     */
    public boolean loadGame(String name) {
        player = saveManager.load(name);
        if (player == null) {
            return false; // no save found - let OpeningView show an error, don't proceed
        }
        spellbook = saveManager.loadSpellbook(name, brewingManager.getRecipeData());
        market.markForRefresh(); // force fresh stock on a loaded game
        showMainMenu();
        return true;
    }

    // -------------------------------------------------------------------------
    // Main menu
    // -------------------------------------------------------------------------

    /**
     * Shows the main menu view. Reuses the same MainMenuView instance every
     * time instead of creating a new window - that's why it's a field, not
     * a local variable like the other screens.
     */
    public void showMainMenu() {
        if (mainMenuView == null) {
            mainMenuView = new MainMenuView(this);
        }
        mainMenuView.refresh(); // pull the latest name/crystals before showing
        mainMenuView.display();
    }

    // -------------------------------------------------------------------------
    // Brew  -->  called from RecipeModeView / CreativeModeView
    // -------------------------------------------------------------------------

    /**
     * Attempts to brew a recipe from the spellbook by its concoction ID.
     * Call this from RecipeModeView after the player picks a recipe and confirms.
     * Just display whatever String comes back - all the pass/fail logic already happened.
     *
     * @param concoctionId the ID of the recipe to brew
     * @return result message to show the player
     */
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

    /**
     * Attempts to brew in creative mode with a chosen base and fruits.
     * Call this from CreativeModeView after the player picks 1 base + 1-3
     * fruits and confirms. A failed combo still damages a cauldron, so
     * always refresh the parent screen after calling this.
     *
     * @param base   the chosen base ingredient name
     * @param fruits the chosen fruit ingredient names (1 to 3 of them)
     * @return result message to show the player
     */
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

    // -------------------------------------------------------------------------
    // Market  -->  called from MarketView
    // -------------------------------------------------------------------------

    /**
     * Called when the player opens the market. Handles refresh logic
     * (restocks if 3+ concoctions were brewed since the last visit).
     * Call this BEFORE creating/showing MarketView, not from inside it.
     */
    public void visitMarket() {
        market.onVisit(player);
    }

    /**
     * Buys out the given market slots completely. Buying is all-or-nothing
     * per slot - the player can't choose a partial quantity, only which
     * whole slots to buy.
     * Call this from MarketView's Buy tab. To buy multiple slots at once,
     * pass them all in the same array.
     *
     * @param slotIndices slot numbers (1-based, matches what's shown on screen)
     * @return result message (may be multiple lines if multiple slots were bought)
     */
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

    /**
     * Sells a quantity of an ingredient from the player's inventory.
     * Call this once per ingredient type from MarketView's Sell tab - to
     * sell multiple types at once, just call this in a loop, one call per type.
     *
     * @param ingredientName the ingredient to sell
     * @param qty            quantity to sell
     * @return result message
     */
    public String sellIngredient(String ingredientName, int qty) {
        Ingredient ing = catalog.getByName(ingredientName);
        if (ing == null) return "Unknown ingredient: " + ingredientName;
        if (player.getInventory().hasItem(ingredientName, qty) == false) return "Not enough " + ingredientName + " to sell.";
        int earnings = ing.getSellPrice() * qty;
        player.getInventory().removeItem(ingredientName, qty);
        player.addCrystals(earnings);
        return "Sold " + qty + "x " + ingredientName + " for " + earnings + " crystals.";
    }

    // -------------------------------------------------------------------------
    // Bless Cauldron  -->  called from BlessCauldronView
    // -------------------------------------------------------------------------

    /**
     * Blesses (repairs) the first damaged cauldron found.
     * Call this from BlessCauldronView after the player confirms.
     * Safe to call even with 0 damaged cauldrons - it just returns a message instead of doing anything.
     *
     * @return result message
     */
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

    // -------------------------------------------------------------------------
    // Login Bonus  -->  called from MainMenuView directly (no separate screen)
    // -------------------------------------------------------------------------

    /**
     * Claims the login bonus. Call this straight from the Login Bonus button
     * on MainMenuView and show the result in a simple message popup - this
     * one doesn't need its own dialog class.
     *
     * @return result message
     */
    public String claimLoginBonus() {
        if (player.isLoginBonusClaimed()) {
            return "Already claimed! Exit and reenter to claim again.";
        }
        Ingredient bonus = player.claimLoginBonus();
        return "Login bonus: 1x " + bonus.getName() + " added to your inventory!";
    }

    // -------------------------------------------------------------------------
    // Exit / Save  -->  called from MainMenuView's Exit button
    // -------------------------------------------------------------------------

    /**
     * Saves the game and exits. Call this after the player confirms exiting.
     */
    public void saveAndExit() {
        market.markForRefresh(); // so the market restocks on next login regardless of concoction count
        saveManager.save(player, spellbook, brewingManager.getRecipeData());
        System.exit(0);
    }

    // -------------------------------------------------------------------------
    // Getters for views - read-only, safe to call from any screen at any time
    // -------------------------------------------------------------------------

    /** @return the current player - used by almost every screen for name/crystals/inventory */
    public Player getPlayer() { return player; }

    /** @return the market - used by MarketView to list slots */
    public Market getMarket() { return market; }

    /** @return the spellbook - used by RecipeModeView and SpellbookView to list recipes */
    public Spellbook getSpellbook() { return spellbook; }

    /** @return the ingredient catalog - used by InventoryView, CreativeModeView, MarketView */
    public IngredientCatalog getCatalog() { return catalog; }

    /** @return the bless cost constant - used by BlessCauldronView to show the price */
    public int getBlessCost() { return BLESS_COST; }
}
