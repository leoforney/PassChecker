package tk.leoforney.passchecker;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.Random;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    FragmentManager fm;
    Fragment listFrag;
    Fragment camFrag;
    TextView ipTextView;
    NavigationView navigationView;
    //ThemeColors themeColors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!CredentialsManager.getInstance(this).alreadyExists()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        listFrag = PassListFragment.newInstance();

        Toolbar toolbar = findViewById(R.id.toolbar);
        ipTextView = findViewById(R.id.ip_address_textview);
        ipTextView.setText(CredentialsManager.getInstance(this).getIP());

        findViewById(R.id.logout).setOnClickListener(this);
        setSupportActionBar(toolbar);
        fm = getSupportFragmentManager();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        CredentialsManager.getInstance(this).setDisplayData(this);

        if (listFrag == null) {
            listFrag = PassListFragment.newInstance();
        }
        navigationView.getMenu().getItem(0).setChecked(true);
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.contentFragment, listFrag).commit();

        CredentialsManager.getInstance(this).setDisplayData(this);

        //themeColors = new ThemeColors(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.action_retrieve_color) {
            int red= new Random().nextInt(255);
            int green= new Random().nextInt(255);
            int blue= new Random().nextInt(255);
            //ThemeColors.setNewThemeColor(MainActivity.this, red, green, blue);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        FragmentTransaction transaction = fm.beginTransaction();
        if (id == R.id.nav_list) {
            if (listFrag == null) {
                listFrag = PassListFragment.newInstance();
            }
            transaction.replace(R.id.contentFragment, listFrag);
        } else if (id == R.id.nav_camera) {
            if (camFrag == null) {
                camFrag = CameraFragment.newInstance();
            }
            transaction.replace(R.id.contentFragment, camFrag);
        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.action_uvc_camera) {
            startActivity(new Intent(this, USBCameraActivity.class));
        }

        transaction.commit();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.logout:
                CredentialsManager.getInstance(this).clearData();
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
            break;
        }
    }

}
