import java.util.*;

public class RandomSolutionConstructor {

    private final Instance instance;

    public RandomSolutionConstructor(Instance instance) {
        this.instance = instance;
    }

    public Solution constructRandomSolution() {
        Solution solution = new Solution();
        List<Task> tasksToSchedule = new ArrayList<>(instance.getTasks());
        Set<Integer> scheduledTasks = new HashSet<>();
        Map<Integer, Integer> resourceAvailability = new HashMap<>();

        // Initialize resource availability to 0 (i.e., they are available from the beginning)
        for (Resource resource : instance.getResources()) {
            resourceAvailability.put(resource.getId(), 0);
        }

        // While there are tasks to schedule
        while (!tasksToSchedule.isEmpty()) {
            // Randomly choose a task that hasn't been scheduled and has all its predecessors scheduled
            Task task = selectRandomTask(tasksToSchedule, scheduledTasks);
            if (task == null) {
                return null;  // If no valid task can be selected (due to predecessor constraints), return null
            }

            // Randomly select a resource for the task
            Resource resource = selectRandomResourceForTask(task, resourceAvailability);
            if (resource == null) {
                return null;  // If no valid resource can be found, return null
            }

            // Find the earliest available start time for the task
            Map<Integer, Integer> taskStartTimes = getTaskStartTimes(solution);
            List<Integer> predecessorEndTimes = new ArrayList<>();
            for (Integer predId : task.getPredecessorIds()) {
                predecessorEndTimes.add(taskStartTimes.get(predId) + getTaskDuration(predId));
            }

            // Calculate the earliest time the task can start
            int startTime = Math.max(resourceAvailability.get(resource.getId()), predecessorEndTimes.isEmpty() ? 0 : Collections.max(predecessorEndTimes) + 1);

            // Assign the task to the resource at the calculated start time
            solution.getSchedule().computeIfAbsent(startTime, k -> new ArrayList<>())
                    .add(new TaskAssignment(task, resource));
            resourceAvailability.put(resource.getId(), startTime + task.getDuration());
            scheduledTasks.add(task.getId());
            tasksToSchedule.remove(task);
        }

        return solution;
    }

    private Task selectRandomTask(List<Task> tasks, Set<Integer> scheduledTasks) {
        List<Task> validTasks = new ArrayList<>();
        for (Task task : tasks) {
            if (task.getPredecessorIds().stream().allMatch(scheduledTasks::contains)) {
                validTasks.add(task);
            }
        }

        if (validTasks.isEmpty()) {
            return null;
        }

        // Return a random valid task
        Random random = new Random();
        return validTasks.get(random.nextInt(validTasks.size()));
    }

    private Resource selectRandomResourceForTask(Task task, Map<Integer, Integer> resourceAvailability) {
        List<Resource> availableResources = new ArrayList<>();
        for (Resource resource : instance.getResources()) {
            if (isResourceSuitable(resource, task)) {
                availableResources.add(resource);
            }
        }

        if (availableResources.isEmpty()) {
            return null;
        }

        // Return a random valid resource
        Random random = new Random();
        return availableResources.get(random.nextInt(availableResources.size()));
    }

    private boolean isResourceSuitable(Resource resource, Task task) {
        for (Map.Entry<String, Integer> skillRequirement : task.getSkill().entrySet()) {
            if (resource.getSkills().getOrDefault(skillRequirement.getKey(), 0) < skillRequirement.getValue()) {
                return false;
            }
        }
        return true;
    }

    private Map<Integer, Integer> getTaskStartTimes(Solution solution) {
        Map<Integer, Integer> taskStartTimes = new HashMap<>();
        for (Map.Entry<Integer, List<TaskAssignment>> entry : solution.getSchedule().entrySet()) {
            for (TaskAssignment taskAssignment : entry.getValue()) {
                taskStartTimes.put(taskAssignment.getTask().getId(), entry.getKey());
            }
        }
        return taskStartTimes;
    }

    private int getTaskDuration(int taskId) {
        for (Task task : instance.getTasks()) {
            if (task.getId() == taskId) {
                return task.getDuration();
            }
        }
        return 0; // Return 0 if task is not found (shouldn't happen)
    }
}
