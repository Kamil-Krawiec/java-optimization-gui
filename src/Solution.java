import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;

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

    public void visualizeAsPng(String outputFilePath) {
        if (schedule.isEmpty()) {
            System.out.println("No tasks scheduled.");
            return;
        }

        // Determine unique resources and time range
        Set<Integer> resourceIds = new HashSet<>();
        int maxTime = 0;

        for (Map.Entry<Integer, List<TaskAssignment>> entry : schedule.entrySet()) {
            maxTime = Math.max(maxTime, entry.getKey());
            for (TaskAssignment taskAssignment : entry.getValue()) {
                resourceIds.add(taskAssignment.getResource().getId());
            }
        }

        List<Integer> sortedResources = resourceIds.stream().sorted().collect(Collectors.toList());
        int numResources = sortedResources.size();

        // Set dimensions for the grid
        int cellWidth = 50;
        int cellHeight = 50;
        int padding = 50;
        int imageWidth = padding + (maxTime + 1) * cellWidth;
        int imageHeight = padding + numResources * cellHeight;

        // Create a BufferedImage
        BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();

        // Background color
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, imageWidth, imageHeight);

        // Draw grid
        g.setColor(Color.BLACK);
        for (int i = 0; i <= maxTime; i++) {
            int x = padding + i * cellWidth;
            g.drawLine(x, padding, x, padding + numResources * cellHeight);
        }
        for (int i = 0; i <= numResources; i++) {
            int y = padding + i * cellHeight;
            g.drawLine(padding, y, padding + (maxTime + 1) * cellWidth, y);
        }

        // Fill grid with tasks (span for their duration)
        Random random = new Random(); // To generate random colors for tasks
        Map<Integer, Color> taskColors = new HashMap<>();

        for (Map.Entry<Integer, List<TaskAssignment>> entry : schedule.entrySet()) {
            int startTime = entry.getKey();
            for (TaskAssignment taskAssignment : entry.getValue()) {
                int resourceIndex = sortedResources.indexOf(taskAssignment.getResource().getId());
                Task task = taskAssignment.getTask();
                int duration = task.getDuration(); // Get task duration
                int endTime = Math.min(startTime + duration, maxTime + 1);

                // Get or assign a random color for the task
                Color color = taskColors.computeIfAbsent(task.getId(), k -> new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256)));

                // Draw the task as a filled rectangle spanning its duration
                g.setColor(color);
                int x = padding + startTime * cellWidth;
                int y = padding + resourceIndex * cellHeight;
                int taskWidth = (endTime - startTime) * cellWidth;
                g.fillRect(x, y, taskWidth, cellHeight);

                // Draw task ID at the start of the rectangle
                g.setColor(Color.BLACK);
                g.drawString("T" + task.getId(), x + 10, y + 25);
            }
        }

        // Draw labels
        g.setColor(Color.BLACK);
        for (int i = 0; i < sortedResources.size(); i++) {
            String label = "R" + sortedResources.get(i);
            int y = padding + i * cellHeight + cellHeight / 2;
            g.drawString(label, 10, y);
        }
        for (int i = 0; i <= maxTime; i++) {
            String label = String.valueOf(i);
            int x = padding + i * cellWidth + cellWidth / 2 - 10;
            g.drawString(label, x, padding - 10);
        }

        // Save the image
        g.dispose();
        try {
            ImageIO.write(image, "png", new File(outputFilePath));
            System.out.println("Schedule visualization saved to: " + outputFilePath);
        } catch (IOException e) {
            System.err.println("Error saving the image: " + e.getMessage());
        }
    }
}
