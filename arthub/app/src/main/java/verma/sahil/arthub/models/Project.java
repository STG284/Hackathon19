package verma.sahil.arthub.models;

import java.util.ArrayList;

public class Project {

    private String id;
    private String title;
    private String desc;
    private String image;
    private String leader_id;
    private ArrayList<String> collaborator_id;
    private String diary_Id;
    private Boolean isCompleted = false;


    public Project(String title, String desc, String image, String leader_id, ArrayList<String> collaborator_id, String diary_Id, Boolean isCompleted) {
        this.title = title;
        this.desc = desc;
        this.image = image;
        this.leader_id = leader_id;
        this.collaborator_id = collaborator_id;
        this.diary_Id = diary_Id;
        this.isCompleted = isCompleted;
    }

    public Project() {

    }

    public Project(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLeader_id() {
        return leader_id;
    }

    public void setLeader_id(String leader_id) {
        this.leader_id = leader_id;
    }

    public ArrayList<String> getCollaborator_id() {
        return collaborator_id;
    }

    public void setCollaborator_id(ArrayList<String> collaborator_id) {
        this.collaborator_id = collaborator_id;
    }

    public String getDiary_Id() {
        return diary_Id;
    }

    public void setDiary_Id(String diary_Id) {
        this.diary_Id = diary_Id;
    }

    public Boolean getCompleted() {
        return isCompleted;
    }

    public void setCompleted(Boolean completed) {
        isCompleted = completed;
    }
}
