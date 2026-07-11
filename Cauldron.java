/**
 * Represents a single brewing cauldron owned by a player. A cauldron can
 * become damaged after a failed brew and must be blessed before it can
 * be used again.
 */
public class Cauldron {
    private boolean usable;
    private String junkContents;

    /**
     * Constructs a new Cauldron. New cauldrons start out usable.
     */
    public Cauldron() {
        this.usable = true;
    }

    /**
     * @return true if this cauldron is currently usable for brewing
     */
    public boolean isUsable() {
        return usable;
    }

    /**
     * Marks this cauldron as damaged and unusable.
     *
     * @param junk description of the failed ingredient combination left inside
     */
    public void setDamaged(String junk) {
        usable = false;
        junkContents = junk;
    }

    /**
     * Restores this cauldron to a usable state and clears its junk contents.
     */
    public void bless() {
        usable = true;
        junkContents = null;
    }

    /**
     * @return the description of the junk contents left in a damaged cauldron,
     *         or null if the cauldron has none
     */
    public String getVoidContents() {
        return junkContents;
    }

    /**
     * @return "Usable" if this cauldron can brew, otherwise its damaged junk contents
     */
    @Override 
    public String toString() {
        if (usable) {
            return "Usable";
        } else {
            return "Damaged (" + getVoidContents() + ")";
        }
}
}
