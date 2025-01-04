import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class InstanceGenerator {

    public static void generateInstance(int numTasks, int numResources, int numPrecedences, int numSkills, String fileName) {
        Random random = new Random();

        // Generate resources
        List<Resource> resources = new ArrayList<>();
        for (int i = 1; i <= numResources; i++) {
            float salary = 20 + random.nextFloat() * 80; // Random salary between 20 and 100
            Map<String, Integer> skills = new HashMap<>();
            for (int j = 0; j < numSkills; j++) {
                String skillName = "Q" + j;
                int skillLevel = random.nextInt(3); // Skill level between 0 and 2
                skills.put(skillName, skillLevel);
            }
            resources.add(new Resource(i, salary, skills));
        }

        // Generate tasks ensuring at least one resource matches skill requirements
        List<Task> tasks = new ArrayList<>();
        while (tasks.size() < numTasks) {
            int taskId = tasks.size() + 1;
            int duration = 10 + random.nextInt(41); // Duration between 10 and 50
            String requiredSkill = "Q" + random.nextInt(numSkills);
            int skillLevel = 1 + random.nextInt(2); // Skill level between 1 and 2

            Map<String, Integer> skillRequirements = new HashMap<>();
            skillRequirements.put(requiredSkill, skillLevel);

            // Check if there is at least one resource with required skill level
            boolean isValid = resources.stream()
                    .anyMatch(resource -> resource.getSkills().getOrDefault(requiredSkill, 0) >= skillLevel);

            if (isValid) {
                tasks.add(new Task(taskId, duration, skillRequirements, new ArrayList<>()));
            }
        }

        // Generate precedences (without cycles)
        generatePrecedences(tasks, numPrecedences, random);

        // Write instance to file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write("==========================================================\n");
            writer.write("General characteristics:\n");
            writer.write("Tasks: " + numTasks + "\n");
            writer.write("Resources: " + numResources + "\n");
            writer.write("Precedence relations: " + numPrecedences + "\n");
            writer.write("Number of skill types: " + numSkills + "\n");
            writer.write("==========================================================\n");
            writer.write("ResourceID \t Salary \t Skills\n");
            for (Resource resource : resources) {
                writer.write(resource.getId() + "\t\t" + String.format(Locale.US, "%.1f", resource.getSalary()) + "\t ");
                for (Map.Entry<String, Integer> skill : resource.getSkills().entrySet()) {
                    writer.write(skill.getKey() + ": " + skill.getValue() + "\t ");
                }
                writer.write("\n");
            }
            writer.write("==========================================================\n");
            writer.write("TaskID \t Duration \t Skill \t Predecessor IDs\n");
            for (Task task : tasks) {
                writer.write(task.getId() + "\t\t" + task.getDuration() + "\t ");
                for (Map.Entry<String, Integer> skill : task.getSkill().entrySet()) {
                    writer.write(skill.getKey() + ": " + skill.getValue() + "\t ");
                }
                writer.write("\t" + task.getPredecessorIds().stream()
                        .map(Object::toString)
                        .collect(Collectors.joining(" ")) + "\n");
            }
            writer.write("==========================================================\n");
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }

    private static void generatePrecedences(List<Task> tasks, int numPrecedences, Random random) {
        int taskCount = tasks.size();
        Set<String> precedenceRelations = new HashSet<>();
        while (precedenceRelations.size() < numPrecedences) {
            int from = 1 + random.nextInt(taskCount);
            int to = 1 + random.nextInt(taskCount);

            // Ensure no self-loops or duplicate relations
            if (from != to && !precedenceRelations.contains(from + "->" + to)) {
                if (!createsCycle(tasks, from, to)) {
                    precedenceRelations.add(from + "->" + to);
                    Task fromTask = tasks.get(from - 1);
                    tasks.get(to - 1).getPredecessorIds().add(fromTask.getId());
                }
            }
        }
    }

    private static boolean createsCycle(List<Task> tasks, int from, int to) {
        Set<Integer> visited = new HashSet<>();
        return dfs(tasks, to, from, visited);
    }

    private static boolean dfs(List<Task> tasks, int current, int target, Set<Integer> visited) {
        if (current == target) return true;
        if (!visited.add(current)) return false;

        for (int predecessor : tasks.get(current - 1).getPredecessorIds()) {
            if (dfs(tasks, predecessor, target, visited)) {
                return true;
            }
        }
        return false;
    }
}
