package verma.sahil.arthub.models;

import java.util.ArrayList;

public class User {
    private String id;
    private String name;
    private ArrayList<String> project_ids = new ArrayList<>();

    public User() {
    }

    public User(String id) {
        this.id = id;
    }

    public User(String id, String name, ArrayList<String> project_ids) {
        this.id = id;
        this.name = name;
        if(project_ids != null )
            this.project_ids = project_ids;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getProject_ids() {
        return project_ids;
    }

    public void setProject_ids(ArrayList<String> project_ids) {
        this.project_ids = project_ids;
    }
}
