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

package eu.z3r0byteapps.shary.MagisterLibrary.adapter;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import eu.z3r0byteapps.shary.MagisterLibrary.container.Study;
import eu.z3r0byteapps.shary.MagisterLibrary.util.DateUtil;
import eu.z3r0byteapps.shary.MagisterLibrary.util.GsonUtil;
import eu.z3r0byteapps.shary.MagisterLibrary.util.LogUtil;

public class StudyAdapter extends TypeAdapter<Study[]> {
    public Gson gson = GsonUtil.getGson();

    @Override
    public void write(JsonWriter out, Study[] value) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Study[] read(JsonReader in) throws IOException {
        JsonObject object = gson.getAdapter(JsonElement.class).read(in).getAsJsonObject();
        JsonArray array = object.get("Items").getAsJsonArray();
        List<Study> studyList = new ArrayList<Study>();
        for (JsonElement element : array) {
            JsonObject object1 = element.getAsJsonObject();
            Study study = gson.fromJson(object1, Study.class);
            JsonObject object2 = object1.get("Studie").getAsJsonObject();
            study.studyId = object2.get("Id").getAsInt();
            study.description = object2.get("Omschrijving").getAsString();
            try {
                study.startDate = DateUtil.stringToDate(study.startDateString);
                study.endDate = DateUtil.stringToDate(study.endDateString);
            } catch (ParseException e) {
                LogUtil.printError("Unable to parse date", e);
            }
            studyList.add(study);
        }
        return studyList.toArray(new Study[studyList.size()]);
    }
}
