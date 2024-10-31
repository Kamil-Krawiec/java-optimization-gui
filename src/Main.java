import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        InstanceReader reader = new InstanceReader();
        try {
            Instance instance = reader.readInstance("instances/10_3_5_3.def");

            System.out.println("Instance Information:");
            System.out.println("----------------------");
            System.out.println("Number of Tasks: " + instance.getNumberOfTasks());
            System.out.println("Number of Resources: " + instance.getNumberOfResources());
            System.out.println("Number of Precedence Relations: " + instance.getNumberOfPrecedenceRelations());
            System.out.println("Number of Skill Types: " + instance.getNumberOfSkillTypes());
            System.out.println();

            System.out.println("Resources:");
            System.out.println("----------------------");
            for (Resource resource : instance.getResources()) {
                System.out.println("Resource ID: " + resource.getId());
                System.out.println("Salary: " + resource.getSalary());
                System.out.println("Skills: " + resource.getSkills());
                System.out.println();
            }

            System.out.println("Tasks:");
            System.out.println("----------------------");
            for (Task task : instance.getTasks()) {
                System.out.println("Task ID: " + task.getId());
                System.out.println("Duration: " + task.getDuration());
                System.out.println("Skill: " + task.getSkill());
                System.out.println("Predecessor IDs: " + task.getPredecessorIds());
                System.out.println();
            }
        } catch (IOException e) {
            System.err.println("Error reading the instance file: " + e.getMessage());
        }
    }
}
