package clinicapp.gui.components;

import clinicapp.gui.UIConstants;
import javax.swing.*;
import java.awt.*;

/**
 * Breadcrumb navigation component
 * Shows navigation path like: Dashboard > Patients
 */
public class BreadcrumbPanel extends JPanel {

    public BreadcrumbPanel(String... breadcrumbs) {
        setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        setBackground(UIConstants.GRAY_50);
        setBorder(BorderFactory.createEmptyBorder(10, 20, 5, 20));

        for (int i = 0; i < breadcrumbs.length; i++) {
            JLabel label = new JLabel(breadcrumbs[i]);
            label.setFont(UIConstants.FONT_BODY);

            if (i == breadcrumbs.length - 1) {
                // Last breadcrumb - current page
                label.setForeground(UIConstants.PRIMARY_BLUE);
                label.setFont(new Font(label.getFont().getName(), Font.BOLD, label.getFont().getSize()));
            } else {
                // Previous breadcrumbs
                label.setForeground(UIConstants.GRAY_600);
            }

            add(label);

            // Add separator except for last item
            if (i < breadcrumbs.length - 1) {
                JLabel separator = new JLabel(">");
                separator.setForeground(UIConstants.GRAY_400);
                separator.setFont(UIConstants.FONT_BODY);
                add(separator);
            }
        }
    }
}
