import java.util.Map;

public class Resource {
    private int id;
    private float salary;
    private Map<String, Integer> skills;

    public Resource(int id, float salary, Map<String, Integer> skills) {
        this.id = id;
        this.salary = salary;
        this.skills = skills;
    }

    public int getId() {
        return id;
    }

    public float getSalary() {
        return salary;
    }

    public Map<String, Integer> getSkills() {
        return skills;
    }
}
