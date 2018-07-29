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
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import eu.z3r0byteapps.shary.MagisterLibrary.container.SingleMessage;
import eu.z3r0byteapps.shary.MagisterLibrary.util.GsonUtil;

public class SingleMessageAdapter extends TypeAdapter<SingleMessage[]> {
    public Gson gson = GsonUtil.getGson();

    @Override
    public void write(JsonWriter out, SingleMessage[] value) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public SingleMessage[] read(JsonReader in) throws IOException {
        JsonObject object = gson.getAdapter(JsonElement.class).read(in).getAsJsonObject();
        List<SingleMessage> singleMessageList = new ArrayList<SingleMessage>();
        singleMessageList.add(gson.fromJson(object.getAsJsonObject(), SingleMessage.class));
        return singleMessageList.toArray(new SingleMessage[singleMessageList.size()]);
    }
}
