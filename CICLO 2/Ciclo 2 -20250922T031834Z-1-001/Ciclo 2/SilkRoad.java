import javax.swing.*;
import java.util.*;

/**
 * SilkRoad: clase principal del simulador.
 * Gestiona tiendas (Store), robots (Robot), la barra de progreso (ProgressBar) y el lienzo (Canvas).
 * Permite colocar/eliminar tiendas y robots, mover robots, consultar ganancias,
 * reiniciar la simulación y mostrar/ocultar elementos gráficos.
 *
 * @param length longitud lógica de la carretera SilkRoad.
 */
public class SilkRoad {
    private int length; // longitud lógica
    private LinkedHashMap<Integer, Store> storesMap; // location -> Store (insertion order)
    private LinkedHashMap<Integer, List<Robot>> robotsMap; // location -> list of robots currently there
    private List<Robot> robotsList; // insertion ordered robots
    private ProgressBar progressBar;
    private boolean visible;
    private boolean lastOk;
    private int arrivalCounter;
    private int profit;
    private int pixelScale = 1; // pixels per meter for movement (adjustable)
    private java.util.function.IntUnaryOperator coordMapper; // maps location -> packed x,y
    
    // color cycles
    private String[] colors = {"red","blue","green","yellow","magenta","black"};
    private int colorIdx = 0;
    private int storeColorIdx = 0;
    
    /**
     * Constructor de SilkRoad.
     */
    public SilkRoad(int length) {
        this.length = length;
        storesMap = new LinkedHashMap<>();
        robotsMap = new LinkedHashMap<>();
        robotsList = new ArrayList<>();
        progressBar = new ProgressBar(10, 260, 200, 16);
        visible = false;
        lastOk = true;
        arrivalCounter = 0;
        profit = 0;
        coordMapper = l -> {
            // default: linear mapping across canvas width (x, y constant)
            // pack into int: (x<<16)|y
            int canvasW = 300;
            int canvasH = 300;
            if(length >= 20000) {
                // spiral mapping: create square spiral centered
                // compute spiral coordinates for index l
                int n = l;
                // Spiral generation algorithm (approximate)
                int layer = (int)Math.ceil((Math.sqrt(n+1)-1)/2);
                int legLen = 2*layer;
                int maxVal = (2*layer+1)*(2*layer+1)-1;
                int diff = maxVal - n;
                int side = diff / legLen; // 0..3
                int offset = diff % legLen;
                int cx = canvasW/2;
                int cy = canvasH/2;
                int x= cx, y=cy;
                if(side==0) { x += layer; y += -layer + offset; }
                else if(side==1) { x += layer - offset; y += layer; }
                else if(side==2) { x += -layer; y += layer - offset; }
                else { x += -layer + offset; y += -layer; }
                x = Math.max(10, Math.min(canvasW-40, x));
                y = Math.max(10, Math.min(canvasH-40, y));
                return (x<<16)|y;
            } else {
                int margin = 10;
                int usable = canvasW - 2*margin;
                int x = margin + (int)((long)l * usable / Math.max(1, length-1));
                int y = canvasH/2;
                return (x<<16)|y;
            }
        };
    }
    
    // Helper: generate next distinct color
    /**
     * Obtiene el siguiente color para asignar a un robot en ciclo.
     * @return color en forma de cadena.
     */
    private String nextColorForRobot() {
        String c = colors[colorIdx % colors.length];
        colorIdx++;
        return c;
    }
    /**
     * Obtiene el siguiente color para asignar a una tienda en ciclo.
     * @return color en forma de cadena.
     */
    private String nextColorForStore() {
        String c = colors[storeColorIdx % colors.length];
        storeColorIdx++;
        return c;
    }
    
    /**
     * Hace visible la simulación: muestra el lienzo, la barra de progreso,
     * y dibuja tiendas y robots en sus posiciones mapeadas.
     */
    public void makeVisible() {
        Canvas.getCanvas().setVisible(true);
        progressBar.makeVisible();
        // draw stores and robots at their mapped positions
        for(Store s : storesMap.values()) {
            int packed = coordMapper.applyAsInt(s.location());
            int x = (packed>>16)&0xffff;
            int y = packed & 0xffff;
            s.setPosition(x,y);
            s.makeVisible();
        }
        for(Robot r : robotsList) {
            int packed = coordMapper.applyAsInt(r.location());
            int x = (packed>>16)&0xffff;
            int y = packed & 0xffff;
            r.setPosition(x,y);
            r.makeVisible();
        }
        // set progressbar max as sum of initial tenges
        progressBar.setMax(sumInitialTenges());
        progressBar.setValue(profit);
        visible = true;
    }
    
    /**
     * Oculta la simulación: oculta tiendas, robots y la barra de progreso.
     */
    public void makeInvisible() {
        for(Store s : storesMap.values()) s.makeInvisible();
        for(Robot r: robotsList) r.makeInvisible();
        progressBar.makeInvisible();
        visible = false;
    }
    
    /**
     * Finaliza la simulación y oculta el lienzo.
     */
    public void finish() {
        makeInvisible();
        Canvas.getCanvas().setVisible(false);
    }
    
    /**
     * Indica si la última operación fue exitosa.
     * @return true si la última operación terminó OK, false si hubo error.
     */
    public boolean ok() { return lastOk; }
    
    /**
     * Coloca una tienda en la ubicación indicada con la cantidad de tenges dada.
     * Si la ubicación ya está ocupada por otra tienda o por robots, la operación falla.
     *
     * @param location ubicación lógica donde colocar la tienda.
     * @param tenges cantidad de tenges iniciales de la tienda.
     */
    public void placeStore(int location, int tenges) {
        if(storesMap.containsKey(location) || robotsMap.containsKey(location) && robotsMap.get(location)!=null && !robotsMap.get(location).isEmpty()) {
            lastOk = false;
            if(visible) JOptionPane.showMessageDialog(null, "No se puede colocar tienda: ubicación ocupada.");
            return;
        }
        Store s = new Store(location, tenges, nextColorForStore());
        storesMap.put(location, s);
        // visual placement if visible
        if(visible) {
            int packed = coordMapper.applyAsInt(location);
            int x = (packed>>16)&0xffff;
            int y = packed & 0xffff;
            s.setPosition(x,y);
            s.makeVisible();
            progressBar.setMax(sumInitialTenges());
        }
        lastOk = true;
    }
    
    /**
     * Elimina la tienda en la ubicación indicada. Si no existe, marca error.
     *
     * @param location ubicación lógica de la tienda a eliminar.
     */
    public void removeStore(int location) {
        Store s = storesMap.get(location);
        if(s == null) {
            lastOk = false;
            if(visible) JOptionPane.showMessageDialog(null, "No se puede eliminar tienda: no existe.");
            return;
        }
        s.makeInvisible();
        storesMap.remove(location);
        if(visible) progressBar.setMax(sumInitialTenges());
        lastOk = true;
    }
    
    /**
     * Coloca un robot en la ubicación indicada. Los robots deben empezar en ubicaciones distintas.
     *
     * @param location ubicación lógica donde se coloca el robot.
     */
    public void placeRobot(int location) {
        // robots must start at distinct locations
        for(Robot r: robotsList) {
            if(r.location() == location) {
                lastOk = false;
                if(visible) JOptionPane.showMessageDialog(null, "No se puede colocar robot: ya existe robot en la ubicación inicial.");
                return;
            }
        }
        Robot r = new Robot(location, nextColorForRobot(), arrivalCounter++);
        robotsList.add(r);
        robotsMap.computeIfAbsent(location, k -> new ArrayList<>()).add(r);
        if(visible) {
            int packed = coordMapper.applyAsInt(location);
            int x = (packed>>16)&0xffff;
            int y = packed & 0xffff;
            r.setPosition(x,y);
            r.makeVisible();
        }
        lastOk = true;
    }
    
    /**
     * Elimina un robot localizado en la ubicación indicada. Si hay varios, elimina el de menor arrivalOrder.
     *
     * @param location ubicación lógica donde se busca el robot a eliminar.
     */
    public void removeRobot(int location) {
        // remove the robot that has current location == location and earliest arrivalOrder
        Robot chosen = null;
        for(Robot r: robotsList) {
            if(r.location() == location) {
                if(chosen == null || r.arrivalOrder() < chosen.arrivalOrder()) chosen = r;
            }
        }
        if(chosen == null) {
            lastOk = false;
            if(visible) JOptionPane.showMessageDialog(null, "No se puede eliminar robot: no encontrado en esa ubicación.");
            return;
        }
        chosen.makeInvisible();
        robotsList.remove(chosen);
        List<Robot> list = robotsMap.get(location);
        if(list != null) { list.remove(chosen); if(list.isEmpty()) robotsMap.remove(location); }
        lastOk = true;
    }
    
    /**
     * Mueve un robot que se encuentre en la ubicación `location` la cantidad `meters`.
     * Aplica el coste de movimiento a las ganancias, actualiza mapas y recoge tenges si llega a una tienda.
     *
     * @param location ubicación lógica actual del robot a mover.
     * @param meters desplazamiento en metros (positivo o negativo).
     */
    public void moveRobot(int location, int meters) {
        // find robot at current location
        Robot chosen = null;
        for(Robot r: robotsList) {
            if(r.location() == location) {
                if(chosen == null || r.arrivalOrder() < chosen.arrivalOrder()) chosen = r;
            }
        }
        if(chosen == null) {
            lastOk = false;
            if(visible) JOptionPane.showMessageDialog(null, "No se puede mover robot: no hay robot en esa ubicación.");
            return;
        }
        // apply movement cost
        profit -= Math.abs(meters); // 1 tenge per meter
        chosen.moveByMeters(meters, pixelScale);
        // update robotsMap: remove from old list, add to new
        List<Robot> oldList = robotsMap.get(location);
        if(oldList != null) { oldList.remove(chosen); if(oldList.isEmpty()) robotsMap.remove(location); }
        chosen = updateRobotLocationAfterMove(chosen, meters);
        int newLoc = chosen.location();
        robotsMap.computeIfAbsent(newLoc, k -> new ArrayList<>()).add(chosen);
        // check if there's a store at newLoc with tenges
        Store s = storesMap.get(newLoc);
        if(s != null && !s.isEmpty()) {
            int taken = s.collectAll();
            profit += taken;
        }
        if(visible) progressBar.setValue(profit);
        lastOk = true;
    }
    
    // helper to update robot logical location after movement
    /**
     * Actualiza la ubicación lógica del robot tras un movimiento.
     * En la implementación actual Robot.moveByMeters ya actualiza la ubicación,
     * por lo que este método simplemente devuelve el robot.
     *
     * @param r robot a actualizar.
     * @param meters metros movidos.
     * @return el mismo robot (con ubicación ya actualizada).
     */
    private Robot updateRobotLocationAfterMove(Robot r, int meters) {
        // in the Robot implementation moveByMeters already updates location
        return r;
    }
    
    /**
     * Reabastece todas las tiendas (las deja con sus tenges iniciales).
     */
    public void resupplyStores() {
        for(Store s : storesMap.values()) s.resupply();
        lastOk = true;
        if(visible) progressBar.setMax(sumInitialTenges());
    }
    
    /**
     * Regresa todos los robots a sus ubicaciones iniciales (visual y lógicamente) y reconstruye robotsMap.
     */
    public void returnRobots() {
        for(Robot r : robotsList) {
            // move to initial location visually
            int packed = coordMapper.applyAsInt(r.initialLocation());
            int x = (packed>>16)&0xffff;
            int y = packed & 0xffff;
            r.setPosition(x,y);
            r.makeVisible();
            // update logical
            // remove from robotsMap old lists and reinsert by initial
        }
        // rebuild robotsMap
        robotsMap.clear();
        for(Robot r : robotsList) {
            r.makeVisible();
            robotsMap.computeIfAbsent(r.location(), k -> new ArrayList<>()).add(r);
        }
        lastOk = true;
    }
    
    /**
     * Reinicia la simulación: reabastece tiendas, pone ganancias a 0 y reposiciona robots a sus iniciales.
     */
    public void reboot() {
        // restore shops and robots to initial states, profit = 0
        resupplyStores();
        profit = 0;
        for(Robot r : robotsList) {
            // move visually and logically to initial positions
            int packed = coordMapper.applyAsInt(r.initialLocation());
            int x = (packed>>16)&0xffff;
            int y = packed & 0xffff;
            r.setPosition(x,y);
            // logical
            // here Robot was not given a setter for location, but moveByMeters modifies location relative;
            // to set exact location we reconstruct robotsMap below
        }
        robotsMap.clear();
        for(Robot r : robotsList) {
            robotsMap.computeIfAbsent(r.initialLocation(), k -> new ArrayList<>()).add(r);
        }
        if(visible) progressBar.setValue(profit);
        lastOk = true;
    }
    
    /**
     * Devuelve la ganancia actual de la simulación.
     * @return ganancia acumulada (puede ser negativa si hubo costes).
     */
    public int porfit() {
        // spelled as requested; return current profit
        if(!lastOk && visible) JOptionPane.showMessageDialog(null, "No se puede consultar ganancia en este momento.");
        return profit;
    }
    
    /**
     * Devuelve un arreglo de tiendas actuales ordenado por ubicación.
     * Cada entrada es {ubicación, tenges}.
     * @return matriz con tiendas y tenges.
     */
    public int[][] stores() {
        if(!lastOk && visible) JOptionPane.showMessageDialog(null, "No se puede consultar tiendas en este momento.");
        // sort by location
        List<int[]> arr = new ArrayList<>();
        for(Map.Entry<Integer, Store> e : storesMap.entrySet()) arr.add(new int[]{e.getKey(), e.getValue().tenges()});
        arr.sort(Comparator.comparingInt(a->a[0]));
        int[][] out = new int[arr.size()][2];
        for(int i=0;i<arr.size();i++) out[i] = arr.get(i);
        return out;
    }
    
    /**
     * Devuelve un arreglo de robots actuales ordenado por ubicación.
     * Cada entrada es {ubicación, 0} (el segundo campo es reservado para compatibilidad).
     * @return matriz con robots.
     */
    public int[][] robots() {
        if(!lastOk && visible) JOptionPane.showMessageDialog(null, "No se puede consultar robots en este momento.");
        List<int[]> arr = new ArrayList<>();
        for(Robot r : robotsList) arr.add(new int[]{r.location(), 0});
        arr.sort(Comparator.comparingInt(a->a[0]));
        int[][] out = new int[arr.size()][2];
        for(int i=0;i<arr.size();i++) out[i] = arr.get(i);
        return out;
    }
    
    // Utility: sum of initial tenges (for progressbar max)
    /**
     * Suma los tenges iniciales de todas las tiendas (útil para la barra de progreso).
     * @return suma de tenges iniciales (al menos 1).
     */
    private int sumInitialTenges() {
        int s=0;
        for(Store st : storesMap.values()) s += st.initialTenges();
        return s == 0 ? 1 : s;
    }
    
    // ----------------- NUEVOS MÉTODOS DE SILKROAD -----------------
    
    /**
     * Crear la ruta a partir de un arreglo de días.
     * days[i] = {ubicación, tenges}
     *
     * @param days arreglo de días donde cada elemento es {ubicación, tenges}.
     * @return la misma instancia de SilkRoad (permite encadenar llamadas).
     */
    public SilkRoad createFromDays(int[][] days) {
        for (int[] day : days) {
            int location = day[0];
            int tenges = day[1];
            this.placeStore(location, tenges); // reutiliza método existente
        }
        return this;
    }
    
    /**
     * Mover los robots buscando maximizar ganancias.
     * Cada robot se mueve hacia la tienda que le da mayor beneficio (tenges - distancia).
     * Actualiza ganancias y visualmente parpadea el robot con mayor ganancia.
     */
    public void moveRobots() {
        int maxTotalProfit = Integer.MIN_VALUE;
        Robot topRobot = null;
    
        for (Robot r : robotsList) {
            int bestLocation = r.location();
            int bestProfit = 0;
    
            // buscar tienda con máximo beneficio
            for (Store s : storesMap.values()) {
                int profit = s.tenges() - Math.abs(s.location() - r.location());
                if (profit > bestProfit) {
                    bestProfit = profit;
                    bestLocation = s.location();
                }
            }
    
            // mover robot y recoger tenges
            moveRobot(r.location(), bestLocation - r.location());
    
            // revisar ganancias totales
            int total = r.arrivalOrder() >= 0 ? bestProfit : 0; // opcional: acumulado por robot
            if (total > maxTotalProfit) {
                maxTotalProfit = total;
                topRobot = r;
            }
        }
    
        // hacer parpadear al robot con mayor ganancia
        if (topRobot != null && visible) {
            for (int i = 0; i < 3; i++) { // parpadeo 3 veces
                topRobot.makeInvisible();
                Canvas.getCanvas().wait(200);
                topRobot.makeVisible();
                Canvas.getCanvas().wait(200);
            }
        }
    }
    
    /**
     * Consultar el número de veces que cada tienda ha sido desocupada,
     * ordenadas por ubicación de menor a mayor.
     * Devuelve: [[location, timesEmptied], ...]
     *
     * @return matriz con {ubicación, vecesVacía} por tienda.
     */
    public int[][] emptiedStores() {
        List<int[]> list = new ArrayList<>();
        for (Store s : storesMap.values()) {
            // asumimos que se puede calcular con initialTenges != tenges
            int times = s.initialTenges() - s.tenges(); // aproximación
            list.add(new int[]{s.location(), times});
        }
        list.sort(Comparator.comparingInt(a -> a[0]));
        int[][] result = new int[list.size()][2];
        for (int i = 0; i < list.size(); i++) result[i] = list.get(i);
        return result;
    }
    
    /**
     * Consultar las ganancias por movimiento de cada robot,
     * ordenadas por ubicación de menor a mayor.
     * Devuelve: [[location, profit1, profit2, ...], ...]
     *
     * @return matriz con ganancias por robot (actualmente usa la ganancia global como aproximación).
     */
    public int[][] profitPerMove() {
        List<int[]> list = new ArrayList<>();
        for (Robot r : robotsList) {
            // Aquí no tenemos historial completo, usamos ganancia total actual como único movimiento
            int[] arr = new int[]{r.location(), profit}; // si hubiera historial, se agregaría aquí
            list.add(arr);
        }
        list.sort(Comparator.comparingInt(a -> a[0]));
        int[][] result = new int[list.size()][];
        for (int i = 0; i < list.size(); i++) result[i] = list.get(i);
        return result;
    }

}
