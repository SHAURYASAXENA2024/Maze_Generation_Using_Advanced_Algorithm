package GUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/** Right sidebar stats and status. */
public class InfoPanel extends JPanel {

    private final JLabel statusLabel;
    private final JLabel visitedLabel;
    private final JLabel pathLabel;
    private final JLabel mazeSizeLabel;

    public InfoPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(ColorScheme.PANEL_DARK);
        setBorder(new EmptyBorder(8, 8, 8, 8));

        add(section("Status"));
        statusLabel = value("Ready");
        add(statusLabel);
        add(Box.createVerticalStrut(12));

        add(section("Maze"));
        mazeSizeLabel = value("— × —");
        add(mazeSizeLabel);
        add(Box.createVerticalStrut(12));

        add(section("Solve stats"));
        visitedLabel = value("Cells visited: —");
        add(visitedLabel);
        add(Box.createVerticalStrut(6));
        pathLabel = value("Path length: —");
        add(pathLabel);
        add(Box.createVerticalGlue());
    }

    private static JLabel section(String title) {
        JLabel l = new JLabel(title.toUpperCase());
        l.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 11));
        l.setForeground(ColorScheme.TEXT_SECONDARY);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private static JLabel value(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        l.setForeground(ColorScheme.TEXT_PRIMARY);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    public void setStatus(String s) {
        statusLabel.setText(s);
    }

    public void updateMazeSize(int rows, int cols) {
        if (rows <= 0 || cols <= 0) {
            mazeSizeLabel.setText("— × —");
        } else {
            mazeSizeLabel.setText(rows + " × " + cols);
        }
    }

    public void updateVisited(int n) {
        visitedLabel.setText("Cells visited: " + n);
    }

    public void updatePathLength(int n) {
        pathLabel.setText("Path length: " + n);
    }

    public void resetStats() {
        visitedLabel.setText("Cells visited: —");
        pathLabel.setText("Path length: —");
    }
}
