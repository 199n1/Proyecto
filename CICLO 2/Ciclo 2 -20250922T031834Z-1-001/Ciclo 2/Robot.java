/**
 * Clase Robot: encapsula la representación y estado de un robot dentro de la simulación.
 * Cada robot tiene una ubicación inicial, una ubicación actual, un color y un círculo como representación visual.
 * También puede moverse, volver a su posición inicial y hacerse visible o invisible en el lienzo.
 *
 * @param location ubicación inicial lógica del robot.
 * @param color color del robot (para la visualización).
 * @param arrivalOrder número que indica el orden de llegada (para desempates).
 */
public class Robot {
    private int initialLocation;   // ubicación inicial del robot
    private int location;          // ubicación actual del robot
    private Circle shape;          // representación visual del robot
    private String color;          // color del robot
    private boolean visible;       // estado de visibilidad
    private int arrivalOrder;      // orden de llegada (para desempates)

    /**
     * Constructor del robot.
     */
    public Robot(int location, String color, int arrivalOrder) {
        this.initialLocation = location;
        this.location = location;
        this.color = color;
        this.shape = new Circle();
        this.visible = false;
        this.arrivalOrder = arrivalOrder;
        shape.changeColor(color);
        shape.changeSize(20);
    }

    /**
     * Devuelve la ubicación inicial del robot.
     * @return ubicación inicial.
     */
    public int initialLocation() { return initialLocation; }

    /**
     * Devuelve la ubicación actual del robot.
     * @return ubicación actual.
     */
    public int location() { return location; }

    /**
     * Devuelve el orden de llegada del robot (para desempates).
     * @return número de orden de llegada.
     */
    public int arrivalOrder() { return arrivalOrder; }

    /**
     * Establece la posición visual del robot en el lienzo.
     *
     * @param x coordenada X en píxeles.
     * @param y coordenada Y en píxeles.
     */
    public void setPosition(int x, int y) {
        shape.makeVisible();
        int dx = x - 20; // desplazamiento horizontal desde posición por defecto
        int dy = y - 15; // desplazamiento vertical desde posición por defecto
        shape.moveHorizontal(dx);
        shape.moveVertical(dy);
    }

    /**
     * Mueve el robot una cierta cantidad de metros en la simulación.
     * La distancia en metros se convierte en píxeles usando el factor pixelScale.
     *
     * @param meters distancia en metros a mover.
     * @param pixelScale número de píxeles equivalentes a un metro.
     */
    public void moveByMeters(int meters, int pixelScale) {
        int px = meters * pixelScale;
        shape.moveHorizontal(px); // movimiento visual
        this.location += meters;  // actualización lógica de la ubicación
    }

    /**
     * Regresa el robot a su ubicación inicial tanto lógica como visualmente.
     *
     * @param pixelScale escala de conversión de metros a píxeles.
     * @param coordMapper función que convierte la ubicación lógica en coordenadas de píxeles.
     */
    public void returnToInitial(int pixelScale, java.util.function.IntUnaryOperator coordMapper) {
        this.location = initialLocation;
        int coords = coordMapper.applyAsInt(initialLocation); // empaqueta X y Y
        int x = (coords >> 16) & 0xffff;
        int y = coords & 0xffff;
        setPosition(x, y);
    }

    /**
     * Hace visible al robot en el lienzo.
     */
    public void makeVisible() {
        if(!visible) {
            visible = true;
            shape.makeVisible();
        }
    }

    /**
     * Hace invisible al robot en el lienzo.
     */
    public void makeInvisible() {
        if(visible) {
            visible = false;
            shape.makeInvisible();
        }
    }
}
