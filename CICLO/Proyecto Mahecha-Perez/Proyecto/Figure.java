/**
 * Clase abstracta que representa una figura visual manipulable.
 * Proporciona métodos comunes para movimiento, visibilidad y cambio de color.
 * Las subclases deben implementar los métodos de dibujo y borrado.
 */
public abstract class Figure implements VisualElement {
    protected int xPosition;
    protected int yPosition;
    protected String color;
    protected boolean isVisible;
    /**
     * Hace visible la figura en pantalla.
     * Llama al método draw() si la figura no era visible.
     */
    public void makeVisible() {
        isVisible = true;
        draw();
    }

    /**
     * Oculta la figura de la pantalla.
     * Llama al método erase() si la figura era visible.
     */
    public void makeInvisible() {
        erase();
        isVisible = false;
    }

    /**
     * Mueve la figura horizontalmente una distancia específica.
     * 
     * @param distance número de píxeles a mover. Puede ser negativo.
     */
    public void moveHorizontal(int distance) {
        erase();
        xPosition += distance;
        draw();
    }

    /**
     * Mueve la figura verticalmente una distancia específica.
     * 
     * @param distance número de píxeles a mover. Puede ser negativo.
     */
    public void moveVertical(int distance) {
        erase();
        yPosition += distance;
        draw();
    }

    /**
     * Mueve la figura a una posición absoluta en pantalla.
     * 
     * @param x nueva coordenada horizontal
     * @param y nueva coordenada vertical
     */
    public void moveTo(int x, int y) {
        moveHorizontal(x - xPosition);
        moveVertical(y - yPosition);
    }

    /**
     * Mueve la figura 20 píxeles hacia la derecha.
     */
    public void moveRight() {
        moveHorizontal(20);
    }

    /**
     * Mueve la figura 20 píxeles hacia la izquierda.
     */
    public void moveLeft() {
        moveHorizontal(-20);
    }

    /**
     * Mueve la figura 20 píxeles hacia arriba.
     */
    public void moveUp() {
        moveVertical(-20);
    }

    /**
     * Mueve la figura 20 píxeles hacia abajo.
     */
    public void moveDown() {
        moveVertical(20);
    }

    /**
     * Mueve la figura lentamente en dirección horizontal, paso a paso.
     * 
     * @param distance número total de píxeles a mover. Puede ser negativo.
     */
    public void slowMoveHorizontal(int distance) {
        int delta = (distance < 0) ? -1 : 1;
        for (int i = 0; i < Math.abs(distance); i++) {
            xPosition += delta;
            draw();
        }
    }

    /**
     * Mueve la figura lentamente en dirección vertical, paso a paso.
     * 
     * @param distance número total de píxeles a mover. Puede ser negativo.
     */
    public void slowMoveVertical(int distance) {
        int delta = (distance < 0) ? -1 : 1;
        for (int i = 0; i < Math.abs(distance); i++) {
            yPosition += delta;
            draw();
        }
    }

    /**
     * Cambia el color de la figura.
     * 
     * @param newColor nombre del nuevo color (por ejemplo, "red", "blue")
     */
    public void changeColor(String newColor) {
        color = newColor;
        draw();
    }

    /**
     * Dibuja la figura en pantalla según sus atributos actuales.
     * Debe ser implementado por cada subclase.
     */
    protected abstract void draw();

    /**
     * Borra la figura de la pantalla.
     * Debe ser implementado por cada subclase.
     */
    protected abstract void erase();
}