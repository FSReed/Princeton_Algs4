import edu.princeton.cs.algs4.Picture;
import edu.princeton.cs.algs4.IndexMinPQ;

public class SeamCarver {
    // create a seam carver object based on the given picture

    private Picture pic;

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
        if (onBorder(x, y)) {
            return 1000.0;
        } else {
            int deltaX = calcX(x, y);
            int deltaY = calcY(x, y);
            return Math.sqrt(deltaX + deltaY);
        }
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        return null;
    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
       int[] result = new int[pic.height()];

       int totalMark;
       boolean[] marked;
       double[] distTo = new double[pic.width()];
       double[] updatedDistTo;
       int[][] edgeTo = new int[pic.height()][pic.width()];
       IndexMinPQ<Double> prevQueue = new IndexMinPQ<>(pic.width());
       IndexMinPQ<Double> currentQueue;


       for (int w = 0; w < pic.width(); w++) {
           distTo[w] = energy(0, w);
           prevQueue.insert(w, distTo[w]);
           for (int h = 0; h < pic.height(); h++) {
               edgeTo[h][w] = -1;
           }
       }
       for (int h = 1; h < pic.height(); h++) {

           marked = new boolean[pic.width()];
           totalMark = 0;
           currentQueue = new IndexMinPQ<>(pic.width());
           updatedDistTo = new double[pic.width()];

           while (totalMark < pic.width() - 1) {
               int key = prevQueue.delMin();
               for (int child = key - 1; child < key + 2; child++) {
                   if (child < 0 || child >= pic.width()) continue;
                   if (marked[child]) continue;
                   updatedDistTo[child] = energy(child, h) + distTo[key];
                   marked[child] = true;
                   totalMark += 1;
                   currentQueue.insert(child, updatedDistTo[child]);
                   edgeTo[h][child] = key;
               }
           }
           prevQueue = currentQueue;
           distTo = updatedDistTo;
       }

       int resultKey = prevQueue.delMin();
       result[pic.height() - 1] = resultKey;
       for (int h = pic.height() - 2; h >= 0; h--) {
           result[h] = edgeTo[h + 1][result[h + 1]];
       }
       return result;
    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {

    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        Picture newPic = new Picture(pic.width() - 1, pic.height());
        for (int row = 0; row < pic.height(); row++) {
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

    public static void main(String[] args) {
        Picture test = new Picture(args[0]);
        SeamCarver carver = new SeamCarver(test);
        for (int i = 0; i < 200; i++) {
            int[] result = carver.findVerticalSeam();
//            carver.showVerticalPath(result, 1000);
            carver.removeVerticalSeam(result);
        }
        carver.picture().show();
    }
}