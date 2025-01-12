import java.util.*;

public class AntColonyOptimizer {
    private final Instance instance;
    private final int numAnts;
    private final int numIterations;
    private final double alpha;
    private final double beta;
    private final double evaporationRate;
    private Map<String, Map<String, Double>> pheromones;

    public AntColonyOptimizer(Instance instance, int numAnts, int numIterations, double alpha, double beta, double evaporationRate) {
        this.instance = instance;
        this.numAnts = numAnts;
        this.numIterations = numIterations;
        this.alpha = alpha;
        this.beta = beta;
        this.evaporationRate = evaporationRate;
        this.pheromones = initializePheromones();
    }

    private Map<String, Map<String, Double>> initializePheromones() {
        Map<String, Map<String, Double>> pheromones = new HashMap<>();
        for (Task task : instance.getTasks()) {
            Map<String, Double> taskPheromones = new HashMap<>();
            for (Resource resource : instance.getResources()) {
                taskPheromones.put(String.valueOf(resource.getId()), 1.0); // Initialize with 1.0
            }
            pheromones.put(String.valueOf(task.getId()), taskPheromones);
        }
        return pheromones;
    }

    public Solution run() {
        Solution bestSolution = null;
        double bestFitness = Double.POSITIVE_INFINITY;

        for (int iteration = 0; iteration < numIterations; iteration++) {
            List<Solution> solutions = constructSolutions();
            updatePheromones(solutions);

            for (Solution solution : solutions) {
                double fitness = fitness(solution);
                if (fitness < bestFitness) {
                    bestFitness = fitness;
                    bestSolution = solution;
                }
            }
        }
        return bestSolution;
    }

    public Solution run2() {
        Solution bestSolution = null;
        double bestFitness = Double.POSITIVE_INFINITY;

        for (int iteration = 0; iteration < numIterations; iteration++) {
            List<Solution> solutions = constructSolutions();
            updatePheromones2(solutions);

            for (Solution solution : solutions) {
                double fitness = fitness2(solution);
                if (fitness < bestFitness) {
                    bestFitness = fitness;
                    bestSolution = solution;
                }
            }
        }
        return bestSolution;
    }

    private List<Solution> constructSolutions() {
        List<Solution> solutions = new ArrayList<>();
        for (int i = 0; i < numAnts; i++) {
            Solution solution = constructSolution();
            if (solution != null) {
                solutions.add(solution);
            }
        }
        return solutions;
    }

    private Solution constructSolution() {
        Solution solution = new Solution();
        List<Task> tasksToSchedule = new ArrayList<>(instance.getTasks());
        Set<Integer> scheduledTasks = new HashSet<>();
        Map<Integer, Integer> resourceAvailability = new HashMap<>();

        for (Resource resource : instance.getResources()) {
            resourceAvailability.put(resource.getId(), 0);  // Initialize resource availability times
        }

        while (!tasksToSchedule.isEmpty()) {
            Task task = selectNextTask(tasksToSchedule, scheduledTasks);
            if (task == null) {
                return null;
            }

            Resource resource = selectResourceForTask(task, resourceAvailability);
            if (resource == null) {
                return null;
            }

            Map<Integer, Integer> taskStartTimes = new HashMap<>();
            for (Map.Entry<Integer, List<TaskAssignment>> entry : solution.getSchedule().entrySet()) {
                for (TaskAssignment taskAssignment : entry.getValue()) {
                    taskStartTimes.put(taskAssignment.getTask().getId(), entry.getKey());
                }
            }

            List<Integer> predecessorEndTimes = new ArrayList<>();
            for (Integer predId : task.getPredecessorIds()) {
                predecessorEndTimes.add(taskStartTimes.get(predId) + getTaskDuration(predId));
            }
            int startTime = Math.max(resourceAvailability.get(resource.getId()), predecessorEndTimes.isEmpty() ? 0 : Collections.max(predecessorEndTimes) + 1);

            solution.getSchedule().computeIfAbsent(startTime, k -> new ArrayList<>())
                    .add(new TaskAssignment(task, resource));
            resourceAvailability.put(resource.getId(), startTime + task.getDuration());
            scheduledTasks.add(task.getId());
            tasksToSchedule.remove(task);
        }

        return solution;
    }

    private Task selectNextTask(List<Task> tasks, Set<Integer> scheduledTasks) {
        List<Task> validTasks = new ArrayList<>();
        for (Task task : tasks) {
            if (task.getPredecessorIds().stream().allMatch(scheduledTasks::contains)) {
                validTasks.add(task);
            }
        }

        if (validTasks.isEmpty()) {
            return null;
        }

        List<Double> pheromoneLevels = new ArrayList<>();
        List<Double> heuristics = new ArrayList<>();
        for (Task task : validTasks) {
            double pheromone = pheromones.get(String.valueOf(task.getId()))
                    .values().stream().mapToDouble(Double::doubleValue).sum();
            double heuristic = calculateHeuristic(task);
            pheromoneLevels.add(Math.pow(pheromone, alpha));
            heuristics.add(Math.pow(heuristic, beta));
        }

        List<Double> probabilities = new ArrayList<>();
        double totalProbability = 0;
        for (int i = 0; i < validTasks.size(); i++) {
            probabilities.add(pheromoneLevels.get(i) * heuristics.get(i));
            totalProbability += probabilities.get(i);
        }

        if (totalProbability == 0) {
            probabilities.replaceAll(p -> 1.0 / probabilities.size());
        } else {
            final double total = totalProbability;
            probabilities.replaceAll(p -> p / total);
        }

        double rand = Math.random();
        double cumulativeProbability = 0.0;
        for (int i = 0; i < validTasks.size(); i++) {
            cumulativeProbability += probabilities.get(i);
            if (rand <= cumulativeProbability) {
                return validTasks.get(i);
            }
        }

        return null;
    }

    private double calculateHeuristic(Task task) {
        return 1.0 / task.getDuration();
    }

    private Resource selectResourceForTask(Task task, Map<Integer, Integer> resourceAvailability) {
        List<Resource> availableResources = new ArrayList<>();
        for (Resource resource : instance.getResources()) {
            if (isResourceSuitable(resource, task)) {
                availableResources.add(resource);
            }
        }

        if (availableResources.isEmpty()) {
            return null;
        }

        return availableResources.stream()
                .min(Comparator.comparingInt(r -> resourceAvailability.get(r.getId())))
                .orElse(null);
    }

    private boolean isResourceSuitable(Resource resource, Task task) {
        for (Map.Entry<String, Integer> skillRequirement : task.getSkill().entrySet()) {
            if (resource.getSkills().getOrDefault(skillRequirement.getKey(), 0) < skillRequirement.getValue()) {
                return false;
            }
        }
        return true;
    }

    private void updatePheromones(List<Solution> solutions) {
        for (Map.Entry<String, Map<String, Double>> taskEntry : pheromones.entrySet()) {
            for (Map.Entry<String, Double> resourceEntry : taskEntry.getValue().entrySet()) {
                pheromones.get(taskEntry.getKey()).put(resourceEntry.getKey(), resourceEntry.getValue() * (1 - evaporationRate));
            }
        }

        for (Solution solution : solutions) {
            for (Map.Entry<Integer, List<TaskAssignment>> entry : solution.getSchedule().entrySet()) {
                List<TaskAssignment> tasksAtTime = entry.getValue();
                for (TaskAssignment taskAssignment : tasksAtTime) {
                    String taskId = String.valueOf(taskAssignment.getTask().getId());
                    String resourceId = String.valueOf(taskAssignment.getResource().getId());
                    double pheromoneIncrease = 1.0 / fitness(solution); // Modify based on your fitness function
                    pheromones.get(taskId).put(resourceId, pheromones.get(taskId).get(resourceId) + pheromoneIncrease);
                }
            }
        }
    }

    private void updatePheromones2(List<Solution> solutions) {
        for (Map.Entry<String, Map<String, Double>> taskEntry : pheromones.entrySet()) {
            for (Map.Entry<String, Double> resourceEntry : taskEntry.getValue().entrySet()) {
                pheromones.get(taskEntry.getKey()).put(resourceEntry.getKey(), resourceEntry.getValue() * (1 - evaporationRate));
            }
        }

        for (Solution solution : solutions) {
            for (Map.Entry<Integer, List<TaskAssignment>> entry : solution.getSchedule().entrySet()) {
                List<TaskAssignment> tasksAtTime = entry.getValue();
                for (TaskAssignment taskAssignment : tasksAtTime) {
                    String taskId = String.valueOf(taskAssignment.getTask().getId());
                    String resourceId = String.valueOf(taskAssignment.getResource().getId());
                    double pheromoneIncrease = 1.0 / fitness2(solution); // Modify based on your fitness function
                    pheromones.get(taskId).put(resourceId, pheromones.get(taskId).get(resourceId) + pheromoneIncrease);
                }
            }
        }
    }

    // Alternative solution construction method similar to construct_solution2 in Python
    public Solution constructSolution2() {
        Solution solution = new Solution();
        List<Task> tasksToSchedule = new ArrayList<>(instance.getTasks());
        Set<Integer> scheduledTasks = new HashSet<>();
        Map<Integer, Integer> resourceAvailability = new HashMap<>();

        // Initialize resource availability to 0 for each resource
        for (Resource resource : instance.getResources()) {
            resourceAvailability.put(resource.getId(), 0);
        }

        // While there are tasks to schedule
        while (!tasksToSchedule.isEmpty()) {
            // Select the best task-resource pair
            TaskAssignment bestAssignment = selectTaskAndResource(tasksToSchedule, scheduledTasks);
            if (bestAssignment == null) {
                return null;
            }

            Task task = bestAssignment.getTask();
            Resource resource = bestAssignment.getResource();

            // Calculate the start time for the task
            Map<Integer, Integer> taskStartTimes = new HashMap<>();
            for (Map.Entry<Integer, List<TaskAssignment>> entry : solution.getSchedule().entrySet()) {
                for (TaskAssignment assignment : entry.getValue()) {
                    taskStartTimes.put(assignment.getTask().getId(), entry.getKey());
                }
            }

            // Calculate the end time of task predecessors
            List<Integer> predecessorEndTimes = new ArrayList<>();
            for (int predId : task.getPredecessorIds()) {
                if (taskStartTimes.containsKey(predId)) {
                    int endTime = taskStartTimes.get(predId) + instance.getTaskById(predId).getDuration();
                    predecessorEndTimes.add(endTime);
                }
            }

            int startTime;
            if (predecessorEndTimes.isEmpty()) {
                startTime = resourceAvailability.get(resource.getId());
            } else {
                startTime = Math.max(resourceAvailability.get(resource.getId()), 
                                    Collections.max(predecessorEndTimes, Integer::compareTo) + 1);
            }

            // Assign the task to the resource at the calculated start time
            solution.getSchedule().computeIfAbsent(startTime, k -> new ArrayList<>())
                    .add(new TaskAssignment(task, resource));

            // Update the resource availability
            resourceAvailability.put(resource.getId(), startTime + task.getDuration());
            scheduledTasks.add(task.getId());
            tasksToSchedule.remove(task);
        }

        return solution;
    }

    // Method to select the best task-resource pair based on the heuristic and pheromone levels
    private TaskAssignment selectTaskAndResource(List<Task> tasks, Set<Integer> scheduledTasks) {
        Task bestTask = null;
        Resource bestResource = null;
        double bestScore = Double.MAX_VALUE;

        for (Task task : tasks) {
            if (scheduledTasks.containsAll(task.getPredecessorIds())) { // Check if all predecessors are scheduled
                for (Resource resource : instance.getResources()) {
                    if (isResourceSuitable(resource, task)) {
                        // Calculate heuristic and pheromone scores
                        double heuristic = calculateScore(task, resource);
                        double pheromone = pheromones.get(String.valueOf(task.getId())).get(String.valueOf(resource.getId()));
                        double score = Math.pow(heuristic, beta) * Math.pow(pheromone, alpha);

                        if (score < bestScore) {
                            bestScore = score;
                            bestTask = task;
                            bestResource = resource;
                        }
                    }
                }
            }
        }

        return bestTask != null && bestResource != null ? 
               new TaskAssignment(bestTask, bestResource) : null;
    }

    private double fitness(Solution solution) {
        return duration(solution);  // You can modify to include additional factors such as cost
    }

    private double duration(Solution solution) {
        if (solution.getSchedule().isEmpty()) {
            return 0;
        }
        return Collections.max(solution.getSchedule().keySet());
    }

    // Method to calculate the fitness of a solution based on duration and cost
    public double fitness2(Solution solution) {
        double duration = duration(solution);
        double cost = cost(solution);
        return cost + duration;
    }

    // Heuristic score calculation (task-resource suitability)
    private double calculateScore(Task task, Resource resource) {
        // Example: Heuristic based on task duration and resource salary
        double durationFactor = task.getDuration();
        double costFactor = resource.getSalary();
        return durationFactor * costFactor;
    }

    // Calculate the total cost of the solution
    public double cost(Solution solution) {
        double totalCost = 0;
        for (Map.Entry<Integer, List<TaskAssignment>> entry : solution.getSchedule().entrySet()) {
            for (TaskAssignment taskAssignment : entry.getValue()) {
                totalCost += taskAssignment.getResource().getSalary() * taskAssignment.getTask().getDuration();
            }
        }
        return totalCost;
    }

    private int getTaskDuration(int taskId) {
        for (Task task : instance.getTasks()) {
            if (task.getId() == taskId) {
                return task.getDuration();
            }
        }
        return 0; // If not found, return 0
    }
}
