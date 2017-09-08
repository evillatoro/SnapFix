package edwinvillatoro.snapfix.objects;


public class Report {

    private String id;
    private String timestamp;
    private String problem_type;
    private String location;
    private String description;

    public Report(String id, String timestamp, String problem_type, String location, String description) {
        this.id = id;
        this.timestamp = timestamp;
        this.problem_type = problem_type;
        this.location = location;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getProblem_type() {
        return problem_type;
    }

    public String getLocation() {
        return location;
    }

    public String getDescription() {
        return description;
    }
}
