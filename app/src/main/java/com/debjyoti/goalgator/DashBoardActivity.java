package com.debjyoti.goalgator;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Main Activity for application.
 * All fragments are displayed form here using the fragment manager.
 * Used for navigation and displaying ads
 */
public class DashBoardActivity extends AppCompatActivity
        implements DashboardFragment.Callback, DetailFragment.Callback, NavigationView.OnNavigationItemSelectedListener {


    SettingsFragment mSettingsFragment;
    private final String TITLE_KEY = "title";
    private final String DUE_DATE_KEY = "due_date";
    private static final String NAV_ITEM_ID = "navItemId";
    private static final long DRAWER_CLOSE_DELAY_MS = 250;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.container)
    FrameLayout container;
    @BindView(R.id.adView)
    AdView adView;
    @BindView(R.id.navigation_view)
    NavigationView navigationView;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.container_layout)
    LinearLayout containerLayout;

    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;
    private InterstitialAd mInterstitialAd;

    private final Handler mDrawerActionHandler = new Handler();
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private int mNavItemId;

    /**
     * method called when DashBoardActivity is created
     * initializes navigation drawer
     * sets alarm for GoalAlarm intent service
     * loads and displays ads
     *
     * @param savedInstanceState bundle for restoring activity incase of lifecycle interuptions
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);
        ButterKnife.bind(this);


        mSettingsFragment = new SettingsFragment();
        mDrawerLayout = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        if (null == savedInstanceState) {
            mNavItemId = R.id.drawer_item_dashboard;
        } else {
            mNavItemId = savedInstanceState.getInt(NAV_ITEM_ID);
        }

        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        navigationView.getMenu().findItem(mNavItemId).setChecked(true);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, new DashboardFragment())
                    .commit();
        }


        //Set Alarm for database incrementation and notifications
        alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

        // Set the alarm to start at midnight
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 0);

        // set the alarm to repeat daily but not to go off until the device is woken up
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY,
                alarmIntent);


        //admob ad setup
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_ad_unit_id));
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
            }
        });

        requestNewInterstitial();

        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mAdView.loadAd(adRequest);

    }

    /**
     * displays fragment that corresponds to navigation drawer item that was selected
     *
     * @param itemId
     */
    private void navigate(final int itemId) {

        // update the main content by replacing fragments
        Fragment fragment;
        FragmentManager fragmentManager = getSupportFragmentManager();
        getFragmentManager().beginTransaction().remove(mSettingsFragment).commit();
        switch (itemId) {


            case R.id.drawer_item_detail:
                fragment = new DetailFragment();
                break;
            case R.id.drawer_item_focus:
                fragment = new FocusTimerFragment();
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                }
                break;
            case R.id.drawer_item_settings:
                getFragmentManager().beginTransaction()
                        .replace(R.id.container, mSettingsFragment)
                        .commit();

                return;
            default:

                fragment = new DashboardFragment();

        }

        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .addToBackStack(null)
                .commit();
    }


    /**
     * NavigationDrawer Callback method
     * closes drawer when item is selected and runs navigate(int)
     *
     * @param menuItem the item selected by the user
     * @return boolean, true to register navigation event
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull final MenuItem menuItem) {
        // update highlighted item in the navigation menu
        menuItem.setChecked(true);
        mNavItemId = menuItem.getItemId();

        // allow some time after closing the drawer before performing real navigation
        // so the user can see what is happening
        mDrawerLayout.closeDrawer(GravityCompat.START);
        mDrawerActionHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                navigate(menuItem.getItemId());
            }
        }, DRAWER_CLOSE_DELAY_MS);
        return true;
    }


    /**
     * loads a new interstitial ad
     */
    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();

        mInterstitialAd.loadAd(adRequest);
    }


    /**
     * Callback from DashBoardFragment
     * launches detail fragment of selected item
     *
     * @param vh the selected viewholder form the Goal Adapter
     */
    @Override
    public void onItemSelected(GoalAdapter.GoalAdapterViewHolder vh) {
        Bundle args = new Bundle();
        if (vh != null) {
            String title = String.valueOf(vh.mTitleView.getText());
            double date = vh.due_date;
            if (title != null) {
                args.putString(TITLE_KEY, title);
                args.putDouble(DUE_DATE_KEY, date);
            }
        }
        Fragment fragment = new DetailFragment();
        fragment.setArguments(args);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment)
                .addToBackStack(null)
                .commit();
        mNavItemId = R.id.drawer_item_detail;
    }

    /**
     * Callback from DetailFragment
     * Resets detail fragment after save button is pressed
     *
     * @param args bundle containing name and due date of fragment
     */
    @Override
    public void onSave(Bundle args) {
        Fragment fragment = new DetailFragment();
        fragment.setArguments(args);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment)
                .commit();

    }

    /**
     * method called when options itemis selected
     *
     * @param item which item was selected from the options menu
     * @return true to register event
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.support.v7.appcompat.R.id.home) {
            return mDrawerToggle.onOptionsItemSelected(item);
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * open navigation drawer
     */
    public void openDrawer() {
        if (mDrawerLayout != null) {
            mDrawerLayout.openDrawer(GravityCompat.START);
        }
    }

    /**
     * closes navigation when back is pressed while it is open
     */
    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            invalidateOptionsMenu();
            super.onBackPressed();
        }
    }

    /**
     * saves position of navigation drawer on lifecycle interuptions
     *
     * @param outState bundle containing information to restore activity
     */
    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(NAV_ITEM_ID, mNavItemId);
    }

}


