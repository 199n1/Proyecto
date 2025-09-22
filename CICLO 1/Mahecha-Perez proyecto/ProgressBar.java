import java.awt.*;

/**
 * ProgressBar: muestra la ganancia actual proporcionalmente a un máximo.
 * Visual: dos Rectangles: fondo y barra de progreso.
 */
public class ProgressBar {
    private Rectangle background;
    private Rectangle bar;
    private boolean visible;
    private int x, y, width, height;
    private int maxValue;
    private int currentValue;
    private String bgColor = "black";
    private String barColor = "green";
    
    public ProgressBar(int x, int y, int width, int height) {
        background = new Rectangle();
        bar = new Rectangle();
        this.x = x; this.y = y; this.width = width; this.height = height;
        maxValue = 1;
        currentValue = 0;
        background.changeSize(height, width);
        bar.changeSize(height-4, 0); // start zero width
        background.changeColor(bgColor);
        bar.changeColor(barColor);
        visible = false;
    }
    public void setMax(int max) {
        if(max <= 0) max = 1;
        this.maxValue = max;
        updateVisual();
    }
    public void setValue(int val) {
        this.currentValue = Math.max(0, val);
        updateVisual();
    }
    public void makeVisible() {
        if(!visible) {
            visible = true;
            background.makeVisible();
            bar.makeVisible();
            // position them
            background.moveHorizontal(x-70); // rectangle default at 70
            background.moveVertical(y-15);
            bar.moveHorizontal(x-70+2);
            bar.moveVertical(y-15+2);
            updateVisual();
        }
    }
    public void makeInvisible() {
        if(visible) {
            visible = false;
            background.makeInvisible();
            bar.makeInvisible();
        }
    }
    private void updateVisual() {
        if(!visible) return;
        // compute width proportionally
        int w = (int)((double)currentValue / maxValue * (width-4));
        if(w < 0) w = 0;
        bar.changeSize(height-4, Math.max(1, w));
    }
}

