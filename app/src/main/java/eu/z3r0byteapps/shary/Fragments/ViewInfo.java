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

package eu.z3r0byteapps.shary.Fragments;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import eu.z3r0byteapps.shary.MagisterLibrary.util.HttpUtil;
import eu.z3r0byteapps.shary.MagisterLibrary.util.LogUtil;
import eu.z3r0byteapps.shary.R;
import eu.z3r0byteapps.shary.SharyLibrary.Share;
import eu.z3r0byteapps.shary.SharyLibrary.ShareType;
import eu.z3r0byteapps.shary.SharyLibrary.Urls;
import eu.z3r0byteapps.shary.Util.ShareDatabase;
import eu.z3r0byteapps.shary.ViewShareActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class ViewInfo extends Fragment {

    ViewShareActivity activity;


    public ViewInfo() {
        // Required empty public constructor
    }

    TextView comment;
    TextView expiry;
    CheckBox calendar;
    CheckBox newGrades;
    CheckBox grades;

    ProgressBar loading;

    Share share;
    ShareDatabase shareDatabase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_info, container, false);

        activity = (ViewShareActivity) getActivity();
        share = ViewShareActivity.share;

        if (share == null) {
            Toast.makeText(activity, "GTFO", Toast.LENGTH_SHORT).show();
            return view;
        }

        shareDatabase = new ShareDatabase(activity);


        comment = view.findViewById(R.id.comment);
        expiry = view.findViewById(R.id.expiry);
        calendar = view.findViewById(R.id.calendar);
        newGrades = view.findViewById(R.id.newgrades);
        grades = view.findViewById(R.id.grades);
        loading = view.findViewById(R.id.loading);

        updateView();
        loading.setVisibility(View.VISIBLE);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    GsonBuilder gsonBuilder = new GsonBuilder();
                    gsonBuilder.setDateFormat("yyyy-MM-dd HH:mm:ss");
                    String result = LogUtil.getStringFromInputStream(HttpUtil.httpGet(Urls.getShare + share.getSecret()));
                    Share tempshare = gsonBuilder.create().fromJson(result, Share[].class)[0];
                    share.setExpire(tempshare.getExpire());
                    share.setType(tempshare.getType());
                    share.setRestrictions(tempshare.getRestrictions());
                    ViewShareActivity.share = share;
                    shareDatabase.updateShare(share);

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateView();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loading.setVisibility(View.GONE);
                        }
                    });
                }
            }
        }).start();


        return view;
    }

    private void updateView() {
        if (share.getExpire().before(new Date())) {
            comment.setText(String.format("%s (verlopen)", share.getComment()));
        } else {
            comment.setText(share.getComment());
        }

        if (formatDate(share.getExpire()).equals("01-01-2050")) {
            expiry.setText(String.format(activity.getString(R.string.expires_format), "nooit"));
        } else {
            expiry.setText(String.format(activity.getString(R.string.expires_format), formatDate(share.getExpire())));
        }

        Boolean cal = (share.getType() == ShareType.CALENDARANDGRADES || share.getType() == ShareType.CALENDARANDNEWGRADES
                || share.getType() == ShareType.CALENDAR || share.getType() == ShareType.EVERYTHING);
        calendar.setChecked(cal);
        Boolean newGrad = (share.getType() == ShareType.NEWGARDESANDGRADES || share.getType() == ShareType.CALENDARANDNEWGRADES
                || share.getType() == ShareType.NEWGRADES || share.getType() == ShareType.EVERYTHING);
        newGrades.setChecked(newGrad);
        Boolean grad = (share.getType() == ShareType.NEWGARDESANDGRADES || share.getType() == ShareType.CALENDARANDGRADES
                || share.getType() == ShareType.GRADES || share.getType() == ShareType.EVERYTHING);
        grades.setChecked(grad);

        activity.updateBottomBar(cal, newGrad, grad);

        loading.setVisibility(View.GONE);
    }

    @SuppressLint("SimpleDateFormat")
    private static String formatDate(Date date) {
        return new SimpleDateFormat("dd-MM-yyyy").format(date);
    }

}
