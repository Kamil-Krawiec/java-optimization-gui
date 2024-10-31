import java.util.List;
import java.util.Map;

public class Task {
    private int id;
    private int duration;
    private Map<String, Integer> skill;
    private List<Integer> predecessorIds;

    public Task(int id, int duration, Map<String, Integer> skill, List<Integer> predecessorIds) {
        this.id = id;
        this.duration = duration;
        this.skill = skill;
        this.predecessorIds = predecessorIds;
    }

    public int getId() {
        return id;
    }

    public int getDuration() {
        return duration;
    }

    public Map<String, Integer> getSkill() {
        return skill;
    }

    public List<Integer> getPredecessorIds() {
        return predecessorIds;
    }
}
