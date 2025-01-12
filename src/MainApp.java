
import javax.swing.*;

public class MainApp extends JFrame {

    public MainApp() {
        setTitle("MS-RCPSP");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane mainTabbedPane = new JTabbedPane();

        JTabbedPane analysisTabbedPane = new JTabbedPane();

        VisualisationPanel visualisationPanel = new VisualisationPanel();
        AntColonyPanel antColonyPanel = new AntColonyPanel(visualisationPanel, analysisTabbedPane);
        GeneratePanel generatePanel = new GeneratePanel();

        analysisTabbedPane.addTab("Ant Colony", antColonyPanel);
        analysisTabbedPane.addTab("Visualisation", visualisationPanel);
        mainTabbedPane.addTab("Analysis", analysisTabbedPane);

        mainTabbedPane.addTab("Generate", generatePanel);

        add(mainTabbedPane);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainApp app = new MainApp();
            app.setVisible(true);
        });
    }
}
