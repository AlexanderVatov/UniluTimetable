package lu.uni.avatov.guichetetudiant;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class GuichetEtudiant {
    public interface NetworkBackend {
        public String get(String urlSuffix) throws GEError;

        public String post(String urlSuffix, ParametersMultimap parameters) throws GEError;

        public void setCredentials(String username, String password);
    }

    protected static final String hostname = "inscription.uni.lu";
    //If the URL prefix ever needs to be changed, make sure it ends with a slash
    protected static final String urlPrefix = "https://inscription.uni.lu/Inscriptions/Student/GuichetEtudiant/";
    protected String token;
    protected NetworkBackend networkBackend;

    public NetworkBackend getNetworkBackend() {
        return networkBackend;
    }

    public void setNetworkBackend(NetworkBackend networkBackend) {
        this.networkBackend = networkBackend;
    }

    public GuichetEtudiant(NetworkBackend backend) {
        this.networkBackend = backend;
    }

    public void authenticate(String username, String password) throws GEError {
        networkBackend.setCredentials(username, password);
        String html = networkBackend.get("Agenda");
        Document doc = Jsoup.parse(html);
        token = doc.select("[name=__RequestVerificationToken]").first().attr("value");
        System.err.println("GuichetEtudiant: successfully obtained token!");
    }

    public String getStudentName() throws GEError {
        if (token == null) throw new GEAuthenticationError("No credentials provided!");

        ParametersMultimap parameters = new ParametersMultimap();
        parameters.put("__RequestVerificationToken", token);

        return networkBackend.post("getInfosEtudiant", parameters);
    }

    public List<StudyProgram> getStudyPrograms() throws GEError {
        if (token == null) throw new GEAuthenticationError("No credentials provided!");

        ParametersMultimap params = new ParametersMultimap();
        params.put("__RequestVerificationToken", token);

        try {
            JSONArray inArray = new JSONArray(networkBackend.post("getStudentFormation", params));
            List<StudyProgram> outArray = new ArrayList<StudyProgram>(inArray.length());
            for (int i = 0; i < inArray.length(); ++i) {
                JSONObject object = inArray.getJSONObject(i);
                StudyProgram s = new StudyProgram();
                s.id = object.optString("idForm", "");
                s.title = object.optString("libelle", "");
                s.academicYear = object.optString("AA", "");
                s.semester = object.optString("Sem", "");
                s.isMain = object.optBoolean("principal", false);
                outArray.add(s);
            }
            return outArray;
        } catch (JSONException e) {
            throw new GEError("Guichet Étudiant: JSON Exception: " + e.getMessage());
        }
    }

    public List<GEEvent> getEvents(Date startDate, Date endDate, List<String> studyProgramIds) throws GEError {
        if (token == null) throw new GEAuthenticationError("No credentials provided!");

        //Prepare parameters
        //Erase time information; GuichetEtudiant does not distinguish it anyway
        DateFormat requestDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        requestDateFormat.setTimeZone(TimeZone.getTimeZone("Europe/Luxembourg"));
        DateFormat responseDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        responseDateFormat.setTimeZone(TimeZone.getTimeZone("Europe/Luxembourg"));


        ParametersMultimap parameters = new ParametersMultimap();
        for (String s : studyProgramIds)
            parameters.put("formations", s);
        parameters.put("start", requestDateFormat.format(startDate));
        parameters.put("end", requestDateFormat.format(endDate));
        parameters.put("groupFilter", "all");
        parameters.put("__RequestVerificationToken", token);

        //Make request and parse JSON
        try {
            JSONArray inArray  = new JSONArray(networkBackend.post("GetEventInPeriode", parameters));

            int length = inArray.length();
            ArrayList<GEEvent> outArray = new ArrayList<GEEvent>(length);
            for (int i = 0; i < length; ++i) {
                JSONObject o = inArray.getJSONObject(i);
                GEEvent e = new GEEvent();

                e.start = responseDateFormat.parse(o.getString("DateDebut"));
                e.end = responseDateFormat.parse(o.getString("DateFin"));
                e.id = o.getString("Id");
                e.subject = o.optString("Cours", "");
                e.subjectId = o.optString("IdCours", "");
                e.title = o.optString("Title", "");
                e.lecturer = o.optString("Enseignant", "");
                e.eventType = o.optString("TypeCPE", "");
                e.isCanceled = o.optBoolean("IsCancelled", false);
                e.room = o.optString("Local", "");
                e.mainStudyProgramId = o.optString("IdFormPrincipal", "");

                outArray.add(e);
            }
            return outArray;
        } catch (JSONException e) {
            throw new GEError("Guichet Étudiant: JSON Exception: " + e.getMessage());
        } catch (ParseException e) {
            //Date was parsed incorrectly
            throw new GEError("Guichet Étudiant: Error parsing date:  " + e.getMessage());
        }
    }

    public List<GEEvent> getEvents(Date startDate, Date endDate, ArrayList<StudyProgram> studyProgramList) throws GEError {
        ArrayList<String> studyProgramIDs = new ArrayList<String>(studyProgramList.size());
        for (StudyProgram s : studyProgramList) studyProgramIDs.add(s.id);
        return getEvents(startDate, endDate, studyProgramIDs);
    }

}
