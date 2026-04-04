package GUI;

import javax.swing.*;
import java.awt.*;

/** Flat, rounded action button for the control sidebar. */
public class ModernButton extends JButton {

    public ModernButton(String text) {
        super(text);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setForeground(ColorScheme.TEXT_PRIMARY);
        setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setMargin(new Insets(8, 14, 8, 14));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int w = getWidth(), h = getHeight();
        Color bg = getModel().isPressed() ? ColorScheme.BUTTON_PRESS
                : getModel().isRollover() ? ColorScheme.BUTTON_HOVER : ColorScheme.BUTTON_BG;
        g2.setColor(bg);
        g2.fillRoundRect(0, 0, w, h, 10, 10);
        g2.setColor(ColorScheme.BORDER_SUBTLE);
        g2.drawRoundRect(0, 0, w - 1, h - 1, 10, 10);
        g2.dispose();
        super.paintComponent(g);
    }
}
