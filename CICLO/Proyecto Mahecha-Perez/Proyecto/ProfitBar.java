/**
 * Representa una barra de progreso visual que indica la ganancia actual
 * en relación con una ganancia máxima posible. Se compone de un fondo gris
 * y una barra verde proporcional al progreso.
 * 
 * @author Natalia Mahecha y Maria Jose Perez
 * @version 1.0 (Oct 2025)
 */
public class ProfitBar implements VisualElement {
    
    private int currentProfit;
    private int maxProfit;
    private int width;
    private final int height = 40;
    private final int xPosition = 0;
    private final int yPosition = 5;
    private Rectangle background;
    private Rectangle fill;

    /**
     * Crea una nueva barra de ganancia con un ancho específico y una ganancia máxima inicial.
     * 
     * @param width ancho total de la barra en píxeles
     * @param maxProfit valor máximo de ganancia posible
     */

     public ProfitBar(int width, int maxProfit){
        this.width = width;
        this.currentProfit = 0;
        this.maxProfit = maxProfit;
        createVisual();
    }

    /**
     * Inicializa los elementos visuales: fondo gris y barra verde.
     * 
     * @param x posición horizontal inicial
     * @param y posición vertical inicial
     */

    private void createVisual() {
        background = new Rectangle();
        background.changeColor("lightGray");
        background.changeSize(height, width);
        background.moveHorizontal(xPosition - background.xPosition);
        background.moveVertical(yPosition - background.yPosition);
        background.makeVisible();

        fill = new Rectangle();
        fill.changeColor("green");
        fill.changeSize(height, 0);
        fill.moveHorizontal(xPosition - fill.xPosition);
        fill.moveVertical(yPosition - fill.yPosition);
    }

    /**
     * Actualiza la ganancia actual y el máximo, y redibuja la barra verde proporcionalmente.
     * 
     * @param newProfit nueva ganancia actual
     * @param newMaxProfit nuevo valor máximo de ganancia
     */

    public void updateProfit(int newProfit, int newMaxProfit) {
        this.currentProfit = Math.max(0, newProfit);
        this.maxProfit = Math.max(1, newMaxProfit);
    
        if (currentProfit == 0) {
            fill.makeInvisible(); // oculta la raya verde
            return;
        }
    
        int fillWidth = (int) ((double) currentProfit / maxProfit * width);
        fillWidth = Math.min(fillWidth, width);
        fill.changeSize(height, fillWidth);
        fill.moveHorizontal(xPosition - fill.xPosition);
        fill.makeVisible(); // asegúrate de que se vea si no está en cero
    }
    
    
    /**
     * Establece un nuevo valor máximo de ganancia y actualiza la visualización.
     * 
     * @param max nuevo valor máximo
     */

    public void setMaxProfit(int max) {
        this.maxProfit = Math.max(1, max);
        updateVisual();
    }
    
    /**
     * Agrega una cantidad a la ganancia actual y actualiza la barra.
     * 
     * @param profit cantidad a sumar
     */
    public void addProfit(int profit) {
        this.currentProfit = Math.min(currentProfit + profit, maxProfit);
        updateProfit(currentProfit, maxProfit);
    }
    
    /**
     * Redibuja la barra verde según el estado actual.
     * Solo se llama internamente.
     */

    private void updateVisual() {
        if (currentProfit <= 0) {
            fill.makeInvisible();
            return;
        }
    
        int fillWidth = (int) ((double) currentProfit / maxProfit * width);
        fillWidth = Math.min(fillWidth, width);
        fill.changeSize(height, fillWidth);
        fill.moveHorizontal(xPosition - fill.xPosition);
        fill.makeVisible();
    }
    
    /**
     * Hace visible la barra completa (fondo y progreso).
     */
    @Override
    public void makeVisible() {
        background.makeVisible();
        fill.makeVisible();
    }
    
    /**
     * Oculta la barra completa (fondo y progreso).
     */

    @Override
    public void makeInvisible() {
        background.makeInvisible();
        fill.makeInvisible();
    }
}



