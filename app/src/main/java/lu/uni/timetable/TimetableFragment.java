package lu.uni.timetable;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.alamkanak.weekview.OnLoadMoreListener;
import com.alamkanak.weekview.WeekView;

import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TimetableFragment extends Fragment implements ITimetableView, Updater.UpdateListener, OnLoadMoreListener {

    private View parentView;
    private WeekView<Event> weekView;
    private Presenter presenter;
    private ITimetableFragmentObserver observer;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        presenter = Presenter.getInstance();
        presenter.register(new WeakReference<ITimetableView>(this));

        parentView = inflater.inflate(R.layout.timetable_fragment, container, false);
        weekView = parentView.findViewById(R.id.weekView);
        System.err.println("Weekview: " + weekView);

        System.err.println("Setting listener...");
        weekView.setOnLoadMoreListener(this);

        return parentView;
    }

    //Called by activity containing this fragment
    public void requestDatabaseUpdate() {
        Date start = Utils.firstDayOfMonth(Utils.Month.CURRENT_MONTH);
        Date end = Utils.lastDayOfMonth(Utils.Month.NEXT_MONTH);

        Date lastVisible = weekView.getLastVisibleDate().getTime();
        if (lastVisible.after(end)) {
            end = Utils.lastDayOfMonth(lastVisible);
        }
        Updater.asyncUpdate(this, start, end);
    }


    @Override
    public void onDatabaseUpdated(Date startOfUpdatedPeriod, Date endOfUpdatedPeriod) {

        System.err.println("TimetableFragment: Notified of an update. Querying fresh data...");
        presenter.requestEvents(new WeakReference<ITimetableView>(this), startOfUpdatedPeriod, endOfUpdatedPeriod);
    }

    @Override
    public void onEventsReady(List<Event> events) {
        System.err.println("TimetableFragment: Received " + events.size() + " events! ");
        if(events.size() == 0)  {
            System.err.println("TimetableFragment: Submitted nothing.");
        }
        else {
            weekView.submit(events);
            System.err.println("TimetableFragment: Submitted them.");
        }
    }

    public void setObserver(ITimetableFragmentObserver observer) {
        this.observer = observer;
    }

    @Override
    public void onUpdateFinished(Date startDate, Date endDate) {
        //Do nothing with data; it will also be received by onDatabaseUpdated (called by Presenter) anyway
        if (observer != null) observer.onUpdateFinished();
    }

    @Override
    public void onUpdateError(Exception error) {
        if(observer != null) observer.onUpdateError(error);

    }

    @Override
    public void onLoadMore(Calendar startCalendar, Calendar endCalendar) {
        System.err.println("Asked for events between " + startCalendar.getTime() + " and " + endCalendar.getTime());
        presenter.requestEvents(new WeakReference<ITimetableView>(this), startCalendar.getTime(), endCalendar.getTime());
    }


    public interface ITimetableFragmentObserver {
        void onUpdateFinished(); //Replaced by UpdateListener.onUpdateFinished
        void onUpdateError(Exception error);
    }
}
