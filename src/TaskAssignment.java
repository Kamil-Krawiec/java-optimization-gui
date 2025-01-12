public class TaskAssignment {
    private Task task;
    private Resource resource;

    public TaskAssignment(Task task, Resource resource) {
        this.task = task;
        this.resource = resource;
    }

    public Task getTask() {
        return task;
    }

    public Resource getResource() {
        return resource;
    }
}
