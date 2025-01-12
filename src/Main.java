import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        InstanceReader reader = new InstanceReader();
        try {
            Instance instance = reader.readInstance("instances/10_3_5_3.def");

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

        } catch (IOException e) {
            System.err.println("Error reading the instance file: " + e.getMessage());
        }
    }
}
