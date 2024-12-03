import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class Solution {
    private Map<Integer, List<TaskAssignment>> schedule;

    public Solution() {
        schedule = new HashMap<>();
    }

    public Map<Integer, List<TaskAssignment>> getSchedule() {
        return schedule;
    }

    public int getScheduleDuration() {
        if (schedule.isEmpty()) {
            return 0;
        }
        return Collections.max(schedule.keySet());
    }

    public double getScheduleCost() {
        double totalCost = 0;
        for (Map.Entry<Integer, List<TaskAssignment>> entry : schedule.entrySet()) {
            for (TaskAssignment taskAssignment : entry.getValue()) {
                totalCost += taskAssignment.getResource().getSalary() * taskAssignment.getTask().getDuration();
            }
        }
        return totalCost;
    }

    public double getFitnessSum(){
        return getScheduleCost() + getScheduleDuration();
    }

    public double getFitnessProduct(){
        return getScheduleCost() * getScheduleDuration();
    }

    public void prettyPrintSchedule() {
        if (schedule.isEmpty()) {
            System.out.println("No tasks scheduled.");
            return;
        }

        System.out.println("Task Schedule:");

        // Sort the time slots (keys) in ascending order
        List<Integer> sortedTimeSlots = schedule.keySet().stream()
            .sorted()
            .collect(Collectors.toList());

        // Iterate through the sorted time slots
        for (int time : sortedTimeSlots) {
            List<TaskAssignment> assignments = schedule.get(time);

            System.out.println("Time: " + time + " hours");

            // Print each task assignment at this time
            for (TaskAssignment taskAssignment : assignments) {
                Task task = taskAssignment.getTask();
                Resource resource = taskAssignment.getResource();
                System.out.println("  Task ID: " + task.getId() + " assigned to Resource ID: " 
                    + resource.getId() + " (Salary: " + resource.getSalary() + ")");
            }
            System.out.println();  // For better readability
        }
    }
}
