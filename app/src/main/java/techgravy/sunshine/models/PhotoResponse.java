package techgravy.sunshine.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by aditlal on 14/04/16.
 */
public class PhotoResponse implements Parcelable {
    @SerializedName("photos")
    private List<PhotoModel> photos;

    private String total_items;

    private String total_pages;

    private String current_page;

    public PhotoResponse() {
    }

    public List<PhotoModel> getPhotos() {
        return photos;
    }

    public void setPhotos(List<PhotoModel> photos) {
        this.photos = photos;
    }

    public String getTotal_items() {
        return total_items;
    }

    public void setTotal_items(String total_items) {
        this.total_items = total_items;
    }

    public String getTotal_pages() {
        return total_pages;
    }

    public void setTotal_pages(String total_pages) {
        this.total_pages = total_pages;
    }

    public String getCurrent_page() {
        return current_page;
    }

    public void setCurrent_page(String current_page) {
        this.current_page = current_page;
    }

    @Override
    public String toString() {
        return "ClassPojo [photos = " + photos + ", total_items = " + total_items + ", total_pages = " + total_pages + ", current_page = " + current_page + "]";
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(total_items);
        dest.writeString(total_pages);
        dest.writeString(current_page);
        dest.writeList(photos);
    }

    // Creator
    public static final Parcelable.Creator<PhotoResponse> CREATOR
            = new Parcelable.Creator<PhotoResponse>() {
        public PhotoResponse createFromParcel(Parcel in) {
            return new PhotoResponse(in);
        }

        public PhotoResponse[] newArray(int size) {
            return new PhotoResponse[size];
        }
    };

    // "De-parcel object
    public PhotoResponse(Parcel in) {
        total_items = in.readString();
        total_pages = in.readString();
        current_page = in.readString();
        List<PhotoModel> list = null;
        in.readList(list, PhotoModel.class.getClassLoader());
        photos = list;
    }

}

