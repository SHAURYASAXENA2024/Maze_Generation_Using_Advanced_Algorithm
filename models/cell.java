import java.awt.*;
import javax.swing.*;

public class cell extends JPanel {

    int[][] maze = {
        {1,1,1,1,1},
        {1,0,0,0,1},
        {1,0,1,0,1},
        {1,0,0,0,1},
        {1,1,1,1,1}
    };

    int cellSize = 80;

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        for(int i=0;i<maze.length;i++){
            for(int j=0;j<maze[0].length;j++){

                if(maze[i][j]==1)
                    g.setColor(Color.BLACK);
                else
                    g.setColor(Color.WHITE);

                g.fillRect(j*cellSize, i*cellSize, cellSize, cellSize);

                g.setColor(Color.GRAY);
                g.drawRect(j*cellSize, i*cellSize, cellSize, cellSize);
            }
        }
    }

    public static void main(String[] args) {

        JFrame frame = new JFrame("Maze Grid");

        cell panel = new cell();

        frame.add(panel);
        frame.setSize(500,500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}