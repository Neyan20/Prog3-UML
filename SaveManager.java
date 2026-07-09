import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;


public class SaveManager {
    private static final String SAVE_DIR = "saves/";
    private static final String EXTENSION = ".txt";
    private IngredientCatalog catalog;

    public SaveManager(IngredientCatalog catalog) {
        this.catalog = catalog;
        File dir = new File(SAVE_DIR);
        if (dir.exists() == false) dir.mkdirs();
    }

    private String getSavePath(String name) { return SAVE_DIR + name + EXTENSION; }

    public boolean fileExists(String name) { return new File(getSavePath(name)).exists(); }

    public String[] listSaves() {
        String[] files = new File(SAVE_DIR).list((d, n) -> n.endsWith(EXTENSION));
        if (files == null) return new String[0];
        for (int i = 0; i < files.length; i++) files[i] = files[i].replace(EXTENSION, "");
        return files;
    }

    public void save(Player player, Spellbook spellbook, ArrayList<Recipe> recipeData) {
        try {
            PrintWriter writer = new PrintWriter(new FileWriter(getSavePath(player.getName())));
            writer.println("NAME = " + player.getName());
            writer.println("CRYSTALS = " + player.getCrystals());
            writer.println();
            writer.println("[INVENTORY]");

            Ingredient[] allFruits = catalog.getFruits();
            for (int i = 0; i < allFruits.length; i++)
                writer.println(allFruits[i].getName().toUpperCase() + " = " + player.getInventory().getQuantity(allFruits[i].getName()));
            writer.println();

            Ingredient[] allBases = catalog.getBases();
            for (int i = 0; i < allBases.length; i++)
                writer.println(allBases[i].getName().toUpperCase() + " = " + player.getInventory().getQuantity(allBases[i].getName()));
            writer.println();

            writer.println("TOTAL CAULDRONS = " + player.getCauldrons().length);
            writer.println("USABLE CAULDRONS = " + player.getUsableCauldronCount());
            int dmgIdx = 1;
            Cauldron[] cauldrons = player.getCauldrons();
            for (int i = 0; i < cauldrons.length; i++) {
                if (cauldrons[i].isUsable() == false) {
                    writer.println("DAMAGED CAULDRON " + dmgIdx + " = " + cauldrons[i].getJunkContents());
                    dmgIdx++;
                }
            }
            writer.println();

            writer.println("[SPELLBOOK]");
            ArrayList<Recipe> unlocked = spellbook.getRecipes();
            String idList = "";
            for (int i = 0; i < unlocked.size(); i++) {
                idList = idList + unlocked.get(i).getConcoctionId();
                if (i < unlocked.size() - 1) idList = idList + ",";
            }
            writer.println(idList);
            writer.close();
            System.out.println("  Game saved as '" + player.getName() + "'.");
        } catch (IOException e) {
            System.out.println("  [Error] Could not save: " + e.getMessage());
        }
    }

    public Player load(String playerName) {
        if (fileExists(playerName) == false) { System.out.println("  Save not found for '" + playerName + "'."); return null; }

        String name = null;
        int crystals = 0, totalCauldrons = 0, usableCauldrons = 0;
        Inventory inventory = new Inventory();
        HashMap<Integer, String> damagedJunk = new HashMap<Integer, String>();
        String currentSection = "";

        try {
            BufferedReader reader = new BufferedReader(new FileReader(getSavePath(playerName)));
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                if (line.startsWith("[")) { currentSection = line; continue; }
                if (line.contains(" = ") == false) continue;

                String[] kv = line.split(" = ", 2);
                String key = kv[0].trim(), val = kv[1].trim();

                if (key.equals("NAME")) name = val;
                else if (key.equals("CRYSTALS")) crystals = Integer.parseInt(val);
                else if (key.equals("TOTAL CAULDRONS")) totalCauldrons = Integer.parseInt(val);
                else if (key.equals("USABLE CAULDRONS")) usableCauldrons = Integer.parseInt(val);
                else if (key.startsWith("DAMAGED CAULDRON "))
                    damagedJunk.put(Integer.parseInt(key.replace("DAMAGED CAULDRON ", "").trim()), val);
                else if (currentSection.equals("[INVENTORY]")) {
                    Ingredient ing = catalog.getByName(toTitleCase(key));
                    int qty = Integer.parseInt(val);
                    if (ing != null && qty > 0) inventory.addItem(ing.getName(), qty);
                }
            }
            reader.close();

            Cauldron[] cauldrons = new Cauldron[totalCauldrons];
            for (int i = 0; i < totalCauldrons; i++) {
                cauldrons[i] = new Cauldron();
                if (i >= usableCauldrons)
                    cauldrons[i].setDamaged(damagedJunk.getOrDefault(i - usableCauldrons + 1, "Unknown junk"));
            }
            System.out.println("  Loaded save for '" + playerName + "'.");
            return new Player(name, crystals, inventory, cauldrons);
        } catch (IOException e) { System.out.println("  [Error] Could not load: " + e.getMessage()); return null; }
    }

    public Spellbook loadSpellbook(String playerName, ArrayList<Recipe> recipeData) {
        Spellbook spellbook = new Spellbook();
        if (fileExists(playerName) == false) return spellbook;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(getSavePath(playerName)));
            String line;
            boolean inSpellbook = false;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                if (line.equals("[SPELLBOOK]")) { inSpellbook = true; continue; }
                if (inSpellbook == true) {
                    String[] ids = line.split(",");
                    for (int i = 0; i < ids.length; i++) {
                        int id = Integer.parseInt(ids[i].trim());
                        for (int j = 0; j < recipeData.size(); j++) {
                            if (recipeData.get(j).getConcoctionId() == id) { spellbook.addRecipe(recipeData.get(j)); break; }
                        }
                    }
                    break;
                }
            }
            reader.close();
        } catch (IOException e) { System.out.println("  [Warning] Could not load spellbook: " + e.getMessage()); }
        return spellbook;
    }

    public void deleteFile(String name) {
        File f = new File(getSavePath(name));
        if (f.exists() == true) { f.delete(); System.out.println("  Existing save deleted."); }
    }

    // converts all caps to title case
    private String toTitleCase(String input) {
        if (input == null || input.isEmpty()) return input;
        String[] words = input.split(" ");
        String result = "";
        for (int i = 0; i < words.length; i++) {
            if (words[i].isEmpty()) continue;
            result = result + words[i].substring(0, 1).toUpperCase() + words[i].substring(1).toLowerCase() + " ";
        }
        return result.trim();
    }
}
