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

package eu.z3r0byteapps.shary.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import eu.z3r0byteapps.shary.MagisterLibrary.container.Grade;
import eu.z3r0byteapps.shary.R;
import eu.z3r0byteapps.shary.Util.DateUtils;

/**
 * Created by bas on 6-7-16.
 */
public class NewGradesAdapter extends ArrayAdapter<Grade> {
    private static final String TAG = "ScheduleChangeAdapter";

    private final Context context;
    private final Grade[] grades;

    public NewGradesAdapter(Context context, Grade[] grades) {
        super(context, -1, grades);
        this.context = context;
        this.grades = grades;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_new_grades, parent, false);

        IconicsDrawable emptyStar = new IconicsDrawable(context, GoogleMaterial.Icon.gmd_star).color(Color.LTGRAY).sizeDp(24);
        IconicsDrawable fullStar = new IconicsDrawable(context, GoogleMaterial.Icon.gmd_star)
                .color(context.getResources().getColor(R.color.accent)).sizeDp(24);


        TextView subject = rowView.findViewById(R.id.list_text_subject);
        if (grades[position].description != null) {
            subject.setText(grades[position].subject.name + " - " + grades[position].description);
        } else {
            subject.setText(grades[position].subject.name);
        }
        TextView date = rowView.findViewById(R.id.list_text_date);
        date.setText(DateUtils.formatDate(grades[position].filledInDate, "dd-MM-yyyy"));

        TextView grade = rowView.findViewById(R.id.list_text_grade);
        grade.setText(grades[position].grade);
        try {
            if (Double.parseDouble(grades[position].grade.replace(",", ".")) < 5.5) {
                grade.setTextColor(Color.RED);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        TextView wage = rowView.findViewById(R.id.wage);
        if (grades[position].wage > 0) {
            if (Math.floor(grades[position].wage) == grades[position].wage) {
                wage.setText("x " + grades[position].wage.intValue());
            } else {
                wage.setText("x " + grades[position].wage.toString());
            }
        } else {
            wage.setText(null);
        }


        return rowView;
    }
}
