package models;

public class GuideNote {

    private String content;

    public GuideNote(String content){
        setContent(content);
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }
}
