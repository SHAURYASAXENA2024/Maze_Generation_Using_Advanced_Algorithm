import java.util.*;

public class RecursiveBacktracker {

    static int rows = 5;
    static int cols = 5;

    static boolean[][] visited = new boolean[rows][cols];

    static int[] dx = {-1, 1, 0, 0};
    static int[] dy = {0, 0, -1, 1};

    static void generateMaze(int x, int y) {

        visited[x][y] = true;

        List<Integer> directions = Arrays.asList(0,1,2,3);
        Collections.shuffle(directions);

        for (int dir : directions) {

            int nx = x + dx[dir];
            int ny = y + dy[dir];

            if (nx >= 0 && ny >= 0 && nx < rows && ny < cols && !visited[nx][ny]) {

                System.out.println("Move from (" + x + "," + y + ") to (" + nx + "," + ny + ")");

                generateMaze(nx, ny);
            }
        }
    }

    public static void main(String[] args) {

        generateMaze(0,0);

    }
}