/*
 * Copyright (c) 2016-2018 Bas van den Boom 'Z3r0byte'
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
