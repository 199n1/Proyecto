public class Store extends Rectangle {
    private int position;
    private int tenges;
    private int initialTenges;
 
    public Store(int location, int tenges) {
        super(); // Rectangle
        this.position = location;
        this.tenges = tenges;
        this.initialTenges = tenges;
        changeColor("green");
        moveHorizontal(location);
        moveVertical(200); // 👈 mismo eje Y que los robots
    }
 
    public int getPosition() {
        return position;
    }
 
    public int getTenges() {
        return tenges;
    }
 
    public void resupply() {
        this.tenges = initialTenges;
    }
}