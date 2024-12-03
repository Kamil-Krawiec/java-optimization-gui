import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class GeneratePanel extends JPanel {

    private JTextField directoryField;
    private JTextField filenameField;
    private JTextField numberOfTasksField;
    private JTextField numberOfResourcesField;
    private JTextField precedenceRelationsField;
    private JTextField numberOfSkillsField;
    private JButton generateButton;

    public GeneratePanel() {
        setLayout(new GridBagLayout());
        JPanel centeredPanel = new JPanel();
        centeredPanel.setLayout(new BoxLayout(centeredPanel, BoxLayout.Y_AXIS));

        JPanel directoryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel directoryLabel = new JLabel("Directory:");
        directoryField = createTextField(20);
        directoryField.setEditable(false);
        JButton browseDirectoryButton = new JButton("Choose Directory");
        browseDirectoryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chooseDirectory();
            }
        });
        directoryPanel.add(directoryLabel);
        directoryPanel.add(directoryField);
        directoryPanel.add(browseDirectoryButton);

        JPanel parametersPanel = new JPanel();
        parametersPanel.setLayout(new GridLayout(6, 2, 5, 5));

        parametersPanel.add(new JLabel("Filename:"));
        filenameField = createTextField(15);
        parametersPanel.add(filenameField);

        parametersPanel.add(new JLabel("Number of tasks:"));
        numberOfTasksField = createTextField(10);
        parametersPanel.add(numberOfTasksField);

        parametersPanel.add(new JLabel("Number of resources:"));
        numberOfResourcesField = createTextField(10);
        parametersPanel.add(numberOfResourcesField);

        parametersPanel.add(new JLabel("Number of precedence relations:"));
        precedenceRelationsField = createTextField(10);
        parametersPanel.add(precedenceRelationsField);

        parametersPanel.add(new JLabel("Number of skills:"));
        numberOfSkillsField = createTextField(10);
        parametersPanel.add(numberOfSkillsField);

        centeredPanel.add(directoryPanel);
        centeredPanel.add(Box.createVerticalStrut(10));
        centeredPanel.add(parametersPanel);
        centeredPanel.add(Box.createVerticalStrut(10));

        JPanel generateButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        generateButton = new JButton("Generate");
        generateButton.setEnabled(false);
        generateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateFiles();
            }
        });

        generateButtonPanel.add(generateButton);
        centeredPanel.add(generateButtonPanel);

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

        filenameField.getDocument().addDocumentListener(documentListener);
        numberOfTasksField.getDocument().addDocumentListener(documentListener);
        numberOfResourcesField.getDocument().addDocumentListener(documentListener);
        precedenceRelationsField.getDocument().addDocumentListener(documentListener);
        numberOfSkillsField.getDocument().addDocumentListener(documentListener);

        add(centeredPanel);
    }

    private JTextField createTextField(int columns) {
        JTextField textField = new JTextField(columns);
        textField.setPreferredSize(new Dimension(100, 25));
        return textField;
    }

    private void chooseDirectory() {
        JFileChooser directoryChooser = new JFileChooser();
        directoryChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int result = directoryChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedDirectory = directoryChooser.getSelectedFile();
            directoryField.setText(selectedDirectory.getAbsolutePath());
        }
        validateInputs();
    }

    private void generateFiles() {
        String directoryPath = directoryField.getText();
        String filename = filenameField.getText();
        String numberOfTasks = numberOfTasksField.getText();
        String numberOfResources = numberOfResourcesField.getText();
        String precedenceRelations = precedenceRelationsField.getText();
        String numberOfSkills = numberOfSkillsField.getText();

        System.out.println("Directory Path: " + directoryPath);
        System.out.println("Filename: " + filename);
        System.out.println("Number of Tasks: " + numberOfTasks);
        System.out.println("Number of Resources: " + numberOfResources);
        System.out.println("Precedence Relations: " + precedenceRelations);
        System.out.println("Number of Skills: " + numberOfSkills);
    }

    private void validateInputs() {
        boolean isValid = true;

        isValid &= !directoryField.getText().trim().isEmpty();
        isValid &= !filenameField.getText().trim().isEmpty();
        isValid &= !numberOfTasksField.getText().trim().isEmpty() && isInteger(numberOfTasksField.getText().trim());
        isValid &= !numberOfResourcesField.getText().trim().isEmpty() && isInteger(numberOfResourcesField.getText().trim());
        isValid &= !precedenceRelationsField.getText().trim().isEmpty() && isInteger(precedenceRelationsField.getText().trim());
        isValid &= !numberOfSkillsField.getText().trim().isEmpty() && isInteger(numberOfSkillsField.getText().trim());

        generateButton.setEnabled(isValid);
    }

    private boolean isInteger(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
