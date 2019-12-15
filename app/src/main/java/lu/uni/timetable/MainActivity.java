package lu.uni.timetable;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.Date;

import lu.uni.timetable.ui.login.LoginActivity;

public class MainActivity extends AppCompatActivity implements TimetableFragment.ITimetableFragmentObserver, Updater.UpdateListener {
    static final int LOGIN_REQUEST_CODE = 27;
    enum ViewOption {
        View1Day,
        View3Days,
        View5Days,
        View7Days
    }

    private TimetableFragment timetableFragment;
    private FloatingActionButton updateButton;
    private Animation updateButtonAnimation;
    private boolean updateRunning = false;
    private ViewOption portraitViewOption = ViewOption.View3Days;
    private ViewOption landscapeViewOption = ViewOption.View5Days;
    private MenuItem viewOptionMenu;
    private boolean landscapeOrientation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.err.println("MainActivity: Just created!");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        System.err.println("MainActivity: Getting preferences...");
        SharedPreferences prefs = Settings.preferences();
        System.err.println("MainActivity: Got preferences: " + prefs);

        updateButton = findViewById(R.id.updateButton);
        updateButtonAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate_update_button);
        landscapeOrientation = (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE);
        if(landscapeOrientation) {
            landscapeViewOption = ViewOption.View5Days;
            timetableFragment.getWeekView().setNumberOfVisibleDays(5);
        }
        else {
            portraitViewOption = ViewOption.View3Days;
            timetableFragment.getWeekView().setNumberOfVisibleDays(3);
        }

        if(!prefs.getBoolean(Settings.USER_LOGGED_IN, false)) {
            System.err.println("User not logged in!");
            startActivityForResult(new Intent(this, LoginActivity.class),LOGIN_REQUEST_CODE);
        }
        else if(Settings.preferences().getBoolean(Settings.MAIN_UPDATE_NEEDED, true)) {
            System.err.println("Update needed!");
            Updater.firstUpdate(this);
            updateRunning = true;
            updateButton.startAnimation(updateButtonAnimation);
        }
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        if (fragment instanceof TimetableFragment) {
            System.err.println("MainActivity: setting TimetableFragment observer");
            timetableFragment = (TimetableFragment) fragment;
            timetableFragment.setObserver(this);
        }
    }

    public void update(View view) {
        //For future: if you ever use view, note that it can be null, since this function is not only
        //used as a callback

        if(
                !updateRunning //Do not update if an update is already in progress
                && timetableFragment != null //Perhaps if the user manages to press the update updateButton
                //before the fragment is attached, or in case something weird happens
          )
        {
            timetableFragment.requestDatabaseUpdate();
            updateRunning = true;
            updateButton.startAnimation(updateButtonAnimation);
        }
    }

    @Override
    public void onUpdateFinished() {
        Snackbar.make(findViewById(R.id.coordinatorLayout), getString(R.string.update_finished), Snackbar.LENGTH_SHORT).show();
        updateButton.clearAnimation();
        updateRunning = false;
        Settings.preferences().edit().putBoolean(Settings.MAIN_UPDATE_NEEDED, false).apply();
    }

    @Override
    public void onUpdateFinished(Date startDate, Date endDate) {
        //This is called by Updater when the first database update after user login has finished
        onUpdateFinished();
    }

    @Override
    public void onUpdateError(Exception error) {
        Snackbar.make(findViewById(R.id.coordinatorLayout), getString(R.string.update_error), Snackbar.LENGTH_SHORT).show();
        updateButton.clearAnimation();
        updateRunning = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater i = getMenuInflater();
        i.inflate(R.menu.main_activity_menu, menu);
        viewOptionMenu = menu.findItem(R.id.action_view_option);
        if (landscapeOrientation) {
            viewOptionMenu.setIcon(R.drawable.view_7days);
            viewOptionMenu.setTitle(R.string.view_7days);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_today:
                timetableFragment.getWeekView().goToToday();
                return true;
            case R.id.action_view_option:
                if(landscapeOrientation) {
                    switch(landscapeViewOption) {
                        case View3Days:
                            landscapeViewOption = ViewOption.View5Days;
                            timetableFragment.getWeekView().setNumberOfVisibleDays(5);
                            viewOptionMenu.setIcon(R.drawable.view_7days);
                            viewOptionMenu.setTitle(R.string.view_7days);
                            break;
                        case View5Days:
                            landscapeViewOption = ViewOption.View7Days;
                            timetableFragment.getWeekView().setNumberOfVisibleDays(7);
                            viewOptionMenu.setIcon(R.drawable.view_3days);
                            viewOptionMenu.setTitle(R.string.view_3days);
                            break;
                        case View7Days:
                            landscapeViewOption = ViewOption.View3Days;
                            timetableFragment.getWeekView().setNumberOfVisibleDays(3);
                            viewOptionMenu.setIcon(R.drawable.view_5days);
                            viewOptionMenu.setTitle(R.string.view_5days);
                    }

                }
                else {
                    if (portraitViewOption == ViewOption.View1Day) {
                        portraitViewOption = ViewOption.View3Days;
                        viewOptionMenu.setIcon(R.drawable.view_day);
                        viewOptionMenu.setTitle(getString(R.string.view_day));
                        timetableFragment.getWeekView().setNumberOfVisibleDays(3);
                    } else { //if view.Option == ViewOption.View3Days
                        portraitViewOption = ViewOption.View1Day;
                        viewOptionMenu.setIcon(R.drawable.view_3days);
                        viewOptionMenu.setTitle(getString(R.string.view_3days));
                        timetableFragment.getWeekView().setNumberOfVisibleDays(1);
                    }
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            timetableFragment.getWeekView().setNumberOfVisibleDays(5);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == LOGIN_REQUEST_CODE) {
            System.err.println("MainActivity: back from LoginActivity!");
            Updater.firstUpdate(this);
        }
    }
}
