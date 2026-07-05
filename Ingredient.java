public class Ingredient {
    private String name;
    private int buyPrice;
    private int sellPrice;
    private String type;

    public Ingredient(String name,int buyPrice, int sellPrice, String type) {
        this.name = name;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public int getBuyPrice() {
        return buyPrice;
    }

    public int getSellPrice() {
        return sellPrice;
    }

    public String getType() {
        return type;
    }

    public boolean isFruit() {
        return type.equals("fruit");
    }

    public boolean isBase() {
        return type.equals("base");
    }
}