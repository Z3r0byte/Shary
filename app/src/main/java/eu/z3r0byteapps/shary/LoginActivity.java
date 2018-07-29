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
