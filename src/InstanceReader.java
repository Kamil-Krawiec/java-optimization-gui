import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class InstanceReader {
    private static final Pattern RESOURCE_HEADER_PATTERN = Pattern.compile("^ResourceID\\s+Salary\\s+Skills\\s*$");
    private static final Pattern TASK_HEADER_PATTERN = Pattern.compile("^TaskID\\s+Duration\\s+Skill\\s+Predecessor IDs\\s*$");

    public Instance readInstance(String filename) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            int numberOfTasks = 0;
            int numberOfResources = 0;
            int numberOfPrecedenceRelations = 0;
            int numberOfSkillTypes = 0;
            List<Resource> resources = new ArrayList<>();
            List<Task> tasks = new ArrayList<>();
            boolean inResourceSection = false;
            boolean inTaskSection = false;

            while ((line = br.readLine()) != null) {
                line = line.trim();

                // Skip empty lines
                if (line.isEmpty()) continue;

                // Read general characteristics
                if (line.startsWith("Tasks:")) {
                    numberOfTasks = Integer.parseInt(line.split(":")[1].trim());
                } else if (line.startsWith("Resources:")) {
                    numberOfResources = Integer.parseInt(line.split(":")[1].trim());
                } else if (line.startsWith("Precedence relations:")) {
                    numberOfPrecedenceRelations = Integer.parseInt(line.split(":")[1].trim());
                } else if (line.startsWith("Number of skill types:")) {
                    numberOfSkillTypes = Integer.parseInt(line.split(":")[1].trim());
                } else if (RESOURCE_HEADER_PATTERN.matcher(line).matches()) {
                    inResourceSection = true; // Start of the resource section
                    continue; // Skip the header line
                } else if (TASK_HEADER_PATTERN.matcher(line).matches()) {
                    inResourceSection = false; // End of the resource section
                    inTaskSection = true; // Start of the task section
                    continue; // Skip the header line
                }

                // Parse resource section
                if (inResourceSection) {

                    Map<String, Integer> skills = new HashMap<>();

                    String[] parts = line.split("\\s+"); // Split by whitespace
                    if (parts.length < 3) {
                        continue;
                    }

                    int resourceId = Integer.parseInt(parts[0]);
                    float salary = Float.parseFloat(parts[1]);

                    // Extract skills from parts after salary
                    String skillName = "Error";
                    int skillLevel = -1;
                    for (int i = 2; i < parts.length; i++) {

                        if (parts[i].length() >= 3) { // "Q1:"
                            skillName = parts[i].substring(0, parts[i].length() - 1);
                        }
                        if (parts[i].length() == 1) { // "1"
                            skillLevel = Integer.parseInt(parts[i]);
                            skills.put(skillName, skillLevel);
                        }
                    }

                    Resource resource = new Resource(resourceId, salary, skills);
                    resources.add(resource);
                }

                // Parse task section
                if (inTaskSection) {
                    String[] parts = line.split("\\s+"); // Split by whitespace
                    if (parts.length < 3) {
                        continue; // Skip malformed lines
                    }

                    int taskId = Integer.parseInt(parts[0]);
                    int duration = Integer.parseInt(parts[1]);
                    Map<String, Integer> skills = new HashMap<>();
                    List<Integer> predecessorIds = new ArrayList<>();
                    
                    String skillName = parts[2].substring(0, parts[2].length() - 1);
                    int skillLevel = Integer.parseInt(parts[3]);
                    skills.put(skillName, skillLevel);

                    // Parse predecessor IDs
                    for (int i = 4; i < parts.length; i++) {
                        String predecessorId = parts[i].trim();
                        if (!predecessorId.isEmpty()) {
                            predecessorIds.add(Integer.parseInt(predecessorId));
                        }
                    }

                    Task task = new Task(taskId, duration, skills, predecessorIds);
                    tasks.add(task);
                }
            }

            // Create instance and add resources and tasks
            Instance instance = new Instance(numberOfTasks, numberOfResources, numberOfPrecedenceRelations, numberOfSkillTypes);
            for (Resource resource : resources) {
                instance.addResource(resource);
            }
            for (Task task : tasks) {
                instance.addTask(task);
            }

            return instance;
        }
    }
}
