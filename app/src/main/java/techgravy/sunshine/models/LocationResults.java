package techgravy.sunshine.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by aditlal on 06/07/16.
 */
public class LocationResults {
    private String place_id;

    @SerializedName("address_components")
    private List<Address_components> address_components;

    private String formatted_address;


    public String getPlace_id() {
        return place_id;
    }

    public void setPlace_id(String place_id) {
        this.place_id = place_id;
    }

    public List<Address_components> getAddress_components() {
        return address_components;
    }

    public void setAddress_components(List<Address_components> address_components) {
        this.address_components = address_components;
    }

    public String getFormatted_address() {
        return formatted_address;
    }

    public void setFormatted_address(String formatted_address) {
        this.formatted_address = formatted_address;
    }


    @Override
    public String toString() {
        return "ClassPojo [place_id = " + place_id + ", address_components = " + address_components + ", formatted_address = " + formatted_address  + "]";
    }
}
