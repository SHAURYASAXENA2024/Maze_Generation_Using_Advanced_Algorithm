package main;

import GUI.MainFrame;

import javax.swing.SwingUtilities;

/**
 * Application entry point. Equivalent to {@link MainFrame#main(String[])}.
 */
public class main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
