package Activities.models.usermodel;

public class Model {
    String id;
    String username;
    String imageURL;

    public Model(String id, String username, String imageURL) {
        this.id = id;
        this.username = username;
        this.imageURL = imageURL;
    }

    public Model() {
    }

    public String getId() {return id;}
    public void setId(String id) {this.id = id;}
    public String getUsername() {return username;}
    public void setUsername(String username) {this.username = username;}
    public String getImageURL() {return imageURL; }
    public void setImageURL(String imageURL) {this.imageURL = imageURL;}

}
