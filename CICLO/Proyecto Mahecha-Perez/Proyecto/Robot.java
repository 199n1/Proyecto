/**
 * Representa un robot dentro del sistema SilkRoad.
 * Cada robot tiene una ubicación lógica, un estado de actividad, un color único
 * y una representación visual compuesta por cuerpo, ojos y boca.
 * 
 * @author Natalia Mahecha y Maria Jose Perez
 * @version 1.0 (Oct 2025)
 */
public class Robot implements VisualElement {

    private int location;
    private int initialLocation;
    private boolean active;
    private String colorName;
    private int maxLength;

    // Componentes visuales
    private Rectangle body;
    private Rectangle eyeLeft;
    private Rectangle eyeRight;
    private Rectangle mouth;

    /**
     * Crea un nuevo robot en una ubicación lógica específica, con color único y componentes visuales.
     * 
     * @param location ubicación lógica inicial del robot
     * @param maxLength longitud máxima del camino
     * @param generator generador de colores para asignar color único
     * @param x coordenada horizontal para la visualización
     * @param y coordenada vertical para la visualización
     */

    public Robot(int location, int maxLength, ColorGenerator generator, int x, int y) {
        this.location = location;
        this.initialLocation = location;
        this.maxLength = maxLength;
        this.active = true;
        this.colorName = generator.nextColor();

        createVisual(x, y);
    }
    
    /**
     * Crea los elementos visuales del robot: cuerpo, ojos y boca.
     * 
     * @param x coordenada horizontal base
     * @param y coordenada vertical base
     */
    private void createVisual(int x, int y) {
        int cellSize = 40;
    
        // Tamaños
        int bodySize = 30;
        int eyeSize = 6;
        int eyeSpacing = 6; 
        int mouthHeight = 4;
    
        // Posición del cuerpo
        int bodyX = x + (cellSize - bodySize) / 2;
        int bodyY = y + (cellSize - bodySize) / 2;
    
        // Posición de los ojos
        int eyeY = bodyY + 6;
        int eyeLeftX = bodyX + (bodySize / 2) - eyeSize - (eyeSpacing / 2);
        int eyeRightX = bodyX + (bodySize / 2) + (eyeSpacing / 2);
    
        // Boca alineada con los extremos de los ojos
        int mouthX = eyeLeftX;
        int mouthY = bodyY + bodySize - mouthHeight - 6;
        int mouthWidth = (eyeRightX + eyeSize) - eyeLeftX;
    
        // Cuerpo
        body = new Rectangle();
        body.changeColor(colorName);
        body.changeSize(bodySize, bodySize);
        body.moveHorizontal(bodyX - body.xPosition);
        body.moveVertical(bodyY - body.yPosition);
    
        // Ojo izquierdo
        eyeLeft = new Rectangle();
        eyeLeft.changeColor("black");
        eyeLeft.changeSize(eyeSize, eyeSize);
        eyeLeft.moveHorizontal(eyeLeftX - eyeLeft.xPosition);
        eyeLeft.moveVertical(eyeY - eyeLeft.yPosition);
    
        // Ojo derecho
        eyeRight = new Rectangle();
        eyeRight.changeColor("black");
        eyeRight.changeSize(eyeSize, eyeSize);
        eyeRight.moveHorizontal(eyeRightX - eyeRight.xPosition);
        eyeRight.moveVertical(eyeY - eyeRight.yPosition);
    
        // Boca
        mouth = new Rectangle();
        mouth.changeColor("black");
        mouth.changeSize(mouthHeight, mouthWidth);
        mouth.moveHorizontal(mouthX - mouth.xPosition);
        mouth.moveVertical(mouthY - mouth.yPosition);
    }

    /**
     * Hace visible el robot en pantalla.
     */

    @Override
    public void makeVisible() {
        body.makeVisible();
        eyeLeft.makeVisible();
        eyeRight.makeVisible();
        mouth.makeVisible();
    }

    /**
     * Oculta el robot de la pantalla.
     */
    @Override
    public void makeInvisible() {
        body.makeInvisible();
        eyeLeft.makeInvisible();
        eyeRight.makeInvisible();
        mouth.makeInvisible();
    }

    /**
     * Devuelve la ubicación lógica actual del robot.
     * 
     * @return índice de ubicación
     */

    public int getLocation() {
        return location;
    }

    /**
     * Devuelve el nombre del color asignado al robot.
     * 
     * @return nombre del color
     */
    public String getColorName() {
        return colorName;
    }
    
    /**
     * Indica si el robot está activo.
     * 
     * @return true si está activo, false si está inactivo
     */
    public boolean isActive() {
        return active;
    }
    
    /**
     * Desactiva el robot.
     */
    public void deactivate() {
        active = false;
    }

        /**
     * Activa el robot.
     */
    public void activate() {
        active = true;
    }
    
    
    /**
     * Representación textual del robot.
     * 
     * @return descripción con ubicación y estado
     */

    public String toString() {
        return "Robot en " + location + (active ? " (activo)" : " (inactivo)");
    }
    
    /**
     * Establece una nueva ubicación lógica para el robot.
     * 
     * @param newLocation nueva ubicación lógica
     */
    public void setLocation(int newLocation) {
        this.location = newLocation;
    }
    
    /**
     * Mueve el robot a una nueva posición visual en pantalla.
     * 
     * @param x nueva coordenada horizontal
     * @param y nueva coordenada vertical
     */
    public void moveTo(int x, int y) {
        int cellSize = 40;
        int bodySize = 30;
        int eyeSize = 6;
        int eyeSpacing = 6;
        int mouthHeight = 4;
    
        // Recalcular posiciones relativas
        int bodyX = x + (cellSize - bodySize) / 2;
        int bodyY = y + (cellSize - bodySize) / 2;
    
        int eyeY = bodyY + 6;
        int eyeLeftX = bodyX + (bodySize / 2) - eyeSize - (eyeSpacing / 2);
        int eyeRightX = bodyX + (bodySize / 2) + (eyeSpacing / 2);
    
        int mouthX = eyeLeftX;
        int mouthY = bodyY + bodySize - mouthHeight - 6;
        int mouthWidth = (eyeRightX + eyeSize) - eyeLeftX;
    
        // Mover cuerpo
        body.moveHorizontal(bodyX - body.xPosition);
        body.moveVertical(bodyY - body.yPosition);
    
        // Mover ojos
        eyeLeft.moveHorizontal(eyeLeftX - eyeLeft.xPosition);
        eyeLeft.moveVertical(eyeY - eyeLeft.yPosition);
    
        eyeRight.moveHorizontal(eyeRightX - eyeRight.xPosition);
        eyeRight.moveVertical(eyeY - eyeRight.yPosition);
    
        // Mover boca
        mouth.moveHorizontal(mouthX - mouth.xPosition);
        mouth.moveVertical(mouthY - mouth.yPosition);
    }
    
    /**
     * Devuelve la ubicación inicial del robot.
     * 
     * @return índice de ubicación inicial
     */

    public int getInitialLocation() {
        return initialLocation;
    }
}

