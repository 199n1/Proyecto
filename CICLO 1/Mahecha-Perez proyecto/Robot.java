public class Robot extends Circle {
    private int position;
    private int initialPosition;
 
    public Robot(int location) {
        super(); // Circle
        this.position = location;
        this.initialPosition = location;
        changeColor("blue"); // color por defecto
        moveHorizontal(location); 
        moveVertical(200); // línea base Y=200
    }
 
    public int getPosition() {
        return position;
    }
 
    public void move(int meters) {
        moveHorizontal(meters);
        position += meters;
    }
 
    public void returnToStart() {
        int distance = initialPosition - position;
        moveHorizontal(distance);
        position = initialPosition;
    }
}