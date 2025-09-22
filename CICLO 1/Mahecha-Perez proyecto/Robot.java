import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Robot {
    private int location;
    private int totalProfit;
    private JLabel label;
    private List<Integer> moveProfits; // ganancias por cada movimiento

    public Robot(int startLocation) {
        this.location = startLocation;
        this.totalProfit = 0;
        this.moveProfits = new ArrayList<>();

        label = new JLabel("🤖");
        label.setOpaque(true);
        label.setBackground(Color.CYAN);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        updateLabel();
    }

    public int getLocation() {
        return location;
    }

    public int getTotalProfit() {
        return totalProfit;
    }

    public JLabel getLabel() {
        return label;
    }

    public List<Integer> getMoveProfits() {
        return moveProfits;
    }

    public int profitPerMove(int index) {
        if (index < 0 || index >= moveProfits.size()) return 0;
        return moveProfits.get(index);
    }

    public int moveTo(Store store) {
        int distance = Math.abs(store.getLocation() - location);
        int coinsTaken = store.takeCoins();
        int profit = coinsTaken - distance;

        totalProfit += profit;
        moveProfits.add(profit);

        location = store.getLocation();
        updateLabel();
        return profit;
    }

    private void updateLabel() {
        label.setText("🤖 " + totalProfit);
        if (totalProfit > 0) {
            label.setBackground(Color.GREEN);
        } else if (totalProfit < 0) {
            label.setBackground(Color.RED);
        } else {
            label.setBackground(Color.CYAN);
        }
    }
}
