import java.util.ArrayList;

public class Recipe {
    private int concoctionId;
    private String base;
    private ArrayList<String> fruits = new ArrayList<>();
    private String resultName;
    private int sellValue;

    public int getConcontionId() {
        return concoctionId;
    }

    public String getBase() {
        return base;
    }

    public ArrayList<String> getFruits() {
        return fruits;
    }

    public String getResultName() {
        return resultName;
    }

    public int getSellValue() {
        return sellValue;
    }

    public boolean matches(String base, ArrayList<String> fruits) {
        if (!this.base.equals(base)) {
            return false;
        }
        if (this.fruits.size() != fruits.size()) {
            return false;
        }
        for (String fruit : fruits) {
            if (!this.fruits.contains(fruit)) {
                return false;
            }
        }
        return true;
    }
}
