package techgravy.sunshine.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by aditlal on 06/07/16.
 */

public class LocationModel {
    @SerializedName("results")
    List<LocationResults> resultsList;

    private String status;

    public List<LocationResults> getResultsList() {
        return resultsList;
    }

    public void setResultsList(List<LocationResults> resultsList) {
        this.resultsList = resultsList;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "ClassPojo [results = " + resultsList + ", status = " + status + "]";
    }

}
