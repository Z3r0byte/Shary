package eu.z3r0byteapps.shary;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

public class SharedActivity extends AppCompatActivity {
    private static final String TAG = "SharedActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shared);

        Intent intent = getIntent();
        Uri data = intent.getData();
        if (data != null) {
            Log.d(TAG, "onCreate: ID: " + data.getLastPathSegment());
            Toast.makeText(this, "Link geopend! ID: " + data.getLastPathSegment(), Toast.LENGTH_LONG).show();
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
}
