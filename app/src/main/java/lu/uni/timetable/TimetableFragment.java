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

    public static TimetableFragment newInstance() {
        return new TimetableFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        presenter = TimetableApplication.getPresenter();
        presenter.register(new WeakReference<ITimetableView>(this));
        Calendar c = Calendar.getInstance();
        Date start = c.getTime(); //Today
        c.roll(Calendar.WEEK_OF_YEAR, 2);
        Date end = c.getTime();
        presenter.query(start, end);


//        mViewModel.updateRunning().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
//            @Override
//            public void onChanged(Boolean aBoolean) {
//
//                mViewModel.triggerDatabaseQuery();
//            }
//        });
//
//        mViewModel.update().observe(getViewLifecycleOwner(), new Observer<List<Event>>() {
//            @Override
//            public void onChanged(List<Event> events) {
//                //
//            }
//        });

        parentView = inflater.inflate(R.layout.timetable_fragment, container, false);
        weekView = parentView.findViewById(R.id.weekView);
        return parentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // TODO: Use the ViewModel
        //mViewModel.triggerDatabaseUpdate();
    }

    public void databaseUpdate() {
        Updater.asyncUpdate();

    }

    @Override
    public void onDatabaseUpdate(Date start, Date end) {
//            EventDAO dao = Database.instance(getContext()).getEventDAO();
//            List<Event> events = dao.getAllEvents();
//            System.err.println(events.size() + " events loaded from database.");
//            weekView.submit(events);
        presenter.query(start, end);
    }

    @Override
    public void queryFinished(List<Event> events) {
        System.err.println("TimetableFragment: Received " + events.size() + " events! ");
        weekView.submit(events);
    }
}
