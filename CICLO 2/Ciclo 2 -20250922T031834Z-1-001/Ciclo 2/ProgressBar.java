import java.awt.*;

/**
 * Clase ProgressBar: representa una barra de progreso para mostrar las ganancias de la simulación.
 * Está compuesta por dos rectángulos: el fondo y la barra que crece proporcionalmente al valor actual.
 *
 * @param x coordenada X de la barra en el lienzo.
 * @param y coordenada Y de la barra en el lienzo.
 * @param width ancho total de la barra.
 * @param height alto total de la barra.
 */
public class ProgressBar {
    private Rectangle background;   // rectángulo de fondo
    private Rectangle bar;          // rectángulo de progreso
    private boolean visible;
    private int x, y, width, height;
    private int maxValue;
    private int currentValue;
    private String bgColor = "black";
    private String barColor = "green";

    /**
     * Constructor de la barra de progreso.
     */
    public ProgressBar(int x, int y, int width, int height) {
        background = new Rectangle();
        bar = new Rectangle();
        this.x = x; this.y = y; this.width = width; this.height = height;
        maxValue = 1;
        currentValue = 0;
        background.changeSize(height, width);
        bar.changeSize(height-4, 0);
        background.changeColor(bgColor);
        bar.changeColor(barColor);
        visible = false;
    }

    /**
     * Establece el valor máximo de la barra.
     * @param max valor máximo permitido.
     */
    public void setMax(int max) {
        if(max <= 0) max = 1;
        this.maxValue = max;
        updateVisual();
    }

    /**
     * Establece el valor actual de la barra y actualiza la visualización.
     * @param val valor actual.
     */
    public void setValue(int val) {
        this.currentValue = Math.max(0, val);
        updateVisual();
    }

    /**
     * Hace visible la barra de progreso en el lienzo.
     */
    public void makeVisible() {
        if(!visible) {
            visible = true;
            background.makeVisible();
            bar.makeVisible();
            background.moveHorizontal(x-70);
            background.moveVertical(y-15);
            bar.moveHorizontal(x-70+2);
            bar.moveVertical(y-15+2);
            updateVisual();
        }
    }

    /**
     * Hace invisible la barra de progreso en el lienzo.
     */
    public void makeInvisible() {
        if(visible) {
            visible = false;
            background.makeInvisible();
            bar.makeInvisible();
        }
    }

    /**
     * Actualiza visualmente la barra de progreso de acuerdo al valor actual.
     */
    private void updateVisual() {
        if(!visible) return;
        int w = (int)((double)currentValue / maxValue * (width-4));
        if(w < 0) w = 0;
        bar.changeSize(height-4, Math.max(1, w));
    }
}
