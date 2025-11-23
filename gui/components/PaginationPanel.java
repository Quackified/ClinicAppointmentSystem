package clinicapp.gui.components;

import clinicapp.gui.UIConstants;
import javax.swing.*;
import java.awt.*;

/**
 * Reusable pagination component for tables
 * Provides Previous, Next, and Jump to Page controls
 */
public class PaginationPanel extends JPanel {

    private int currentPage = 1;
    private int totalPages = 1;
    private int itemsPerPage = 10;

    private JButton prevButton;
    private JButton nextButton;
    private JLabel pageLabel;
    private JTextField jumpField;
    private JButton jumpButton;

    private PaginationCallback callback;

    public interface PaginationCallback {
        void onPageChanged(int newPage);
    }

    public PaginationPanel(PaginationCallback callback) {
        this.callback = callback;
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UIConstants.GRAY_200));

        // Previous button
        prevButton = StyledButton.createOutline("← Previous");
        prevButton.setPreferredSize(new Dimension(100, 35));
        prevButton.addActionListener(e -> previousPage());

        // Page label
        pageLabel = new JLabel("Page 1 of 1");
        pageLabel.setFont(UIConstants.FONT_LABEL);
        pageLabel.setForeground(UIConstants.GRAY_700);
        pageLabel.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));

        // Next button
        nextButton = StyledButton.createOutline("Next →");
        nextButton.setPreferredSize(new Dimension(100, 35));
        nextButton.addActionListener(e -> nextPage());

        // Jump to page controls
        JLabel jumpLabel = new JLabel("Go to:");
        jumpLabel.setFont(UIConstants.FONT_LABEL);
        jumpLabel.setForeground(UIConstants.GRAY_700);
        jumpLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 5));

        jumpField = new JTextField(3);
        jumpField.setFont(UIConstants.FONT_BODY);
        jumpField.setPreferredSize(new Dimension(50, 35));

        jumpButton = StyledButton.createPrimary("Go");
        jumpButton.setPreferredSize(new Dimension(60, 35));
        jumpButton.addActionListener(e -> jumpToPage());

        // Add components
        add(prevButton);
        add(pageLabel);
        add(nextButton);
        add(jumpLabel);
        add(jumpField);
        add(jumpButton);

        updateButtonStates();
    }

    public void setTotalItems(int totalItems) {
        this.totalPages = (int) Math.ceil((double) totalItems / itemsPerPage);
        if (totalPages < 1)
            totalPages = 1;

        // Reset to page 1 if current page exceeds total
        if (currentPage > totalPages) {
            currentPage = totalPages;
        }

        updateDisplay();
    }

    public void setItemsPerPage(int itemsPerPage) {
        this.itemsPerPage = itemsPerPage;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getItemsPerPage() {
        return itemsPerPage;
    }

    public int getStartIndex() {
        return (currentPage - 1) * itemsPerPage;
    }

    public int getEndIndex(int totalItems) {
        return Math.min(currentPage * itemsPerPage, totalItems);
    }

    private void previousPage() {
        if (currentPage > 1) {
            currentPage--;
            updateDisplay();
            notifyPageChanged();
        }
    }

    private void nextPage() {
        if (currentPage < totalPages) {
            currentPage++;
            updateDisplay();
            notifyPageChanged();
        }
    }

    private void jumpToPage() {
        try {
            int page = Integer.parseInt(jumpField.getText().trim());
            if (page >= 1 && page <= totalPages) {
                currentPage = page;
                updateDisplay();
                notifyPageChanged();
                jumpField.setText("");
            } else {
                JOptionPane.showMessageDialog(this,
                        "Please enter a page number between 1 and " + totalPages,
                        "Invalid Page",
                        JOptionPane.WARNING_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a valid page number",
                    "Invalid Input",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void updateDisplay() {
        pageLabel.setText("Page " + currentPage + " of " + totalPages);
        updateButtonStates();
    }

    private void updateButtonStates() {
        prevButton.setEnabled(currentPage > 1);
        nextButton.setEnabled(currentPage < totalPages);
    }

    private void notifyPageChanged() {
        if (callback != null) {
            callback.onPageChanged(currentPage);
        }
    }

    public void reset() {
        currentPage = 1;
        updateDisplay();
    }
}
