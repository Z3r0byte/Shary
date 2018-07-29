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


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.text.ParseException;

import agency.tango.materialintroscreen.SlideFragment;
import eu.z3r0byteapps.shary.MagisterLibrary.Magister;
import eu.z3r0byteapps.shary.MagisterLibrary.container.Profile;
import eu.z3r0byteapps.shary.MagisterLibrary.container.School;
import eu.z3r0byteapps.shary.MagisterLibrary.container.User;
import eu.z3r0byteapps.shary.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class Login extends SlideFragment {
    private static final String TAG = "Login";
    Boolean mAllowForward = false;
    Boolean mSuccessfulLogin = false;
    Boolean mLoginError = false;

    EditText mUserNameEditText;
    EditText mPasswordEditText;
    Button mLogin;

    String mUrl;
    String mCookie;

    Profile mProfile = new Profile();
    School mSchool;
    User mUser;

    View view;

    Context c;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_login, container, false);

        c = getActivity();


        mUserNameEditText = view.findViewById(R.id.username);
        mPasswordEditText = view.findViewById(R.id.password);
        mLogin = view.findViewById(R.id.login);

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mUrl == null) {
                    String url = c.getSharedPreferences("data", Context.MODE_PRIVATE).getString("School", null);
                    if (url == null) {
                        throw new IllegalArgumentException("No school is saved!");
                    }
                    mSchool = new Gson().fromJson(url, School.class);
                    mUrl = mSchool.url;
                }

                login(mUserNameEditText.getText().toString().trim(), mPasswordEditText.getText().toString().trim());
            }
        });

        return view;
    }

    private void ResetButton() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mSuccessfulLogin) {
                    mLogin.setText(R.string.logged_in);
                } else {
                    mLogin.setEnabled(true);
                    mUserNameEditText.setEnabled(true);
                    mPasswordEditText.setEnabled(true);
                    mLogin.setText(R.string.login);
                }
            }
        });
    }

    private void login(final String UserName, final String Password) {
        mLoginError = false;
        mLogin.setEnabled(false);
        mUserNameEditText.setEnabled(false);
        mPasswordEditText.setEnabled(false);
        mLogin.setText(R.string.logging_in);

        if (UserName == null || UserName.length() == 0 || UserName == "") {
            Log.e(TAG, "login: Username is not filled in");
            ResetButton();
        }

        if (Password == null || Password.length() == 0 || Password == "") {
            Log.e(TAG, "login: Password is not filled in");
            ResetButton();
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                Magister magister = null;
                try {
                    magister = Magister.login(mSchool, UserName, Password);
                } catch (final IOException e) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(c, R.string.err_no_connection, Toast.LENGTH_SHORT).show();
                        }
                    });
                    ResetButton();
                    return;
                } catch (final InvalidParameterException e) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(c, R.string.invalid_credentials, Toast.LENGTH_SHORT).show();
                        }
                    });
                    ResetButton();
                    e.printStackTrace();
                    return;
                } catch (ParseException | IllegalArgumentException e) {
                    e.printStackTrace();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(c, R.string.unknown_error, Toast.LENGTH_SHORT).show();
                        }
                    });
                    ResetButton();
                }


                if (magister != null) {
                    mProfile = magister.profile;
                    if (mProfile.nickname != null && mProfile.nickname != "null") {
                        mUser = new User(UserName, Password, true);
                        String Account = new Gson().toJson(mProfile, Profile.class);
                        String User = new Gson().toJson(mUser, User.class);

                        SharedPreferences.Editor editor = c.getSharedPreferences("data", Context.MODE_PRIVATE).edit();
                        editor.putString("Profile", Account);
                        editor.putString("User", User);
                        editor.apply();
                    } else {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(c, R.string.unknown_error, Toast.LENGTH_SHORT).show();
                            }
                        });
                        ResetButton();
                        return;
                    }
                } else {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(c, R.string.invalid_credentials, Toast.LENGTH_SHORT).show();
                        }
                    });
                    ResetButton();
                }
                if (magister != null) {
                    try {
                        magister.logout();
                    } catch (IOException e) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(c, R.string.err_no_connection, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    mAllowForward = true;
                    mSuccessfulLogin = true;
                    ResetButton();
                } else {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(c, R.string.unknown_error, Toast.LENGTH_SHORT).show();
                        }
                    });
                    ResetButton();
                }
            }
        }).start();
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
        return getString(R.string.login_first);
    }
}
