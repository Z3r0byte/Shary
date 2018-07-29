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

package eu.z3r0byteapps.shary.MagisterLibrary.adapter.type;

import com.google.gson.Gson;
import com.google.gson.JsonPrimitive;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

import eu.z3r0byteapps.shary.MagisterLibrary.container.type.InfoType;
import eu.z3r0byteapps.shary.MagisterLibrary.util.GsonUtil;

public class InfoTypeAdapter extends TypeAdapter<InfoType> {
    public Gson gson = GsonUtil.getGson();

    @Override
    public void write(JsonWriter out, InfoType value) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public InfoType read(JsonReader in) throws IOException {
        JsonPrimitive primitive = gson.getAdapter(JsonPrimitive.class).read(in);
        int id = primitive.getAsInt();
        return InfoType.getTypeById(id);
    }
}
