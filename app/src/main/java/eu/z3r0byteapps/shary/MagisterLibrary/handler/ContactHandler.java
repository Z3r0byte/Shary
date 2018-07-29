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

package eu.z3r0byteapps.shary.MagisterLibrary.handler;

import com.google.gson.Gson;

import java.io.IOException;

import eu.z3r0byteapps.shary.MagisterLibrary.Magister;
import eu.z3r0byteapps.shary.MagisterLibrary.adapter.ArrayAdapter;
import eu.z3r0byteapps.shary.MagisterLibrary.container.Contact;
import eu.z3r0byteapps.shary.MagisterLibrary.exeption.PrivilegeException;
import eu.z3r0byteapps.shary.MagisterLibrary.util.GsonUtil;
import eu.z3r0byteapps.shary.MagisterLibrary.util.HttpUtil;

public class ContactHandler implements IHandler {
    private Gson gson = GsonUtil.getGsonWithAdapter(Contact[].class, new ArrayAdapter<Contact>(Contact.class, Contact[].class));
    private Magister magister;

    public ContactHandler(Magister magister) {
        this.magister = magister;
    }

    /**
     * Get an array of {@link Contact}s with pupil contact information. If no contacts can be found, an empty array will
     * be returned instead.
     *
     * @param name the name of the pupil.
     * @return an array of {@link Contact}s with the contact information.
     * @throws IOException        if there is no active internet connection.
     * @throws PrivilegeException if the profile doesn't have the privilege to perform this action.
     */
    public Contact[] getPupilInfo(String name) throws IOException, PrivilegeException {
        return getContactInfo(name, "Leerling");
    }

    /**
     * Get an array of {@link Contact}s with teacher contact information. If no contacts can be found, an empty array
     * will be returned instead.
     *
     * @param name the name of the teacher.
     * @return an array of {@link Contact} with the contact information.
     * @throws IOException        if there is no active internet connection.
     * @throws PrivilegeException if the profile doesn't have the privilege to perform this action.
     */
    public Contact[] getTeacherInfo(String name) throws IOException, PrivilegeException {
        return getContactInfo(name, "Personeel");
    }

    /**
     * Get an array of {@link Contact}s with contact information. If no contacts can be found, an empty array will be
     * returned instead.
     *
     * @param name the name.
     * @param type the type.
     * @return an array of {@link Contact} with the contact information.
     * @throws IOException        if there is no active internet connection.
     * @throws PrivilegeException if the profile doesn't have the privilege to perform this action.
     */
    public Contact[] getContactInfo(String name, String type) throws IOException, PrivilegeException {
        return gson.fromJson(HttpUtil.httpGet(magister.schoolUrl.getApiUrl() + "personen/" + magister.profile.id + "/contactpersonen?contactPersoonType=" + type + "&q=" + name), Contact[].class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPrivilege() {
        return "Contactpersonen";
    }
}
