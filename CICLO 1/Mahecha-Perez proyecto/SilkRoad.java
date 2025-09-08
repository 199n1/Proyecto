import java.util.ArrayList;
 
public class SilkRoad {
    private int length;
    private ArrayList<Robot> robots;
    private ArrayList<Store> stores;
    private boolean isVisible;
    private int profit;
 
    public SilkRoad(int length) {
        this.length = length;
        this.robots = new ArrayList<>();
        this.stores = new ArrayList<>();
        this.isVisible = true;
        this.profit = 0;
    }
 
    // 🔹 Verificar si ya existe algo en esa posición
    private boolean occupied(int location) {
        for (Robot r : robots) {
            if (r.getPosition() == location) return true;
        }
        for (Store s : stores) {
            if (s.getPosition() == location) return true;
        }
        return false;
    }
 
    public void placeRobot(int location) {
        if (occupied(location)) {
            if (isVisible) {
                System.out.println("⚠️ No se puede colocar un robot en " + location + ", ya está ocupado.");
            }
            return;
        }
        Robot r = new Robot(location);
        robots.add(r);
        if (isVisible) r.makeVisible();
        System.out.println("Robot agregado en " + location);
    }
 
    public void removeRobot(int location) {
        Robot toRemove = null;
        for (Robot r : robots) {
            if (r.getPosition() == location) {
                toRemove = r;
                break;
            }
        }
        if (toRemove != null) {
            toRemove.makeInvisible();
            robots.remove(toRemove);
            System.out.println("Robot eliminado en " + location);
        }
    }
 
    public void placeStore(int location, int tenges) {
        if (occupied(location)) {
            if (isVisible) {
                System.out.println("⚠️ No se puede colocar una tienda en " + location + ", ya está ocupado.");
            }
            return;
        }
        Store s = new Store(location, tenges);
        stores.add(s);
        if (isVisible) s.makeVisible();
        System.out.println("Tienda agregada en " + location + " con " + tenges + " tenges.");
    }
 
    public void removeStore(int location) {
        Store toRemove = null;
        for (Store s : stores) {
            if (s.getPosition() == location) {
                toRemove = s;
                break;
            }
        }
        if (toRemove != null) {
            toRemove.makeInvisible();
            stores.remove(toRemove);
            System.out.println("Tienda eliminada en " + location);
        }
    }
 
    public void moveRobot(int location, int meters) {
        for (Robot r : robots) {
            if (r.getPosition() == location) {
                r.move(meters);
                System.out.println("Robot en " + location + " se movió " + meters + " metros.");
                return;
            }
        }
        if (isVisible) {
            System.out.println("⚠️ No existe robot en la posición " + location);
        }
    }
 
    public void resupplyStores() {
        for (Store s : stores) {
            s.resupply();
        }
        System.out.println("Todas las tiendas han sido reabastecidas.");
    }
 
    public void returnRobots() {
        for (Robot r : robots) {
            r.returnToStart();
        }
        System.out.println("Todos los robots han vuelto a su posición inicial.");
    }
 
    public void reboot() {
        resupplyStores();
        returnRobots();
        System.out.println("Ruta de seda reiniciada.");
    }
 
    public int getProfit() {
        return profit; // por ahora fijo en 0
    }
 
    public void makeVisible() {
        isVisible = true;
        for (Robot r : robots) r.makeVisible();
        for (Store s : stores) s.makeVisible();
    }
 
    public void makeInvisible() {
        isVisible = false;
        for (Robot r : robots) r.makeInvisible();
        for (Store s : stores) s.makeInvisible();
    }
 
    public void finish() {
        robots.clear();
        stores.clear();
        System.out.println("Simulador terminado.");
    }
}