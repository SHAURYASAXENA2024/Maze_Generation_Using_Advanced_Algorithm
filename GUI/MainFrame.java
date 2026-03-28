package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MazeFrame extends JFrame {

    private MazePanel   mazePanel;
    private ControlPanel controlPanel;
    private InfoPanel    infoPanel;

    public MazeFrame() {
        setupFrame();
        createComponents();
        layoutComponents();
        registerKeyboardShortcuts();
    }

    private void setupFrame() {
        setTitle("MazeQuest — Algorithm Visualizer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1380, 860);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);
        getContentPane().setBackground(ColorScheme.BACKGROUND_DARK);

        // Try Nimbus for a more modern Swing feel
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {}

        // Global dark background for all panels
        UIManager.put("Panel.background",          ColorScheme.PANEL_DARK);
        UIManager.put("ComboBox.background",       ColorScheme.BUTTON_BG);
        UIManager.put("ComboBox.foreground",       ColorScheme.TEXT_PRIMARY);
        UIManager.put("ComboBox.selectionBackground", ColorScheme.BUTTON_HOVER);
        UIManager.put("ComboBox.selectionForeground", ColorScheme.TEXT_PRIMARY);
        UIManager.put("Slider.background",         ColorScheme.PANEL_DARK);
        UIManager.put("Slider.foreground",         ColorScheme.NEON_BLUE);
    }

    private void createComponents() {
        mazePanel    = new MazePanel();
        infoPanel    = new InfoPanel();
        controlPanel = new ControlPanel(mazePanel);
        controlPanel.setInfoPanel(infoPanel);
        mazePanel.setInfoPanel(infoPanel);
    }

    private void layoutComponents() {
        JPanel root = new JPanel(new BorderLayout(8, 8));
        root.setBackground(ColorScheme.BACKGROUND_DARK);
        root.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        // Left sidebar
        JScrollPane leftScroll = new JScrollPane(controlPanel);
        leftScroll.setPreferredSize(new Dimension(260, 0));
        leftScroll.setBorder(glassPane());
        leftScroll.getViewport().setBackground(ColorScheme.PANEL_DARK);
        leftScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        // Center maze
        JPanel centerWrap = glassWrapper();
        centerWrap.setLayout(new BorderLayout());
        centerWrap.add(mazePanel, BorderLayout.CENTER);

        // Right sidebar
        JScrollPane rightScroll = new JScrollPane(infoPanel);
        rightScroll.setPreferredSize(new Dimension(230, 0));
        rightScroll.setBorder(glassPane());
        rightScroll.getViewport().setBackground(ColorScheme.PANEL_DARK);
        rightScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        root.add(leftScroll,  BorderLayout.WEST);
        root.add(centerWrap,  BorderLayout.CENTER);
        root.add(rightScroll, BorderLayout.EAST);

        setContentPane(root);
    }

    private void registerKeyboardShortcuts() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager()
            .addKeyEventDispatcher(e -> {
                if (e.getID() != KeyEvent.KEY_PRESSED) return false;
                // Ignore if typing in a text field
                Component focused = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
                if (focused instanceof JTextField || focused instanceof JTextArea) return false;

                switch (e.getKeyCode()) {
                    case KeyEvent.VK_G: SwingUtilities.invokeLater(() -> controlPanel.getGenerateButton().doClick());  break;
                    case KeyEvent.VK_S: SwingUtilities.invokeLater(() -> controlPanel.getSolveButton().doClick());     break;
                    case KeyEvent.VK_C: SwingUtilities.invokeLater(() -> controlPanel.getClearPathButton().doClick()); break;
                    case KeyEvent.VK_R: SwingUtilities.invokeLater(() -> controlPanel.getResetButton().doClick());     break;
                    case KeyEvent.VK_SPACE:
                        SwingUtilities.invokeLater(() -> controlPanel.getPauseResumeButton().doClick());
                        return true;
                }
                return false;
            });
    }

    private Border glassPane() {
        return BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ColorScheme.BORDER_SUBTLE, 1),
            BorderFactory.createEmptyBorder(12, 12, 12, 12)
        );
    }

    private JPanel glassWrapper() {
        JPanel p = new JPanel();
        p.setBackground(ColorScheme.PANEL_GLASS);
        p.setBorder(glassPane());
        return p;
    }

    // ─── Entry point ─────────────────────────────────────────────────────────

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MazeFrame frame = new MazeFrame();
            frame.setVisible(true);
        });
    }
}