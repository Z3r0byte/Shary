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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;

import java.io.IOException;

import eu.z3r0byteapps.shary.Adapters.GradesAdapter;
import eu.z3r0byteapps.shary.MagisterLibrary.adapter.GradeAdapter;
import eu.z3r0byteapps.shary.MagisterLibrary.adapter.StudyAdapter;
import eu.z3r0byteapps.shary.MagisterLibrary.container.Grade;
import eu.z3r0byteapps.shary.MagisterLibrary.container.Study;
import eu.z3r0byteapps.shary.MagisterLibrary.util.GsonUtil;
import eu.z3r0byteapps.shary.MagisterLibrary.util.HttpUtil;
import eu.z3r0byteapps.shary.MagisterLibrary.util.LogUtil;
import eu.z3r0byteapps.shary.R;
import eu.z3r0byteapps.shary.SharyLibrary.Result;
import eu.z3r0byteapps.shary.SharyLibrary.Share;
import eu.z3r0byteapps.shary.SharyLibrary.Urls;
import eu.z3r0byteapps.shary.Util.DateUtils;
import eu.z3r0byteapps.shary.ViewShareActivity;
import tr.xip.errorview.ErrorView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ViewGrades extends Fragment {

    ViewShareActivity activity;
    Share share;

    ErrorView errorView;
    ListView listView;
    GradesAdapter gradesAdapter;

    public ViewGrades() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_grades, container, false);


        activity = (ViewShareActivity) getActivity();
        share = ViewShareActivity.share;
        errorView = view.findViewById(R.id.error_view_grades);
        listView = view.findViewById(R.id.list_grades);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getActivity(), R.string.subjects_coming_soon, Toast.LENGTH_SHORT).show();
            }
        });

        getStudies();

        setHasOptionsMenu(true);
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

                    final Result result = new Gson().fromJson(response, Result.class);
                    if (result.error != null) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        listView.setVisibility(View.GONE);
                                        errorView.setVisibility(View.VISIBLE);
                                        errorView.setTitle("Fout tijdens ophalen data:");
                                        errorView.setSubtitle(result.error);
                                        errorView.setRetryText(R.string.retry);
                                        errorView.setRetryVisible(true);
                                        errorView.setRetryListener(new ErrorView.RetryListener() {
                                            @Override
                                            public void onRetry() {
                                                getStudies();
                                            }
                                        });
                                    }
                                });
                            }
                        });
                        return;
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
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    listView.setVisibility(View.GONE);
                                    errorView.setVisibility(View.VISIBLE);
                                    errorView.setTitle("Fout tijdens ophalen data:");
                                    errorView.setSubtitle(getString(R.string.err_no_connection));
                                    errorView.setRetryText(R.string.retry);
                                    errorView.setRetryVisible(true);
                                    errorView.setRetryListener(new ErrorView.RetryListener() {
                                        @Override
                                        public void onRetry() {
                                            getStudies();
                                        }
                                    });
                                }
                            });
                        }
                    });
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

    private void chooseStudy(final Study[] studies) {
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
                        getGrades(studies[which]);
                    }
                })
                .show();
    }

    private void getGrades(final Study study) {
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
                    Gson gson = GsonUtil.getGsonWithAdapter(Grade[].class, new GradeAdapter());
                    String response = LogUtil.getStringFromInputStream(HttpUtil.httpGet(String.format(Urls.getGrades, share.getSecret(), study.id, DateUtils.formatDate(study.endDate, "yyyy-MM-dd"))));

                    final Result result = new Gson().fromJson(response, Result.class);
                    if (result.error != null) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        listView.setVisibility(View.GONE);
                                        errorView.setVisibility(View.VISIBLE);
                                        errorView.setTitle("Fout tijdens ophalen data:");
                                        errorView.setSubtitle(result.error);
                                        errorView.setRetryText(R.string.retry);
                                        errorView.setRetryVisible(true);
                                        errorView.setRetryListener(new ErrorView.RetryListener() {
                                            @Override
                                            public void onRetry() {
                                                getStudies();
                                            }
                                        });
                                    }
                                });
                            }
                        });
                        return;
                    }
                    final Grade[] grades = gson.fromJson(response, Grade[].class);
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            gradesAdapter = new GradesAdapter(getActivity(), grades, true);
                            listView.setVisibility(View.VISIBLE);
                            errorView.setVisibility(View.GONE);
                            listView.setAdapter(gradesAdapter);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    listView.setVisibility(View.GONE);
                                    errorView.setVisibility(View.VISIBLE);
                                    errorView.setTitle("Fout tijdens ophalen data:");
                                    errorView.setSubtitle(getString(R.string.err_no_connection));
                                    errorView.setRetryText(R.string.retry);
                                    errorView.setRetryVisible(true);
                                    errorView.setRetryListener(new ErrorView.RetryListener() {
                                        @Override
                                        public void onRetry() {
                                            getStudies();
                                        }
                                    });
                                }
                            });
                        }
                    });
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.grades, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.choose_study) {
            getStudies();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
