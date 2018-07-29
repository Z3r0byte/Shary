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
import com.google.gson.TypeAdapter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import eu.z3r0byteapps.shary.MagisterLibrary.Magister;
import eu.z3r0byteapps.shary.MagisterLibrary.adapter.ArrayAdapter;
import eu.z3r0byteapps.shary.MagisterLibrary.adapter.SingleStudyGuideAdapter;
import eu.z3r0byteapps.shary.MagisterLibrary.container.SingleStudyGuide;
import eu.z3r0byteapps.shary.MagisterLibrary.container.Source;
import eu.z3r0byteapps.shary.MagisterLibrary.container.StudyGuide;
import eu.z3r0byteapps.shary.MagisterLibrary.util.GsonUtil;
import eu.z3r0byteapps.shary.MagisterLibrary.util.HttpUtil;

public class ELOHandler implements IHandler {
    public Gson gson;
    private Magister magister;

    public ELOHandler(Magister magister) {
        this.magister = magister;
        Map<Class<?>, TypeAdapter<?>> map = new HashMap<Class<?>, TypeAdapter<?>>();
        map.put(Source[].class, new ArrayAdapter<Source>(Source.class, Source[].class));
        map.put(StudyGuide[].class, new ArrayAdapter<StudyGuide>(StudyGuide.class, StudyGuide[].class));
        map.put(SingleStudyGuide.class, new SingleStudyGuideAdapter());
        gson = GsonUtil.getGsonWithAdapters(map);
    }

    /**
     * Get all the ELO sources for this profile.
     *
     * @return all the ELO sources for this profile.
     * @throws IOException if there is no active internet connection.
     */
    public Source[] getSources() throws IOException {
        return gson.fromJson(HttpUtil.httpGet(magister.schoolUrl.getApiUrl() + "personen/" + magister.profile.id + "/bronnen?soort=0"), Source[].class);
    }

    /**
     * Get all study guides for this profile.
     *
     * @return all study guides for this profile.
     * @throws IOException if there is no active internet connection.
     */
    public StudyGuide[] getStudyGuides() throws IOException {
        return gson.fromJson(HttpUtil.httpGet(magister.schoolUrl.getApiUrl() + "leerlingen/" + magister.profile.id + "/studiewijzers"), StudyGuide[].class);
    }

    /**
     * Get more data about a specific study guide.
     *
     * @param studyGuide the study guide.
     * @return an object with more data about the study guide.
     * @throws IOException if there is no active internet connection.
     */
    public SingleStudyGuide getStudyGuide(StudyGuide studyGuide) throws IOException {
        return getStudyGuide(studyGuide.id);
    }

    /**
     * Get more data about a specific study guide.
     *
     * @param studyGuideID the study guide id.
     * @return an object with more data about the study guide.
     * @throws IOException if there is no active internet connection.
     */
    public SingleStudyGuide getStudyGuide(int studyGuideID) throws IOException {
        return gson.fromJson(HttpUtil.httpGet(magister.schoolUrl.getApiUrl() + "leerlingen/" + magister.profile.id + "/studiewijzers/" + studyGuideID), SingleStudyGuide.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPrivilege() {
        return "EloOpdracht";
    }
}
