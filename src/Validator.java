import java.util.*;

public class Validator {

    public static boolean validateSolution(Solution solution, Instance instance) {
        return validateAssignments(solution, instance)
                && validateConflicts(solution)
                && validateSkills(solution, instance)
                && validatePrecedenceRelations(solution, instance);
    }

    private static boolean validateAssignments(Solution solution, Instance instance) {
        // Ensure all tasks are assigned to resources
        Set<Integer> assignedTasks = new HashSet<>();
        for (List<TaskAssignment> assignments : solution.getSchedule().values()) {
            for (TaskAssignment assignment : assignments) {
                assignedTasks.add(assignment.getTask().getId());
            }
        }

        Set<Integer> allTasks = new HashSet<>();
        for (Task task : instance.getTasks()) {
            allTasks.add(task.getId());
        }

        if (!assignedTasks.equals(allTasks)) {
            System.out.println("TASK ASSIGNMENT NOT VALID");
        }

        return assignedTasks.equals(allTasks);
    }

    private static boolean validateConflicts(Solution solution) {
        // Ensure no resource is assigned to more than one task at the same time
        for (Map.Entry<Integer, List<TaskAssignment>> entry : solution.getSchedule().entrySet()) {
            List<TaskAssignment> assignments = entry.getValue();
            Set<Integer> resources = new HashSet<>();
            for (TaskAssignment assignment : assignments) {
                if (!resources.add(assignment.getResource().getId())) {
                    System.out.println("CONFLICTS!");
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean validateSkills(Solution solution, Instance instance) {
        // Ensure resources have the required skills for assigned tasks
        for (Map.Entry<Integer, List<TaskAssignment>> entry : solution.getSchedule().entrySet()) {
            List<TaskAssignment> assignments = entry.getValue();
            for (TaskAssignment assignment : assignments) {
                Resource resource = assignment.getResource();
                Task task = assignment.getTask();
                for (Map.Entry<String, Integer> requiredSkill : task.getSkill().entrySet()) {
                    String skillName = requiredSkill.getKey();
                    int requiredLevel = requiredSkill.getValue();
                    if (resource.getSkills().getOrDefault(skillName, 0) < requiredLevel) {
                        System.out.println("SKILLS NOT VALID!");
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private static boolean validatePrecedenceRelations(Solution solution, Instance instance) {
        // Ensure precedence relations are respected
        Map<Integer, Integer> taskStartTimes = new HashMap<>();

        for (Map.Entry<Integer, List<TaskAssignment>> entry : solution.getSchedule().entrySet()) {
            int time = entry.getKey();
            List<TaskAssignment> assignments = entry.getValue();
            for (TaskAssignment assignment : assignments) {
                taskStartTimes.put(assignment.getTask().getId(), time);
            }
        }

        for (Task task : instance.getTasks()) {
            for (int predecessorId : task.getPredecessorIds()) {
                Task predecessor = instance.getTaskById(predecessorId);
                int predecessorEndTime = taskStartTimes.getOrDefault(predecessorId, Integer.MAX_VALUE) 
                                        + predecessor.getDuration();
                int taskStartTime = taskStartTimes.getOrDefault(task.getId(), -1);
                if (predecessorEndTime >= taskStartTime) {
                    System.out.println("PRECEDENCE RELATIONS NOT VALID!");
                    return false;
                }
            }
        }
        return true;
    }
}
