package techgravy.sunshine.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by aditlal on 14/04/16.
 */
public class Wind {
    @SerializedName("speed")
    private float speed;

    @SerializedName("double")
    private float deg;

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getDeg() {
        return deg;
    }

    public void setDeg(float deg) {
        this.deg = deg;
    }

    @Override
    public String toString() {
        return "ClassPojo [speed = " + speed + ", deg = " + deg + "]";
    }
}
