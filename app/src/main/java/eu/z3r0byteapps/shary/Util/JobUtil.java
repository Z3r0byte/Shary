/*
 * Copyright (c) 2018-2018 Bas van den Boom 'Z3r0byte'
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

package eu.z3r0byteapps.shary.Util;

import android.content.Context;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

import eu.z3r0byteapps.shary.Services.RenewSessionKey;

public class JobUtil {
    public static void scheduleJob(Context context) {
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));

        Job myJob = dispatcher.newJobBuilder()
                .setService(RenewSessionKey.class)
                .setTag("shary-renew-session")
                .setTrigger(Trigger.executionWindow(12 * 3600, 24 * 3600))
                .setLifetime(Lifetime.FOREVER)
                .setConstraints(
                        Constraint.ON_UNMETERED_NETWORK
                )
                .build();

        dispatcher.mustSchedule(myJob);
    }

    public static void cancelAllJobs(Context context) {
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));
        dispatcher.cancelAll();
    }
}
