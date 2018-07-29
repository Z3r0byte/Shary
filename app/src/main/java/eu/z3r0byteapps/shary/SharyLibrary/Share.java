package eu.z3r0byteapps.shary.SharyLibrary;

import java.util.Date;

import eu.z3r0byteapps.shary.MagisterLibrary.container.ShareRestriction;

public class Share {
    private Date expire;
    private ShareRestriction restrictions;
    private ShareType type;
    private String comment;
    private String secret;
    private int id;

    public Date getExpire() {
        return expire;
    }

    public void setExpire(Date expire) {
        this.expire = expire;
    }

    public ShareRestriction getRestrictions() {
        return restrictions;
    }

    public void setRestrictions(ShareRestriction restrictions) {
        this.restrictions = restrictions;
    }

    public ShareType getType() {
        return type;
    }

    public void setType(ShareType type) {
        this.type = type;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Share{" +
                "expire=" + expire +
                ", restrictions=" + restrictions +
                ", type=" + type +
                ", comment='" + comment + '\'' +
                ", secret='" + secret + '\'' +
                ", id=" + id +
                '}';
    }
}
