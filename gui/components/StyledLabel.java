package clinicapp.gui.components;

import clinicapp.gui.UIConstants;
import javax.swing.*;
import java.awt.*;

/**
 * Custom label with support for required field indicator (red asterisk)
 * and various font styles
 */
public class StyledLabel extends JLabel {

    private boolean isRequired = false;

    public StyledLabel(String text) {
        super(text);
        setFont(UIConstants.FONT_LABEL);
        setForeground(UIConstants.GRAY_700);
    }

    public StyledLabel(String text, boolean isRequired) {
        this(text);
        this.isRequired = isRequired;
        updateText();
    }

    /**
     * Set whether this label marks a required field
     */
    public void setRequired(boolean required) {
        this.isRequired = required;
        updateText();
    }

    private void updateText() {
        String baseText = getText();
        // Remove existing asterisk if present
        if (baseText.contains("<html>")) {
            baseText = baseText.replaceAll("<html>|</html>|<font[^>]*>|</font>|\\s*\\*", "").trim();
        }

        if (isRequired) {
            // Add red asterisk using HTML
            setText("<html>" + baseText + " <font color='red'>*</font></html>");
        } else {
            setText(baseText);
        }
    }

    /**
     * Create a bold label
     */
    public static StyledLabel createBold(String text) {
        StyledLabel label = new StyledLabel(text);
        label.setFont(UIConstants.FONT_LABEL_BOLD);
        return label;
    }

    /**
     * Create a heading label
     */
    public static StyledLabel createHeading(String text) {
        StyledLabel label = new StyledLabel(text);
        label.setFont(UIConstants.FONT_HEADING);
        label.setForeground(UIConstants.GRAY_800);
        return label;
    }

    /**
     * Create a subheading label
     */
    public static StyledLabel createSubheading(String text) {
        StyledLabel label = new StyledLabel(text);
        label.setFont(UIConstants.FONT_SUBHEADING);
        label.setForeground(UIConstants.GRAY_700);
        return label;
    }

    /**
     * Create a small label
     */
    public static StyledLabel createSmall(String text) {
        StyledLabel label = new StyledLabel(text);
        label.setFont(UIConstants.FONT_SMALL);
        label.setForeground(UIConstants.GRAY_600);
        return label;
    }

    /**
     * Create a required field label with red asterisk
     */
    public static StyledLabel createRequired(String text) {
        return new StyledLabel(text, true);
    }
}
