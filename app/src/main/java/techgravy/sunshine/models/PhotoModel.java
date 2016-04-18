package techgravy.sunshine.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by aditlal on 14/04/16.
 */
public class PhotoModel {
    private String id;

    private String favorites_count;

    @SerializedName("width")
    private int width;

    @SerializedName("height")
    private int height;

    private String image_url;

    private String description;

    private String name;

    private String created_at;

    private String user_id;

    private String url;

    @SerializedName("user")
    private PhotoUserModel userModel;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public PhotoUserModel getUserModel() {
        return userModel;
    }

    public void setUserModel(PhotoUserModel userModel) {
        this.userModel = userModel;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFavorites_count() {
        return favorites_count;
    }

    public void setFavorites_count(String favorites_count) {
        this.favorites_count = favorites_count;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    @Override
    public String toString() {
        return "PhotoModel{" +
                "id='" + id + '\'' +
                ", favorites_count='" + favorites_count + '\'' +
                ", height='" + height + '\'' +
                ", image_url='" + image_url + '\'' +
                ", description='" + description + '\'' +
                ", name='" + name + '\'' +
                ", created_at='" + created_at + '\'' +
                ", user_id='" + user_id + '\'' +
                '}';
    }
}
