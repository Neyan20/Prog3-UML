import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;


public class CreativeModeView extends JDialog {

    private GameController controller;
    private MainMenuView parent;

    private JComboBox<String> baseCombo;
    private JList<String> fruitList;
    private JLabel resultLabel;

  
    public CreativeModeView(GameController controller, MainMenuView parent) {
        super(parent, "Creative Mode", true);
        this.controller = controller;
        this.parent = parent;
        buildUI();
    }

    private void buildUI() {
        setSize(420, 480);
        setLocationRelativeTo(parent);
        setResizable(false);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JLabel title = new JLabel("Creative Mode", SwingConstants.CENTER);
        panel.add(title, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout(8, 8));

        JPanel basePanel = new JPanel(new BorderLayout(6, 6));
        JLabel baseLabel = new JLabel("Select a Base:");
        basePanel.add(baseLabel, BorderLayout.NORTH);

        ArrayList<Ingredient> bases = controller.getCatalog().getBases();
        Player player = controller.getPlayer();
        DefaultComboBoxModel<String> baseModel = new DefaultComboBoxModel<String>();
        for (int i = 0; i < bases.size(); i++) {
            String name = bases.get(i).getName();
            int qty = player.getInventory().getQuantity(name);
            baseModel.addElement(name + " (have: " + qty + ")");
        }
        baseCombo = new JComboBox<String>(baseModel);
        basePanel.add(baseCombo, BorderLayout.CENTER);
        centerPanel.add(basePanel, BorderLayout.NORTH);

        JPanel fruitPanel = new JPanel(new BorderLayout(6, 6));
        JLabel fruitLabel = new JLabel("Select 1 to 3 Fruits:");
        fruitPanel.add(fruitLabel, BorderLayout.NORTH);

        ArrayList<Ingredient> fruits = controller.getCatalog().getFruits();
        DefaultListModel<String> fruitModel = new DefaultListModel<String>();
        for (int i = 0; i < fruits.size(); i++) {
            String name = fruits.get(i).getName();
            int qty = player.getInventory().getQuantity(name);
            fruitModel.addElement(name + " (have: " + qty + ")");
        }
        fruitList = new JList<String>(fruitModel);
        fruitList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        JScrollPane scrollPane = new JScrollPane(fruitList);
        fruitPanel.add(scrollPane, BorderLayout.CENTER);
        centerPanel.add(fruitPanel, BorderLayout.CENTER);

        panel.add(centerPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout(6, 6));

        resultLabel = new JLabel(" ", SwingConstants.CENTER);
        bottomPanel.add(resultLabel, BorderLayout.NORTH);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));

        JButton brewBtn = new JButton("Brew");
        JButton cancelBtn = new JButton("Back");

        btnRow.add(brewBtn);
        btnRow.add(cancelBtn);
        bottomPanel.add(btnRow, BorderLayout.SOUTH);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        add(panel);

        brewBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleBrew();
            }
        });

        cancelBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }

    private void handleBrew() {
        int baseIdx = baseCombo.getSelectedIndex();
        if (baseIdx < 0) { resultLabel.setText("Please select a base."); return; }

        int[] fruitIdxs = fruitList.getSelectedIndices();
        if (fruitIdxs.length == 0) { resultLabel.setText("Please select at least 1 fruit."); return; }
        if (fruitIdxs.length > 3) { resultLabel.setText("Please select at most 3 fruits."); return; }

        ArrayList<Ingredient> bases = controller.getCatalog().getBases();
        ArrayList<Ingredient> fruits = controller.getCatalog().getFruits();
        String base = bases.get(baseIdx).getName();

        ArrayList<String> chosenFruits = new ArrayList<String>();
        for (int i = 0; i < fruitIdxs.length; i++) {
            chosenFruits.add(fruits.get(fruitIdxs[i]).getName());
        }

        int choice = JOptionPane.showConfirmDialog(
            this,
            "Brew with " + base + " + " + chosenFruits + "?",
            "Confirm Brew",
            JOptionPane.YES_NO_OPTION
        );
        if (choice == JOptionPane.YES_OPTION) {
            String result = controller.brewCreative(base, chosenFruits);
            resultLabel.setText(result);
            parent.refresh();
        }
    }

    public void display() { setVisible(true); }
}
