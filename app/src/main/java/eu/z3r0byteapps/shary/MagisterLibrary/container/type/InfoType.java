/*
 * Copyright (c) 2016-2016 Bas van den Boom 'Z3r0byte'
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

package eu.z3r0byteapps.shary.MagisterLibrary.container.type;

import java.io.Serializable;

public enum InfoType implements Serializable {
    NONE(0),
    HOMEWORK(1),
    TEST(2),
    EXAM(3),
    QUIZ(4),
    ORAL(5),
    INFORMATION(6),
    ANNOTATION(7);

    private int id;

    InfoType(int i) {
        id = i;
    }

    public static InfoType getTypeById(int i) {
        for (InfoType type : values()) {
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