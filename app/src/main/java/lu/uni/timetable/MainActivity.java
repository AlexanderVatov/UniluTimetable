package lu.uni.timetable;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity implements TimetableFragment.ITimetableFragmentObserver {
    private TimetableFragment timetableFragment;
    private FloatingActionButton updateButton;
    private Animation updateButtonAnimation;
    private boolean updateRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        updateButton = findViewById(R.id.updateButton);
        updateButtonAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate_update_button);
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
    }

    @Override
    public void onUpdateError(Exception error) {
        Snackbar.make(findViewById(R.id.coordinatorLayout), getString(R.string.update_error), Snackbar.LENGTH_SHORT).show();
        updateButton.clearAnimation();
        updateRunning = false;
    }
}
