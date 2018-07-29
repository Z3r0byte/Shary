package eu.z3r0byteapps.shary;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

public class HomeActivity extends AppCompatActivity {

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
    }
}
