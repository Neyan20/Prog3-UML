import javax.swing.*;
import java.awt.*;

public class BlessCauldronView extends JDialog{
    private GameController controller;
    private MainMenuView parent;
    private JLabel infoLabel;
    private JLabel statusLabel;

    public BlessCauldronView(GameController controller, MainMenuView parent) {
        super(parent, "Bless Cauldron", true);
        this.controller = controller;
        this.parent = parent;
        buildUI();
    }

    public void buildUI() {
        setSize(380, 260);
        setLocationRelativeTo(parent);
        setResizable(false);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JLabel title = new JLabel("Bless Cauldron", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        panel.add(title, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        Player player = controller.getPlayer();

        infoLabel =new JLabel("Damaged Cauldrons" + player.getDamagedCauldronCount(), SwingConstants.CENTER);
        JLabel costLabel = new JLabel("Blessing cost: " + controller.getBlessCost() + " Crystals", SwingConstants.CENTER);
        statusLabel = new JLabel(" ", SwingConstants.CENTER);

        centerPanel.add(infoLabel);
        centerPanel.add(costLabel);
        centerPanel.add(statusLabel);
        panel.add(centerPanel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        JButton blessBtn = new JButton("Bless Cauldron");
        JButton backBtn = new JButton("Back");

        btnPanel.add(blessBtn);
        btnPanel.add(backBtn);
        panel.add(btnPanel, BorderLayout.CENTER);

        add(panel);

        blessBtn.addActionListener(e -> {
            String result = controller.blessCauldron();
            statusLabel.setText(result);
            infoLabel.setText("Damaged Cauldrons: " + controller.getPlayer().getDamagedCauldronCount());
            parent.refresh();
        });

        backBtn.addActionListener(e -> dispose());
    }

    public void display() {
        setVisible(true);
    }
}