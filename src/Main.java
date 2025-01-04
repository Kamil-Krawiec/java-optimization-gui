import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        InstanceReader reader = new InstanceReader();
        try {
            Instance instance = reader.readInstance("instances/generated.txt");

            // System.out.println("Instance Information:");
            // System.out.println("----------------------");
            // System.out.println("Number of Tasks: " + instance.getNumberOfTasks());
            // System.out.println("Number of Resources: " + instance.getNumberOfResources());
            // System.out.println("Number of Precedence Relations: " + instance.getNumberOfPrecedenceRelations());
            // System.out.println("Number of Skill Types: " + instance.getNumberOfSkillTypes());
            // System.out.println();

            // System.out.println("Resources:");
            // System.out.println("----------------------");
            // for (Resource resource : instance.getResources()) {
            //     System.out.println("Resource ID: " + resource.getId());
            //     System.out.println("Salary: " + resource.getSalary());
            //     System.out.println("Skills: " + resource.getSkills());
            //     System.out.println();
            // }

            // System.out.println("Tasks:");
            // System.out.println("----------------------");
            // for (Task task : instance.getTasks()) {
            //     System.out.println("Task ID: " + task.getId());
            //     System.out.println("Duration: " + task.getDuration());
            //     System.out.println("Skill: " + task.getSkill());
            //     System.out.println("Predecessor IDs: " + task.getPredecessorIds());
            //     System.out.println();
            // }

            AntColonyOptimizer aco = new AntColonyOptimizer(instance, 100, 50, 1.0, 1.0, 0.4);
            RandomSolutionConstructor rsc = new RandomSolutionConstructor(instance);
            Solution aco_solution = aco.run();
            if(!Validator.validateSolution(aco_solution, instance))
            {
                System.out.println("ACO1 not valid!");
            }
            System.out.println();
            System.out.println("ACO1 duration: " + aco_solution.getScheduleDuration());
            System.out.println("ACO1 cost: " + aco_solution.getScheduleCost());
            System.out.println("ACO1 fitness sum: " + aco_solution.getFitnessSum());
            System.out.println("ACO1 fitness product: " + aco_solution.getFitnessProduct());
            System.out.println();
            Solution aco_solution2 = aco.run2();
            if(!Validator.validateSolution(aco_solution2, instance))
            {
                System.out.println("ACO2 not valid!");
            }
            System.out.println("ACO2 duration: " + aco_solution2.getScheduleDuration());
            System.out.println("ACO2 cost: " + aco_solution2.getScheduleCost());
            System.out.println("ACO2 fitness sum: " + aco_solution2.getFitnessSum());
            System.out.println("ACO2 fitness product: " + aco_solution2.getFitnessProduct());
            System.out.println();
            Solution random_solution = rsc.constructRandomSolution();
            if(!Validator.validateSolution(random_solution, instance))
            {
                System.out.println("Random not valid!");
            }
            System.out.println("Random duration: " + random_solution.getScheduleDuration());
            System.out.println("Random cost: " + random_solution.getScheduleCost());
            System.out.println("Random fitness sum: " + random_solution.getFitnessSum());
            System.out.println("Random fitness product: " + random_solution.getFitnessProduct());
            System.out.println();

            aco_solution.visualizeAsPng("visualization.png");
            InstanceGenerator.generateInstance(10, 3, 4, 3, "generated.txt");
        } catch (IOException e) {
            System.err.println("Error reading the instance file: " + e.getMessage());
        }
    }
}
