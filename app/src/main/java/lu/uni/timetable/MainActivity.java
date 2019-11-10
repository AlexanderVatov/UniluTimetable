package lu.uni.timetable;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import lu.uni.avatov.guichetetudiant.Credentials;
import lu.uni.avatov.guichetetudiant.Event;
import lu.uni.avatov.guichetetudiant.GuichetEtudiant;
import lu.uni.avatov.guichetetudiant.OkHttpBackend;
import lu.uni.avatov.guichetetudiant.StudyProgram;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @SuppressLint("StaticFieldLeak")
    public void getEvents(View view) {
        final TextView textView = (TextView) findViewById(R.id.textView);
        textView.setText("Getting data...");
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground( final Void ... params ) {
                try {
                    GuichetEtudiant g = new GuichetEtudiant(new OkHttpBackend());
                    g.authenticate(Credentials.username, Credentials.password);
                    List<StudyProgram> programs = g.getStudyPrograms();
                    StringBuilder mainSB = new StringBuilder();
                    ArrayList<StudyProgram> mainPrograms = new ArrayList<>();
                    for (StudyProgram p: programs) {
                        if(p.isMain) {
                            mainPrograms.add(p);
                            mainSB.append(p.title + "\n");
                        }

                    }

                    Calendar c = Calendar.getInstance();
                    c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY); //Last Monday
                    c.set(Calendar.HOUR_OF_DAY, 0);
                    c.set(Calendar.MINUTE, 0);
                    c.set(Calendar.SECOND, 0);
                    c.roll(Calendar.WEEK_OF_YEAR, true); //Next Monday
                    Date nextMonday = c.getTime();
                    //c.roll(Calendar.DAY_OF_YEAR, true);
                    //Date nextTuesday = c.getTime();
                    List<Event> events = g.getEvents(nextMonday, nextMonday, mainPrograms);
                    System.err.println(events.size() + " events loaded.");
                    StringBuilder timetable = new StringBuilder();
                    for (int i = 0; i < events.size(); ++i) {
                        Event e = events.get(i);
                        timetable.append((i + 1) + ". " + e.subject + "\n");
                        timetable.append("   " + e.lecturer);
                        timetable.append("   " + e.eventType);
                        timetable.append("   " + e.room);
                    }

                    StringBuilder s = new StringBuilder();
                    s.append("Your name: " + g.getStudentName() + "\n\n");
                    s.append("Your course of study:\n\n");
                    s.append(mainSB.toString());
                    s.append("\nYour timetable next Monday (" + nextMonday.toString() + "):\n\n");
                    s.append(timetable.toString());

                    return s.toString();
                }
                catch(Exception e){
                    return e.toString();
                }
            }

            @Override
            protected void onPostExecute( final String result ) {
                // continue what you are doing...
                textView.setText(result);
            }
        }.execute();
    }
}
