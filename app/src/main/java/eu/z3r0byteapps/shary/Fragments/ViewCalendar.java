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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import eu.z3r0byteapps.shary.Adapters.AppointmentsAdapter;
import eu.z3r0byteapps.shary.MagisterLibrary.adapter.AppointmentAdapter;
import eu.z3r0byteapps.shary.MagisterLibrary.container.Appointment;
import eu.z3r0byteapps.shary.MagisterLibrary.util.GsonUtil;
import eu.z3r0byteapps.shary.MagisterLibrary.util.HttpUtil;
import eu.z3r0byteapps.shary.MagisterLibrary.util.LogUtil;
import eu.z3r0byteapps.shary.R;
import eu.z3r0byteapps.shary.SharyLibrary.Result;
import eu.z3r0byteapps.shary.SharyLibrary.Share;
import eu.z3r0byteapps.shary.SharyLibrary.ShareType;
import eu.z3r0byteapps.shary.SharyLibrary.Urls;
import eu.z3r0byteapps.shary.Util.ConfigUtil;
import eu.z3r0byteapps.shary.Util.DateUtils;
import eu.z3r0byteapps.shary.ViewShareActivity;
import tr.xip.errorview.ErrorView;

public class ViewCalendar extends Fragment {
    private static final String TAG = "ViewCalendar";


    public ViewCalendar() {
        // Required empty public constructor
    }

    View view;
    SwipeRefreshLayout mSwipeRefreshLayout;
    ErrorView errorView;
    ListView listView;
    TextView currentDay;
    ImageView nextDay;
    ImageView previousDay;
    AppointmentsAdapter mAppointmentsAdapter;

    Appointment[] Appointments;
    Date selectedDate;

    Share share;
    ViewShareActivity activity;

    Thread refreshThread;
    ConfigUtil configUtil;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_view_calendar, container, false);

        activity = (ViewShareActivity) getActivity();
        share = ViewShareActivity.share;

        if (share == null) {
            Toast.makeText(activity, "GTFO", Toast.LENGTH_SHORT).show();
            return view;
        }

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
        errorView = view.findViewById(R.id.error_view_appointments);

        Appointments = new Appointment[0];

        listView = view.findViewById(R.id.list_appointments);
        mAppointmentsAdapter = new AppointmentsAdapter(getActivity(), Appointments);
        listView.setAdapter(mAppointmentsAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Intent intent = new Intent(getActivity(), AppointmentDetailsActivity.class);
                //intent.putExtra("Appointment", new Gson().toJson(Appointments[i]));
                //startActivity(intent);
            }
        });

        currentDay = view.findViewById(R.id.current_day);
        nextDay = view.findViewById(R.id.nextDay);
        previousDay = view.findViewById(R.id.previousDay);

        IconicsDrawable previousDayIcon = new IconicsDrawable(getActivity(), GoogleMaterial.Icon.gmd_navigate_before)
                .color(getResources().getColor(R.color.colorPrimary))
                .sizeDp(25);
        IconicsDrawable nextDayIcon = new IconicsDrawable(getActivity(), GoogleMaterial.Icon.gmd_navigate_next)
                .color(getResources().getColor(R.color.colorPrimary))
                .sizeDp(25);
        nextDay.setImageDrawable(nextDayIcon);
        previousDay.setImageDrawable(previousDayIcon);

        nextDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedDate = DateUtils.addDays(selectedDate, 1);
                refresh();
            }
        });
        previousDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedDate = DateUtils.addDays(selectedDate, -1);
                refresh();
            }
        });

        selectedDate = DateUtils.getToday();
        configUtil = new ConfigUtil(getActivity());

        if ((share.getType() == ShareType.CALENDARANDGRADES || share.getType() == ShareType.CALENDARANDNEWGRADES
                || share.getType() == ShareType.CALENDAR || share.getType() == ShareType.EVERYTHING)) {
            refresh();
        } else {
            listView.setVisibility(View.GONE);
            errorView.setVisibility(View.VISIBLE);
            errorView.setTitle("Geen toegang tot de kalender");
            errorView.setSubtitle("Vraag de eigenaar van deze share om de kalender toe te voegen");
            errorView.setRetryVisible(false);
            mSwipeRefreshLayout.setRefreshing(false);
        }

        return view;
    }

    public void refresh() {
        if (refreshThread != null) {
            refreshThread.interrupt();
        }
        mSwipeRefreshLayout.setRefreshing(true);
        currentDay.setText(DateUtils.formatDate(selectedDate, "EEE dd MMM"));
        refreshThread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (Thread.interrupted()) {
                    return;
                }

                try {
                    Gson gson = GsonUtil.getGsonWithAdapter(Appointment[].class, new AppointmentAdapter());
                    DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                    String dateNow = format.format(selectedDate);
                    String response = LogUtil.getStringFromInputStream(HttpUtil.httpGet(Urls.getCalendar + dateNow + "&s=" + share.getSecret()));

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
                    Appointments = gson.fromJson(response, Appointment[].class);
                } catch (InterruptedIOException e) {
                    e.printStackTrace();
                    return;
                } catch (IOException e) {
                    Appointments = null;
                    Log.e(TAG, "run: No connection...", e);
                }

                if (Thread.interrupted() || getActivity() == null) {
                    return;
                }

                if (Appointments != null && Appointments.length != 0) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mAppointmentsAdapter = new AppointmentsAdapter(getActivity(), Appointments);
                            listView.setAdapter(mAppointmentsAdapter);
                            listView.setVisibility(View.VISIBLE);
                            errorView.setVisibility(View.GONE);
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                    });
                } else if (Appointments != null && Appointments.length < 1) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            listView.setVisibility(View.GONE);
                            errorView.setVisibility(View.VISIBLE);
                            errorView.setTitle(getString(R.string.no_lessons));
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
        });
        refreshThread.start();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    /*@Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_appointments, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_today) {
            selectedDate = new Date();
            refresh();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }*/


}
