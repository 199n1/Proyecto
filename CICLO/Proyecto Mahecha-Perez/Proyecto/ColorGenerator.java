import java.util.*;

public class ColorGenerator {

    private final List<String> baseColors = Arrays.asList(
        "red", "blue", "green", "orange", "cyan", "magenta", "yellow", "pink",
        "darkGray", "gray", "brown", "purple", "turquoise", "lime", "teal",
        "indigo", "gold", "silver", "maroon", "beige", "violet", "navy", "salmon",
        "coral", "olive", "mint", "lavender", "peach", "plum", "sky", "apricot",
        "denim", "jade", "ruby", "amber", "charcoal", "sand", "forest", "ice",
        "rose", "steel", "fuchsia", "mustard", "aqua", "cherry", "copper", "fog",
        "wine", "ocean"
    );

    private List<String> shuffledColors;
    private int index = 0;

    public ColorGenerator() {
        reset(); // baraja al inicio
    }

    public String nextColor() {
        if (index >= shuffledColors.size()) {
            throw new IllegalStateException("Se han agotado los 50 colores únicos disponibles.");
        }
        return shuffledColors.get(index++);
    }

    public void reset() {
        shuffledColors = new ArrayList<>(baseColors);
        shuffledColors.remove("lightGray");
        shuffledColors.remove("black");
        Collections.shuffle(shuffledColors); // ← baraja los colores
        index = 0;
    }
}