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


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;

import java.io.IOException;

import eu.z3r0byteapps.shary.MagisterLibrary.adapter.StudyAdapter;
import eu.z3r0byteapps.shary.MagisterLibrary.container.Study;
import eu.z3r0byteapps.shary.MagisterLibrary.util.GsonUtil;
import eu.z3r0byteapps.shary.MagisterLibrary.util.HttpUtil;
import eu.z3r0byteapps.shary.MagisterLibrary.util.LogUtil;
import eu.z3r0byteapps.shary.R;
import eu.z3r0byteapps.shary.SharyLibrary.Result;
import eu.z3r0byteapps.shary.SharyLibrary.Share;
import eu.z3r0byteapps.shary.SharyLibrary.Urls;
import eu.z3r0byteapps.shary.ViewShareActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class ViewGrades extends Fragment {

    ViewShareActivity activity;
    Share share;

    public ViewGrades() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_grades, container, false);


        activity = (ViewShareActivity) getActivity();
        share = ViewShareActivity.share;

        getStudies();

        return view;
    }


    private void getStudies() {
        final MaterialDialog dialog = new MaterialDialog.Builder(activity)
                .title(R.string.loading)
                .content(R.string.please_wait)
                .progress(true, 0)
                .autoDismiss(false)
                .show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Gson gson = GsonUtil.getGsonWithAdapter(Study[].class, new StudyAdapter());
                    String response = LogUtil.getStringFromInputStream(HttpUtil.httpGet(Urls.getStudies + share.getSecret()));

                    Result result = new Gson().fromJson(response, Result.class);
                    if (result.error != null) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //show error
                            }
                        });
                    }
                    final Study[] studies = gson.fromJson(response, Study[].class);
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            chooseStudy(studies);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                        }
                    });
                }
            }
        }).start();
    }

    private void chooseStudy(Study[] studies) {
        String[] values = new String[studies.length];
        int i = 0;
        for (Study study :
                studies) {
            values[i] = study.description;
            i++;
        }
        new MaterialDialog.Builder(activity)
                .title(R.string.choose_a_grade_period)
                .items(values)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        Toast.makeText(activity, "Thank you, now f*ck off", Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }

}
