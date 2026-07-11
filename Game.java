import java.util.ArrayList;
import java.util.Scanner;


/**
 * Main entry point and game loop for Potion Prodigy. Handles the opening
 * menu, main menu, and all gameplay flows (brewing, market, cauldrons,
 * login bonus, saving/loading).
 */
public class Game {
    private static final int BLESS_COST = 1000;
    private static final String RECIPE_DATA_PATH = "data/POTION_COMPENDIUM.csv";

    private Player player;
    private Market market;
    private Spellbook spellbook;
    private BrewingManager brewingManager;
    private SaveManager saveManager;
    private IngredientCatalog catalog;
    private Scanner scanner;

    /**
     * Program entry point; starts a new Game instance.
     *
     * @param args unused command-line arguments
     */
    public static void main(String[] args) {
        new Game().startGame();
    }

    /**
     * Initializes all core game systems (catalog, save manager, brewing
     * manager, market), loads recipe data, and shows the opening menu.
     */
    public void startGame() {
        scanner = new Scanner(System.in);
        catalog = new IngredientCatalog();
        saveManager = new SaveManager(catalog);
        brewingManager = new BrewingManager(catalog);
        market = new Market(catalog);
        brewingManager.loadRecipeData(RECIPE_DATA_PATH);
        showOpeningMenu();
    }

    /**
     * Displays the opening menu, letting the player start a new game or
     * load an existing save, until a valid player is ready. Then proceeds
     * to the main menu.
     */
    public void showOpeningMenu() {
        System.out.println("============================================");
        System.out.println("       WELCOME TO POTION PRODIGY           ");
        System.out.println("============================================");
        boolean ready = false;
        while (ready == false) {
            System.out.println("\n[1] New Game\n[2] Load Game");
            System.out.print("Choice: ");
            String choice = scanner.nextLine().trim();
            if (choice.equals("1")) {
                ready = handleNewGame();
            } else if (choice.equals("2")) {
                ready = handleLoadGame();
                if (ready == false) {
                    System.out.print("Start a new game instead? (Y/N): ");
                    if (scanner.nextLine().trim().equalsIgnoreCase("Y")) ready = handleNewGame();
                }
            } else {
                System.out.println("  Please enter 1 or 2.");
            }
        }
        showMainMenu();
    }

    /**
     * Prompts for a player name and creates a new Player with default
     * starting recipes, prompting to overwrite if a save already exists
     * under that name.
     *
     * @return true if a new game was successfully started
     */
    private boolean handleNewGame() {
        System.out.print("\nEnter your player name: ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) { System.out.println("  Name cannot be empty."); return false; }
        if (saveManager.fileExists(name) == true) {
            System.out.print("  Save '" + name + "' exists. Overwrite? (Y/N): ");
            if (scanner.nextLine().trim().equalsIgnoreCase("Y") == false) { System.out.println("  Cancelled."); return false; }
            saveManager.deleteFile(name);
        }
        player = new Player(name, catalog);
        spellbook = new Spellbook();

        // load only the 8 default starting recipes per Sam-Paul.txt
        int[] startingIds = {1, 2, 16, 17, 36, 37, 55, 56};
        for (int i = 0; i < startingIds.length; i++) {
            ArrayList<Recipe> allRecipes = brewingManager.getRecipeData();
            for (int j = 0; j < allRecipes.size(); j++) {
                if (allRecipes.get(j).getConcoctionId() == startingIds[i]) {
                    spellbook.addRecipe(allRecipes.get(j));
                    break;
                }
            }
        }

        System.out.println("\n  Welcome, " + name + "! Starting crystals: 5000 | Cauldrons: 3");
        return true;
    }

    /**
     * Prompts for a save file name and loads the corresponding player and
     * spellbook, marking the market for a refresh on next visit.
     *
     * @return true if a save was found and successfully loaded
     */
    private boolean handleLoadGame() {
        System.out.print("\nEnter save file name: ");
        String name = scanner.nextLine().trim();
        player = saveManager.load(name);
        if (player == null) return false;
        spellbook = saveManager.loadSpellbook(name, brewingManager.getRecipeData());
        market.markForRefresh();
        System.out.println("  Welcome back, " + player.getName() + "!");
        return true;
    }

    /**
     * Displays the main menu in a loop, dispatching to the appropriate
     * handler until the player chooses to exit.
     */
    public void showMainMenu() {
        boolean running = true;
        while (running == true) {
            System.out.println("\n============================================");
            System.out.printf("  Player: %s  |  Crystals: %d%n", player.getName(), player.getCrystals());
            System.out.println("============================================");
            System.out.println("[1] Brew Concoction");
            System.out.println("[2] Check Inventory");
            System.out.println("[3] Check Spellbook");
            System.out.println("[4] Visit Market");
            System.out.println("[5] Bless Cauldron");
            System.out.println("[6] Login Bonus");
            System.out.println("[7] Exit Game");
            System.out.print("Choice: ");
            String choice = scanner.nextLine().trim();

            if (choice.equals("1")) handleBrew();
            else if (choice.equals("2")) player.displayInventory();
            else if (choice.equals("3")) spellbook.displayAll();
            else if (choice.equals("4")) handleMarket();
            else if (choice.equals("5")) handleBlessCauldron();
            else if (choice.equals("6")) handleLoginBonus();
            else if (choice.equals("7")) running = handleExit();
            else System.out.println("  Please enter 1 to 7.");
        }
    }

    /**
     * Prompts the player to choose between Recipe Mode and Creative Mode,
     * or cancel.
     */
    private void handleBrew() {
        System.out.println("\n--- BREW CONCOCTION ---");
        System.out.println("[1] Recipe Mode\n[2] Creative Mode\n[3] Cancel");
        System.out.print("Choice: ");
        String choice = scanner.nextLine().trim();
        if (choice.equals("1")) handleRecipeMode();
        else if (choice.equals("2")) handleCreativeMode();
        else System.out.println("  Cancelled.");
    }

    /**
     * Handles brewing a known recipe from the spellbook by Concoction ID,
     * confirming with the player before committing to the brew.
     */
    private void handleRecipeMode() {
        if (player.getUsableCauldronCount() == 0) { System.out.println("  No usable cauldrons!"); return; }
        spellbook.displayAll();
        if (spellbook.getCount() == 0) return;
        System.out.print("Enter Concoction ID (or 0 to cancel): ");
        String idInput = scanner.nextLine().trim();
        if (isNumeric(idInput) == false) { System.out.println("  Invalid input."); return; }
        int id = Integer.parseInt(idInput);
        if (id == 0) { System.out.println("  Cancelled."); return; }
        Recipe recipe = spellbook.getRecipeById(id);
        if (recipe == null) { System.out.println("  Recipe ID " + id + " not found."); return; }
        if (brewingManager.hasIngredients(player, recipe) == false) { System.out.println("  Not enough ingredients."); return; }
        System.out.print("  Brew " + recipe.getResultName() + "? (Y/N): ");
        if (scanner.nextLine().trim().equalsIgnoreCase("Y")) brewingManager.brewRecipeMode(player, recipe);
        else System.out.println("  Cancelled.");
    }

    /**
     * Handles Creative Mode: lets the player pick a base and up to 3 fruits
     * to experiment with, then attempts the brew. Locked when only 1
     * usable cauldron remains, to avoid soft-locking the game.
     */
    private void handleCreativeMode() {
        if (player.getUsableCauldronCount() <= 1) {
            System.out.println("  Only 1 cauldron left — creative mode locked to prevent soft-locking.");
            return;
        }

        ArrayList<Ingredient> bases = catalog.getBases();
        System.out.println("\n  --- Select a Base ---");
        for (int i = 0; i < bases.size(); i++) {
            int qty = player.getInventory().getQuantity(bases.get(i).getName());
            System.out.printf("  [%d] %s (have: %d)%n", i + 1, bases.get(i).getName(), qty);
        }
        System.out.print("  Base number (or 0 to cancel): ");
        String baseInput = scanner.nextLine().trim();
        if (isNumeric(baseInput) == false) { System.out.println("  Invalid."); return; }
        int baseIdx = Integer.parseInt(baseInput) - 1;
        if (baseIdx == -1) { System.out.println("  Cancelled."); return; }
        if (baseIdx < 0 || baseIdx >= bases.size()) { System.out.println("  Invalid selection."); return; }
        String chosenBase = bases.get(baseIdx).getName();
        if (player.getInventory().hasItem(chosenBase, 1) == false) { System.out.println("  No " + chosenBase + " in inventory."); return; }

        ArrayList<Ingredient> fruits = catalog.getFruits();
        ArrayList<String> chosen = new ArrayList<String>();

        while (chosen.size() < 3) {
            System.out.println("\n  --- Select Fruit " + (chosen.size() + 1) + " ---");
            for (int i = 0; i < fruits.size(); i++) {
                int qty = player.getInventory().getQuantity(fruits.get(i).getName());
                System.out.printf("  [%d] %s (have: %d)%n", i + 1, fruits.get(i).getName(), qty);
            }
            String doneLabel = "";
            if (chosen.size() >= 1) doneLabel = " (or 0 to finish)";
            System.out.print("  Fruit number" + doneLabel + " (or -1 to cancel): ");
            String fruitInput = scanner.nextLine().trim();
            if (isNumeric(fruitInput) == false) { System.out.println("  Invalid."); continue; }
            int fruitIdx = Integer.parseInt(fruitInput);
            if (fruitIdx == -1) { System.out.println("  Cancelled."); return; }
            if (fruitIdx == 0 && chosen.size() >= 1) break;
            fruitIdx = fruitIdx - 1;
            if (fruitIdx < 0 || fruitIdx >= fruits.size()) { System.out.println("  Invalid selection."); continue; }
            String fruitName = fruits.get(fruitIdx).getName();
            if (chosen.contains(fruitName) == true) { System.out.println("  Already selected " + fruitName + "."); continue; }
            if (player.getInventory().hasItem(fruitName, 1) == false) { System.out.println("  No " + fruitName + " in inventory."); continue; }
            chosen.add(fruitName);
            System.out.println("  Added: " + fruitName);
        }

        System.out.print("\n  Brew with " + chosenBase + " + " + chosen + "? (Y/N): ");
        if (scanner.nextLine().trim().equalsIgnoreCase("Y")) brewingManager.brewCreativeMode(player, chosenBase, chosen, spellbook);
        else System.out.println("  Cancelled.");
    }

    /**
     * Handles the market submenu loop: buying from slots, selling
     * ingredients, or exiting back to the main menu.
     */
    private void handleMarket() {
        market.onVisit(player);
        boolean inMarket = true;
        while (inMarket == true) {
            System.out.println("\n--- MARKET --- Crystals: " + player.getCrystals());
            System.out.println("[1] Buy\n[2] Sell\n[3] Exit Market");
            System.out.print("Choice: ");
            String choice = scanner.nextLine().trim();

            if (choice.equals("1")) {
            market.display();
            System.out.print("  Slot:qty to buy (e.g. 1:2,3:1) or 0 to cancel: ");
            String buyInput = scanner.nextLine().trim();
            if (buyInput.equals("0") == false) {
            String[] parts = buyInput.split(",");
            boolean validInput = true;
            for (int i = 0; i < parts.length; i++) {
            String[] pair = parts[i].trim().split(":");
            if (pair.length != 2 || isNumeric(pair[0].trim()) == false || isNumeric(pair[1].trim()) == false) validInput = false;
        }
        if (validInput == false) {
            System.out.println("  Invalid input.");
        } else {
            int[] slots = new int[parts.length];
            int[] quantities = new int[parts.length];
            for (int i = 0; i < parts.length; i++) {
                String[] pair = parts[i].trim().split(":");
                slots[i] = Integer.parseInt(pair[0].trim());
                quantities[i] = Integer.parseInt(pair[1].trim());
            }
            market.buySlots(slots, quantities, player);
        }
    }
            } else if (choice.equals("2")) {
                player.getInventory().display();
                System.out.println("  Format: Name,qty — separate multiple with semicolons");
                System.out.print("  What to sell (blank to cancel): ");
                String sellInput = scanner.nextLine().trim();
                if (sellInput.isEmpty() == false) {
                    String[] entries = sellInput.split(";");
                    for (int i = 0; i < entries.length; i++) {
                        String[] p = entries[i].trim().split(",");
                        if (p.length != 2) { System.out.println("  Invalid: " + entries[i]); continue; }
                        if (isNumeric(p[1].trim()) == false) { System.out.println("  Invalid quantity for: " + p[0]); continue; }
                        market.sellIngredient(p[0].trim(), Integer.parseInt(p[1].trim()), player);
                    }
                }
            } else if (choice.equals("3")) {
                inMarket = false;
            } else {
                System.out.println("  Enter 1, 2, or 3.");
            }
        }
    }

    /**
     * Handles blessing a damaged cauldron for a crystal cost, restoring it
     * to usable if the player confirms and has enough crystals.
     */
    private void handleBlessCauldron() {
        if (player.getDamagedCauldronCount() == 0) { System.out.println("  All cauldrons are fine!"); return; }
        System.out.printf("  Damaged: %d | Cost: %d crystals | Your crystals: %d%n",
                player.getDamagedCauldronCount(), BLESS_COST, player.getCrystals());
        System.out.print("  Bless a cauldron? (Y/N): ");
        if (scanner.nextLine().trim().equalsIgnoreCase("Y")) {
            boolean paid = player.spendCrystals(BLESS_COST);
            if (paid == true) { player.getDamagedCauldron().bless(); System.out.println("  Cauldron blessed and usable again!"); }
        } else {
            System.out.println("  Cancelled.");
        }
    }

    /**
     * Handles claiming the one-time login bonus, if not already claimed
     * this session.
     */
    private void handleLoginBonus() {
        if (player.isLoginBonusClaimed() == true) { System.out.println("  Already claimed! Exit and reenter to claim again."); return; }
        Ingredient bonus = player.claimLoginBonus();
        System.out.println("  Login bonus: 1x " + bonus.getName() + " added to inventory!");
    }

    // checks if a string can be safely parsed as an integer (allows a leading + or -)
    private boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) return false;
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (i == 0 && (c == '-' || c == '+') && str.length() > 1) continue;
            if (Character.isDigit(c) == false) return false;
        }
        return true;
    }

    /**
     * Handles exiting the game: marks the market for refresh, saves the
     * current player and spellbook, and closes the input scanner.
     *
     * @return false, to stop the main menu loop
     */
    private boolean handleExit() {
        System.out.println("\n  Saving...");
        market.markForRefresh();
        saveManager.save(player, spellbook, brewingManager.getRecipeData());
        System.out.println("  Goodbye, " + player.getName() + "!");
        scanner.close();
        return false;
    }
}
