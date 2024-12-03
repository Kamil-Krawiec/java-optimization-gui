import javax.swing.*;

public class MainApp extends JFrame {

    public MainApp() {
        setTitle("MS-RCPSP");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane mainTabbedPane = new JTabbedPane();

        JTabbedPane analysisTabbedPane = new JTabbedPane();

        AntColonyPanel antColonyPanel = new AntColonyPanel();
        analysisTabbedPane.addTab("Ant Colony", antColonyPanel);

        MultiobjectivePanel multiObjectivePanel = new MultiobjectivePanel();
        analysisTabbedPane.addTab("NSGA-II", multiObjectivePanel);

        mainTabbedPane.addTab("Analysis", analysisTabbedPane);

        GeneratePanel generatePanel = new GeneratePanel();
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
