package lu.uni.timetable;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import lu.uni.avatov.guichetetudiant.Credentials;
import lu.uni.avatov.guichetetudiant.GuichetEtudiant;
import lu.uni.avatov.guichetetudiant.OkHttpBackend;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void getEvents(View view) {
        final TextView textView = (TextView) findViewById(R.id.textView);
        textView.setText("Getting data...");
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground( final Void ... params ) {
                // a methods takes very long time
                try {
                    GuichetEtudiant g = new GuichetEtudiant(new OkHttpBackend());
                    g.authenticate(Credentials.username, Credentials.password);
                    List<GuichetEtudiant.StudyProgram> programs = g.getStudyPrograms();
                    StringBuilder main = new StringBuilder(), nonMain = new StringBuilder();
                    for (GuichetEtudiant.StudyProgram p: programs) {
                        if(p.isMain) {
                            main.append(p.title + "\n");
                        }
                        else {
                            nonMain.append(p.title + "\n");
                        }
                    }

                    StringBuilder s = new StringBuilder();
                    s.append("You probably study:\n\n");
                    s.append(main.toString());
                    s.append("\nbut perhaps also follow courses from:\n\n");
                    s.append(nonMain.toString());

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
