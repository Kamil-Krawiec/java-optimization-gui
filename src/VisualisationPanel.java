

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class VisualisationPanel extends JPanel {

    private JLabel imageLabel; // Label to display the image

    public VisualisationPanel() {
        setLayout(new BorderLayout());

        // Create the file chooser button
        JButton chooseFileButton = new JButton("Choose PNG File");
        add(chooseFileButton, BorderLayout.NORTH);

        // Create the label for displaying the image
        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        imageLabel.setVerticalAlignment(JLabel.CENTER);
        add(new JScrollPane(imageLabel), BorderLayout.CENTER);

        // Add an action listener to the button
        chooseFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();

                // Set file filter to allow only PNG files
                FileNameExtensionFilter filter = new FileNameExtensionFilter("PNG Images", "png");
                fileChooser.setFileFilter(filter);

                // Show the file chooser dialog
                int result = fileChooser.showOpenDialog(VisualisationPanel.this);

                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    displayImage(selectedFile);
                }
            }
        });
    }

    public void displayImage(File file) {
        try {
            // Read the image file
            BufferedImage image = ImageIO.read(file);

            // Set the image to the label
            ImageIcon icon = new ImageIcon(image);
            imageLabel.setIcon(icon);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading image: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

