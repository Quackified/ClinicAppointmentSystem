package clinicapp.gui.components;

import clinicapp.gui.UIConstants;
import javax.swing.*;
import java.awt.*;

// Card-style panel with optional colored left border accent
// Used for creating modern card layouts
public class StyledPanel extends JPanel {

    public StyledPanel() {
        super();
        setBackground(Color.WHITE);
        setBorder(UIConstants.createCardBorder());
    }

    public StyledPanel(LayoutManager layout) {
        super(layout);
        setBackground(Color.WHITE);
        setBorder(UIConstants.createCardBorder());
    }

    // Create a card panel with colored left accent
    public static StyledPanel createWithAccent(Color accentColor) {
        StyledPanel panel = new StyledPanel();
        panel.setBorder(UIConstants.createCardBorderWithAccent(accentColor));
        return panel;
    }

    // Create a card panel with colored left accent and specific layout
    public static StyledPanel createWithAccent(LayoutManager layout, Color accentColor) {
        StyledPanel panel = new StyledPanel(layout);
        panel.setBorder(UIConstants.createCardBorderWithAccent(accentColor));
        return panel;
    }



}
