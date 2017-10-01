package edwinvillatoro.snapfix.objects;


public class Report {

    private String id;
    private String userID;
    private String timestamp;
    private String problem_type;
    private String location;
    private String description;
    private String assigned_to;

    public Report(String id, String userID, String timestamp, String problem_type, String location, String description, String assigned_to) {
        this.id = id;
        this.userID = userID;
        this.timestamp = timestamp;
        this.problem_type = problem_type;
        this.location = location;
        this.description = description;
        this.assigned_to = assigned_to;
    }

    public String getId() {
        return id;
    }

    public String getUserID() { return userID; }

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

    public String getAssigned_to() { return assigned_to; }

    public void setAssigned_to(String assigned) {
        this.assigned_to = assigned;
    }
}
