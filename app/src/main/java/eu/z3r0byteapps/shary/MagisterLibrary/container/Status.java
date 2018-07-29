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

package eu.z3r0byteapps.shary.MagisterLibrary.container;

import android.util.Log;

/**
 * Created by bas on 5-10-16.
 */

public enum Status {
    OK("ok"),
    SLOW("warn"),
    OFFLINE("error");

    private static final String TAG = "Status";

    private String status;

    Status(String s) {
        status = s;
    }

    public static Status getStatusByString(String s) {
        for (Status status : values()) {
            if (s.contains(status.getStatus())) {
                return status;
            }
        }
        Log.d(TAG, "getStatusByString: No valid status.");
        return null;
    }

    public String getStatus() {
        return status;
    }
}
