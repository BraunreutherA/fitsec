package secureapps.com.fitsec;

import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import java.util.Timer;
import java.util.TimerTask;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private HomeFragment homeFragment;
    private SettingsFragment settingsFragment;
    private AppListFragment appListFragment;
    private Timer timer;
    private TimerTask timerTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppService appService = new AppService();
        appService.updateInternalAppList(this);

        SecureReportService secureReportService = new SecureReportService();
        secureReportService.syncSecureReports();

        InstallationReportService installationReportService = new InstallationReportService();
        installationReportService.syncInstallationReports();

        setContentView(R.layout.main_layout);

        homeFragment = new HomeFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, homeFragment).commit();

        settingsFragment = new SettingsFragment();
        timer = new Timer();
        timerTask = new TimerTask() {
            public void run() {
                settingsFragment.monitorAppUsage();
            };
        };

        settingsFragment.setOnStartAppTimerListener(new SettingsFragment.OnStartAppTimerListener(){
            @Override
            public void startAppTimer(boolean start) {
                if (start) {
                    timer.scheduleAtFixedRate(timerTask, 100,100);
                } else {
                    //stopp Timer!
                    timer.cancel();
                    timer.purge();
                }
            }
        });
        appListFragment = new AppListFragment();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //init receiver for screen on, off
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        Receiver mReceiver = new Receiver();
        registerReceiver(mReceiver, filter);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FragmentManager fragmentManager = getSupportFragmentManager();

        switch (id) {
            case R.id.nav_home:
                fragmentManager.beginTransaction()
                        .replace(R.id.content_frame, homeFragment)
                        .commit();
                break;
            case R.id.nav_list:
                //TODO maybe enter passwort here to get access to list
                // Insert the fragment by replacing any existing fragment
                fragmentManager.beginTransaction()
                        .replace(R.id.content_frame, appListFragment)
                        .commit();
                break;

            case R.id.nav_settings:
                // Insert the fragment by replacing any existing fragment
                fragmentManager.beginTransaction()
                        .replace(R.id.content_frame, settingsFragment)
                        .commit();
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
