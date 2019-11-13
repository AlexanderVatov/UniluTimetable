package lu.uni.timetable;

import android.os.AsyncTask;

import java.util.Date;

public class AsyncUpdate extends AsyncTask<Void, Void, Boolean> {
    private Date start, end;
    //private WeakReference<AbstractViewModel> viewModel;

    public AsyncUpdate(Date startDate, Date endDate) {
        start = startDate;
        end = endDate;
    }


    @Override
    protected Boolean doInBackground(Void... emptiness) {
        System.err.println("ASyncUpdate: Running now...");
        Updater.update(start, end);
        return true;
    }

    @Override
    protected void onPostExecute(Boolean params) {
        TimetableApplication.getPresenter().updatePerformed(start, end);
    }
}
