package clinicapp.gui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

// UI Constants for the Clinic Management System
// Contains color palette, fonts, and styling helpers
public class UIConstants {

    // COLOR PALETTE
    // Primary Colors (Blue)
    public static final Color PRIMARY_BLUE = new Color(37, 99, 235); // rgba(37, 99, 235, 1)
    public static final Color PRIMARY_BLUE_LIGHT = new Color(219, 234, 254); // #dbeafe
    public static final Color PRIMARY_BLUE_DARK = new Color(30, 64, 175); // #1e40af
    public static final Color PRIMARY_BLUE_HOVER = new Color(29, 78, 216); // #1d4ed8

    // Success Colors (Green)
    public static final Color SUCCESS_GREEN = new Color(22, 163, 74); // #16a34a
    public static final Color SUCCESS_GREEN_LIGHT = new Color(220, 252, 231); // #dcfce7
    public static final Color SUCCESS_GREEN_DARK = new Color(21, 128, 61); // #15803d

    // Warning Colors (Amber/Yellow)
    public static final Color WARNING_AMBER = new Color(245, 158, 11); // #f59e0b
    public static final Color WARNING_AMBER_LIGHT = new Color(254, 243, 199); // #fef3c7
    public static final Color WARNING_AMBER_DARK = new Color(217, 119, 6); // #d97706

    // Danger Colors (Red)
    public static final Color DANGER_RED = new Color(220, 38, 38); // #dc2626
    public static final Color DANGER_RED_LIGHT = new Color(254, 226, 226); // #fee2e2
    public static final Color DANGER_RED_DARK = new Color(185, 28, 28); // #b91c1c

    // Cyan (for secondary elements)
    public static final Color CYAN_500 = new Color(6, 182, 212); // #06b6d4
    public static final Color CYAN_50 = new Color(236, 254, 255); // #ecfeff

    // Purple (for accents)
    public static final Color PURPLE_600 = new Color(147, 51, 234); // #9333ea
    public static final Color PURPLE_50 = new Color(250, 245, 255); // #faf5ff

    // Neutral/Gray Colors
    public static final Color GRAY_50 = new Color(249, 250, 251); // #f9fafb
    public static final Color GRAY_100 = new Color(243, 244, 246); // #f3f4f6
    public static final Color GRAY_200 = new Color(229, 231, 235); // #e5e7eb
    public static final Color GRAY_300 = new Color(209, 213, 219); // #d1d5db
    public static final Color GRAY_400 = new Color(156, 163, 175); // #9ca3af
    public static final Color GRAY_500 = new Color(107, 114, 128); // #6b7280
    public static final Color GRAY_600 = new Color(75, 85, 99); // #4b5563
    public static final Color GRAY_700 = new Color(55, 65, 81); // #374151
    public static final Color GRAY_800 = new Color(31, 41, 55); // #1f2937
    public static final Color GRAY_900 = new Color(17, 24, 39); // #111827

    // Base Colors
    public static final Color WHITE = Color.WHITE;
    public static final Color BLACK = Color.BLACK;

    // FONTS
    public static final Font FONT_TITLE = new Font("Arial", Font.BOLD, 24);
    public static final Font FONT_HEADING = new Font("Arial", Font.BOLD, 18);
    public static final Font FONT_SUBHEADING = new Font("Arial", Font.BOLD, 16);
    public static final Font FONT_LABEL = new Font("Arial", Font.PLAIN, 14);
    public static final Font FONT_LABEL_BOLD = new Font("Arial", Font.BOLD, 14);
    public static final Font FONT_BODY = new Font("Arial", Font.PLAIN, 13);
    public static final Font FONT_SMALL = new Font("Arial", Font.PLAIN, 12);
    public static final Font FONT_BUTTON = new Font("Arial", Font.BOLD, 13);

    // SPACING
    public static final int PADDING_SMALL = 5;
    public static final int PADDING_MEDIUM = 10;
    public static final int PADDING_LARGE = 15;
    public static final int PADDING_XLARGE = 20;

    public static final int MARGIN_SMALL = 5;
    public static final int MARGIN_MEDIUM = 10;
    public static final int MARGIN_LARGE = 15;

    // DIMENSIONS
    public static final int BUTTON_HEIGHT = 36;
    public static final int INPUT_HEIGHT = 36;
    public static final int SIDEBAR_WIDTH = 240;
    public static final int BORDER_RADIUS = 6;

    // HELPER METHODS
    // Creates a card-style border with subtle shadow effect
    public static Border createCardBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GRAY_200, 1),
                BorderFactory.createEmptyBorder(PADDING_LARGE, PADDING_LARGE, PADDING_LARGE, PADDING_LARGE));
    }

    // Creates a card border with colored left accent
    public static Border createCardBorderWithAccent(Color accentColor) {
        Border leftBorder = BorderFactory.createMatteBorder(0, 4, 0, 0, accentColor);
        Border outerBorder = BorderFactory.createLineBorder(GRAY_200, 1);
        Border padding = BorderFactory.createEmptyBorder(PADDING_LARGE, PADDING_LARGE, PADDING_LARGE, PADDING_LARGE);

        return BorderFactory.createCompoundBorder(
                BorderFactory.createCompoundBorder(outerBorder, leftBorder),
                padding);
    }

    // Creates a simple padding border
    public static Border createPaddingBorder(int padding) {
        return BorderFactory.createEmptyBorder(padding, padding, padding, padding);
    }

    // Creates a focus border for input fields
    public static Border createFocusBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(PRIMARY_BLUE, 2),
                BorderFactory.createEmptyBorder(6, 10, 6, 10));
    }

    // Creates a normal border for input fields
    public static Border createInputBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GRAY_300, 1),
                BorderFactory.createEmptyBorder(7, 11, 7, 11));
    }
}
