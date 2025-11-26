package clinicapp.gui.components;

import clinicapp.gui.UIConstants;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

// Custom styled button with color variants matching the React UI
// Supports: Primary (blue), Success (green), Danger (red), Outline
public class StyledButton extends JButton {

    public enum ButtonVariant {
        PRIMARY, SUCCESS, DANGER, WARNING, OUTLINE
    }

    private ButtonVariant variant;
    private Color normalColor;
    private Color hoverColor;

    public StyledButton(String text, ButtonVariant variant) {
        super(text);
        this.variant = variant;
        setupButton();
    }

    private void setupButton() {
        setFont(UIConstants.FONT_BUTTON);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(true);
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Set preferred height
        setPreferredSize(new Dimension(getPreferredSize().width, UIConstants.BUTTON_HEIGHT));

        // Apply colors based on variant
        switch (variant) {
            case PRIMARY:
                normalColor = UIConstants.PRIMARY_BLUE;
                hoverColor = UIConstants.PRIMARY_BLUE_HOVER;
                setForeground(Color.WHITE);
                break;
            case SUCCESS:
                normalColor = UIConstants.SUCCESS_GREEN;
                hoverColor = UIConstants.SUCCESS_GREEN_DARK;
                setForeground(Color.WHITE);
                break;
            case DANGER:
                normalColor = UIConstants.DANGER_RED;
                hoverColor = UIConstants.DANGER_RED_DARK;
                setForeground(Color.WHITE);
                break;
            case WARNING:
                normalColor = UIConstants.WARNING_AMBER;
                hoverColor = UIConstants.WARNING_AMBER_DARK;
                setForeground(Color.WHITE);
                break;
            case OUTLINE:
                normalColor = Color.WHITE;
                hoverColor = UIConstants.GRAY_50;
                setForeground(UIConstants.GRAY_700);
                setBorderPainted(true);
                setBorder(BorderFactory.createLineBorder(UIConstants.GRAY_300, 1));
                break;
        }

        setBackground(normalColor);

        // Add hover effect
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (isEnabled()) {
                    setBackground(hoverColor);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (isEnabled()) {
                    setBackground(normalColor);
                }
            }
        });
    }

    // Factory methods for easy creation
    public static StyledButton createPrimary(String text) {
        return new StyledButton(text, ButtonVariant.PRIMARY);
    }

    public static StyledButton createSuccess(String text) {
        return new StyledButton(text, ButtonVariant.SUCCESS);
    }

    public static StyledButton createDanger(String text) {
        return new StyledButton(text, ButtonVariant.DANGER);
    }

    public static StyledButton createOutline(String text) {
        return new StyledButton(text, ButtonVariant.OUTLINE);
    }
}
