package techgravy.sunshine.models;

/**
 * Created by aditlal on 06/07/16.
 */
public class Address_components {
    private String long_name;

    private String[] types;

    private String short_name;

    public String getLong_name ()
    {
        return long_name;
    }


    public String[] getTypes ()
    {
        return types;
    }


    @Override
    public String toString()
    {
        return "ClassPojo [long_name = "+long_name+", types = "+types+", short_name = "+short_name+"]";
    }
}
