public class Cauldron {
    private boolean usable;
    private String junkContents;

    public Cauldron {
        this.usable = true;
    }
    
    public boolean isUsable() {
        return usable;
    }

    public void setDamaged(String junk) {
        usable = false;
    }

    public void bless() {
        usable = true;
        junkContents = null;
    }

    public String getVoidContents() {
        return junkContents;
    }
}
