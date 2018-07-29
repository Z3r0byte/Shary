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

package eu.z3r0byteapps.shary;

import android.os.Bundle;

import agency.tango.materialintroscreen.MaterialIntroActivity;
import agency.tango.materialintroscreen.SlideFragmentBuilder;
import eu.z3r0byteapps.shary.Fragments.Login;
import eu.z3r0byteapps.shary.Fragments.PrivacyPolicy;
import eu.z3r0byteapps.shary.Fragments.SelectSchool;
import eu.z3r0byteapps.shary.Fragments.Setup;

public class LoginActivity extends MaterialIntroActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addSlide(new PrivacyPolicy());

        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.colorPrimary)
                .buttonsColor(R.color.colorAccent)
                .title(getString(R.string.your_password_title))
                .description(getString(R.string.your_password_desc))
                .build());

        addSlide(new SelectSchool());

        addSlide(new Login());

        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.colorPrimary)
                .buttonsColor(R.color.accent)
                .title(getString(R.string.important))
                .description(getString(R.string.valid_session_desc))
                .build());

        addSlide(new Setup());
    }
}
