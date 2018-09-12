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

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
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
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import eu.z3r0byteapps.shary.Adapters.ShareAdapter;
import eu.z3r0byteapps.shary.MagisterLibrary.util.HttpUtil;
import eu.z3r0byteapps.shary.MagisterLibrary.util.LogUtil;
import eu.z3r0byteapps.shary.SharyLibrary.Result;
import eu.z3r0byteapps.shary.SharyLibrary.Share;
import eu.z3r0byteapps.shary.SharyLibrary.ShareType;
import eu.z3r0byteapps.shary.SharyLibrary.Urls;
import eu.z3r0byteapps.shary.Util.ConfigUtil;
import tr.xip.errorview.ErrorView;

public class ShareActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    private static final String TAG = "ShareActivity";


    ConfigUtil configUtil;
    ErrorView errorView;
    ProgressBar loading;
    Date expiry;
    Boolean loaded = false;

    ConstraintLayout nothing_shared_layout;
    ConstraintLayout loading_layout;
    ConstraintLayout shares_layout;
    ConstraintLayout setup_layout;

    Button dateButton;

    ListView listView;
    Share[] shares;
    ShareAdapter shareAdapter;

    @SuppressLint("SimpleDateFormat")
    public static Date parseDate(String date) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date);
        } catch (ParseException e) {
            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);


        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.shary_share);
        setSupportActionBar(toolbar);

        errorView = findViewById(R.id.error);
        loading = findViewById(R.id.progress);
        listView = findViewById(R.id.list);

        setup_layout = findViewById(R.id.setup_layout);
        loading_layout = findViewById(R.id.loading);
        shares_layout = findViewById(R.id.shares_list);
        nothing_shared_layout = findViewById(R.id.nothing_shared);

        final PrimaryDrawerItem homeItem = new PrimaryDrawerItem().withIdentifier(1).withName(R.string.home);
        final PrimaryDrawerItem shareItem = new PrimaryDrawerItem().withIdentifier(2).withName(R.string.share).withSetSelected(true);
        final PrimaryDrawerItem sharedItem = new PrimaryDrawerItem().withIdentifier(3).withName(R.string.shared_with_me);
        final PrimaryDrawerItem aboutItem = new PrimaryDrawerItem().withIdentifier(4).withName(R.string.about_title).withSelectable(false);
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
                        new SectionDrawerItem().withName(R.string.additional_info),
                        websiteItem,
                        responsibleDisclosureItem,
                        privacyItem
                )
                .withSelectedItem(2)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (drawerItem == homeItem) {
                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                            finish();
                        } else if (drawerItem == shareItem) {
                            //already here
                        } else if (drawerItem == sharedItem) {
                            startActivity(new Intent(getApplicationContext(), SharedActivity.class));
                            finish();
                        } else if (drawerItem == aboutItem) {
                            new LibsBuilder()
                                    .withActivityStyle(Libs.ActivityStyle.LIGHT_DARK_TOOLBAR)
                                    .start(ShareActivity.this);
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

        Button login = findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });

        setup();
    }

    private void setup() {
        configUtil = new ConfigUtil(this);
        Boolean setup = configUtil.getBoolean("loggedIn", false);
        if (setup && !loaded) {
            loaded = true;
            loadSharedItems();
        } else if (!setup) {
            showSetup();
        }
    }

    private void showSetup() {
        setup_layout.setVisibility(View.VISIBLE);
    }

    private void loadSharedItems() {
        setup_layout.setVisibility(View.GONE);
        errorView.setVisibility(View.GONE);
        loading.setVisibility(View.VISIBLE);

        final Context context = this;

        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loading_layout.setVisibility(View.VISIBLE);
                    }
                });

                FloatingActionButton addButton = findViewById(R.id.add_share);
                addButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        newShare();
                    }
                });
                FloatingActionButton addButton2 = findViewById(R.id.add_share2);
                addButton2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        newShare();
                    }
                });

                final Share[] shares = getShares();
                if (shares == null) {
                    error("Shares konden niet worden geladen");
                    return;
                }

                Log.d(TAG, String.format("run: shares: %s", shares.toString()));

                if (shares.length == 0) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            nothing_shared_layout.setVisibility(View.VISIBLE);
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            nothing_shared_layout.setVisibility(View.GONE);
                            shares_layout.setVisibility(View.VISIBLE);
                            errorView.setVisibility(View.GONE);
                            shareAdapter = new ShareAdapter(context, shares);
                            listView.setAdapter(shareAdapter);
                        }
                    });
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loading_layout.setVisibility(View.GONE);
                    }
                });
            }
        }).start();

    }

    private void error(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ConstraintLayout layout = findViewById(R.id.nothing_shared);
                final ConstraintLayout finalLayout2 = layout;
                finalLayout2.setVisibility(View.GONE);
                loading.setVisibility(View.GONE);
                errorView.setTitle(message);
                errorView.setImage(R.drawable.error_squirrel);
                errorView.setRetryListener(new ErrorView.RetryListener() {
                    @Override
                    public void onRetry() {
                        loadSharedItems();
                    }
                });
                errorView.setRetryText(getString(R.string.retry));
                errorView.setVisibility(View.VISIBLE);
                Snackbar.make(findViewById(R.id.share_layout), message, Snackbar.LENGTH_SHORT);
            }
        });
    }

    private Share[] getShares() {
        try {
            HttpUtil.setCookie(configUtil.getString("token"));
            String result = LogUtil.getStringFromInputStream(HttpUtil.httpGet(Urls.shares));
            Result[] resultType = new Gson().fromJson(result, Result[].class);

            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.setDateFormat("yyyy-MM-dd HH:mm:ss");
            shares = gsonBuilder.create().fromJson(result, Share[].class);
            if (resultType.length != 0 && resultType[0].error != null) {
                Log.e(TAG, "getShares: Error: " + resultType[0].error);
                error(resultType[0].error);
                return null;
            }
            return shares;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void newShare() {
        expiry = parseDate("2050-01-01 00:00:00");
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title(R.string.new_share)
                .customView(R.layout.dialog_new_share, true)
                .positiveText(R.string.share)
                .negativeText(R.string.cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        View view = dialog.getCustomView();
                        Spinner spinner = view.findViewById(R.id.type);
                        EditText comment = view.findViewById(R.id.comment);
                        Share share = new Share();
                        share.setComment(comment.getText().toString().trim());
                        share.setType(ShareType.getTypeById(spinner.getSelectedItemPosition() + 1));
                        share.setExpire(expiry);
                        submit(share);
                    }
                }).show();
        dateButton = dialog.getCustomView().findViewById(R.id.date);
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        ShareActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.setMinDate(Calendar.getInstance());
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
        });
    }

    private void submit(final Share share) {
        final MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title("Share opslaan...")
                .content("Geheime sleutel wordt gegenereerd...")
                .progress(true, 0)
                .show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    GsonBuilder gsonBuilder = new GsonBuilder();
                    gsonBuilder.setDateFormat("yyyy-MM-dd HH:mm:ss");
                    Gson gson = gsonBuilder.create();
                    String resultStr = LogUtil.getStringFromInputStream(HttpUtil.httpPost(Urls.addShare, gson.toJson(share)));
                    Log.d(TAG, "run: Result: " + resultStr);
                    dialog.dismiss();
                    Result result[] = new Gson().fromJson(resultStr, Result[].class);
                    Share[] shares = gson.fromJson(resultStr, Share[].class);
                    if (result.length != 0 && result[0].error != null) {
                        error("Fout tijdens aanmaken van share: " + result[0].error);
                        return;
                    }
                    Log.d(TAG, "run: shares: " + shares[0].toString());
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, "Bekijk mijn Magister account via Shary: https://shary.z3r0byteapps.eu/view/share/" + shares[0].getSecret() + "/");
                    sendIntent.setType("text/plain");
                    startActivity(Intent.createChooser(sendIntent, getString(R.string.send_link)));
                } catch (IOException e) {
                    e.printStackTrace();
                    dialog.dismiss();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ShareActivity.this, R.string.err_no_connection, Toast.LENGTH_SHORT).show();
                        }
                    });
                } finally {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loadSharedItems();
                        }
                    });
                }
            }
        }).start();
    }

    @Override
    public void onResume() {
        super.onResume();
        setup();

    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        monthOfYear = monthOfYear + 1;
        dateButton.setText(String.format("%d-%d-%d", dayOfMonth, monthOfYear, year));
        expiry = parseDate(String.format("%d-%d-%d 00:00:00", year, monthOfYear, dayOfMonth));
    }
}
