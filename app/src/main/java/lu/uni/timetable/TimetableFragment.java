package lu.uni.timetable;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.alamkanak.weekview.WeekView;

import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TimetableFragment extends Fragment implements ITimetableView {

    private View parentView;
    WeekView<Event> weekView;
    Presenter presenter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        presenter = Presenter.getInstance();
        presenter.register(new WeakReference<ITimetableView>(this));
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        Date start = c.getTime(); //Today
        c.roll(Calendar.WEEK_OF_YEAR, 2);
        Date end = c.getTime();
        presenter.getEvents(start, end);

        parentView = inflater.inflate(R.layout.timetable_fragment, container, false);
        weekView = parentView.findViewById(R.id.weekView);
        return parentView;
    }


    @Override
    public void onDatabaseUpdate(Date start, Date end) {
        System.err.println("TimetableFragment.onDatabaseUpdate: An update was performed! Querying...");
        presenter.getEvents(start, end);
    }

    @Override
    public void queryFinished(List<Event> events) {
        System.err.println("TimetableFragment: Received " + events.size() + " events! ");
        weekView.submit(events);
    }
}
