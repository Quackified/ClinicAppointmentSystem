package clinicapp.gui.components;

import clinicapp.gui.UIConstants;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

// Enhanced text field with better styling and validation support
public class StyledTextField extends JTextField {



    public StyledTextField() {
        super();
        setupTextField();
    }

    public StyledTextField(int columns) {
        super(columns);
        setupTextField();
    }

    public StyledTextField(String text) {
        super(text);
        setupTextField();
    }

    private void setupTextField() {
        setFont(UIConstants.FONT_BODY);
        setBorder(UIConstants.createInputBorder());
        setPreferredSize(new Dimension(getPreferredSize().width, UIConstants.INPUT_HEIGHT));

        // Add focus listener for border color change
        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                setBorder(UIConstants.createFocusBorder());
            }

            @Override
            public void focusLost(FocusEvent e) {
                setBorder(UIConstants.createInputBorder());
            }
        });
    }

}
