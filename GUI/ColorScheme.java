package GUI;

import java.awt.Color;

/** Dark theme palette shared by Swing components. */
public final class ColorScheme {

    private ColorScheme() {}

    public static final Color BACKGROUND_DARK = new Color(0x0d, 0x11, 0x17);
    public static final Color PANEL_DARK      = new Color(0x14, 0x18, 0x20);
    public static final Color PANEL_GLASS     = new Color(0x18, 0x1c, 0x26);
    public static final Color BORDER_SUBTLE   = new Color(0x2a, 0x32, 0x40);

    public static final Color BUTTON_BG       = new Color(0x22, 0x28, 0x34);
    public static final Color BUTTON_HOVER    = new Color(0x2d, 0x36, 0x46);
    public static final Color BUTTON_PRESS    = new Color(0x1a, 0x1f, 0x28);

    public static final Color TEXT_PRIMARY    = new Color(0xe8, 0xec, 0xf0);
    public static final Color TEXT_SECONDARY  = new Color(0x8b, 0x92, 0x9e);
    public static final Color NEON_BLUE       = new Color(0x38, 0xbf, 0xff);

    public static final Color CELL_START      = new Color(0x22, 0xc5, 0x5e);
    public static final Color CELL_END        = new Color(0xf4, 0x71, 0x71);
    /** Base fill for wall cells */
    public static final Color CELL_WALL       = new Color(0x3d, 0x2a, 0x22);
    /** Dark edge drawn on walls for depth */
    public static final Color CELL_WALL_EDGE  = new Color(0x1a, 0x12, 0x0e);
    public static final Color CELL_WALL_HIGHLIGHT = new Color(0x5c, 0x40, 0x32);
    /** Open passage before any search visualization */
    public static final Color CELL_PATH       = new Color(0x2a, 0x2e, 0x38);
    /** Open cell not yet expanded by the current search */
    public static final Color CELL_UNVISITED_SEARCH = new Color(0xc4, 0x1e, 0x2e);
    /** Expanded / visited during search */
    public static final Color CELL_VISITED    = new Color(0xf5, 0xd9, 0x0a);
    public static final Color CELL_FINAL_PATH = new Color(0x2d, 0xa0, 0xff);
    /** User-drawn path while solving manually */
    public static final Color CELL_MANUAL_TRAIL = new Color(0xe8, 0x7d, 0x00);

    public static final Color GRID_LINE       = new Color(0x2a, 0x32, 0x40);
}
