import java.util.ArrayList;

/**
 * Clase visualizadora que dibuja una cuadrícula con un recorrido en espiral
 * comenzando desde la esquina inferior izquierda. Las celdas válidas se muestran en gris
 * y las inválidas en negro.
 * 
 * @author Natalia Mahecha y Maria Jose Perez
 * @version 1.0 (Oct 2025)
 */
public class Road implements VisualElement {

    private int length;
    private int cellSize = 40;
    private int spacing = 2;
    private int marginTop = 50;
    private ArrayList<Rectangle> gridCells;
    
    /**
     * Crea una nueva cuadrícula visual con un recorrido en espiral.
     * 
     * @param length número de celdas válidas en el camino
     */
    public Road(int length) {
        this.length = length;
        this.gridCells = new ArrayList<>();
        generateGrid();
    }

    /**
     * Genera la cuadrícula visual y asigna colores según validez de cada celda.
     * Las celdas válidas son grises; las inválidas son negras.
     */
    private void generateGrid() {
        int gridSize = (int) Math.ceil(Math.sqrt(length));
        ArrayList<int[]> spiral = generateSpiralFromBottomLeft(gridSize);

        for (int i = 0; i < spiral.size(); i++) {
            int[] coord = spiral.get(i);
            int row = coord[0];
            int col = coord[1];

            int x = col * (cellSize + spacing);
            int y = row * (cellSize + spacing) + marginTop;

            Rectangle cell = new Rectangle();
            cell.changeSize(cellSize, cellSize);
            cell.moveHorizontal(x - cell.xPosition);
            cell.moveVertical(y - cell.yPosition);

            if (i < length) {
                cell.changeColor("lightGray"); // celda válida
            } else {
                cell.changeColor("black"); // celda inválida
            }

            cell.makeVisible();
            gridCells.add(cell);
        }
    }
    
    /**
     * Genera una lista de coordenadas en espiral comenzando desde la esquina inferior izquierda.
     * 
     * @param gridSize tamaño de la cuadrícula (número de filas/columnas)
     * @return lista de coordenadas [fila, columna] en orden espiral
     */
    private ArrayList<int[]> generateSpiralFromBottomLeft(int gridSize) {
        ArrayList<int[]> path = new ArrayList<>();
        boolean[][] visited = new boolean[gridSize][gridSize];

        // Direcciones: derecha, arriba, izquierda, abajo
        int[][] directions = { {0, 1}, {-1, 0}, {0, -1}, {1, 0} };
        int dir = 0;

        int row = gridSize - 1;
        int col = 0;

        for (int i = 0; i < gridSize * gridSize; i++) {
            path.add(new int[] {row, col});
            visited[row][col] = true;

            int nextRow = row + directions[dir][0];
            int nextCol = col + directions[dir][1];

            if (nextRow < 0 || nextRow >= gridSize || nextCol < 0 || nextCol >= gridSize || visited[nextRow][nextCol]) {
                dir = (dir + 1) % 4;
                nextRow = row + directions[dir][0];
                nextCol = col + directions[dir][1];
            }

            row = nextRow;
            col = nextCol;
        }

        return path;
    }

    /**
     * Devuelve la posición visual (x, y) de una celda lógica específica.
     * 
     * @param logicalIndex índice lógico de la celda
     * @return arreglo con coordenadas [x, y] en píxeles
     */
    public int[] getVisualPosition(int logicalIndex) {
        Rectangle cell = gridCells.get(logicalIndex);
        return new int[] { cell.xPosition, cell.yPosition };
    }
    
    /**
     * Devuelve el ancho total de la cuadrícula en píxeles.
     * 
     * @return ancho total en píxeles
     */
    public int getTotalWidth() {
        int gridSize = (int) Math.ceil(Math.sqrt(length));
        return gridSize * (cellSize + spacing);
    }
    
    
    /**
     * Oculta toda la cuadrícula de la pantalla.
     */
    @Override
    public void makeInvisible() {
        for (Rectangle cell : gridCells) {
            cell.makeInvisible();
        }
    }
    
    /**
     * Hace visible toda la cuadrícula en pantalla.
     */
    @Override
    public void makeVisible() {
        for (Rectangle cell : gridCells) {
            cell.makeVisible();
        }
    }
}