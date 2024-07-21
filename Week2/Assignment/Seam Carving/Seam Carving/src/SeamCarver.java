import edu.princeton.cs.algs4.Picture;

public class SeamCarver {
    // create a seam carver object based on the given picture
    private static Picture pic;

    public SeamCarver(Picture picture) {
        pic = picture;
    }

    // current picture
    public Picture picture() {
        return pic;
    }

    // width of current picture
    public int width() {
        return pic.width();
    }

    // height of current picture
    public int height() {
        return pic.height();
    }

    private boolean onBorder(int col, int row) {
        return col == 0 || col == pic.width() || row == 0 || row == pic.height();
    }

    private int calcX(int col, int row) {
        int xLeft = pic.getRGB(col - 1, row);
        int xRight = pic.getRGB(col + 1, row);
        int mask = 0xff;
        int rX = ((xRight>> 16) & mask) - ((xLeft >> 16) & mask);
        int gX = ((xRight>> 8) & mask) - ((xLeft >> 8) & mask);
        int bX = (xRight & mask) - (xLeft & mask);
        return rX * rX + gX * gX + bX * bX;
    }

    private int calcY(int col, int row) {
        int yUp = pic.getRGB(col - 1, row);
        int yDown = pic.getRGB(col + 1, row);
        int mask = 0xff;
        int rY = ((yDown>> 16) & mask) - ((yUp >> 16) & mask);
        int gY = ((yDown>> 8) & mask) - ((yUp >> 8) & mask);
        int bY = (yDown & mask) - (yUp & mask);
        return rY * rY + gY * gY + bY * bY;
    }

    // energy of pixel at column x and row y
    public double energy(int x, int y) {
        if (onBorder(x, y)) {
            return 1000.0;
        } else {
            int yUp = pic.getRGB(x, y - 1);
            int yDown = pic.getRGB(x, y + 1);
            int deltaX = calcX(x, y);
            int deltaY = calcY(x, y);
            return Math.sqrt(deltaX + deltaY);
        }
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {

    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {

    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {

    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {

    }
    public static void main(String[] args) {

    }
}