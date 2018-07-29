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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.z3r0byteapps.shary.MagisterLibrary.adapter.type.AppointmentTypeAdapter;
import eu.z3r0byteapps.shary.MagisterLibrary.adapter.type.DisplayTypeAdapter;
import eu.z3r0byteapps.shary.MagisterLibrary.adapter.type.InfoTypeAdapter;
import eu.z3r0byteapps.shary.MagisterLibrary.container.Appointment;
import eu.z3r0byteapps.shary.MagisterLibrary.container.type.AppointmentType;
import eu.z3r0byteapps.shary.MagisterLibrary.container.type.DisplayType;
import eu.z3r0byteapps.shary.MagisterLibrary.container.type.InfoType;
import eu.z3r0byteapps.shary.MagisterLibrary.util.DateUtil;
import eu.z3r0byteapps.shary.MagisterLibrary.util.GsonUtil;

public class AppointmentAdapter extends TypeAdapter<Appointment[]> {
    private static Gson gson;

    static {
        Map<Class<?>, TypeAdapter<?>> map = new HashMap<Class<?>, TypeAdapter<?>>();
        map.put(AppointmentType.class, new AppointmentTypeAdapter());
        map.put(DisplayType.class, new DisplayTypeAdapter());
        map.put(InfoType.class, new InfoTypeAdapter());
        gson = GsonUtil.getGsonWithAdapters(map);
    }

    @Override
    public void write(JsonWriter out, Appointment[] value) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Appointment[] read(JsonReader in) throws IOException {
        JsonObject object = gson.getAdapter(JsonElement.class).read(in).getAsJsonObject();
        if (object.has("Items")) {
            JsonArray array = object.get("Items").getAsJsonArray();
            List<Appointment> appointmentList = new ArrayList<Appointment>();
            for (JsonElement element : array) {
                JsonObject object1 = element.getAsJsonObject();
                Appointment appointment = gson.fromJson(object1, Appointment.class);
                try {
                    appointment.startDate = DateUtil.stringToDate(appointment.startDateString);
                    appointment.endDate = DateUtil.stringToDate(appointment.endDateString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                appointment.type = gson.getAdapter(AppointmentType.class).fromJsonTree(object1.getAsJsonPrimitive("Type"));
                appointment.displayType = gson.getAdapter(DisplayType.class).fromJsonTree(object1.getAsJsonPrimitive("WeergaveType"));
                appointment.infoType = gson.getAdapter(InfoType.class).fromJsonTree(object1.getAsJsonPrimitive("InfoType"));
                appointmentList.add(appointment);
            }
            return appointmentList.toArray(new Appointment[appointmentList.size()]);
        } else {
            Appointment appointment = gson.fromJson(object, Appointment.class);
            try {
                appointment.startDate = DateUtil.stringToDate(appointment.startDateString);
                appointment.endDate = DateUtil.stringToDate(appointment.endDateString);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            appointment.type = gson.getAdapter(AppointmentType.class).fromJsonTree(object.getAsJsonPrimitive("Type"));
            appointment.displayType = gson.getAdapter(DisplayType.class).fromJsonTree(object.getAsJsonPrimitive("WeergaveType"));
            appointment.infoType = gson.getAdapter(InfoType.class).fromJsonTree(object.getAsJsonPrimitive("InfoType"));
            return new Appointment[]{appointment};
        }
    }
}
