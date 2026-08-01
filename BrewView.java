import javax.swing.*;
import java.awt.*;

public class BrewView extends JDialog {
    private GameController controller;
    private MainMenuView parent;

    public BrewView(GameController controller, MainMenuView parent) {
        super(parent, "Brew Concoction", true);
        this.controller = controller;
        this.parent = parent;
        buildUI();
    }

    private void buildUI() {
        setSize(320, 220);
        setLocationRelativeTo(parent);
        setResizable(false);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JLabel title = new JLabel("Select Brewing Mode", SwingConstants.CENTER);
        panel.add(title, BorderLayout.NORTH);

        JPanel btnPanel = new JPanel(new GridLayout(2, 1, 0, 10));
        JButton recipeModeBtn = new JButton("Recipe Mode");
        JButton creativeModeBtn = new JButton("Creative Mode");

        btnPanel.add(recipeModeBtn);
        btnPanel.add(creativeModeBtn);
        panel.add(btnPanel, BorderLayout.CENTER);

        JButton cancelBtn = new JButton("Back");
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.add(cancelBtn);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        add(panel);

        recipeModeBtn.addActionListener(e -> {
            dispose();
            RecipeModeView recipeView = new RecipeModeView(controller, parent);
            recipeView.display();
        });

        creativeModeBtn.addActionListener(e -> {
            dispose();
            CreativeModeView creativeView = new CreativeModeView(controller, parent);
            creativeView.display();
        });

        cancelBtn.addActionListener(e -> dispose());
    }

    public void display() {
        setVisible(true);
    }
}