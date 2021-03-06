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

package eu.z3r0byteapps.shary.Fragments;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;

import agency.tango.materialintroscreen.SlideFragment;
import eu.z3r0byteapps.shary.Adapters.SchoolAdapter;
import eu.z3r0byteapps.shary.MagisterLibrary.container.School;
import eu.z3r0byteapps.shary.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SelectSchool extends SlideFragment {
    private static final String TAG = "SelectSchool";

    Boolean mAllowForward = false;
    EditText mEditTextSchool;
    Button mButtonSearch;
    ListView mListSchool;
    View view;

    SchoolAdapter mAdapter;

    School[] mSchools;
    String[] mSchoolNames;


    Thread mSearchThread;
    Context c;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_select_school, container, false);

        c = getActivity();

        mEditTextSchool = view.findViewById(R.id.edit_text_search_school);
        mButtonSearch = view.findViewById(R.id.button_search_school);
        mListSchool = view.findViewById(R.id.list_schools);

        mButtonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SearchSchool(true);
            }
        });


        mSchools = new School[1];
        mSchools[0] = new School();
        mSchoolNames = new String[1];
        mSchoolNames[0] = getString(R.string.no_results);

        mEditTextSchool.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                Log.d(TAG, "afterTextChanged: Text Changed!");
                if (mEditTextSchool.getText().length() > 3 && !mEditTextSchool.getText().toString().endsWith(" ")) {
                    SearchSchool(false);
                } else {
                    Log.e(TAG, "afterTextChanged: String not long enough!");
                }
            }
        });


        return view;
    }

    private void SearchSchool(final Boolean ActionCameFromButton) {
        final String school = mEditTextSchool.getText().toString();
        if (ActionCameFromButton) {
            mEditTextSchool.setEnabled(false);
            mButtonSearch.setEnabled(false);
            mButtonSearch.setText(R.string.searching);
        }
        mSearchThread = new Thread(new Runnable() {
            @Override
            public void run() {
                mSchools = School.findSchool(school.replace(" ", "%20"));
                Log.d(TAG, "run: Er zijn " + mSchools.length + " resultaten gevonden.");

                if (mSchools.length == 0) {
                    mSchools = new School[1];
                    School mNoResults = new School();
                    mNoResults.name = getString(R.string.no_results);
                    mSchools[0] = mNoResults;
                    Log.d(TAG, "run: No results");
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (ActionCameFromButton) {
                            mEditTextSchool.setEnabled(true);
                            mButtonSearch.setEnabled(true);
                            mButtonSearch.setText(R.string.search);
                        }
                        mAdapter = new SchoolAdapter(getActivity(), mSchools);
                        mListSchool.setAdapter(mAdapter);

                        mListSchool.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                                if (mSchools[position] != null && !mSchools[position].name.equals(getResources().getString(R.string.no_results))) {
                                    Log.d(TAG, "onItemClick: Url: " + mSchools[position].url);
                                    String school = new Gson().toJson(mSchools[position]);
                                    c.getSharedPreferences("data", Context.MODE_PRIVATE).edit().
                                            putString("School", school).apply();
                                    mAllowForward = true;
                                    Toast.makeText(c, getString(R.string.school_selected) + ' ' + mSchools[position].name
                                            , Toast.LENGTH_SHORT).show();
                                } else if (mSchools[position].name.equals(getResources().getString(R.string.no_results))) {
                                    manualInput();
                                }
                            }
                        });
                    }
                });

            }
        });
        mSearchThread.start();
    }

    private void manualInput() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.manual_school_title);
        builder.setMessage(R.string.manual_school_desc);

        final EditText input = new EditText(getActivity());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                confirmSchool(input.getText().toString());
            }
        });
        builder.setNegativeButton("Annuleer", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void confirmSchool(final String URL) {
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle(R.string.confirm_url_title);
        alertDialogBuilder.setMessage(String.format(getString(R.string.confirm_url_desc), URL, URL));
        alertDialogBuilder.setPositiveButton("Dit klopt", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                School manualschool = new School();
                manualschool.name = "Handmatig ingevoerd";
                manualschool.url = "https://" + URL + ".magister.net";
                manualschool.id = "999";
                String school = new Gson().toJson(manualschool);
                c.getSharedPreferences("data", Context.MODE_PRIVATE).edit().
                        putString("School", school).apply();
                mAllowForward = true;
                Toast.makeText(c, getResources().getString(R.string.school_selected) + ' ' + manualschool.name
                        , Toast.LENGTH_SHORT).show();
            }
        });
        alertDialogBuilder.setNegativeButton("Ik heb me vergist", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                manualInput();
            }
        });
        android.app.AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public int backgroundColor() {
        return R.color.primary;
    }

    @Override
    public int buttonsColor() {
        return R.color.primary_dark;
    }

    @Override
    public boolean canMoveFurther() {
        return mAllowForward;
    }

    @Override
    public String cantMoveFurtherErrorMessage() {
        return getString(R.string.choose_school_first);
    }
}
