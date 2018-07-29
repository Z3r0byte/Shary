package eu.z3r0byteapps.shary.SharyLibrary;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public enum ShareType implements Serializable {
    @SerializedName("1") CALENDAR(1),
    @SerializedName("2") NEWGRADES(2),
    @SerializedName("3") GRADES(3);

    private int id;

    ShareType(int i) {
        id = i;
    }

    public static ShareType getTypeById(int i) {
        for (ShareType type : values()) {
            if (type.getID() == i) {
                return type;
            }
        }
        return null;
    }

    public int getID() {
        return id;
    }
}
