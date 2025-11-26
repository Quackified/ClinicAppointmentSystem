package clinicapp.gui.components;

import clinicapp.gui.UIConstants;
import javax.swing.*;

// Custom label with support for required field indicator (red asterisk)
// and various font styles
public class StyledLabel extends JLabel {

    public StyledLabel(String text) {
        super(text);
        setFont(UIConstants.FONT_LABEL);
        setForeground(UIConstants.GRAY_700);
    }

    // Create a heading label
    public static StyledLabel createHeading(String text) {
        StyledLabel label = new StyledLabel(text);
        label.setFont(UIConstants.FONT_HEADING);
        label.setForeground(UIConstants.GRAY_800);
        return label;
    }

    // Create a subheading label
    public static StyledLabel createSubheading(String text) {
        StyledLabel label = new StyledLabel(text);
        label.setFont(UIConstants.FONT_SUBHEADING);
        label.setForeground(UIConstants.GRAY_700);
        return label;
    }

    // Create a small label
    public static StyledLabel createSmall(String text) {
        StyledLabel label = new StyledLabel(text);
        label.setFont(UIConstants.FONT_SMALL);
        label.setForeground(UIConstants.GRAY_600);
        return label;
    }

}
