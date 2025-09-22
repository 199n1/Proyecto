import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class SilkRoad extends JFrame {
    private Store[] stores;
    private List<Robot> robots;
    private JProgressBar progressBar;
    private int totalProfit;
    private int pixelScale;

    // Constructor 1: básico
    public SilkRoad(int length) {
        this(length, 40); // por defecto 40px por celda
    }

    // Constructor 2: con escala personalizada
    public SilkRoad(int length, int pixelScale) {
        this.pixelScale = pixelScale;
        this.stores = new Store[length];
        this.robots = new ArrayList<>();
        this.totalProfit = 0;

        Random rand = new Random();
        for (int i = 0; i < length; i++) {
            int coins = rand.nextInt(20) + 1;
            stores[i] = new Store(i, coins);
        }

        setupUI(length);
    }

    private void setupUI(int length) {
        setTitle("Silk Road Simulation");
        setLayout(new BorderLayout());

        JPanel grid = new JPanel(new GridLayout(2, length));
        for (Store store : stores) {
            grid.add(store.getLabel());
        }
        for (int i = 0; i < length; i++) {
            JLabel placeholder = new JLabel("");
            placeholder.setOpaque(true);
            placeholder.setBackground(Color.WHITE);
            grid.add(placeholder);
        }

        progressBar = new JProgressBar(0, 1000);
        progressBar.setStringPainted(true);

        add(grid, BorderLayout.CENTER);
        add(progressBar, BorderLayout.SOUTH);

        setSize(length * pixelScale, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public void addRobot(int startLocation) {
        Robot robot = new Robot(startLocation);
        robots.add(robot);
    }

    public int moveRobot(int robotIndex, int storeIndex) {
        if (robotIndex < 0 || robotIndex >= robots.size()) return 0;
        if (storeIndex < 0 || storeIndex >= stores.length) return 0;

        Robot robot = robots.get(robotIndex);
        Store store = stores[storeIndex];

        int profit = robot.moveTo(store);
        totalProfit += profit;
        progressBar.setValue(totalProfit);

        return profit;
    }

    // movimiento automático para maximizar ganancia
    public void autoMoveRobots() {
        for (Robot robot : robots) {
            int bestProfit = Integer.MIN_VALUE;
            Store bestStore = null;

            for (Store store : stores) {
                if (!store.isEmpty()) {
                    int distance = Math.abs(store.getLocation() - robot.getLocation());
                    int potentialProfit = store.getCoins() - distance;
                    if (potentialProfit > bestProfit) {
                        bestProfit = potentialProfit;
                        bestStore = store;
                    }
                }
            }
            if (bestStore != null) {
                int profit = robot.moveTo(bestStore);
                totalProfit += profit;
                progressBar.setValue(totalProfit);
            }
        }
    }

    // consultar veces que una tienda fue desocupada
    public int timesEmptied(int location) {
        if (location < 0 || location >= stores.length) return 0;
        return stores[location].getEmptiedCount();
    }

    // consultar ganancias por movimiento de un robot
    public List<Integer> robotProfits(int robotIndex) {
        if (robotIndex < 0 || robotIndex >= robots.size()) return Collections.emptyList();
        return robots.get(robotIndex).getMoveProfits();
    }

    public int getTotalProfit() {
        return totalProfit;
    }

    public int getNumberOfStores() {
        return stores.length;
    }

    public int getNumberOfRobots() {
        return robots.size();
    }
}
