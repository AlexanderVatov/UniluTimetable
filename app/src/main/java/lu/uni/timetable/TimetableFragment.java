package lu.uni.timetable;

import android.content.Intent;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.alamkanak.weekview.OnEventClickListener;
import com.alamkanak.weekview.OnLoadMoreListener;
import com.alamkanak.weekview.WeekView;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * A fragment showing the timetable. It is embedded in the main activity.
 */
public class TimetableFragment
        extends Fragment
        implements Presenter.Observer,
                   Updater.UpdateListener,
                   OnLoadMoreListener,
                   OnEventClickListener<Event> {

    private View parentView;
    private FragmentActivity activity;


    private WeekView<Event> weekView;
    private Presenter presenter;
    private ITimetableFragmentObserver observer;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        presenter = Presenter.getInstance();
        presenter.register(new WeakReference<Presenter.Observer>(this));

        parentView = inflater.inflate(R.layout.timetable_fragment, container, false);
        weekView = parentView.findViewById(R.id.weekView);
        weekView.goToHour(8);
        weekView.setOnLoadMoreListener(this);
        weekView.setOnEventClickListener(this);

        activity = getActivity();
        return parentView;
    }

    //Called by activity containing this fragment
    public void requestDatabaseUpdate() {
        Date start = Utils.firstDayOfMonth(Utils.Month.CURRENT_MONTH);
        Date end = Utils.lastDayOfMonth(Utils.Month.NEXT_MONTH);

        Calendar lastVisible = weekView.getLastVisibleDate();//.getTime();
        if (lastVisible != null && lastVisible.after(end)) {
            end = Utils.lastDayOfMonth(lastVisible.getTime());
        }
        Updater.asyncUpdate(this, start, end);
    }


    @Override
    public void onDatabaseUpdated(Date startOfUpdatedPeriod, Date endOfUpdatedPeriod) {

        System.err.println("TimetableFragment: Notified of an update. Querying fresh data...");
        presenter.requestEvents(new WeakReference<Presenter.Observer>(this), startOfUpdatedPeriod, endOfUpdatedPeriod);
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
        presenter.requestEvents(new WeakReference<Presenter.Observer>(this), startCalendar.getTime(), endCalendar.getTime());
    }

    @Override
    public void onEventClick(Event event, @NotNull RectF rectF) {
        System.err.println("TimetableFragment.onEventClick");
        Intent intent = EventIntent.newIntent(event, activity, EventDetailsActivity.class);
        startActivity(intent);
    }


    public interface ITimetableFragmentObserver {
        void onUpdateFinished(); //Replaced by UpdateListener.onUpdateFinished
        void onUpdateError(Exception error);
    }

    public WeekView getWeekView() {
        return weekView;
    }
}
