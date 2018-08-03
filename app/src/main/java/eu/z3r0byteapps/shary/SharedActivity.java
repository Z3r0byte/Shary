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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.GsonBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.io.IOException;

import eu.z3r0byteapps.shary.MagisterLibrary.util.HttpUtil;
import eu.z3r0byteapps.shary.MagisterLibrary.util.LogUtil;
import eu.z3r0byteapps.shary.SharyLibrary.Result;
import eu.z3r0byteapps.shary.SharyLibrary.Share;
import eu.z3r0byteapps.shary.SharyLibrary.Urls;

public class SharedActivity extends AppCompatActivity {
    private static final String TAG = "SharedActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shared);

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
                            addShare(data.getLastPathSegment(), input.toString());
                        }
                    }).show();
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.shary_shared_with_me);
        setSupportActionBar(toolbar);

        final PrimaryDrawerItem homeItem = new PrimaryDrawerItem().withIdentifier(1).withName(R.string.home);
        final PrimaryDrawerItem shareItem = new PrimaryDrawerItem().withIdentifier(2).withName(R.string.share);
        final PrimaryDrawerItem sharedItem = new PrimaryDrawerItem().withIdentifier(3).withName(R.string.shared_with_me).withSetSelected(true);

        Drawer drawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .addDrawerItems(
                        homeItem,
                        shareItem,
                        sharedItem
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
                        }
                        return true;
                    }
                })
                .build();
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
                        error(getString(R.string.unknown_error));
                        return;
                    }
                    Share[] shares = gsonBuilder.create().fromJson(response, Share[].class);
                    if (shares.length == 0) {
                        error("Geen share gevonden met dit ID");
                        return;
                    }
                    Share share = shares[0];
                    share.setComment(comment);
                    Log.d(TAG, "run: " + share.toString());
                } catch (IOException e) {
                    error(getString(R.string.err_no_connection));
                }
            }
        }).start();
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
