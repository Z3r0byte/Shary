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

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStreamReader;
import java.security.MessageDigest;

import agency.tango.materialintroscreen.SlideFragment;
import eu.z3r0byteapps.shary.MagisterLibrary.Magister;
import eu.z3r0byteapps.shary.MagisterLibrary.container.School;
import eu.z3r0byteapps.shary.MagisterLibrary.container.User;
import eu.z3r0byteapps.shary.MagisterLibrary.util.HttpUtil;
import eu.z3r0byteapps.shary.MagisterLibrary.util.LogUtil;
import eu.z3r0byteapps.shary.MagisterLibrary.util.SchoolUrl;
import eu.z3r0byteapps.shary.R;
import eu.z3r0byteapps.shary.SharyLibrary.Result;
import eu.z3r0byteapps.shary.SharyLibrary.Urls;
import eu.z3r0byteapps.shary.Util.ConfigUtil;


public class Setup extends SlideFragment {
    private static final String TAG = "Setup";
    Boolean ready = false;
    ProgressBar progressBar;
    Button button;

    ConfigUtil configUtil;

    TextView step1;
    TextView step2;
    TextView step3;
    TextView step4;
    TextView step5;
    TextView step6;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_setup, container, false);

        step1 = view.findViewById(R.id.step1);
        step2 = view.findViewById(R.id.step2);
        step3 = view.findViewById(R.id.step3);
        step4 = view.findViewById(R.id.step4);
        step5 = view.findViewById(R.id.step5);
        step6 = view.findViewById(R.id.step6);

        configUtil = new ConfigUtil(getActivity());

        progressBar = view.findViewById(R.id.loading);

        button = view.findViewById(R.id.finish);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                button.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Magister magister = login();
                        if (magister == null) {
                            failure();
                            return;
                        }
                        complete(step1);
                        String profile = getProfile(magister);
                        if (profile == null) {
                            failure();
                            return;
                        }
                        complete(step2);
                        Integer personID = magister.profile.id;
                        String token = generateToken(profile);
                        complete(step3);
                        Log.d(TAG, String.format("run: Token: %s", token));
                        Boolean success = createUser(token, HttpUtil.getSessionToken(), personID);
                        complete(step4);
                        if (!success) {
                            failure();
                            return;
                        }
                        complete(step5);
                        done(token);
                    }
                }).start();
            }
        });
        return view;
    }

    private Magister login() {
        School school = new Gson().fromJson(configUtil.getString("School"), School.class);
        User user = new Gson().fromJson(configUtil.getString("User"), User.class);
        try {
            return Magister.login(school, user.username, user.password);
        } catch (IOException e) {
            return null;
        }
    }

    private String getProfile(Magister magister) {
        SchoolUrl url = new SchoolUrl(magister.school);
        try {
            return LogUtil.getStringFromInputStream(HttpUtil.httpGet(url.getAccountUrl()));
        } catch (IOException e) {
            return null;
        }
    }

    private String generateToken(String profile) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(profile.getBytes("UTF-8"));
            StringBuffer hexString = new StringBuffer();

            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if (hex.length() == 1)
                    hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private Boolean createUser(String token, String sessionid, Integer personID) {
        School school = new Gson().fromJson(configUtil.getString("School"), School.class);
        String schoolPrefix = school.url.substring(8, school.url.indexOf("."));
        eu.z3r0byteapps.shary.SharyLibrary.User user = new eu.z3r0byteapps.shary.SharyLibrary.User(sessionid, token, personID, schoolPrefix);
        try {
            InputStreamReader inputStreamReader = HttpUtil.httpPost(Urls.createUser, new Gson().toJson(user));
            Result result = new Gson().fromJson(LogUtil.getStringFromInputStream(inputStreamReader), Result.class);
            return result.error == null;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void failure() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int gray = Color.parseColor("#bbbbbb");
                step1.setTextColor(gray);
                step2.setTextColor(gray);
                step3.setTextColor(gray);
                step4.setTextColor(gray);
                step5.setTextColor(gray);
                step6.setTextColor(gray);
                Toast.makeText(getActivity(), R.string.an_error_occured_try_again, Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                button.setVisibility(View.VISIBLE);
            }
        });
    }

    private void done(String token) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
                step6.setTextColor(Color.parseColor("#ffffff"));
            }
        });
        configUtil.setBoolean("loggedIn", true);
        configUtil.setString("token", token);
        ready = true;
    }

    private void complete(final TextView textView) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int white = Color.parseColor("#ffffff");
                textView.setTextColor(white);
            }
        });
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
        return ready;
    }

    @Override
    public String cantMoveFurtherErrorMessage() {
        return getString(R.string.wait_till_done);
    }
}
