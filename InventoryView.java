import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class InventoryView extends JDialog{
    private GameController controller;

    public InventoryView(GameController controller, MainMenuView parent) {
        super(parent, "Inventory", true);
        this.controller = controller;
        buildUI(parent);
    }

    public void buildUI(MainMenuView parent) {
        setSize(400, 420);
        setLocationRelativeTo(parent);
        setResizable(false);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        
        JLabel title = new JLabel("Inventory", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        panel.add(title, BorderLayout.NORTH);

        DefaultListModel<String> listModel = new DefaultListModel<>();
        Player player = controller.getPlayer();
        Inventory inv = player.getInventory();

        //Adding Fruits to inventory
        listModel.addElement("=== Fruits ===");
        if (inv.getFruits().isEmpty()) {
            listModel.addElement("  (None)  ");
        }
        else {
            for (Map.Entry<String, Integer> entry : inv.getFruits().entrySet()) {
                if (entry.getValue() > 0) {
                    listModel.addElement(" " + entry.getKey() + ": " + entry.getValue());
                }
            }
        }

        // Adding Bases to inventory
        listModel.addElement("");
        listModel.addElement("=== Bases ===");
        if (inv.getBases().isEmpty()) {
            listModel.addElement("  (None)  ");
        }
        else {
            for (Map.Entry<String, Integer> entry : inv.getBases().entrySet()) {
                if (entry.getValue() > 0) {
                    listModel.addElement(" " + entry.getKey() + ": " + entry.getValue());
                }
            }
        }

        // Adding Caulrons to inventory
        listModel.addElement("");
        listModel.addElement("=== CAULDRONS ===");
        Cauldron[] cauldrons = player.getCauldrons();
        for (int i = 0; i < cauldrons.length; i++) {
            listModel.addElement("  Cauldron " + (i + 1) + ": " + cauldrons[i].toString());
        }

        JList<String> list = new JList<>(listModel);
        panel.add(new JScrollPane(list), BorderLayout.CENTER);

        // Adding close button
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
