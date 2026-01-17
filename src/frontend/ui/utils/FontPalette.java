package ui.utils;

import java.awt.Font;

public class FontPalette {
    // Centralized font palette for consistent typography
    public static final String FAMILY_PRIMARY = "Segoe UI"; // Fallback to default if unavailable

    public static final Font H1 = new Font(FAMILY_PRIMARY, Font.BOLD, 24);
    public static final Font H2 = new Font(FAMILY_PRIMARY, Font.BOLD, 20);
    public static final Font H3 = new Font(FAMILY_PRIMARY, Font.BOLD, 16);

    public static final Font BODY = new Font(FAMILY_PRIMARY, Font.PLAIN, 14);
    public static final Font BODY_SMALL = new Font(FAMILY_PRIMARY, Font.PLAIN, 12);

    public static final Font BUTTON = new Font(FAMILY_PRIMARY, Font.BOLD, 13);
    public static final Font CAPTION = new Font(FAMILY_PRIMARY, Font.ITALIC, 12);
}
