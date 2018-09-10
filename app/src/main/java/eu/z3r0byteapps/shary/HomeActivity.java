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

package eu.z3r0byteapps.shary;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.google.gson.Gson;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.io.IOException;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.util.Date;

import eu.z3r0byteapps.shary.MagisterLibrary.Magister;
import eu.z3r0byteapps.shary.MagisterLibrary.container.School;
import eu.z3r0byteapps.shary.MagisterLibrary.container.User;
import eu.z3r0byteapps.shary.MagisterLibrary.util.HttpUtil;
import eu.z3r0byteapps.shary.MagisterLibrary.util.LogUtil;
import eu.z3r0byteapps.shary.MagisterLibrary.util.SchoolUrl;
import eu.z3r0byteapps.shary.SharyLibrary.Result;
import eu.z3r0byteapps.shary.SharyLibrary.Urls;
import eu.z3r0byteapps.shary.Util.ConfigUtil;
import eu.z3r0byteapps.shary.Util.DateUtils;
import eu.z3r0byteapps.shary.Util.JobUtil;

public class HomeActivity extends AppCompatActivity {
    ConfigUtil configUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);

        final PrimaryDrawerItem homeItem = new PrimaryDrawerItem().withIdentifier(1).withName(R.string.home).withSetSelected(true);
        final PrimaryDrawerItem shareItem = new PrimaryDrawerItem().withIdentifier(2).withName(R.string.share);
        final PrimaryDrawerItem sharedItem = new PrimaryDrawerItem().withIdentifier(3).withName(R.string.shared_with_me);

        Drawer drawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .addDrawerItems(
                        homeItem,
                        shareItem,
                        sharedItem
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (drawerItem == homeItem) {
                            //already here
                        } else if (drawerItem == shareItem) {
                            startActivity(new Intent(getApplicationContext(), ShareActivity.class));
                            finish();
                        } else if (drawerItem == sharedItem) {
                            startActivity(new Intent(getApplicationContext(), SharedActivity.class));
                            finish();
                        }
                        return true;
                    }
                })
                .build();

        JobUtil.cancelAllJobs(this);
        JobUtil.scheduleJob(this);

        configUtil = new ConfigUtil(this);
        if (configUtil.getBoolean("loggedIn", false)) updateSession();
    }

    private void updateSession() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Magister magister = login();
                if (magister == null) {
                    return;
                }
                String profile = getProfile(magister);
                if (profile == null) {
                    return;
                }
                Integer personID = magister.profile.id;
                String token = generateToken(profile);
                Boolean success = createUser(token, HttpUtil.getSessionToken(), personID);
                if (success)
                    configUtil.setString("lastSessionUpdate", DateUtils.formatDate(new Date(), "dd-MM-yyyy"));
            }
        }).start();
    }

    private Boolean createUser(String token, String sessionid, Integer personID) {
        School school = new Gson().fromJson(configUtil.getString("School"), School.class);
        String schoolPrefix = school.url.substring(8, school.url.indexOf("."));
        eu.z3r0byteapps.shary.SharyLibrary.User user = new eu.z3r0byteapps.shary.SharyLibrary.User(sessionid, token, personID, schoolPrefix);
        try {
            InputStreamReader inputStreamReader = HttpUtil.httpPost(Urls.createUser, new Gson().toJson(user));
            Result result = new Gson().fromJson(LogUtil.getStringFromInputStream(inputStreamReader), Result.class);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
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
}
