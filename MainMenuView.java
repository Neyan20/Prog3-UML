import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


public class MainMenuView extends JFrame {

    private GameController controller;

    // UI components
    private JLabel playerLabel;
    private JLabel crystalLabel;
    private JButton brewBtn;
    private JButton inventoryBtn;
    private JButton spellbookBtn;
    private JButton marketBtn;
    private JButton blessBtn;
    private JButton bonusBtn;
    private JButton exitBtn;

    public MainMenuView(GameController controller) {
        this.controller = controller;
        buildUI();
    }

  
    private void buildUI() {
        setTitle("Potion Prodigy - Main Menu");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(420, 520);
        setLocationRelativeTo(null);
        setResizable(false);

        // Confirm before closing window directly
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                handleExit();
            }
        });

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(0, 0));

        // ---- Top panel: player info ----
        JPanel topPanel = new JPanel(new GridLayout(2, 1));
        topPanel.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        playerLabel = new JLabel("Player: ");
        crystalLabel = new JLabel("Crystals: ");

        topPanel.add(playerLabel);
        topPanel.add(crystalLabel);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // ---- Center panel: menu buttons ----
        JPanel buttonPanel = new JPanel(new GridLayout(7, 1, 0, 8));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 10, 40));

        brewBtn      = new JButton("Brew Concoction");
        inventoryBtn = new JButton("Check Inventory");
        spellbookBtn = new JButton("Check Spellbook");
        marketBtn    = new JButton("Visit Market");
        blessBtn     = new JButton("Bless Cauldron");
        bonusBtn     = new JButton("Login Bonus");
        exitBtn      = new JButton("Exit Game");

        buttonPanel.add(brewBtn);
        buttonPanel.add(inventoryBtn);
        buttonPanel.add(spellbookBtn);
        buttonPanel.add(marketBtn);
        buttonPanel.add(blessBtn);
        buttonPanel.add(bonusBtn);
        buttonPanel.add(exitBtn);

        mainPanel.add(buttonPanel, BorderLayout.CENTER);

        add(mainPanel);

        // ---- Button actions ----
        brewBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                BrewView brewView = new BrewView(controller, MainMenuView.this);
                brewView.display();
            }
        });

        inventoryBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                InventoryView invView = new InventoryView(controller, MainMenuView.this);
                invView.display();
            }
        });

        spellbookBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                SpellbookView sbView = new SpellbookView(controller, MainMenuView.this);
                sbView.display();
            }
        });

        marketBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.visitMarket();
                MarketView mktView = new MarketView(controller, MainMenuView.this);
                mktView.display();
            }
        });

        blessBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                BlessCauldronView blessView = new BlessCauldronView(controller, MainMenuView.this);
                blessView.display();
            }
        });

        bonusBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String result = controller.claimLoginBonus();
                JOptionPane.showMessageDialog(MainMenuView.this, result, "Login Bonus", JOptionPane.INFORMATION_MESSAGE);
                refresh();
            }
        });

        exitBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleExit();
            }
        });
    }

 
    private void handleExit() {
        int choice = JOptionPane.showConfirmDialog(
            this,
            "Save and exit the game?",
            "Exit",
            JOptionPane.YES_NO_OPTION
        );
        if (choice == JOptionPane.YES_OPTION) {
            controller.saveAndExit();
        }
    }

    public void refresh() {
        playerLabel.setText("Player: " + controller.getPlayer().getName());
        crystalLabel.setText("Crystals: " + controller.getPlayer().getCrystals());
    }


    public void display() {
        refresh();
        setVisible(true);
    }
}
