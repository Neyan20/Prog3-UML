/**
 * Represents a single ingredient (fruit, base, or cauldron) that can be
 * bought, sold, or used in potion recipes.
 */
public class Ingredient {
    private String name;
    private int buyPrice;
    private int sellPrice;
    private String type;

    /**
     * Constructs a new Ingredient.
     *
     * @param name      the ingredient's display name
     * @param buyPrice  the price to buy this ingredient from the market
     * @param sellPrice the price a player receives for selling this ingredient
     * @param type      the ingredient category (e.g. "fruit", "base", "CAULDRON")
     */
    public Ingredient(String name,int buyPrice, int sellPrice, String type) {
        this.name = name;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
        this.type = type;
    }

    /**
     * @return the ingredient's name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the price to buy this ingredient
     */
    public int getBuyPrice() {
        return buyPrice;
    }

    /**
     * @return the price received when selling this ingredient
     */
    public int getSellPrice() {
        return sellPrice;
    }

    /**
     * @return the ingredient's type category
     */
    public String getType() {
        return type;
    }

    /**
     * @return true if this ingredient's type is "fruit"
     */
    public boolean isFruit() {
        return type.equals("fruit");
    }

    /**
     * @return true if this ingredient's type is "base"
     */
    public boolean isBase() {
        return type.equals("base");
    }
}
