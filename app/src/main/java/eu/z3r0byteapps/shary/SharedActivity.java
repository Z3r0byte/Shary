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
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.LibsBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.io.IOException;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;

import eu.z3r0byteapps.shary.Adapters.SharedAdapter;
import eu.z3r0byteapps.shary.MagisterLibrary.Magister;
import eu.z3r0byteapps.shary.MagisterLibrary.container.School;
import eu.z3r0byteapps.shary.MagisterLibrary.container.User;
import eu.z3r0byteapps.shary.MagisterLibrary.util.HttpUtil;
import eu.z3r0byteapps.shary.MagisterLibrary.util.LogUtil;
import eu.z3r0byteapps.shary.MagisterLibrary.util.SchoolUrl;
import eu.z3r0byteapps.shary.SharyLibrary.Result;
import eu.z3r0byteapps.shary.SharyLibrary.Share;
import eu.z3r0byteapps.shary.SharyLibrary.Urls;
import eu.z3r0byteapps.shary.Util.ConfigUtil;
import eu.z3r0byteapps.shary.Util.ShareDatabase;
import tr.xip.errorview.ErrorView;

public class SharedActivity extends AppCompatActivity {
    private static final String TAG = "SharedActivity";

    ShareDatabase shareDatabase;

    ListView listView;
    ErrorView errorView;
    ProgressBar loading;
    ConfigUtil configUtil;

    String secret;

    ConstraintLayout nothing_shared_layout;
    ConstraintLayout loading_layout;
    ConstraintLayout shares_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shared);


        errorView = findViewById(R.id.error);
        loading = findViewById(R.id.progress);
        listView = findViewById(R.id.list);

        loading_layout = findViewById(R.id.loading);
        shares_layout = findViewById(R.id.shares_list);
        nothing_shared_layout = findViewById(R.id.nothing_shared);

        findViewById(R.id.add_share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addManualShare();
            }
        });
        findViewById(R.id.add_share2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addManualShare();
            }
        });

        Intent intent = getIntent();
        final Uri data = intent.getData();
        if (data != null) {
            new MaterialDialog.Builder(this)
                    .title("Share toevoegen")
                    .content("Voer hieronder een beschrijving van de share in zodat je weet welke share dit is")
                    .inputType(InputType.TYPE_CLASS_TEXT)
                    .input("Cijfers van Bart", null, new MaterialDialog.InputCallback() {
                        @Override
                        public void onInput(MaterialDialog dialog, CharSequence input) {
                            if (input.toString().isEmpty()) input = "Naamloos";
                            Log.d(TAG, "onInput: " + data.getLastPathSegment());
                            addShare(data.getLastPathSegment(), input.toString());
                        }
                    }).show();
        }

        configUtil = new ConfigUtil(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.shary_shared_with_me);
        setSupportActionBar(toolbar);

        final PrimaryDrawerItem homeItem = new PrimaryDrawerItem().withIdentifier(1).withName(R.string.home);
        final PrimaryDrawerItem shareItem = new PrimaryDrawerItem().withIdentifier(2).withName(R.string.share).withSetSelected(true);
        final PrimaryDrawerItem sharedItem = new PrimaryDrawerItem().withIdentifier(3).withName(R.string.shared_with_me);
        final PrimaryDrawerItem aboutItem = new PrimaryDrawerItem().withIdentifier(4).withName(R.string.about_title).withSelectable(false);
        final PrimaryDrawerItem logoutItem = new PrimaryDrawerItem().withIdentifier(4).withName(R.string.logout_session).withSelectable(false);
        final PrimaryDrawerItem donateItem = new PrimaryDrawerItem().withIdentifier(5).withName(R.string.donate).withSelectable(false);
        final PrimaryDrawerItem websiteItem = new PrimaryDrawerItem().withIdentifier(6).withName(R.string.website).withSelectable(false);
        final PrimaryDrawerItem responsibleDisclosureItem = new PrimaryDrawerItem().withIdentifier(7).withName(R.string.responsible_disclosure_short).withSelectable(false);
        final PrimaryDrawerItem privacyItem = new PrimaryDrawerItem().withIdentifier(8).withName(R.string.privacypolicy).withSelectable(false);

        Drawer drawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .addDrawerItems(
                        homeItem,
                        shareItem,
                        sharedItem,
                        new DividerDrawerItem(),
                        aboutItem,
                        logoutItem,
                        new SectionDrawerItem().withName(R.string.additional_info),
                        websiteItem,
                        responsibleDisclosureItem,
                        privacyItem
                )
                .withSelectedItem(3)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (drawerItem == homeItem) {
                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                            finish();
                        } else if (drawerItem == shareItem) {
                            startActivity(new Intent(getApplicationContext(), ShareActivity.class));
                            finish();
                        } else if (drawerItem == sharedItem) {
                            //already here
                        } else if (drawerItem == aboutItem) {
                            new LibsBuilder()
                                    .withActivityStyle(Libs.ActivityStyle.LIGHT_DARK_TOOLBAR)
                                    .start(SharedActivity.this);
                        } else if (drawerItem == logoutItem) {
                            destroySession();
                        } else if (drawerItem == websiteItem) {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://shary.z3r0byteapps.eu/"));
                            startActivity(browserIntent);
                        } else if (drawerItem == privacyItem) {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://shary.z3r0byteapps.eu/privacy-policy.html"));
                            startActivity(browserIntent);
                        } else if (drawerItem == responsibleDisclosureItem) {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://shary.z3r0byteapps.eu/responsible-disclosure.html"));
                            startActivity(browserIntent);
                        }
                        return true;
                    }
                })
                .build();

        shareDatabase = new ShareDatabase(this);

        refresh();
    }

    private void destroySession() {
        new MaterialDialog.Builder(this)
                .title("Sessie uitloggen")
                .content("Als je uitlogt trek je de toegang van Shary tot je Magister account in en kan niemand meer je shares bekijken totdat je opnieuw inlogt. Je kunt nog wel de shares van anderen bekijken.")
                .positiveText("Uitloggen")
                .negativeText("Annuleren")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
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
                                if (success) {
                                    try {
                                        HttpUtil.httpDelete(magister.schoolUrl.getCurrentSessionUrl());
                                    } catch (IOException e) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(SharedActivity.this, R.string.err_no_connection, Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                    configUtil.setBoolean("loggedIn", false);
                                    configUtil.setString("School", null);
                                    configUtil.setString("User", null);
                                    configUtil.setString("Profile", null);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(SharedActivity.this, R.string.logged_out, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } else {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(SharedActivity.this, "Kon niet uitloggen, probeer het later opnieuw", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        }).start();
                    }
                }).show();
    }

    private Boolean createUser(String token, String sessionid, Integer personID) {
        School school = new Gson().fromJson(configUtil.getString("School"), School.class);
        String schoolPrefix = school.url.substring(8, school.url.indexOf("."));
        eu.z3r0byteapps.shary.SharyLibrary.User user = new eu.z3r0byteapps.shary.SharyLibrary.User(sessionid, token, personID, schoolPrefix);
        try {
            InputStreamReader inputStreamReader = HttpUtil.httpPost(Urls.createUser, new Gson().toJson(user));
            Result result = new Gson().fromJson(LogUtil.getStringFromInputStream(inputStreamReader), Result.class);
            if (result.error == null) {
                configUtil.setString("sessionId", sessionid);
            }
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


    private void addManualShare() {
        new MaterialDialog.Builder(this)
                .title("Share toevoegen")
                .content("Voer hieronder de URL van de share in")
                .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_URI)
                .alwaysCallInputCallback()
                .input("URL van de share: https://shary.z3r0byteapps.eu/view/share/...", null, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        if (!input.toString().trim().startsWith("https://shary.z3r0byteapps.eu/view/share/") ||
                                input.toString().trim().replace("https://shary.z3r0byteapps.eu/view/share/", "").replaceAll("/", "").length() != 64) {
                            dialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
                            return;
                        }
                        if (dialog.getActionButton(DialogAction.POSITIVE).isEnabled()) {
                            secret = input.toString().trim().replace("https://shary.z3r0byteapps.eu/view/share/", "").replaceAll("/", "");
                            new MaterialDialog.Builder(SharedActivity.this)
                                    .title("Share toevoegen")
                                    .content("Voer hieronder een beschrijving van de share in zodat je weet welke share dit is")
                                    .inputType(InputType.TYPE_CLASS_TEXT)
                                    .input("Cijfers van Bart", null, new MaterialDialog.InputCallback() {
                                        @Override
                                        public void onInput(MaterialDialog dialog, CharSequence input) {
                                            if (input.toString().isEmpty()) input = "Naamloos";
                                            addShare(secret, input.toString());
                                        }
                                    }).show();
                        } else {
                            dialog.getActionButton(DialogAction.POSITIVE).setEnabled(true);
                        }
                    }
                })
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        secret = dialog.getInputEditText().getText().toString().trim().replace("https://shary.z3r0byteapps.eu/view/share/", "").replaceAll("/", "");
                        new MaterialDialog.Builder(SharedActivity.this)
                                .title("Share toevoegen")
                                .content("Voer hieronder een beschrijving van de share in zodat je weet welke share dit is")
                                .inputType(InputType.TYPE_CLASS_TEXT)
                                .input("Cijfers van Bart", null, new MaterialDialog.InputCallback() {
                                    @Override
                                    public void onInput(MaterialDialog dialog, CharSequence input) {
                                        if (input.toString().isEmpty()) input = "Naamloos";
                                        addShare(secret, input.toString());
                                    }
                                }).show();
                    }
                }).show();
    }

    private void addShare(final String secret, final String comment) {
        final MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title("Share ophalen...")
                .content("Aanvullende informatie wordt opgehaald...")
                .progress(true, 0)
                .show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    GsonBuilder gsonBuilder = new GsonBuilder();
                    gsonBuilder.setDateFormat("yyyy-MM-dd HH:mm:ss");
                    String response = LogUtil.getStringFromInputStream(HttpUtil.httpGet(Urls.getShare + secret));
                    dialog.dismiss();
                    Result[] result = gsonBuilder.create().fromJson(response, Result[].class);
                    if (result.length != 0 && result[0].error != null) {
                        error(result[0].error);
                        return;
                    }
                    Share[] shares = gsonBuilder.create().fromJson(response, Share[].class);
                    if (shares.length == 0) {
                        error("Geen share gevonden met dit ID");
                        return;
                    }
                    Share share = shares[0];
                    share.setComment(comment);
                    String queryResult = shareDatabase.addItem(share);
                    if (queryResult != null) {
                        error(queryResult);
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                getShares();
                            }
                        });
                    }
                } catch (IOException e) {
                    error(getString(R.string.err_no_connection));
                }
            }
        }).start();

        refresh();
    }

    private void refresh() {
        final MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title(R.string.loading)
                .content(R.string.please_wait)
                .progress(true, 0)
                .cancelable(false)
                .show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                Share[] shares = shareDatabase.getShares();
                for (Share share :
                        shares) {
                    try {
                        GsonBuilder gsonBuilder = new GsonBuilder();
                        gsonBuilder.setDateFormat("yyyy-MM-dd HH:mm:ss");
                        String result = LogUtil.getStringFromInputStream(HttpUtil.httpGet(Urls.getShare + share.getSecret()));

                        Share tempshare = gsonBuilder.create().fromJson(result, Share[].class)[0];
                        share.setExpire(tempshare.getExpire());
                        share.setType(tempshare.getType());
                        share.setRestrictions(tempshare.getRestrictions());
                        shareDatabase.updateShare(share);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getShares();
                        dialog.dismiss();
                    }
                });
            }
        }).start();

    }

    private void getShares() {
        errorView.setVisibility(View.GONE);
        loading.setVisibility(View.VISIBLE);
        loading_layout.setVisibility(View.VISIBLE);
        nothing_shared_layout.setVisibility(View.GONE);
        shares_layout.setVisibility(View.GONE);

        final Share[] shares = shareDatabase.getShares();
        if (shares == null) {
            errorView.setVisibility(View.VISIBLE);
            loading.setVisibility(View.GONE);
            errorView.setRetryText(R.string.retry);
            errorView.setImage(R.drawable.error_squirrel);
            errorView.setTitle(R.string.unknown_error);
            errorView.setRetryListener(new ErrorView.RetryListener() {
                @Override
                public void onRetry() {
                    getShares();
                }
            });
        } else if (shares.length == 0) {
            loading_layout.setVisibility(View.GONE);
            nothing_shared_layout.setVisibility(View.VISIBLE);
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    ArrayList<Share> sharesList = new ArrayList<Share>(Arrays.asList(shares));
                    final SharedAdapter sharedAdapter = new SharedAdapter(SharedActivity.this, sharesList);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            listView.setAdapter(sharedAdapter);
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    Share[] shares1 = shareDatabase.getShares(); //Retrieving shares in case one was deleted trough the adapter
                                    Intent intent = new Intent(SharedActivity.this, ViewShareActivity.class);
                                    intent.putExtra("Share", new Gson().toJson(shares1[i]));
                                    startActivity(intent);
                                }
                            });
                        }
                    });
                }
            }).start();
            loading_layout.setVisibility(View.GONE);
            shares_layout.setVisibility(View.VISIBLE);
        }
    }


    private void error(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(SharedActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
