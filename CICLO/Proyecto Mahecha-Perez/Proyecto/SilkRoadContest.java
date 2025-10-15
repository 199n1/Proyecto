import java.util.*;

/**
 * Clase encargada de resolver la maratón SilkRoad sin visualización
 * y de simular paso a paso la solución usando el sistema visual SilkRoad.
 * 
 * @author Natalia Mahecha y Maria Jose Perez
 * @version 1.0 (Oct 2025)
 */
public class SilkRoadContest {

    /**
     * Resuelve la maratón SilkRoad sin usar visualización.
     * Coloca robots y tiendas según los días, y calcula los movimientos óptimos
     * para maximizar la ganancia (tenges - distancia).
     * 
     * @param days arreglo de días, donde cada día contiene tipo, ubicación y opcionalmente tenges
     * @return arreglo plano con movimientos: [ubicaciónRobot, desplazamiento], por cada robot que se mueve
     */
    public int[] solve(int[][] days) {
        List<Integer> result = new ArrayList<>();
        List<Integer> robots = new ArrayList<>();
        Map<Integer, Integer> stores = new HashMap<>();
        Set<Integer> usedStores = new HashSet<>();
    
        for (int[] day : days) {
            int type = day[0];
            int location = day[1];
    
            if (type == 1) {
                // Día: colocar robot
                robots.add(location);
            } else if (type == 2 && day.length == 3) {
                // Día: colocar tienda
                int tenges = day[2];
                stores.put(location, tenges);
            }
    
            // Evaluar si hay posibilidad de ganancia
            boolean moved = false;
            boolean[] visited = new boolean[101];
    
            for (int i = 0; i < robots.size(); i++) {
                int robotLoc = robots.get(i);
                int bestLoc = -1;
                int maxProfit = Integer.MIN_VALUE;
    
                for (Map.Entry<Integer, Integer> entry : stores.entrySet()) {
                    int storeLoc = entry.getKey();
                    int tenges = entry.getValue();
    
                    if (usedStores.contains(storeLoc)) continue;
                    if (visited[storeLoc]) continue;
    
                    int distance = Math.abs(storeLoc - robotLoc);
                    int profit = tenges - distance;
    
                    if (profit > maxProfit) {
                        maxProfit = profit;
                        bestLoc = storeLoc;
                    }
                }
    
                if (bestLoc != -1 && maxProfit > 0) {
                    result.add(robotLoc);
                    result.add(bestLoc - robotLoc);
                    usedStores.add(bestLoc);
                    visited[bestLoc] = true;
                    moved = true;
                }
            }
        }
    
        // Convertir a arreglo
        int[] moves = new int[result.size()];
        for (int i = 0; i < result.size(); i++) {
            moves[i] = result.get(i);
        }
    
        return moves;
    }

    /**
     * Simula la maratón SilkRoad usando el sistema visual.
     * Coloca robots y tiendas en pantalla, y ejecuta movimientos con animación.
     * 
     * @param days arreglo de días con acciones a realizar
     * @param slow si es true, cada día se simula con mayor duración (3s); si es false, con duración estándar (1s)
     */
    public void simulate(int[][] days, boolean slow) {
        int delayBase = slow ? 3000 : 1000; // cada día dura 3s si es lento
    
        // Paso 1: calcular longitud máxima
        int maxLocation = 0;
        for (int[] day : days) {
            maxLocation = Math.max(maxLocation, day[1]);
        }
    
        // Paso 2: crear simulador visual
        SilkRoad sim = new SilkRoad(maxLocation);
    
        Timer timer = new Timer();
    
        for (int i = 0; i < days.length; i++) {
            int[] day = days[i];
            int type = day[0];
            int location = day[1];
            int tenges = (day.length == 3) ? day[2] : 0;
    
            int delay = i * delayBase;
    
            // Día i: agregar objeto
            timer.schedule(new TimerTask() {
                public void run() {
                    sim.reboot(); // ← reinicia visualmente cada día
    
                    if (type == 1) {
                        sim.placeRobot(location);
                    } else if (type == 2 && day.length == 3) {
                        sim.placeStore(location, tenges);
                    }
    
                    // Si ya hay al menos un robot y una tienda, mover
                    if (sim.robots().length > 0 && sim.stores().length > 0) {
                        sim.moveRobots(); // ← calcula y ejecuta movimientos
                    }
                }
            }, delay);
        }
    
        // Finalizar simulación después del último día
        int totalDelay = days.length * delayBase + 500;
        timer.schedule(new TimerTask() {
            public void run() {
                sim.finish();
            }
        }, totalDelay);
    }
}

