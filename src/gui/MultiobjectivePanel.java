package gui;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class MultiobjectivePanel extends JPanel {

    private JTextField filePathField;
    private JTextField mutationRateField;
    private JTextField crossoverRateField;
    private JButton runButton;

    public MultiobjectivePanel() {
        setLayout(new GridBagLayout());
        JPanel centeredPanel = new JPanel();
        centeredPanel.setLayout(new BoxLayout(centeredPanel, BoxLayout.Y_AXIS));

        JPanel filePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel instanceLabel = new JLabel("Instance:");
        filePathField = createTextField(20);
        filePathField.setEditable(false);
        JButton browseButton = new JButton("Wybierz plik");
        browseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chooseFile();
            }
        });
        filePanel.add(instanceLabel);
        filePanel.add(filePathField);
        filePanel.add(browseButton);

        JPanel parametersPanel = new JPanel();
        parametersPanel.setLayout(new GridLayout(3, 2, 5, 5));

        parametersPanel.add(new JLabel("Mutation rate:"));
        mutationRateField = createTextField(10);
        parametersPanel.add(mutationRateField);

        parametersPanel.add(new JLabel("Crossover rate:"));
        crossoverRateField = createTextField(10);
        parametersPanel.add(crossoverRateField);

        centeredPanel.add(filePanel);
        centeredPanel.add(Box.createVerticalStrut(10));
        centeredPanel.add(parametersPanel);
        centeredPanel.add(Box.createVerticalStrut(10));

        JPanel runButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        runButton = new JButton("Run");
        runButton.setEnabled(false);
        runButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                runAlgorithm();
            }
        });

        runButtonPanel.add(runButton);
        centeredPanel.add(runButtonPanel);

        DocumentListener documentListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                validateInputs();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                validateInputs();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                validateInputs();
            }
        };

        mutationRateField.getDocument().addDocumentListener(documentListener);
        crossoverRateField.getDocument().addDocumentListener(documentListener);

        add(centeredPanel);
    }

    private JTextField createTextField(int columns) {
        JTextField textField = new JTextField(columns);
        textField.setPreferredSize(new Dimension(100, 25));
        return textField;
    }

    private void chooseFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith(".rtf");
            }

            @Override
            public String getDescription() {
                return "Rich Text Format Files (*.rtf)";
            }
        });

        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            filePathField.setText(selectedFile.getAbsolutePath());
        }
        validateInputs();
    }

    private void runAlgorithm() {
        String filePath = filePathField.getText();
        String mutationRate = mutationRateField.getText();
        String crossoverRate = crossoverRateField.getText();

        System.out.println("File Path: " + filePath);
        System.out.println("Mutation Rate: " + mutationRate);
        System.out.println("Crossover Rate: " + crossoverRate);
    }

    private void validateInputs() {
        boolean isValid = true;

        isValid &= !filePathField.getText().trim().isEmpty();
        isValid &= !mutationRateField.getText().trim().isEmpty() && isFloat(mutationRateField.getText().trim());
        isValid &= !crossoverRateField.getText().trim().isEmpty() && isFloat(crossoverRateField.getText().trim());

        runButton.setEnabled(isValid);
    }

    private boolean isFloat(String str) {
        try {
            Float.parseFloat(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
