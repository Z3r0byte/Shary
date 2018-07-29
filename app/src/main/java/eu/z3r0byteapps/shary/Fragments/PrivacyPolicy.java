package eu.z3r0byteapps.shary.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;

import agency.tango.materialintroscreen.SlideFragment;
import eu.z3r0byteapps.shary.R;

public class PrivacyPolicy extends SlideFragment {
    private CheckBox checkBox;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_privacy_policy, container, false);
        checkBox = view.findViewById(R.id.agreed);

        Button link = view.findViewById(R.id.privacy);
        link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://shary.z3r0byteapps.eu/privacy-policy.html"));
                startActivity(browserIntent);
            }
        });
        return view;
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
        return checkBox.isChecked();
    }

    @Override
    public String cantMoveFurtherErrorMessage() {
        return getString(R.string.agree_first);
    }
}
