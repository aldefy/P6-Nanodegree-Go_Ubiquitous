package techgravy.sunshine.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.realm.RealmModel;
import io.realm.annotations.RealmClass;

/**
 * Created by aditlal on 06/04/16.
 */
@RealmClass
public class Weather implements RealmModel, Serializable {

    @SerializedName("id")
    private int mId;

    private String icon;

    private String description;

    private String main;

    public int getmId() {
        return mId;
    }

    public void setmId(int id) {
        this.mId = id;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMain() {
        return main;
    }

    public void setMain(String main) {
        this.main = main;
    }

    @Override
    public String toString() {
        return "ClassPojo [id = " + mId + ", icon = " + icon + ", description = " + description + ", main = " + main + "]";
    }


}
