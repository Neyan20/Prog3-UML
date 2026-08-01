import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * SpellbookView - GUI displaying the player's unlocked brewing recipes
 */
public class SpellbookView extends JDialog{
    private GameController controller;

    public SpellbookView(GameController controller, MainMenuView parent) {
        super(parent, "Spellbook", true);
        this.controller = controller;
        buildUI(parent);
    }

    private void buildUI(MainMenuView parent) {
        setSize(520, 420);
        setLocationRelativeTo(parent);
        setResizable(false);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        
        // Title
        JLabel title = new JLabel("Discovered Potion Recipes", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        panel.add(title, BorderLayout.NORTH);
        
        // Populating recipe list from spellbook
        DefaultListModel<String> listModel = new DefaultListModel<>();
        ArrayList<Recipe> recipes = controller.getSpellbook().getRecipes();

        if (recipes == null || recipes.isEmpty()) {
            listModel.addElement("Your Spellbook is empty! Discover recipes in Creative Mode");
        }
        else {
            for (Recipe r : recipes) {
                listModel.addElement(r.toString());
            }
        }

        // Displaying Recipes
        JList<String> recipeList = new JList<>(listModel);
        recipeList.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(recipeList);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Close Button 
        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(e -> dispose());

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.add(closeBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);

        add(panel);
    }

    public void display() {
        setVisible(true);
    }
}
