import edu.princeton.cs.algs4.Picture;
import edu.princeton.cs.algs4.IndexMinPQ;

public class SeamCarver {
    // create a seam carver object based on the given picture

    private Picture pic;

    public SeamCarver(Picture picture) {
        if (picture == null)
            throw new IllegalArgumentException("Argument can't be null");
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
        return col == 0 || col == pic.width()  - 1 || row == 0 || row == pic.height() - 1;
    }

    private int calcX(int col, int row) {
        int xLeft = pic.getRGB(col - 1, row);
        int xRight = pic.getRGB(col + 1, row);
        int mask = 0xff;
        int rX = ((xRight >> 16) & mask) - ((xLeft >> 16) & mask);
        int gX = ((xRight >> 8) & mask) - ((xLeft >> 8) & mask);
        int bX = (xRight & mask) - (xLeft & mask);
        return rX * rX + gX * gX + bX * bX;
    }

    private int calcY(int col, int row) {
        int yUp = pic.getRGB(col, row - 1);
        int yDown = pic.getRGB(col, row + 1);
        int mask = 0xff;
        int rY = ((yDown >> 16) & mask) - ((yUp >> 16) & mask);
        int gY = ((yDown >> 8) & mask) - ((yUp >> 8) & mask);
        int bY = (yDown & mask) - (yUp & mask);
        return rY * rY + gY * gY + bY * bY;
    }

    // energy of pixel at column x and row y
    public double energy(int x, int y) {
        if (x < 0 || x >= pic.width() || y < 0 || y >= pic.height())
            throw new IllegalArgumentException("Index out of bounds");
        if (onBorder(x, y)) {
            return 1000.0;
        } else {
            int deltaX = calcX(x, y);
            int deltaY = calcY(x, y);
            return Math.sqrt(deltaX + deltaY);
        }
    }

    // Helper function
    private int[] findSeam(boolean isHorizontal) {
        int resultSize = isHorizontal ? pic.width() : pic.height();
        int elementSize = isHorizontal ? pic.height() : pic.width();

        int[] result = new int[resultSize];

        int totalMark;
        boolean[] marked;
        double[] distTo = new double[elementSize];
        double[] updatedDistTo;
        int[][] edgeTo = new int[resultSize][elementSize];
        IndexMinPQ<Double> prevQueue = new IndexMinPQ<>(elementSize);
        IndexMinPQ<Double> currentQueue;


        for (int i = 0; i < elementSize; i++) {
            distTo[i] = getEnergy(i, 0, isHorizontal);
            prevQueue.insert(i, distTo[i]);
        }

        for (int i = 1; i < resultSize; i++) {

            marked = new boolean[elementSize];
            totalMark = 0;
            currentQueue = new IndexMinPQ<>(elementSize);
            updatedDistTo = new double[elementSize];

            while (totalMark < elementSize) {
                int key = prevQueue.delMin();
                for (int child = key - 1; child < key + 2; child++) {
                    if (child < 0 || child >= elementSize) continue;
                    if (marked[child]) continue;
                    updatedDistTo[child] = getEnergy(child, i, isHorizontal) + distTo[key];
                    marked[child] = true;
                    totalMark += 1;
                    currentQueue.insert(child, updatedDistTo[child]);
                    edgeTo[i][child] = key;
                }
            }
            prevQueue = currentQueue;
            distTo = updatedDistTo;
        }

        int resultKey = prevQueue.delMin();
        result[resultSize - 1] = resultKey;
        for (int h = resultSize - 2; h >= 0; h--) {
            result[h] = edgeTo[h + 1][result[h + 1]];
        }
        return result;
    }

    private double getEnergy(int elementWise, int resultWise, boolean isHorizontal) {
        if (isHorizontal) return energy(resultWise, elementWise);
        else return energy(elementWise, resultWise);
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        return findSeam(true);
    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        return findSeam(false);
    }

    // Check the validation of the index in the array
    private void checkValidate(int[] seam, int index, boolean isHorizontal) {
        int elementSize = isHorizontal ? pic.height() : pic.width();
        if (seam[index] < 0 || seam[index] >= elementSize)
            throw new IllegalArgumentException("Index out of bounds");
        for (int child = index - 1; child <= index + 1; child += 2) {
            if (child < 0 || child >= elementSize) continue;
            if (Math.abs(seam[child] - seam[index]) > 1)
                throw new IllegalArgumentException("Invalid seam");
        }
    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        if (pic.height() == 1)
            throw new IllegalArgumentException("Can't remove more horizontal pixels");
        if (seam == null)
            throw new IllegalArgumentException("Argument can't be null");
        if (seam.length != pic.width())
            throw new IllegalArgumentException("Wrong array length");
        Picture newPic = new Picture(pic.width(), pic.height() - 1);
        for (int width = 0; width < pic.width(); width++) {
            checkValidate(seam, width, true);
            int position = seam[width];
            for (int row = 0; row < position; row++)
                newPic.setRGB(width, row, pic.getRGB(width, row));
            for (; position < pic.height() - 1; position++) {
                newPic.setRGB(width, position, pic.getRGB(width, position + 1));
            }
        }
        pic = newPic;
    }

    // remove vertical seam from current picture
    private void removeVerticalSeam(int[] seam) {
        if (pic.width() == 1)
            throw new IllegalArgumentException("Can't remove more vertical pixels");
        if (seam == null)
            throw new IllegalArgumentException("Argument can't be null");
        if (seam.length != pic.height())
            throw new IllegalArgumentException("Wrong array length");
        Picture newPic = new Picture(pic.width() - 1, pic.height());
        for (int row = 0; row < pic.height(); row++) {
            checkValidate(seam, row, false);
            int position = seam[row];
            for (int i = 0; i < position; i++)
                newPic.setRGB(i, row, pic.getRGB(i, row));
            for (; position < pic.width() - 1; position++) {
                newPic.setRGB(position, row, pic.getRGB(position + 1, row));
            }
        }
        pic = newPic;
    }

    private void showVerticalPath(int[] seam, int gapTime) {
        for (int h = 0; h < pic.height(); h++) {
            pic.setRGB(seam[h], h, (0xff << 24) + (0xff << 16));
        }
        try {
            pic.show();
            Thread.sleep(gapTime);
            pic.hide();
        } catch (InterruptedException e) {
            System.out.println("Something went wrong");
        }
    }

    private void showHorizontalPath(int[] seam, int gapTime) {
        for (int w = 0; w < pic.width(); w++) {
            pic.setRGB(w, seam[w], (0xff << 24) + (0xff << 16));
        }
        try {
            pic.show();
            Thread.sleep(gapTime);
            pic.hide();
        } catch (InterruptedException e) {
            System.out.println("Something went wrong");
        }
    }
    public static void main(String[] args) {
        Picture test = new Picture(args[0]);
        SeamCarver carver = new SeamCarver(test);
        for (int i = 0; i < 200; i++) {
            int[] result = carver.findHorizontalSeam();
            carver.showHorizontalPath(result, 1000);
            carver.removeHorizontalSeam(result);
        }
        carver.picture().show();
    }
}