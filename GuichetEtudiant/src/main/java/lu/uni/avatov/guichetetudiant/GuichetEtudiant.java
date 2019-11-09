package lu.uni.avatov.guichetetudiant;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class GuichetEtudiant {
    public interface NetworkBackend {
        public String get(String urlSuffix) throws GEError;
        public String post(String urlSuffix, HashMap<String, String> parameters) throws GEError;
        public void setCredentials(String username, String password);
    }

    protected static final String hostname = "inscription.uni.lu";
    //If the URL prefix ever needs to be changed, make sure it ends with a slash
    protected static final String urlPrefix ="https://inscription.uni.lu/Inscriptions/Student/GuichetEtudiant/";
    protected String token;
    protected NetworkBackend networkBackend;

    public NetworkBackend getNetworkBackend() {
        return networkBackend;
    }

    public void setNetworkBackend(NetworkBackend networkBackend) {
        this.networkBackend = networkBackend;
    }

    public class StudyProgram {
        public String id;
        public String title;
        public String academicYear;
        public String semester;
        public Boolean isMain;
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

    public List<StudyProgram> getStudyPrograms() throws GEError {
        //JSONObject result = new JSONObject();
        HashMap<String, String> params = new HashMap<String, String>();
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
        }
        catch (JSONException e) {
            throw new GEError("Guichet Étudiant: JSON Exception: " + e.getMessage());
        }
    }
}
