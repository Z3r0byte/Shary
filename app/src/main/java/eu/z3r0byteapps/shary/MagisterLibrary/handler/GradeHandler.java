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

package eu.z3r0byteapps.shary.MagisterLibrary.handler;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import eu.z3r0byteapps.shary.MagisterLibrary.Magister;
import eu.z3r0byteapps.shary.MagisterLibrary.adapter.GradeAdapter;
import eu.z3r0byteapps.shary.MagisterLibrary.container.Grade;
import eu.z3r0byteapps.shary.MagisterLibrary.container.SingleGrade;
import eu.z3r0byteapps.shary.MagisterLibrary.container.Study;
import eu.z3r0byteapps.shary.MagisterLibrary.container.Subject;
import eu.z3r0byteapps.shary.MagisterLibrary.container.sub.SubSubject;
import eu.z3r0byteapps.shary.MagisterLibrary.util.GsonUtil;
import eu.z3r0byteapps.shary.MagisterLibrary.util.HttpUtil;

public class GradeHandler implements IHandler {
    private Gson gson = GsonUtil.getGsonWithAdapter(Grade[].class, new GradeAdapter());
    private Magister magister;

    public GradeHandler(Magister magister) {
        this.magister = magister;
    }

    /**
     * Get an array of {@link Grade}s. If no grades can be found, an empty array
     * will be returned instead.
     *
     * @param onlyAverage     only count the average grades.
     * @param onlyPTA         only count the PTA grades.
     * @param onlyActiveStudy only check the current study.
     * @return an array of {@link Grade}s.
     * @throws IOException if there is no active internet connection.
     */
    public Grade[] getGrades(boolean onlyAverage, boolean onlyPTA, boolean onlyActiveStudy) throws IOException {
        return gson.fromJson(HttpUtil.httpGet(magister.schoolUrl.getApiUrl() + "personen/" + magister.profile.id + "/aanmeldingen/" + magister.currentStudy.id + "/cijfers/cijferoverzichtvooraanmelding?alleenBerekendeKolommen=" + onlyAverage + "&alleenPTAKolommen=" + onlyPTA + "&actievePerioden=" + onlyActiveStudy), Grade[].class);
    }

    /**
     * Get an array of all the {@link Grade}s this profile hs ever got.
     *
     * @return an array of all the {@link Grade}s this profile has ever got.
     * @throws IOException if there is no active internet connection.
     */
    public Grade[] getAllGrades() throws IOException {
        return getGrades(false, false, false);
    }


    public Grade[] getGradesFromStudy(Study study, boolean onlyAverage, boolean onlyPTA) throws IOException {
        return gson.fromJson(HttpUtil.httpGet(magister.schoolUrl.getApiUrl() + "personen/" + magister.profile.id + "/aanmeldingen/" + study.id + "/cijfers/cijferoverzichtvooraanmelding?alleenBerekendeKolommen=" + onlyAverage + "&alleenPTAKolommen=" + onlyPTA + "&actievePerioden=false&peildatum=" + study.endDateString), Grade[].class);
    }

    /**
     * Get all grades from a period of 7 days.
     *
     * @return all grades from a period of 7 days.
     * @throws IOException if there is no active internet connection.
     */
    public Grade[] getRecentGrades() throws IOException {
        return gson.fromJson(HttpUtil.httpGet(magister.schoolUrl.getApiUrl() + "personen/" + magister.profile.id + "/cijfers/laatste?top=15&skip=0"), Grade[].class);
    }

    /**
     * Get all grades from a specific subject.
     *
     * @param subject         the subject.
     * @param onlyAverage     only count the average grades.
     * @param onlyPTA         only count the PTA grades.
     * @param onlyActiveStudy only check the current study.
     * @return all the grades from the specific subject.
     * @throws IOException if there is no active internet connection.
     */
    public Grade[] getGradesFromSubject(SubSubject subject, boolean onlyAverage, boolean onlyPTA, boolean onlyActiveStudy) throws IOException {
        return getGradesFromSubjectID(subject.id, onlyAverage, onlyPTA, onlyActiveStudy);
    }

    public Grade[] getGradesFromSubject(Subject subject, boolean onlyAverage, boolean onlyPTA, boolean onlyActiveStudy) throws IOException {
        return getGradesFromSubjectID(subject.id, onlyAverage, onlyPTA, onlyActiveStudy);
    }

    public Grade[] getGradesFromSubject(SubSubject subject, boolean onlyAverage, boolean onlyPTA, Study study) throws IOException {
        return getGradesFromSubjectID(subject.id, onlyAverage, onlyPTA, study);
    }

    public Grade[] getGradesFromSubjectID(int subjectID, boolean onlyAverage, boolean onlyPTA, boolean onlyActiveStudy) throws IOException {
        List<Grade> gradeList = new ArrayList<Grade>();
        for (Grade grade : getGrades(onlyAverage, onlyPTA, onlyActiveStudy)) {
            if (grade.subject.id == subjectID) {
                try {
                    grade.singleGrade = getSingleGrade(grade);
                } catch (IOException e) {
                }
                gradeList.add(grade);
            }
        }
        return gradeList.toArray(new Grade[gradeList.size()]);
    }

    public Grade[] getGradesFromSubjectID(int subjectID, boolean onlyAverage, boolean onlyPTA, Study study) throws IOException {
        List<Grade> gradeList = new ArrayList<Grade>();
        for (Grade grade : getGradesFromStudy(study, onlyAverage, onlyPTA)) {
            if (grade.subject.id == subjectID) {
                try {
                    grade.singleGrade = getSingleGrade(grade, study);
                } catch (IOException e) {
                }
                gradeList.add(grade);
            }
        }
        return gradeList.toArray(new Grade[gradeList.size()]);
    }

    /**
     * Get all grades from a specific subject.
     *
     * @param subject the subject.
     * @return all the grades from the specific subject.
     * @throws IOException if there is no active internet connection.
     */
    public Grade[] getAllGradesFromSubject(SubSubject subject) throws IOException {
        return getAllGradesFromSubjectID(subject.id);
    }

    public Grade[] getAllGradesFromSubject(Subject subject) throws IOException {
        return getAllGradesFromSubjectID(subject.id);
    }

    public Grade[] getAllGradesFromSubjectID(int subjectID) throws IOException {
        List<Grade> gradeList = new ArrayList<Grade>();
        for (Grade grade : getAllGrades()) {
            if (grade.subject.id == subjectID) {
                gradeList.add(grade);
            }
        }
        return gradeList.toArray(new Grade[gradeList.size()]);
    }

    /**
     * Get more info about a specific grade.
     *
     * @param grade the grade to get more info about.
     * @return a SingleGrade instance.
     * @throws IOException if there is no active internet connection.
     */
    public SingleGrade getSingleGrade(Grade grade) throws IOException {
        return gson.fromJson(HttpUtil.httpGet(magister.schoolUrl.getApiUrl() + "personen/" + magister.profile.id + "/aanmeldingen/" + magister.currentStudy.id + "/cijfers/extracijferkolominfo/" + grade.gradeRow.id), SingleGrade.class);
    }

    public SingleGrade getSingleGrade(Grade grade, Study study) throws IOException {
        return gson.fromJson(HttpUtil.httpGet(magister.schoolUrl.getApiUrl() + "personen/" + magister.profile.id + "/aanmeldingen/" + study.id + "/cijfers/extracijferkolominfo/" + grade.gradeRow.id), SingleGrade.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPrivilege() {
        return "Cijfers";
    }
}
