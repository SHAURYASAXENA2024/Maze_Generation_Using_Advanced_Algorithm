
import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    
    private MazePanel mazePanel;
    private ControlPanel controlPanel;
    private StatsPanel statsPanel;
    
    public MainFrame() {
        // Window setup
        setTitle("Maze Quest - Pathfinding Adventures");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);  // Center on screen
        setLayout(new BorderLayout(10, 10));
        
        // Create panels
        mazePanel = new MazePanel();
        controlPanel = new ControlPanel(mazePanel);
        statsPanel = new StatsPanel();
        
        // Add panels to frame
        add(controlPanel, BorderLayout.NORTH);
        add(mazePanel, BorderLayout.CENTER);
        add(statsPanel, BorderLayout.SOUTH);
        
        // Connect stats panel to control panel
        controlPanel.setStatsPanel(statsPanel);
    }
    
    public static void main(String[] args) {
        // Run GUI on Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}