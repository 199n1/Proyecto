/**
 * Representa una tienda dentro del sistema SilkRoad.
 * Cada tienda tiene una ubicación lógica, una cantidad de tenges (ganancia),
 * y una representación visual compuesta por una base cuadrada y un techo triangular.
 * 
 * @author Natalia Mahecha y Maria Jose Perez
 * @version 1.0 (Oct 2025)
 */
public class Store implements VisualElement {

    private int location;
    private String colorName;
    private Rectangle base;
    private int tenges;
    private Triangle roof;
    private int initialTenges;

    /**
     * Crea una nueva tienda en una ubicación lógica específica, con color único y valor inicial.
     * 
     * @param location ubicación lógica dentro del camino
     * @param generator generador de colores para asignar color único
     * @param x coordenada horizontal para la visualización
     * @param y coordenada vertical para la visualización
     * @param tenges cantidad inicial de tenges (ganancia)
     */
    public Store(int location, ColorGenerator generator, int x, int y, int tenges) {
        this.location = location;
        this.tenges = tenges;
        this.initialTenges = tenges;
        this.colorName = generator.nextColor();
        createVisual(x, y);
    }
    
    /**
     * Crea los elementos visuales de la tienda: base cuadrada y techo triangular.
     * 
     * @param x coordenada horizontal base
     * @param y coordenada vertical base
     */
    private void createVisual(int x, int y) {
        int cellSize = 40;
    
        // Tamaños
        int baseSize = 19;
        int roofHeight = 16;
        int roofWidth = 28;
    
        // Posición base (centrado en la celda)
        int baseX = x + (cellSize - baseSize) / 2;
        int baseY = y + (cellSize - baseSize) / 2 + roofHeight / 2;
    
        // Posición techo (centrado encima del cuadrado)
        int roofX = baseX + baseSize / 2;
        int roofY = baseY - roofHeight;
    
        // Base cuadrada
        base = new Rectangle();
        base.changeColor(colorName);
        base.changeSize(baseSize, baseSize);
        base.moveHorizontal(baseX - base.xPosition);
        base.moveVertical(baseY - base.yPosition);
    
        // Techo triangular
        roof = new Triangle();
        roof.changeColor(colorName);
        roof.changeSize(roofHeight, roofWidth);
        roof.moveHorizontal(roofX - roof.xPosition);
        roof.moveVertical(roofY - roof.yPosition);
    }
    
    /**
     * Hace visible la tienda en pantalla.
     */
    @Override
    public void makeVisible() {
        base.makeVisible();
        roof.makeVisible();
    }
    
    /**
     * Oculta la tienda de la pantalla.
     */
    @Override
    public void makeInvisible() {
        base.makeInvisible();
        roof.makeInvisible();
    }

    /**
     * Devuelve la ubicación lógica de la tienda.
     * 
     * @return índice de ubicación
     */
    public int getLocation() {
        return location;
    }

    
    /**
     * Devuelve el nombre del color asignado a la tienda.
     * 
     * @return nombre del color
     */
    public String getColorName() {
        return colorName;
    }

    /**
     * Devuelve la cantidad actual de tenges de la tienda.
     * 
     * @return valor actual de ganancia
     */
    public int getTenges() {
        return tenges;
    }
    
    
    /**
     * Representación textual de la tienda.
     * 
     * @return descripción con la ubicación
     */
    public String toString() {
        return "Tienda en " + location;
    }
    
    /**
     * Restaura la cantidad de tenges al valor inicial.
     */
    public void resupply() {
        this.tenges = initialTenges;
    }
    
    /**
     * Cambia el color de la tienda a negro.
     */
    public void setBlack() {
        base.changeColor("black");
        roof.changeColor("black");
    }
    
    /**
     * Vacía la tienda, dejando su ganancia en cero.
     */
    public void empty() {
        this.tenges = 0;
    }
    
    /**
     * Restaura el color original de la tienda.
     */
    public void restoreColor() {
        base.changeColor(colorName);
        roof.changeColor(colorName);
    }
}