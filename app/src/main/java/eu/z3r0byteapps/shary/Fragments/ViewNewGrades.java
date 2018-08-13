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
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.Gson;

import java.io.IOException;

import eu.z3r0byteapps.shary.Adapters.NewGradesAdapter;
import eu.z3r0byteapps.shary.MagisterLibrary.adapter.GradeAdapter;
import eu.z3r0byteapps.shary.MagisterLibrary.container.Grade;
import eu.z3r0byteapps.shary.MagisterLibrary.util.GsonUtil;
import eu.z3r0byteapps.shary.MagisterLibrary.util.HttpUtil;
import eu.z3r0byteapps.shary.MagisterLibrary.util.LogUtil;
import eu.z3r0byteapps.shary.R;
import eu.z3r0byteapps.shary.SharyLibrary.Result;
import eu.z3r0byteapps.shary.SharyLibrary.Share;
import eu.z3r0byteapps.shary.SharyLibrary.Urls;
import eu.z3r0byteapps.shary.Util.ConfigUtil;
import eu.z3r0byteapps.shary.ViewShareActivity;
import tr.xip.errorview.ErrorView;

public class ViewNewGrades extends Fragment {
    private static final String TAG = "NewGradesFragment";

    View view;
    SwipeRefreshLayout mSwipeRefreshLayout;
    ErrorView errorView;
    ListView listView;
    NewGradesAdapter mNewGradesAdapter;
    ConfigUtil configUtil;

    Grade[] Grades;
    Share share;

    ViewShareActivity activity;


    public ViewNewGrades() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_view_new_grades, container, false);

        activity = (ViewShareActivity) getActivity();
        share = ViewShareActivity.share;

        mSwipeRefreshLayout = view.findViewById(R.id.layout_refresh);
        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.colorPrimary,
                R.color.colorAccent);
        mSwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        Log.d(TAG, "onRefresh: Refreshing!");
                        refresh();
                    }
                }
        );
        errorView = view.findViewById(R.id.error_view_new_grades);

        Grades = new Grade[0];

        listView = view.findViewById(R.id.list_new_grades);
        mNewGradesAdapter = new NewGradesAdapter(getActivity(), Grades);
        listView.setAdapter(mNewGradesAdapter);

        configUtil = new ConfigUtil(getActivity());

        refresh();

        return view;
    }

    private void refresh() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Gson gson = GsonUtil.getGsonWithAdapter(Grade[].class, new GradeAdapter());
                    String response = LogUtil.getStringFromInputStream(HttpUtil.httpGet(Urls.getNewGrades + share.getSecret()));

                    final Result result = new Gson().fromJson(response, Result.class);
                    if (result.error != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                listView.setVisibility(View.GONE);
                                errorView.setVisibility(View.VISIBLE);
                                errorView.setTitle("Fout tijdens ophalen data:");
                                errorView.setSubtitle(result.error);
                                errorView.setRetryVisible(false);
                                mSwipeRefreshLayout.setRefreshing(false);
                            }
                        });
                        return;
                    }
                    Grades = gson.fromJson(response, Grade[].class);
                } catch (IOException e) {
                    Grades = null;
                    Log.e(TAG, "run: No connection...", e);
                }
                if (Grades != null && Grades.length != 0) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mNewGradesAdapter = new NewGradesAdapter(getActivity(), Grades);
                            listView.setAdapter(mNewGradesAdapter);
                            listView.setVisibility(View.VISIBLE);
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    //Intent intent = new Intent(getActivity(), GradeDetailsActivity.class);
                                    //intent.putExtra("Grade", new Gson().toJson(Grades[i]));
                                    //startActivity(intent);
                                }
                            });
                            errorView.setVisibility(View.GONE);
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                    });
                } else if (Grades != null && Grades.length < 1) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            listView.setVisibility(View.GONE);
                            errorView.setVisibility(View.VISIBLE);
                            errorView.setTitle(getString(R.string.no_new_grades));
                            errorView.setSubtitle(null);
                            errorView.setRetryVisible(false);
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                    });
                } else {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            listView.setVisibility(View.GONE);
                            errorView.setVisibility(View.VISIBLE);
                            errorView.setTitle(getString(R.string.err_no_connection));
                            errorView.setSubtitle(null);
                            errorView.setRetryVisible(false);
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                    });
                }
            }
        }).start();
    }

}
