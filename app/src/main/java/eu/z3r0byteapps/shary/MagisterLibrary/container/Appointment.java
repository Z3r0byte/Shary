/*
 * Copyright (c) 2016-2017 Bas van den Boom 'Z3r0byte'
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

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

import eu.z3r0byteapps.shary.MagisterLibrary.container.sub.Classroom;
import eu.z3r0byteapps.shary.MagisterLibrary.container.sub.Group;
import eu.z3r0byteapps.shary.MagisterLibrary.container.sub.Link;
import eu.z3r0byteapps.shary.MagisterLibrary.container.sub.SubSubject;
import eu.z3r0byteapps.shary.MagisterLibrary.container.sub.Teacher;
import eu.z3r0byteapps.shary.MagisterLibrary.container.type.AppointmentType;
import eu.z3r0byteapps.shary.MagisterLibrary.container.type.DisplayType;
import eu.z3r0byteapps.shary.MagisterLibrary.container.type.InfoType;

public class Appointment implements Serializable {
    @SerializedName("Id")
    public int id;

    @SerializedName("Links")
    public Link[] links;

    @SerializedName("Start")
    public String startDateString;

    public Date startDate;

    @SerializedName("Einde")
    public String endDateString;

    public Date endDate;

    @SerializedName("LesuurVan")
    public int periodFrom;

    @SerializedName("LesuurTotMet")
    public int periodUpToAndIncluding;

    @SerializedName("DuurtHeleDag")
    public boolean takesAllDay;

    @SerializedName("Lokatie")
    public String location;

    @SerializedName("Status")
    public int classState;

    public AppointmentType type;

    public DisplayType displayType;

    public InfoType infoType;

    @SerializedName("Afgerond")
    public boolean finished;

    @SerializedName("Vakken")
    public SubSubject[] subjects;

    @SerializedName("Lokalen")
    public Classroom[] classrooms;

    @SerializedName("Docenten")
    public Teacher[] teachers;

    @SerializedName("Groep")
    public Group group;

    @SerializedName("Inhoud")
    public String content;

    @SerializedName("Omschrijving")
    public String description;

    @SerializedName("TaakGewijzigdOp")
    public String homeworkLastEdited;

    @Override
    public String toString() {
        return "Appointment{" +
                "id=" + id +
                ", startDateString='" + startDateString + '\'' +
                ", endDateString='" + endDateString + '\'' +
                ", periodFrom=" + periodFrom +
                ", periodUpToAndIncluding=" + periodUpToAndIncluding +
                ", takesAllDay=" + takesAllDay +
                ", location='" + location + '\'' +
                ", classState=" + classState +
                ", finished=" + finished +
                ", content='" + content + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
