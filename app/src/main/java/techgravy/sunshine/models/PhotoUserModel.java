package techgravy.sunshine.models;

/**
 * Created by aditlal on 18/04/16.
 */
public class PhotoUserModel {
    private String store_on;

    private String affection;

    private String cover_url;

    private String upgrade_status;

    private String userpic_https_url;

    private String lastname;

    private String usertype;

    private String firstname;

    private String city;

    private String country;

    private String id;

    private String username;

    private String userpic_url;

    private String fullname;

    public String getStore_on ()
    {
        return store_on;
    }

    public void setStore_on (String store_on)
    {
        this.store_on = store_on;
    }

    public String getAffection ()
    {
        return affection;
    }

    public void setAffection (String affection)
    {
        this.affection = affection;
    }

    public String getCover_url ()
    {
        return cover_url;
    }

    public void setCover_url (String cover_url)
    {
        this.cover_url = cover_url;
    }

    public String getUpgrade_status ()
    {
        return upgrade_status;
    }

    public void setUpgrade_status (String upgrade_status)
    {
        this.upgrade_status = upgrade_status;
    }

    public String getUserpic_https_url ()
    {
        return userpic_https_url;
    }

    public void setUserpic_https_url (String userpic_https_url)
    {
        this.userpic_https_url = userpic_https_url;
    }

    public String getLastname ()
    {
        return lastname;
    }

    public void setLastname (String lastname)
    {
        this.lastname = lastname;
    }

    public String getUsertype ()
    {
        return usertype;
    }

    public void setUsertype (String usertype)
    {
        this.usertype = usertype;
    }

    public String getFirstname ()
    {
        return firstname;
    }

    public void setFirstname (String firstname)
    {
        this.firstname = firstname;
    }

    public String getCity ()
    {
        return city;
    }

    public void setCity (String city)
    {
        this.city = city;
    }

    public String getCountry ()
    {
        return country;
    }

    public void setCountry (String country)
    {
        this.country = country;
    }

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    public String getUsername ()
    {
        return username;
    }

    public void setUsername (String username)
    {
        this.username = username;
    }

    public String getUserpic_url ()
    {
        return userpic_url;
    }

    public void setUserpic_url (String userpic_url)
    {
        this.userpic_url = userpic_url;
    }

    public String getFullname ()
    {
        return fullname;
    }

    public void setFullname (String fullname)
    {
        this.fullname = fullname;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [store_on = "+store_on+", affection = "+affection+", cover_url = "+cover_url+", upgrade_status = "+upgrade_status+", userpic_https_url = "+userpic_https_url+", lastname = "+lastname+", usertype = "+usertype+", firstname = "+firstname+", city = "+city+", country = "+country+", id = "+id+", username = "+username+", userpic_url = "+userpic_url+", fullname = "+fullname+"]";
    }
}
