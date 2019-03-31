package verma.sahil.arthub.models;

import java.util.ArrayList;

public class Diary {

    private String id;
    private ArrayList<Message> messages;

    public Diary(String id) {
        this.id = id;
    }

    public Diary() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<Message> messages) {
        this.messages = messages;
    }
}
