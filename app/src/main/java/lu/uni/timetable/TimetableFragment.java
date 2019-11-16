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

public class TimetableFragment extends Fragment implements ITimetableView, Updater.UpdateListener {

    private View parentView;
    private WeekView<Event> weekView;
    private Presenter presenter;
    private ITimetableFragmentObserver observer;


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

    //Called by activity containing this fragment
    public void requestDatabaseUpdate() {
        Updater.asyncUpdate(this);
    }


    @Override
    public void onDatabaseUpdated(Date start, Date end) {
        if (observer != null) observer.onUpdateFinished();
        System.err.println("TimetableFragment: Notified of an update. Querying fresh data...");
        presenter.getEvents(new WeakReference<ITimetableView>(this), start, end);
    }

    @Override
    public void onQueryFinished(List<Event> events) {
        System.err.println("TimetableFragment: Received " + events.size() + " events! ");
        weekView.submit(events);
    }

    public void setObserver(ITimetableFragmentObserver observer) {
        this.observer = observer;
    }

    @Override
    public void onUpdateFinished(Date startDate, Date endDate) {
        //Do nothing; will be informed by Presenter anyway
    }

    @Override
    public void onUpdateError(Exception error) {
        if(observer != null) observer.onUpdateError(error);

    }


    public interface ITimetableFragmentObserver {
        void onUpdateFinished(); //Replaced by UpdateListener.onUpdateFinished
        void onUpdateError(Exception error);
    }
}
