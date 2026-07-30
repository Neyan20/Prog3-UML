import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

/**
 * The market screen for Potion Prodigy.
 * Lets the player buy ingredients/cauldrons and sell ingredients from their inventory.
 *
 * @author [Your Names Here]
 * @version 2.0
 */
public class MarketView extends JDialog {

    private GameController controller;
    private MainMenuView parent;

    private DefaultListModel<String> buyListModel;
    private JList<String> buyList;
    private JTextField buyField;
    private JLabel buyResultLabel;

    private DefaultListModel<String> sellListModel;
    private JList<String> sellList;
    private JTextField sellField;
    private JLabel sellResultLabel;

    private JLabel crystalLabel;

    /**
     * Constructs the market view.
     *
     * @param controller the game controller
     * @param parent     the main menu view (to refresh on close)
     */
    public MarketView(GameController controller, MainMenuView parent) {
        super(parent, "Market", true);
        this.controller = controller;
        this.parent = parent;
        buildUI();
    }

    /**
     * Builds the market screen with Buy and Sell tabs.
     */
    private void buildUI() {
        setSize(520, 500);
        setLocationRelativeTo(parent);
        setResizable(false);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JLabel title = new JLabel("Market", SwingConstants.CENTER);
        crystalLabel = new JLabel("Crystals: " + controller.getPlayer().getCrystals(), SwingConstants.CENTER);

        JPanel topPanel = new JPanel(new GridLayout(2, 1));
        topPanel.add(title);
        topPanel.add(crystalLabel);
        panel.add(topPanel, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Buy", buildBuyPanel());
        tabs.addTab("Sell", buildSellPanel());
        panel.add(tabs, BorderLayout.CENTER);

        JButton closeBtn = new JButton("Exit Market");
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.add(closeBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);

        add(panel);

        closeBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }

    /**
     * Builds the Buy tab: slot listing plus a slot:qty input field.
     *
     * @return the assembled buy panel
     */
    private JPanel buildBuyPanel() {
        JPanel buyPanel = new JPanel(new BorderLayout(8, 8));
        buyPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        buyListModel = new DefaultListModel<String>();
        refreshBuyList();
        buyList = new JList<String>(buyListModel);

        JScrollPane scrollPane = new JScrollPane(buyList);
        buyPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new BorderLayout(6, 6));

        JLabel hint = new JLabel("Format: slot,slot  (e.g. 1,3) - buys out the entire slot, no partial amounts");
        inputPanel.add(hint, BorderLayout.NORTH);

        buyField = new JTextField();
        inputPanel.add(buyField, BorderLayout.CENTER);

        JButton buyBtn = new JButton("Buy");
        inputPanel.add(buyBtn, BorderLayout.EAST);

        buyResultLabel = new JLabel(" ", SwingConstants.CENTER);

        JPanel bottom = new JPanel(new BorderLayout(4, 4));
        bottom.add(inputPanel, BorderLayout.NORTH);
        bottom.add(buyResultLabel, BorderLayout.SOUTH);
        buyPanel.add(bottom, BorderLayout.SOUTH);

        buyBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleBuy();
            }
        });

        return buyPanel;
    }

    /**
     * Builds the Sell tab: inventory listing plus a Name,qty input field.
     *
     * @return the assembled sell panel
     */
    private JPanel buildSellPanel() {
        JPanel sellPanel = new JPanel(new BorderLayout(8, 8));
        sellPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        sellListModel = new DefaultListModel<String>();
        refreshSellList();
        sellList = new JList<String>(sellListModel);

        JScrollPane scrollPane = new JScrollPane(sellList);
        sellPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new BorderLayout(6, 6));

        JLabel hint = new JLabel("Format: Name,qty  (separate multiple with ;)");
        inputPanel.add(hint, BorderLayout.NORTH);

        sellField = new JTextField();
        inputPanel.add(sellField, BorderLayout.CENTER);

        JButton sellBtn = new JButton("Sell");
        inputPanel.add(sellBtn, BorderLayout.EAST);

        sellResultLabel = new JLabel(" ", SwingConstants.CENTER);

        JPanel bottom = new JPanel(new BorderLayout(4, 4));
        bottom.add(inputPanel, BorderLayout.NORTH);
        bottom.add(sellResultLabel, BorderLayout.SOUTH);
        sellPanel.add(bottom, BorderLayout.SOUTH);

        sellBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleSell();
            }
        });

        return sellPanel;
    }

    /** Rebuilds the buy list display from the current market slots. */
    private void refreshBuyList() {
        buyListModel.clear();
        MarketSlot[] slots = controller.getMarket().getSlots();
        for (int i = 0; i < slots.length; i++) {
            buyListModel.addElement(slots[i].toDisplayString(i + 1));
        }
    }

    /** Rebuilds the sell list display from the player's current inventory. */
    private void refreshSellList() {
        sellListModel.clear();
        IngredientCatalog catalog = controller.getCatalog();
        Player player = controller.getPlayer();
        ArrayList<Ingredient> all = catalog.getAll();
        boolean any = false;
        for (int i = 0; i < all.size(); i++) {
            String name = all.get(i).getName();
            int qty = player.getInventory().getQuantity(name);
            if (qty > 0) {
                sellListModel.addElement(name + " = " + qty + "  (sells for " + all.get(i).getSellPrice() + " each)");
                any = true;
            }
        }
        if (any == false) {
            sellListModel.addElement("No sellable ingredients in inventory.");
        }
    }

    /** Parses the buy field input and submits the purchase to the controller. */
    private void handleBuy() {
        String input = buyField.getText().trim();
        if (input.isEmpty()) { buyResultLabel.setText("Enter a slot number first."); return; }

        String[] parts = input.split(",");
        int[] slots = new int[parts.length];
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i].trim();
            if (isNumeric(part) == false) {
                buyResultLabel.setText("Invalid format. Use slot,slot (e.g. 1,3)");
                return;
            }
            slots[i] = Integer.parseInt(part);
        }

        String result = controller.buyFromMarket(slots);
        buyResultLabel.setText("<html>" + result.replace("\n", "<br>") + "</html>");
        buyField.setText("");
        refreshBuyList();
        refreshSellList();
        crystalLabel.setText("Crystals: " + controller.getPlayer().getCrystals());
        parent.refresh();
    }

    /** Parses the sell field input and submits each sale to the controller. */
    private void handleSell() {
        String input = sellField.getText().trim();
        if (input.isEmpty()) { sellResultLabel.setText("Enter a Name,qty pair first."); return; }

        String[] entries = input.split(";");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < entries.length; i++) {
            String[] p = entries[i].trim().split(",");
            if (p.length != 2 || isNumeric(p[1].trim()) == false) {
                sb.append("Invalid entry: ").append(entries[i]).append("\n");
                continue;
            }
            String result = controller.sellIngredient(p[0].trim(), Integer.parseInt(p[1].trim()));
            sb.append(result).append("\n");
        }

        sellResultLabel.setText("<html>" + sb.toString().trim().replace("\n", "<br>") + "</html>");
        sellField.setText("");
        refreshSellList();
        crystalLabel.setText("Crystals: " + controller.getPlayer().getCrystals());
        parent.refresh();
    }

    // checks if a string can be safely parsed as a non-negative integer
    private boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) return false;
        for (int i = 0; i < str.length(); i++) {
            if (Character.isDigit(str.charAt(i)) == false) return false;
        }
        return true;
    }

    /** Shows this dialog. */
    public void display() { setVisible(true); }
}
