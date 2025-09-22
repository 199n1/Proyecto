/**
 * Clase Store: representa una tienda en la SilkRoad.
 * Cada tienda tiene una ubicación, una cantidad inicial de tenges y una cantidad actual.
 * Se representa visualmente con un rectángulo que cambia de tamaño según esté vacía o no.
 *
 * @param location ubicación lógica de la tienda.
 * @param tenges cantidad inicial de tenges que posee la tienda.
 * @param color color de la tienda (para visualización).
 */
public class Store {
    private int location;           // coordenada lógica (entero)
    private int initialTenges;      // tenges al colocarla
    private int tenges;             // tenges actuales (puede vaciarse)
    private Rectangle shape;        // representación visual
    private String color;           // color visual de la tienda
    private boolean visible;        // estado de visibilidad

    /**
     * Constructor de la tienda.
     */
    public Store(int location, int tenges, String color) {
        this.location = location;
        this.initialTenges = tenges;
        this.tenges = tenges;
        this.color = color;
        this.shape = new Rectangle();
        this.visible = false;
        shape.changeSize(24, 36);
        shape.changeColor(color);
    }

    /**
     * Devuelve la ubicación de la tienda.
     * @return ubicación lógica.
     */
    public int location() { return location; }

    /**
     * Devuelve la cantidad de tenges actuales en la tienda.
     * @return tenges actuales.
     */
    public int tenges() { return tenges; }

    /**
     * Devuelve la cantidad inicial de tenges de la tienda.
     * @return tenges iniciales.
     */
    public int initialTenges() { return initialTenges; }

    /**
     * Restablece la cantidad de tenges a su valor inicial.
     */
    public void resupply() {
        this.tenges = this.initialTenges;
        updateLabel();
    }

    /**
     * Vacía la tienda y devuelve los tenges que contenía.
     * @return cantidad de tenges recogidos.
     */
    public int collectAll() {
        int taken = this.tenges;
        this.tenges = 0;
        updateLabel();
        return taken;
    }

    /**
     * Indica si la tienda está vacía.
     * @return true si no tiene tenges, false en caso contrario.
     */
    public boolean isEmpty() {
        return this.tenges == 0;
    }

    /**
     * Cambia el color visual de la tienda.
     *
     * @param color nuevo color de la tienda.
     */
    public void setColor(String color) {
        this.color = color;
        shape.changeColor(color);
    }

    /**
     * Hace visible la tienda en el lienzo.
     */
    public void makeVisible() {
        if(!visible) {
            visible = true;
            shape.makeVisible();
        }
    }

    /**
     * Hace invisible la tienda en el lienzo.
     */
    public void makeInvisible() {
        if(visible) {
            shape.makeInvisible();
            visible = false;
        }
    }

    /**
     * Establece la posición visual de la tienda en el lienzo.
     *
     * @param x coordenada X en píxeles.
     * @param y coordenada Y en píxeles.
     */
    public void setPosition(int x, int y) {
        shape.makeVisible();
        int dx = x - 70;
        int dy = y - 15;
        shape.moveHorizontal(dx);
        shape.moveVertical(dy);
    }

    /**
     * Actualiza la representación visual de la tienda según esté vacía o no.
     */
    private void updateLabel() {
        if(tenges == 0) {
            shape.changeSize(18, 28);
        } else {
            shape.changeSize(30, 44);
        }
    }
}
