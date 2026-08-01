import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class RecipeModeView extends JDialog {
    private GameController controller;
    private MainMenuView parent;
    private JComboBox<Recipe> recipeCombo;
    private JLabel statusLabel;

    public RecipeModeView(GameController controller, MainMenuView parent) {
        super(parent, "Recipe Mode", true);
        this.controller = controller;
        this.parent = parent;
        buildUI();
    }

    private void buildUI() {
        setSize(450, 250);
        setLocationRelativeTo(parent);
        setResizable(false);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JLabel title = new JLabel("Select Recipe to Brew", SwingConstants.CENTER);
        panel.add(title, BorderLayout.NORTH);

        ArrayList<Recipe> recipes = controller.getSpellbook().getRecipes();
        DefaultComboBoxModel<Recipe> comboModel = new DefaultComboBoxModel<>();
        for (Recipe r : recipes) {
            comboModel.addElement(r);
        }

        recipeCombo = new JComboBox<>(comboModel);
        
        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        centerPanel.add(recipeCombo);

        statusLabel = new JLabel(" ", SwingConstants.CENTER);
        centerPanel.add(statusLabel);
        panel.add(centerPanel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        JButton brewBtn = new JButton("Brew");
        JButton cancelBtn = new JButton("Back");

        btnPanel.add(brewBtn);
        btnPanel.add(cancelBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);

        add(panel);

        brewBtn.addActionListener(e -> {
            Recipe selected = (Recipe) recipeCombo.getSelectedItem();
            if (selected == null) {
                statusLabel.setText("No recipe selected.");
                return;
            }
            String result = controller.brewRecipe(selected.getConcoctionId());
            statusLabel.setText(result);
            parent.refresh();
        });

        cancelBtn.addActionListener(e -> dispose());
    }

    public void display() {
        setVisible(true);
    }
}