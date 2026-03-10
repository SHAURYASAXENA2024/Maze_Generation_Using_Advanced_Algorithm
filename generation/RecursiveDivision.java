import java.util.*;

public class RecursiveDivision {

    static int rows = 15;
    static int cols = 25;
    static char[][] maze = new char[rows][cols];
    static Random rand = new Random();

    static void divide(int top, int bottom, int left, int right) {

        if (bottom - top < 2 || right - left < 2)
            return;

        boolean horizontal = (bottom - top) > (right - left);

        if (horizontal) {

            int wall = rand.nextInt(bottom - top - 1) + top + 1;
            int gap = rand.nextInt(right - left + 1) + left;

            for (int i = left; i <= right; i++) {
                if (i == gap) continue;
                maze[wall][i] = '#';
            }

            divide(top, wall - 1, left, right);
            divide(wall + 1, bottom, left, right);

        } else {

            int wall = rand.nextInt(right - left - 1) + left + 1;
            int gap = rand.nextInt(bottom - top + 1) + top;

            for (int i = top; i <= bottom; i++) {
                if (i == gap) continue;
                maze[i][wall] = '#';
            }

            divide(top, bottom, left, wall - 1);
            divide(top, bottom, wall + 1, right);
        }
    }

    static void printMaze() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                System.out.print(maze[i][j]);
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {

        for (int i = 0; i < rows; i++)
            Arrays.fill(maze[i], ' ');

        divide(0, rows - 1, 0, cols - 1);

        printMaze();
    }
}