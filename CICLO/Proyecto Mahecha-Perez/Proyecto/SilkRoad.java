import java.util.*;
import javax.swing.JOptionPane;
/**
 * Clase principal del sistema SilkRoad.
 * Administra el camino, los robots, las tiendas, la barra de ganancia y las operaciones visuales.
 * Permite agregar, eliminar y mover elementos dentro de una simulación interactiva.
 * 
 * @author Natalia Mahecha y Maria Jose Perez
 * @version 1.0 (Oct 2025)
 */
public class SilkRoad {

    private int length;
    
    ///// Robot

    private Road road;
    private List<Robot> robots = new ArrayList<>();
    private ColorGenerator robotColorGen = new ColorGenerator();
    private boolean visible = false;
    
    ////Tienda
    
    private List<Store> stores = new ArrayList<>();
    private ColorGenerator storeColorGen = new ColorGenerator();
    
    ///// ProfitBar 
    private ProfitBar profitBar;
    
    ///// finish
    private boolean finished = false;
    
    ///// ok
    private boolean ok = true;
    
    
    ///// profit
    private int totalProfit = 0;
    
    /// moverobots
    private Map<Integer, List<Integer>> robotProfitHistory = new HashMap<>();
    int bestLocation = -1;
    
    /// empied store
    private Map<Integer, Integer> emptiedCount = new HashMap<>();

    /**
     * Crea una nueva instancia del sistema SilkRoad con una longitud específica.
     * Inicializa el camino, la barra de ganancia, listas de robots y tiendas.
     * 
     * @param length longitud total del camino (número de posiciones disponibles)
     */
    public SilkRoad(int length) {
        if (!validateLength(length)) return;
    
        this.length = length;
        this.road = new Road(length);
    
        int barWidth = road.getTotalWidth();
        this.profitBar = new ProfitBar(barWidth, 100);
    
        this.robots = new ArrayList<>();
        this.stores = new ArrayList<>();
    
        makeVisible();
    }




    /**
     * Coloca una tienda en una ubicación lógica con una cantidad inicial de tenges.
     * 
     * @param userLocation posición humana (1 a length)
     * @param tenges cantidad inicial de ganancia de la tienda
     */
    public void placeStore(int userLocation, int tenges) {
        if (blockIfFinished("colocar una tienda")) return;

        ok = true;
    
        // Validar ubicación dentro del rango humano
        if (!validatePosition(userLocation)) return;

    
        // Validar que los tenges no sean negativos
        if (!validateTenges(tenges)) return;

    
        // Convertir a índice interno
        int location = userLocation - 1;
    
        // Verificar si esa ubicación esta disponible
        if (!validateFree(location, userLocation)) return;

    
        // Obtener coordenadas visuales desde Road
        int[] pos = road.getVisualPosition(location);
    
        // Crear la tienda con color único, posición visual y tenges iniciales
        Store newStore = new Store(location, storeColorGen, pos[0], pos[1], tenges);
    
        // Agregar a la lista
        stores.add(newStore);
    
        // Mostrar visualmente si el simulador está activo
        if (visible) {
            newStore.makeVisible();
        }
        
        //Recalcular el máximo posible y actualizar la barra
        profitBar.setMaxProfit(calcularGananciaMaximaActual());


    }

    /**
     * Elimina una tienda en una ubicación lógica si existe.
     * 
     * @param userLocation posición humana (1 a length)
     */
    public void removeStore(int userLocation) {
        if (blockIfFinished("eliminar una tienda")) return;

        ok = true;
    
        // Validar ubicación humana (1 a length)
        if (!validatePosition(userLocation)) return;
    
        // Convertir a índice interno
        int location = userLocation - 1;
    
        // Buscar tienda en esa ubicación lógica
        for (Store s : stores) {
            if (s.getLocation() == location) {
                if (visible) {
                    s.makeInvisible();
                }
                stores.remove(s);
                // Recalcular el máximo posible y actualizar la barra
                profitBar.setMaxProfit(calcularGananciaMaximaActual());
                return;
            }
        }
    
        // Si no se encontró ninguna tienda
        ok = false;
        if (visible) {
            JOptionPane.showMessageDialog(null,
                "⚠️ No hay ninguna tienda en la posición " + userLocation + ".");
        }
    }


    /**
     * Coloca un robot en una ubicación lógica del camino.
     * 
     * @param userLocation posición humana (1 a length)
     */    
    public void placeRobot(int userLocation) {
        if (blockIfFinished("colocar un robot")) return;

        
        ok = true;
    
        // Validar que esté dentro del rango humano: 1 a length
        if (!validatePosition(userLocation)) return;
    
        // Convertir a índice interno
        int location = userLocation - 1;
    
        // Verificar si ya hay un robot o tienda en esa ubicación

        if (!validateFree(location, userLocation)) return;

        // Obtener coordenadas visuales desde Road
        int[] pos = road.getVisualPosition(location);
    
        // Crear el robot con color único y posición visual
        Robot newRobot = new Robot(location, length, robotColorGen, pos[0], pos[1]);
    
        // Agregar a la lista
        robots.add(newRobot);
    
        // Mostrar visualmente si está activo
        if (visible) {
            newRobot.makeVisible();
            profitBar.makeVisible(); // ← solo si el sistema está en modo visual
        }
        // Recalcular el máximo posible y actualizar la barra
        profitBar.setMaxProfit(calcularGananciaMaximaActual());
    
    }

    
    /**
     * Elimina un robot en una ubicación lógica si existe.
     * 
     * @param userLocation posición humana (1 a length)
     */
    public void removeRobot(int userLocation) {
        if (blockIfFinished("eliminar un robot")) return;


        ok = true;
    
        // Validar ubicación humana
        if (!validatePosition(userLocation)) return;
    
        // Convertir a índice interno
        int location = userLocation - 1;
    
        // Buscar robot en esa ubicación
        for (Robot r : robots) {
            if (r.getLocation() == location) {
                if (visible) {
                    r.makeInvisible();
                }
                robots.remove(r);
                // 🔄 Recalcular el máximo posible y actualizar la barra
                profitBar.setMaxProfit(calcularGananciaMaximaActual());

                return;
            }
        }
    
        // Si no se encontró ningún robot
        ok = false;
        if (visible) {
            JOptionPane.showMessageDialog(null,
                "⚠️ No hay ningún robot en la posición " + userLocation + ".");
        }
    }

    
    /**
     * Mueve un robot desde su ubicación actual una cantidad de metros.
     * Si hay una tienda en destino, calcula la ganancia y actualiza el sistema.
     * 
     * @param userLocation posición humana del robot (1 a length)
     * @param meters cantidad de posiciones a mover (puede ser negativa)
     */
    public void moveRobot(int userLocation, int meters) {
        if (blockIfFinished("mover un robot")) return;
    
        ok = true;
    
        // Validar ubicación humana
        if (!validatePosition(userLocation)) return;
    
        int location = userLocation - 1;
        Robot target = null;
    
        for (Robot r : robots) {
            if (r.getLocation() == location) {
                target = r;
                break;
            }
        }
    
        if (target == null) {
            ok = false;
            if (visible) {
                JOptionPane.showMessageDialog(null,
                    "⚠️ No hay ningún robot en la posición " + userLocation + ".");
            }
            return;
        }
    
        int destination = location + meters;
    
        // Validar que no se salga del camino
        if (destination < 0 || destination >= length) {
            ok = false;
            if (visible) {
                JOptionPane.showMessageDialog(null,
                    "⚠️ Movimiento inválido: el robot saldría del camino.");
            }
            return;
        }
    
        // Verificar si hay otra tienda o robot en la posición destino
        if (!validateFreeOfRobots(destination, destination + 1)) return;
    
        // Calcular ganancia si hay tienda en destino
        int ganancia = 0;
        for (Store s : stores) {
            if (s.getLocation() == destination && s.getTenges() > 0) {
                ganancia = s.getTenges() - Math.abs(destination - location);
                if (ganancia > 0) {
                    profitBar.addProfit(ganancia);
                    s.setBlack(); // tienda usada
                    s.empty();    // vaciar tienda
                }
                break;
            }
        }
    
        // Actualizar posición lógica
        target.setLocation(destination);
    
        // Actualizar posición visual si está visible
        if (visible) {
            int[] pos = road.getVisualPosition(destination);
            target.moveTo(pos[0], pos[1]);
        }
    
        //  Recalcular el máximo posible y actualizar la barra
        profitBar.setMaxProfit(calcularGananciaMaximaActual());
    }

     /**
     * Restaura todas las tiendas a su estado original: recuperan sus tenges y color.
     * También limpia el registro de tiendas vaciadas.
     */
    public void resupplyStores() {
        if (blockIfFinished("retornar las tiendas")) return;
    
        for (Store s : stores) {
            s.resupply();       // restaurar tenges
            s.restoreColor();   // restaurar color visual original
        }
    
        emptiedCount.clear();   // permitir que las tiendas vuelvan a ser usadas4
        // Recalcular el máximo posible y actualizar la barra
        profitBar.setMaxProfit(calcularGananciaMaximaActual());
    }

    /**
     * Devuelve todos los robots a su posición inicial.
     * Si el sistema está en modo visual, actualiza su posición en pantalla.
     */
    public void returnRobots() {
        if (blockIfFinished("retornar los robots a su posicion inicial")) return;

        for (Robot r : robots) {
            int original = r.getInitialLocation();
            r.setLocation(original);
    
            if (visible) {
                int[] pos = road.getVisualPosition(original);
                r.moveTo(pos[0], pos[1]);
            }
        }
        // Recalcular el máximo posible y actualizar la barra
        profitBar.setMaxProfit(calcularGananciaMaximaActual());

    }

    
    /**
     * Reinicia el sistema: recarga tiendas, devuelve robots y actualiza la barra de ganancia.
     */
    public void reboot() {
        if (blockIfFinished("reiniciar la ruta de seda")) return;

        resupplyStores();  // ← recarga las tiendas con sus tenges originales
        returnRobots();    // ← devuelve los robots a su posición inicial
        // Recalcular el máximo posible y actualizar la barra
        profitBar.setMaxProfit(calcularGananciaMaximaActual());

    }

    /**
     * Devuelve la ganancia total acumulada por los robots.
     * 
     * @return cantidad total de tenges recolectados
     */
    public int profit() {
        return totalProfit;
    }

    /**
     * Devuelve un arreglo con la información de todas las tiendas.
     * Cada fila contiene: [posición humana, tenges actuales].
     * 
     * @return arreglo bidimensional con datos de tiendas
     */
    public int[][] stores() {
        // Crear copia ordenada por ubicación
        List<Store> ordered = new ArrayList<>(stores);
        ordered.sort(Comparator.comparingInt(Store::getLocation));
    
        // Construir arreglo bidimensional
        int[][] result = new int[ordered.size()][2];
        for (int i = 0; i < ordered.size(); i++) {
            Store s = ordered.get(i);
            result[i][0] = s.getLocation() + 1; // convertir a ubicación humana
            result[i][1] = s.getTenges();
        }
    
        return result;
    }   

    /**
     * Devuelve un arreglo con la información de todos los robots.
     * Cada fila contiene: [posición humana, tenges recolectados (aún no calculado)].
     * 
     * @return arreglo bidimensional con datos de robots
     */
    public int[][] robots() {
        // Crear copia ordenada por ubicación
        List<Robot> ordered = new ArrayList<>(robots);
        ordered.sort(Comparator.comparingInt(Robot::getLocation));
    
        // Construir arreglo bidimensional
        int[][] result = new int[ordered.size()][2];
        for (int i = 0; i < ordered.size(); i++) {
            Robot r = ordered.get(i);
            result[i][0] = r.getLocation() + 1; // ubicación humana
            result[i][1] = 0; // aún no se calcula tenges recolectados
        }
    
        return result;
    }

    /**
     * Activa el modo visual del sistema y muestra todos los elementos en pantalla.
     */

    public void makeVisible() {
        visible = true;
    
        // Mostrar camino
        if (road != null) {
            road.makeVisible(); // ← nuevo método en Road
        }
    
        // Mostrar barra de ganancia
        if (profitBar != null) {
            profitBar.makeVisible();
        }
    
        // Mostrar tiendas
        for (Store s : stores) {
            s.makeVisible();
        }
    
        // Mostrar robots
        for (Robot r : robots) {
            r.makeVisible();
        }
    }   
    
    /**
     * Oculta todos los elementos visuales del sistema.
     */
    public void makeInvisible() {
        visible = false;
    
        // Ocultar camino
        if (road != null) {
            road.makeInvisible(); 
        }
    
        // Ocultar barra de ganancia
        if (profitBar != null) {
            profitBar.makeInvisible();
        }
    
        // Ocultar tiendas
        for (Store s : stores) {
            s.makeInvisible();
        }
    
        // Ocultar robots
        for (Robot r : robots) {
            r.makeInvisible();
        }
    }

    
    /**
     * Finaliza la simulación. Muestra mensaje de cierre y oculta todos los elementos.
     */
    public void finish() {
        finished = true;
    
        if (visible) {
            JOptionPane.showMessageDialog(null,
                "✅ La simulación ha terminado.\nYa no se pueden realizar más acciones.");
        }
    
        makeInvisible(); 
    }

     /**
     * Indica si la última operación fue válida y si la simulación no ha terminado.
     * 
     * @return true si el sistema está activo y sin errores
     */
    public boolean ok() {
        return ok && !finished;
    }
    
    
    ///////////////// Metodos privados
     /**
     * Valida que la longitud del camino esté dentro del rango permitido (4 a 100).
     * 
     * @param length longitud solicitada
     * @return true si es válida, false si no
     */
    private boolean validateLength(int length) {
        if (length < 4) {
            if (visible) {
                JOptionPane.showMessageDialog(null, "La longitud mínima del camino debe ser 4.");
            }
            return false;
        }
        if (length > 100) {
            if (visible) {
                JOptionPane.showMessageDialog(null, "La longitud máxima permitida es 100.");
            }
            return false;
        }
        return true;
    }
    
        /**
     * Valida que una ubicación humana esté dentro del rango permitido (1 a length).
     * 
     * @param userLocation posición humana
     * @return true si es válida, false si no
     */
    private boolean validatePosition(int userLocation) {
        if (userLocation < 1 || userLocation > length) {
            ok = false;
            if (visible) {
                JOptionPane.showMessageDialog(null,
                    "⚠️ Ubicación inválida: " + userLocation +
                    "\nDebe estar entre 1 y " + length + ".");
            }
            return false;
        }
        return true;
    }

        /**
     * Valida que la cantidad de tenges esté entre 1 y 100.
     * 
     * @param tenges cantidad de tenges a validar
     * @return true si es válida, false si no
     */
    private boolean validateTenges(int tenges) {
        if (tenges <= 0 || tenges > 100) {
            ok = false;
            if (visible) {
                JOptionPane.showMessageDialog(null,
                    "⚠️ La cantidad de tenges debe estar entre 1 y 100.");
            }
            return false;
        }
        return true;
    }
    
    /**
     * Verifica que una posición esté libre de robots y tiendas.
     * 
     * @param location índice interno
     * @param userLocation posición humana (para mensajes)
     * @return true si está libre, false si está ocupada
     */
    private boolean validateFree(int location, int userLocation) {
        for (Robot r : robots) {
            if (r.getLocation() == location) {
                ok = false;
                if (visible) {
                    JOptionPane.showMessageDialog(null,
                        "⚠️ La posición " + userLocation + " ya está ocupada por un robot.");
                }
                return false;
            }
        }
        for (Store s : stores) {
            if (s.getLocation() == location) {
                ok = false;
                if (visible) {
                    JOptionPane.showMessageDialog(null,
                        "⚠️ La posición " + userLocation + " ya está ocupada por una tienda.");
                }
                return false;
            }
        }
        return true;
    }
    
    /**
     * Bloquea la acción si la simulación ya ha finalizado.
     * 
     * @param actionName nombre de la acción que se intenta realizar
     * @return true si está bloqueado, false si se puede continuar
     */
    private boolean blockIfFinished(String actionName) {
        if (finished) {
            ok = false;
            if (visible) {
                JOptionPane.showMessageDialog(null,
                    "⚠️ La simulación ya ha terminado.\nNo se puede " + actionName + ".");
            }
            return true;
        }
        return false;
    }
    
    /**
     * Calcula la ganancia máxima posible en el estado actual del sistema.
     * 
     * @return suma de las mejores ganancias posibles por robot
     */
    private int calcularGananciaMaximaActual() {
        int total = 0;
        for (Robot r : robots) {
            int mejor = 0;
            for (Store s : stores) {
                int distancia = Math.abs(s.getLocation() - r.getLocation());
                int ganancia = s.getTenges() - distancia;
                if (ganancia > mejor) {
                    mejor = ganancia;
                }
            }
            total += mejor;
        }
        return Math.max(1, total); // evita división por cero
    }
    
    /**
     * Verifica que una posición esté libre de otros robots.
     * 
     * @param location índice interno
     * @param userLocation posición humana (para mensajes)
     * @return true si no hay robot, false si está ocupado
     */
    private boolean validateFreeOfRobots(int location, int userLocation) {
        for (Robot r : robots) {
            if (r.getLocation() == location) {
                ok = false;
                if (visible) {
                    JOptionPane.showMessageDialog(null,
                        "⚠️ Ya hay un robot en la posición " + userLocation + ".");
                }
                return false;
            }
        }
        return true;
    }
    
    ////// Ciclo 2
    
    /**
     * Constructor alternativo que recibe directamente los días de simulación.
     * Calcula la longitud máxima y configura el sistema visual.
     * 
     * @param days arreglo de días con acciones a simular
     */
    public SilkRoad(int[][] days) {
        makeVisible();
    
        // Calcular la longitud máxima del camino
        int maxLocation = 0;
        for (int[] day : days) {
            int location = day[1];
            maxLocation = Math.max(maxLocation, location);
        }
    
        // Validar longitud
        if (!validateLength(maxLocation)) return;
    
        // Inicializar componentes visuales
        this.length = maxLocation;
        this.road = new Road(length);
        this.profitBar = new ProfitBar(road.getTotalWidth(), 100);
        this.robots = new ArrayList<>();
        this.stores = new ArrayList<>();
        this.robotColorGen = new ColorGenerator();
        this.storeColorGen = new ColorGenerator();
    
        makeVisible(); // Mostrar visualmente el camino
    
    }

    
    /**
     * Mueve todos los robots buscando la tienda que les dé mayor ganancia.
     * Registra las ganancias, vacía tiendas y actualiza visualmente al mejor robot.
     */
    public void moveRobots() {
        boolean[] visited = new boolean[length]; // marcas de tiendas ya usadas
    
        for (Robot robot : robots) {
            int bestLocation = -1;
            int maxProfit = Integer.MIN_VALUE;
            Store bestStore = null;
    
            for (Store store : stores) {
                int storeLoc = store.getLocation();
    
                // Evitar tiendas ya desocupadas
                if (emptiedCount.containsKey(storeLoc)) continue;
    
                // Evitar solapamiento visual
                if (visited[storeLoc]) continue;
    
                int distance = Math.abs(storeLoc - robot.getLocation());
                int profit = store.getTenges() - distance;
    
                if (profit > maxProfit) {
                    maxProfit = profit;
                    bestLocation = storeLoc;
                    bestStore = store;
                }
            }
    
            if (bestLocation != -1 && maxProfit > 0) {
                int meters = bestLocation - robot.getLocation();
                int robotLoc = robot.getLocation();
    
                // Registrar ganancia por movimiento
                if (!robotProfitHistory.containsKey(robotLoc)) {
                    robotProfitHistory.put(robotLoc, new ArrayList<>());
                }
                robotProfitHistory.get(robotLoc).add(maxProfit);
    
                // Registrar tienda desocupada
                if (!emptiedCount.containsKey(bestLocation)) {
                    emptiedCount.put(bestLocation, 1);
                } else {
                    int count = emptiedCount.get(bestLocation);
                    emptiedCount.put(bestLocation, count + 1);
                }
    
                // Cambiar color visual a negro
                if (visible && bestStore != null) {
                    bestStore.setBlack();
                }
    
                // Marcar tienda como visitada en esta ronda
                visited[bestLocation] = true;
    
                // Mover robot
                moveRobot(robotLoc + 1, meters); // interfaz humana
            }
        }
        // Encontrar el robot con mayor ganancia total
        int maxTotal = Integer.MIN_VALUE;
        Robot bestRobot = null;
        
        for (Robot r : robots) {
            int loc = r.getLocation();
            List<Integer> profits = robotProfitHistory.get(loc);
            if (profits == null) continue;
        
            int total = 0;
            for (int p : profits) {
                total += p;
            }
        
            if (total > maxTotal) {
                maxTotal = total;
                bestRobot = r;
            }
        }
        
        // Parpadear visualmente
        if (visible && bestRobot != null) {
            final Robot finalBestRobot = bestRobot;
        
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                int count = 0;
                boolean visibleState = true;
        
                public void run() {
                    if (count >= 6) {
                        finalBestRobot.makeVisible();
                        timer.cancel();
                        return;
                    }
        
                    if (visibleState) {
                        finalBestRobot.makeInvisible();
                    } else {
                        finalBestRobot.makeVisible();
                    }
        
                    visibleState = !visibleState;
                    count++;
                }
            }, 0, 200);
        }
    }
    
    /**
     * Devuelve cuántas veces ha sido vaciada cada tienda.
     * 
     * @return matriz [ubicación, veces vaciada]
     */
    public int[][] emptiedStores() {
        // Crear lista de ubicaciones
        List<Integer> locations = new ArrayList<>();
        for (Integer loc : emptiedCount.keySet()) {
            locations.add(loc);
        }
    
        // Ordenar ubicaciones de menor a mayor
        Collections.sort(locations);
    
        // Construir matriz de resultados
        int[][] result = new int[locations.size()][2];
        for (int i = 0; i < locations.size(); i++) {
            int loc = locations.get(i);
            int times = emptiedCount.get(loc);
    
            result[i][0] = loc;
            result[i][1] = times;
        }
    
        return result;
    }
    

    /**
     * Devuelve las ganancias obtenidas por cada robot en cada movimiento.
     * 
     * @return matriz [ubicación, ganancia1, ganancia2, ...]
     */
    public int[][] profitPerMove() {
        // Crear lista de ubicaciones únicas
        List<Integer> locations = new ArrayList<>();
        for (Robot r : robots) {
            int loc = r.getLocation();
            if (!locations.contains(loc)) {
                locations.add(loc);
            }
        }
    
        // Ordenar ubicaciones
        Collections.sort(locations);
    
        // Construir matriz de resultados
        int[][] result = new int[locations.size()][];
        for (int i = 0; i < locations.size(); i++) {
            int loc = locations.get(i);
            List<Integer> profits = robotProfitHistory.get(loc);
    
            if (profits == null) {
                profits = new ArrayList<>();
            }
    
            result[i] = new int[profits.size() + 1];
            result[i][0] = loc;
            for (int j = 0; j < profits.size(); j++) {
                result[i][j + 1] = profits.get(j);
            }
        }
    
        return result;
    }
    
}



