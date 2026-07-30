import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


public class OpeningView extends JFrame {

    private GameController controller;

    // UI components
    private JTextField nameField;
    private JButton newGameBtn;
    private JButton loadGameBtn;
    private JLabel messageLabel;

   
    public OpeningView(GameController controller) {
        this.controller = controller;
        buildUI();
    }

  
    private void buildUI() {
        setTitle("Potion Prodigy");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(480, 320);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 16, 8, 16);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel title = new JLabel("POTION PRODIGY", SwingConstants.CENTER);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        mainPanel.add(title, gbc);

        // Subtitle
        JLabel subtitle = new JLabel("Alchemy Simulator", SwingConstants.CENTER);
        gbc.gridy = 1;
        mainPanel.add(subtitle, gbc);

        // Name label
        JLabel nameLabel = new JLabel("Player Name:");
        gbc.gridy = 2; gbc.gridwidth = 1; gbc.gridx = 0;
        mainPanel.add(nameLabel, gbc);

        // Name field
        nameField = new JTextField();
        nameField.setPreferredSize(new Dimension(200, 28));
        gbc.gridx = 1;
        mainPanel.add(nameField, gbc);

        // Buttons panel
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));

        newGameBtn = new JButton("New Game");
        loadGameBtn = new JButton("Load Game");

        btnPanel.add(newGameBtn);
        btnPanel.add(loadGameBtn);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        mainPanel.add(btnPanel, gbc);

        // Message label
        messageLabel = new JLabel(" ", SwingConstants.CENTER);
        gbc.gridy = 4;
        mainPanel.add(messageLabel, gbc);

        add(mainPanel);

        // Button actions
        newGameBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleNewGame();
            }
        });

        loadGameBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleLoadGame();
            }
        });

        // Allow pressing Enter in name field to start new game
        nameField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleNewGame();
            }
        });
    }

   
    private void handleNewGame() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            messageLabel.setText("Name cannot be empty.");
            return;
        }

        // Check if save exists and confirm overwrite
        if (controller.saveExists(name)) {
            int choice = JOptionPane.showConfirmDialog(
                this,
                "Save '" + name + "' already exists. Overwrite?",
                "Overwrite Save",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );
            if (choice != JOptionPane.YES_OPTION) {
                messageLabel.setText("Cancelled.");
                return;
            }
        }

        setVisible(false);
        controller.startNewGame(name);
    }

   
    private void handleLoadGame() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            messageLabel.setText("Name cannot be empty.");
            return;
        }

        boolean success = controller.loadGame(name);
        if (success == false) {
            messageLabel.setText("Save not found for '" + name + "'.");
        } else {
            setVisible(false);
        }
    }

 
    public void display() {
        setVisible(true);
    }
}
