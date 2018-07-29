package eu.z3r0byteapps.shary.SharyLibrary;

public class User {
    private String session_id;
    private String uuid;
    private Integer personid;
    private String schoolurl;

    public User(String session_id, String uuid, Integer personid, String schoolurl) {
        this.session_id = session_id;
        this.uuid = uuid;
        this.personid = personid;
        this.schoolurl = schoolurl;
    }

    public String getSession_id() {
        return session_id;
    }

    public void setSession_id(String session_id) {
        this.session_id = session_id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Integer getPersonid() {
        return personid;
    }

    public void setPersonid(Integer personid) {
        this.personid = personid;
    }

    public String getSchoolurl() {
        return schoolurl;
    }

    public void setSchoolurl(String schoolurl) {
        this.schoolurl = schoolurl;
    }
}
