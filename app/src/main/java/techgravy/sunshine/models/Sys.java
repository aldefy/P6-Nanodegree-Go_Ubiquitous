package techgravy.sunshine.models;

/**
 * Created by aditlal on 14/04/16.
 */
public class Sys {
    private String pod;

    public String getPod ()
    {
        return pod;
    }

    public void setPod (String pod)
    {
        this.pod = pod;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [pod = "+pod+"]";
    }
}
