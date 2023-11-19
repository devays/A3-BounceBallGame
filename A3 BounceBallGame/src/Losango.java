import java.awt.Polygon;

class Losango extends Polygon {
    public Losango(float x, float y, float width, float height) {
        int[] xPoints = {(int) (x - width / 2), (int) x, (int) (x + width / 2), (int) x};
        int[] yPoints = {(int) y, (int) (y + height / 2), (int) y + (int) height, (int) (y - height / 2)};
        this.xpoints = xPoints;
        this.ypoints = yPoints;
        this.npoints = 4;
    }
}