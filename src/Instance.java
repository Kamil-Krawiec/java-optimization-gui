import java.util.List;
import java.util.ArrayList;

public class Instance {
    private int numberOfTasks;
    private int numberOfResources;
    private int numberOfPrecedenceRelations;
    private int numberOfSkillTypes;
    private List<Resource> resources;
    private List<Task> tasks;

    public Instance(int numberOfTasks, int numberOfResources, int numberOfPrecedenceRelations, int numberOfSkillTypes) {
        this.numberOfTasks = numberOfTasks;
        this.numberOfResources = numberOfResources;
        this.numberOfPrecedenceRelations = numberOfPrecedenceRelations;
        this.numberOfSkillTypes = numberOfSkillTypes;
        this.resources = new ArrayList<>();
        this.tasks = new ArrayList<>();
    }

    public int getNumberOfTasks() {
        return numberOfTasks;
    }

    public int getNumberOfResources() {
        return numberOfResources;
    }

    public int getNumberOfPrecedenceRelations() {
        return numberOfPrecedenceRelations;
    }

    public int getNumberOfSkillTypes() {
        return numberOfSkillTypes;
    }

    public List<Resource> getResources() {
        return resources;
    }

    public void addResource(Resource resource) {
        resources.add(resource);
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void addTask(Task task) {
        tasks.add(task);
    }
}
