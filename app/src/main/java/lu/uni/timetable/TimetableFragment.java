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

        presenter.getEventsThisWeek(new WeakReference<ITimetableView>(this));

        parentView = inflater.inflate(R.layout.timetable_fragment, container, false);
        weekView = parentView.findViewById(R.id.weekView);
        return parentView;
    }


    @Override
    public void onDatabaseUpdate(Date start, Date end) {
        System.err.println("TimetableFragment: Notified of an update. Querying fresh data...");
        presenter.getEvents(new WeakReference<ITimetableView>(this), start, end);
    }

    @Override
    public void queryFinished(List<Event> events) {
        System.err.println("TimetableFragment: Received " + events.size() + " events! ");
        weekView.submit(events);
    }
}
