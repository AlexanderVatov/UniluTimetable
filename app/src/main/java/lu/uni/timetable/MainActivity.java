package lu.uni.timetable;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        System.err.println("MainActivity created!");
    }

    @SuppressLint("StaticFieldLeak")
    public void update(View view) {
//        final WeekView<Event> weekView = findViewById(R.id.weekView);
//        //textView.setText("Getting data...");
//        new AsyncTask<Void, Void, List<Event>>() {
//            @Override
//            protected List<Event> doInBackground( final Void ... params ) {
//                try {
//                    GuichetEtudiant g = new GuichetEtudiant(new OkHttpBackend());
//                    g.authenticate(Credentials.username, Credentials.password);
//                    List<StudyProgram> programs = g.getStudyPrograms();
//                    //StringBuilder mainSB = new StringBuilder();
//                    ArrayList<StudyProgram> mainPrograms = new ArrayList<>();
//                    for (StudyProgram p: programs) {
//                        if(p.isMain) {
//                            mainPrograms.add(p);
//                            //mainSB.append(p.title + "\n");
//                        }
//
//                    }
//
//                    Calendar c = Calendar.getInstance();
//                    c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY); //Last Monday
//                    c.set(Calendar.HOUR_OF_DAY, 0);
//                    c.set(Calendar.MINUTE, 0);
//                    c.set(Calendar.SECOND, 0);
//                    c.roll(Calendar.WEEK_OF_YEAR, true); //Next Monday
//                    Date nextMonday = c.getTime();
//                    c.roll(Calendar.WEEK_OF_YEAR, true);
//                    Date followingMonday = c.getTime();
//                    List<GEEvent> events = g.update(nextMonday, followingMonday, mainPrograms);
//                    System.err.println(events.size() + " events loaded.");
////                    StringBuilder timetable = new StringBuilder();
////                    for (int i = 0; i < events.size(); ++i) {
////                        GEEvent e = events.get(i);
////                        timetable.append((i + 1) + ". " + e.subject + "\n");
////                        timetable.append("   " + e.lecturer);
////                        timetable.append("   " + e.eventType);
////                        timetable.append("   " + e.room);
////                    }
////
////                    StringBuilder s = new StringBuilder();
////                    s.append("Your name: " + g.getStudentName() + "\n\n");
////                    s.append("Your course of study:\n\n");
////                    s.append(mainSB.toString());
////                    s.append("\nYour timetable next Monday (" + nextMonday.toString() + "):\n\n");
////                    s.append(timetable.toString());
////
////                    return s.toString();
//                    List<Event> displayableEvents = new ArrayList<Event>(events.size());
//                    for(GEEvent e: events) displayableEvents.add(new Event(e));
//                    return displayableEvents;
//
//                }
//                catch (Exception e) {
//                    e.printStackTrace();
//                    return new ArrayList<Event>();
//                }
//            }
//
//            @Override
//            protected void onPostExecute( final List<Event> displayableEvents ) {
//                // continue what you are doing...
//                //textView.setText(result);
//                weekView.submit(displayableEvents);
//            }
//        }.execute();
        TimetableFragment fragment = (TimetableFragment) getSupportFragmentManager().findFragmentById(R.id.timetableFragment);
        fragment.databaseUpdate();
    }
}
