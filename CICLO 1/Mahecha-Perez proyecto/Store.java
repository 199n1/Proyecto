import javax.swing.*;
import java.awt.*;

public class Store {
    private int location;
    private int coins;
    private JLabel label;
    private int emptiedCount; // cuántas veces ha sido desocupada

    public Store(int location, int coins) {
        this.location = location;
        this.coins = coins;
        this.emptiedCount = 0;

        label = new JLabel(String.valueOf(coins));
        label.setOpaque(true);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        updateLabel();
    }

    public int getLocation() {
        return location;
    }

    public int getCoins() {
        return coins;
    }

    public JLabel getLabel() {
        return label;
    }

    public int getEmptiedCount() {
        return emptiedCount;
    }

    public boolean isEmpty() {
        return coins <= 0;
    }

    public int takeCoins() {
        int taken = coins;
        coins = 0;
        emptiedCount++;
        updateLabel();
        return taken;
    }

    private void updateLabel() {
        if (isEmpty()) {
            label.setText("0");
            label.setBackground(Color.LIGHT_GRAY); // tienda vacía → gris
            label.setForeground(Color.DARK_GRAY);
            label.setFont(new Font("Arial", Font.ITALIC, 12));
        } else {
            label.setText(String.valueOf(coins));
            label.setBackground(Color.YELLOW); // tienda con dinero → amarilla
            label.setForeground(Color.BLACK);
            label.setFont(new Font("Arial", Font.BOLD, 14));
        }
    }
}
