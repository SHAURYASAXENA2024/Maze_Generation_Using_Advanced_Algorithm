package GUI;

import models.Cell;
import models.Maze;
import pathfinding.PathfinderFactory;
import pathfinding.PathfinderFactory.Algorithm;
import pathfinding.PathfinderFactory.AlgorithmStat;
import pathfinding.PathfinderFactory.Result;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/** Runs all pathfinding algorithms and shows a comparison table. */
public final class AlgorithmCompareDialog {

    private AlgorithmCompareDialog() {}

    public static void show(Component parent, Maze maze, MazePanel mazePanel, InfoPanel infoPanel) {
        List<AlgorithmStat> stats = PathfinderFactory.compareAll(maze);

        String[] cols = {"Algorithm", "Path length", "Cells explored", "Time (ms)", "Found?"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        int minPath = Integer.MAX_VALUE;
        for (AlgorithmStat s : stats) {
            if (s.pathFound) {
                minPath = Math.min(minPath, s.pathLength);
            }
        }

        for (AlgorithmStat s : stats) {
            model.addRow(new Object[]{
                    displayName(s.algorithm),
                    s.pathFound ? Integer.valueOf(s.pathLength) : "—",
                    Integer.valueOf(s.visitedCount),
                    String.format(Locale.US, "%.3f", s.timeNanos / 1_000_000.0),
                    s.pathFound ? "Yes" : "No"
            });
        }

        JTable table = new JTable(model);
        table.setFillsViewportHeight(true);
        table.setRowHeight(22);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);
        styleTable(table);

        JTextArea summary = new JTextArea(buildSummary(stats, minPath));
        summary.setEditable(false);
        summary.setOpaque(false);
        summary.setForeground(ColorScheme.TEXT_PRIMARY);
        summary.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        summary.setLineWrap(true);
        summary.setWrapStyleWord(true);

        JLabel hint = new JLabel("Double-click a row to draw that algorithm’s visited cells and path on the maze.");
        hint.setForeground(ColorScheme.TEXT_SECONDARY);
        hint.setBorder(BorderFactory.createEmptyBorder(0, 8, 8, 8));

        Window win = parent instanceof Window ? (Window) parent : SwingUtilities.getWindowAncestor(parent);
        final JDialog dialog = new JDialog(win, "Compare pathfinding algorithms", Dialog.ModalityType.APPLICATION_MODAL);

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() != 2) {
                    return;
                }
                int row = table.rowAtPoint(e.getPoint());
                if (row < 0) {
                    return;
                }
                AlgorithmStat st = stats.get(row);
                if (!st.pathFound) {
                    return;
                }
                Result r = PathfinderFactory.solve(maze, st.algorithm);
                mazePanel.stopAnimation();
                mazePanel.clearVisualization();
                HashSet<String> vis = new HashSet<>();
                for (Cell c : r.visited) {
                    vis.add(c.getRow() + "," + c.getCol());
                }
                mazePanel.setVisited(vis);
                mazePanel.setPath(r.path);
                if (infoPanel != null) {
                    infoPanel.setStatus(displayName(st.algorithm));
                    infoPanel.updateVisited(r.visited.size());
                    infoPanel.updatePathLength(r.path.size());
                }
                dialog.dispose();
            }
        });

        JPanel tableWrap = new JPanel(new BorderLayout());
        tableWrap.setBackground(ColorScheme.PANEL_DARK);
        JScrollPane sp = new JScrollPane(table);
        sp.getViewport().setBackground(ColorScheme.PANEL_DARK);
        tableWrap.add(sp, BorderLayout.CENTER);

        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(ColorScheme.PANEL_DARK);
        root.add(summary, BorderLayout.NORTH);
        root.add(tableWrap, BorderLayout.CENTER);
        root.add(hint, BorderLayout.SOUTH);

        dialog.setContentPane(root);
        dialog.pack();
        dialog.setSize(new Dimension(580, 440));
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

    private static String buildSummary(List<AlgorithmStat> stats, int minPath) {
        if (minPath == Integer.MAX_VALUE) {
            return "No algorithm found a path. Place start and goal on open cells in the same connected region.";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Shortest path length: ").append(minPath).append(" cell(s).\n");
        List<String> optimal = new ArrayList<>();
        for (AlgorithmStat s : stats) {
            if (s.pathFound && s.pathLength == minPath) {
                optimal.add(displayName(s.algorithm));
            }
        }
        sb.append("Tied for shortest path: ").append(String.join(", ", optimal)).append(".\n");

        Optional<AlgorithmStat> fewestVis = stats.stream()
                .filter(s -> s.pathFound)
                .min(Comparator.comparingInt(s -> s.visitedCount));
        if (fewestVis.isPresent()) {
            AlgorithmStat fv = fewestVis.get();
            sb.append("Fewest cells explored: ").append(displayName(fv.algorithm))
                    .append(" (").append(fv.visitedCount).append(").\n");
        }

        Optional<AlgorithmStat> fastest = stats.stream()
                .filter(s -> s.pathFound)
                .min(Comparator.comparingLong(s -> s.timeNanos));
        if (fastest.isPresent()) {
            AlgorithmStat ft = fastest.get();
            sb.append("Fastest run (rough): ").append(displayName(ft.algorithm))
                    .append(" (").append(String.format(Locale.US, "%.3f ms", ft.timeNanos / 1_000_000.0)).append(").");
        }
        return sb.toString();
    }

    static String displayName(Algorithm a) {
        switch (a) {
            case DFS:
                return "DFS";
            case DIJKSTRA:
                return "Dijkstra";
            case GREEDY_BFS:
                return "Greedy best-first";
            case ASTAR:
                return "A*";
            case BFS:
            default:
                return "BFS";
        }
    }

    private static void styleTable(JTable t) {
        t.setBackground(ColorScheme.PANEL_DARK);
        t.setForeground(ColorScheme.TEXT_PRIMARY);
        t.setSelectionBackground(ColorScheme.BUTTON_HOVER);
        t.setSelectionForeground(ColorScheme.TEXT_PRIMARY);
        t.setGridColor(ColorScheme.BORDER_SUBTLE);
        t.getTableHeader().setBackground(ColorScheme.BUTTON_BG);
        t.getTableHeader().setForeground(ColorScheme.TEXT_PRIMARY);
    }
}
