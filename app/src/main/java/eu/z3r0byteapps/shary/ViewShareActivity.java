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

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.Date;

import eu.z3r0byteapps.shary.Fragments.ViewCalendar;
import eu.z3r0byteapps.shary.Fragments.ViewGrades;
import eu.z3r0byteapps.shary.Fragments.ViewInfo;
import eu.z3r0byteapps.shary.Fragments.ViewNewGrades;
import eu.z3r0byteapps.shary.SharyLibrary.Share;

public class ViewShareActivity extends AppCompatActivity {

    public static Share share;

    ViewInfo infoFragment = new ViewInfo();
    ViewCalendar calendarFragment = new ViewCalendar();
    ViewNewGrades newGradesFragment = new ViewNewGrades();
    ViewGrades gradesFragment = new ViewGrades();

    Toolbar toolbar;
    BottomNavigationView navigation;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_info:
                    toolbar.setTitle(share.getComment());
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_manager, infoFragment).commit();
                    return true;
                case R.id.navigation_calendar:
                    toolbar.setTitle(R.string.calendar);
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_manager, calendarFragment).commit();
                    return true;
                case R.id.navigation_new_grades:
                    toolbar.setTitle(R.string.new_grades);
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_manager, newGradesFragment).commit();
                    return true;
                case R.id.navigation_grades:
                    toolbar.setTitle(R.string.grades);
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_manager, gradesFragment).commit();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_share);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            share = new Gson().fromJson(extras.getString("Share"), Share.class);
        } else {
            Toast.makeText(this, R.string.unknown_error, Toast.LENGTH_SHORT).show();
            finish();
        }

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(share.getComment());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_manager, infoFragment).commit();

        navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    public void updateBottomBar(Boolean calendar, Boolean newGrades, Boolean grades) {
        navigation.getMenu().clear();
        navigation.inflateMenu(R.menu.navigation);
        Boolean expired = share.getExpire().before(new Date());
        if (!grades || expired) navigation.getMenu().removeItem(R.id.navigation_grades);
        if (!newGrades || expired) navigation.getMenu().removeItem(R.id.navigation_new_grades);
        if (!calendar || expired) navigation.getMenu().removeItem(R.id.navigation_calendar);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
