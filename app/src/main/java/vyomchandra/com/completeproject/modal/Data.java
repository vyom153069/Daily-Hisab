package vyomchandra.com.completeproject.modal;

public class Data {
    String title,description,id,budget,data;
    Data(){

    }

    public Data(String title, String description, String id, String budget, String data) {
        this.title = title;
        this.description = description;
        this.id = id;
        this.budget = budget;
        this.data = data;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBudget() {
        return budget;
    }

    public void setBudget(String budget) {
        this.budget = budget;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
