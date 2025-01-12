
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

public class AntColonyPanel extends JPanel {

    private JTextField filePathField;
    private JTextField antsField;
    private JTextField alphaField;
    private JTextField betaField;
    private JTextField evaporationField;
    private JTextField iterationsField;
    private JComboBox<String> focusDropdown;
    private JButton runButton;

    public AntColonyPanel(VisualisationPanel visualisationPanel, JTabbedPane analysisPane) {
        setLayout(new GridBagLayout());
        JPanel centeredPanel = new JPanel();
        centeredPanel.setLayout(new BoxLayout(centeredPanel, BoxLayout.Y_AXIS));

        JPanel filePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel instanceLabel = new JLabel("Instance:");
        filePathField = createTextField(20);
        filePathField.setEditable(false);
        JButton browseButton = new JButton("Choose file");
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
        parametersPanel.setLayout(new GridLayout(6, 2, 5, 5));

        parametersPanel.add(new JLabel("Number of ants:"));
        antsField = createTextField(10);
        parametersPanel.add(antsField);

        parametersPanel.add(new JLabel("Alpha:"));
        alphaField = createTextField(10);
        parametersPanel.add(alphaField);

        parametersPanel.add(new JLabel("Beta:"));
        betaField = createTextField(10);
        parametersPanel.add(betaField);

        parametersPanel.add(new JLabel("Evaporation rate:"));
        evaporationField = createTextField(10);
        parametersPanel.add(evaporationField);

        parametersPanel.add(new JLabel("Number of iterations:"));
        iterationsField = createTextField(10);
        parametersPanel.add(iterationsField);

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
                try {
                    runAlgorithm(visualisationPanel, analysisPane);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
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

        antsField.getDocument().addDocumentListener(documentListener);
        alphaField.getDocument().addDocumentListener(documentListener);
        betaField.getDocument().addDocumentListener(documentListener);
        evaporationField.getDocument().addDocumentListener(documentListener);
        iterationsField.getDocument().addDocumentListener(documentListener);
        
        parametersPanel.add(new JLabel("Focus on:"));
        focusDropdown = new JComboBox<>(new String[]{"Duration", "Cost"});
        parametersPanel.add(focusDropdown);

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
                return f.isDirectory() || f.getName().toLowerCase().endsWith(".def");
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

    private void runAlgorithm(VisualisationPanel visualisationPanel, JTabbedPane analysisPane) throws IOException {
        String filePath = filePathField.getText();
        int numberOfAnts = Integer.parseInt(antsField.getText());
        int numberOfIterations = Integer.parseInt(iterationsField.getText());
        double alpha = Double.valueOf(alphaField.getText());
        double beta = Double.valueOf(betaField.getText());
        double evaporationRate = Double.valueOf(evaporationField.getText());
        String focusOption = (String) focusDropdown.getSelectedItem();

        System.out.println("Parameters read.");
        InstanceReader reader = new InstanceReader();
        Instance instance = reader.readInstance(filePath);
        System.out.println("Instance read.");
        RandomSolutionConstructor random = new RandomSolutionConstructor(instance);
        Solution random_solution = random.constructRandomSolution();
        System.out.println("Random solution constructed.");
        AntColonyOptimizer antColonyOptimizer = new AntColonyOptimizer(instance, numberOfAnts, numberOfIterations, alpha, beta, evaporationRate);
        Solution solution = new Solution();
        if(focusOption == "Duration"){
            solution = antColonyOptimizer.run();
        }
        else if(focusOption == "Cost"){
            solution = antColonyOptimizer.run2();
        }
        if(Validator.validateSolution(solution, instance))
        {
            System.out.println("Created solution, which is valid!");
        }
        else {
            System.out.println("Error: Created solution is not valid!");
            return;
        }
        int random_cost = (int)Math.round(random_solution.getScheduleCost());
        int random_duration = random_solution.getScheduleDuration();
        solution.visualizeAsPng(filePath.replace(".def", "_visulaisation.png"), random_cost, random_duration);
        System.out.println("Visualisation created.");
        solution.saveToTxt(filePath.replace(".def", "_solution.txt"), random_cost, random_duration);
        System.out.println("Txt solution file created.");
        File visualisation_file = new File(filePath.replace(".def", "_visulaisation.png"));
        visualisationPanel.displayImage(visualisation_file);
        analysisPane.setSelectedComponent(visualisationPanel);
    }

    private void validateInputs() {
        boolean isValid = true;

        isValid &= !filePathField.getText().trim().isEmpty();
        isValid &= !antsField.getText().trim().isEmpty() && isInteger(antsField.getText().trim());
        isValid &= !alphaField.getText().trim().isEmpty() && isFloat(alphaField.getText().trim());
        isValid &= !betaField.getText().trim().isEmpty() && isFloat(betaField.getText().trim());
        isValid &= !evaporationField.getText().trim().isEmpty() && isFloat(evaporationField.getText().trim());
        isValid &= !iterationsField.getText().trim().isEmpty() && isInteger(iterationsField.getText().trim());

        runButton.setEnabled(isValid);
    }

    private boolean isInteger(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
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
