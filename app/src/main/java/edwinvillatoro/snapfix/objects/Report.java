package edwinvillatoro.snapfix.objects;


import java.util.List;

public class Report {

    private String id;
    private String userID;
    private String timestamp;
    private String problem_type;
    private String location;
    private String description;
    private String assigned_to;

    public Report(String id, String userID, String timestamp, String problem_type, String location, String description) {
        this.id = id;
        this.userID = userID;
        this.timestamp = timestamp;
        this.problem_type = problem_type;
        this.location = location;
        this.description = description;
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

    public String getAssigned() { return assigned_to; }


    // TODO: get list of all reports with the same userID

    public List<Report> getUserReport(String uid) {
        return null;
    }
}
