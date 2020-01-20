package lu.uni.avatov.guichetetudiant;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * An interface with the Guichet Étudiant online service. Only a subset of the functionality of the
 * service is supported, namely retrieving a student's name, the study programs they may be enrolled
 * in (represented by {@link lu.uni.avatov.guichetetudiant.GEStudyProgram}), including a "main" one,
 * and a list of events (represented by {@link lu.uni.avatov.guichetetudiant.GEEvent}) with information
 * including time, subject, room, lecturers, etc. Before attempting to retrieve any information, the
 * authenticate method needs to be called with the credentials of a registered student. Attempting
 * to retrieve information before calling the authenticate method will result in a
 * {@link lu.uni.avatov.guichetetudiant.GEAuthenticationError}. All errors other than authentication
 * errors will result in a {@link lu.uni.avatov.guichetetudiant.GEError}. Since authentication requires
 * three round-trips to the server (two for NLTM authentication and an additional one to obtain a
 * request verification token), whereas subsequent API calls only take one round-trip, it is
 * recommended to reuse GuichetEtudiant instances. The method isAuthenticated() can be used to check
 * whether an instance is authenticated, and authenticate if necessary.
 *
 * This class handles the higher-level interactions with the service (making requests). Lower-level
 * details, such as forming the requests' headers and bodies, storing cookies (which are essential
 * for the proper functioning of this class), and NLTM authentication, are handled by another class,
 * which implements the {@link lu.uni.avatov.guichetetudiant.GuichetEtudiant.NetworkBackend} interface.
 * Two implementations of this interface or provided in this module:
 * {@link lu.uni.avatov.guichetetudiant.GEHTTPBackend}, which only uses JDK 7 (but for some reason the
 * NLTM authentication fails on Android), and {@link lu.uni.avatov.guichetetudiant.OkHttpBackend},
 * which uses the OkHttp library. The network backend needs to be passed as an argument to the
 * constructor or set using the setNetworkBackend method; it can be accessed using getNetworkBackend.
 * If the backend is changed after authentication, re-authentication is necessary.
 *
 * Note for Uni.lu Timetable: for the reasons laid out above, a single GuichetEtudiant instance is
 * reused throughout the application. This instance should be obtained via App.guichetEtudiant()
 * instead of instantiating this class directly. Also note that GuichetEtudiant works synchronously,
 * so it should not be used on the UI thread.
 */

public class GuichetEtudiant {
    /**
     * A backend for the lower-level networking functionality required by {@link lu.uni.avatov.guichetetudiant.GuichetEtudiant}.
     * Two implementations of this interface or provided in this module:
     * {@link lu.uni.avatov.guichetetudiant.GEHTTPBackend}, which only uses JDK 7 (but for some reason the
     * NLTM authentication fails on Android), and {@link lu.uni.avatov.guichetetudiant.OkHttpBackend},
     * which uses the OkHttp library. Details handled by the network backend include forming the requests'
     * headers and bodies, storing cookies (which are essential for the proper functioning of
     * GuichetEtudiant), NLTM authentication, and re-authenticating after a timeout.
     */
    public interface NetworkBackend {
        /**
         * Should perform an HTTP GET request to a given url.
         * @param url The URL address.
         * @return The body of the response, if the request is successful.
         * @throws GEError In case of a general error
         * @throws GEAuthenticationError In case of an authentication error
         */
        String get(String url) throws GEError;

        /**
         * Should perform an HTTP POST request to a given url.
         * @param url The URL address.
         * @param parameters The parameters to the request
         * @return The body of the response, if the request is successful.
         * @throws GEError In case of a general error
         * @throws GEAuthenticationError In case of an authentication error
         */
        String post(String url, ParametersMultimap parameters) throws GEError;

        /**
         * Should store the credentials. This should not in general require a web access.
         */
        void setCredentials(String username, String password);
    }

    /**
     * The host name of the Guichet Étudiant
     */
    protected static final String hostname = "inscription.uni.lu";
    /**
     * The URL prefix of the Guichet Étudiant. All URLs are formed by adding a suffix. The prefix
     * should end with a slash.
     */
    private static final String urlPrefix = "https://inscription.uni.lu/Inscriptions/Student/GuichetEtudiant/";
    protected String token;
    protected NetworkBackend networkBackend;

    /**
     * Retrieves the NetworkBackend.
     */
    public NetworkBackend getNetworkBackend() {
        return networkBackend;
    }

    /**
     * Sets the NetworkBackend. If the new backend is different, re-authentication will be necessary.
     * @param networkBackend Should never be null.
     */
    public void setNetworkBackend(NetworkBackend networkBackend) {
        if(networkBackend != this.networkBackend) token = null;
        this.networkBackend = networkBackend;
    }

    public GuichetEtudiant(NetworkBackend backend) {
        this.networkBackend = backend;
    }

    /**
     * Authenticate using the provided username and password. This requires three round-trips.
     * @throws GEError In case of a network, parsing, or general error.
     * @throws GEAuthenticationError In case of an authentication error.
     */
    public void authenticate(String username, String password) throws GEError {
        if (username.equals("")) {
            throw new GEAuthenticationError("GuichetEtudiant: blank username!");
        }

        networkBackend.setCredentials(username, password);
        String html = networkBackend.get(urlPrefix + "Agenda");
        Document doc = Jsoup.parse(html);
        /*
        A request verification token is included as a hidden input field in every page. This token
        must then be given as a parameter in every request to the server. This is used to prevent
        cross-site forgery attacks. This token is also stored as a cookie (both are necessary, and
        the lack of either causes requests to the Guichet Étudiant to be rejected with an HTTP 500
        (Internal Server Error) response.
        */

        if(doc.selectFirst("[name=username]") != null) {
            //We've been served the f5 BIG-IP APM login page
            System.err.println("Attempting f5 BIG-IP APM login...");
            ParametersMultimap parameters = new ParametersMultimap();
            parameters.put("username", username);
            parameters.put("password", password);
            doc = Jsoup.parse(networkBackend.post("https://inscription.uni.lu/my.policy",parameters));
            //Now we are either on the Guichet Étudiant page, or on an intermediate page from which
            //a "dummy" value must be extracted which can then be used to proceed to the Guichet
            Element dummy = doc.selectFirst("[name=dummy]");
            if(dummy==null) {
                //We're not on the intermediate page, so we must be on the Guichet Étudiant page
                extractToken(doc);
                return;
            }
            parameters = new ParametersMultimap();
            parameters.put("dummy",dummy.attr("value"));
            //Now we must be on the Guichet Étudiant page
            doc = Jsoup.parse(networkBackend.post(urlPrefix + "Agenda",parameters));
            extractToken(doc);
        }
        else {
            //Since we were not served the login page, we must be on the Guichet Étudiant page
            extractToken(doc);
        }

    }

    private void extractToken(Document doc) throws GEError {
        Element tokenElement = doc.selectFirst("[name=__RequestVerificationToken]");
        if(tokenElement!= null) {
            String extractedToken = tokenElement.attr("value");
            if(!extractedToken.isEmpty()) {
                token = extractedToken;
                System.err.println("GuichetEtudiant: successfully obtained token!");
                return;
            }
        }
        //Either the token element does not exist, it has no value, or its value is empty
        System.err.println("Error obtaining token from Guichet Étudiant response:");
        System.err.println(doc.html());
        throw new GEError("Error obtaining token from Guichet Étudiant response");
    }

    /**
     * Used to check whether authentication has already been successfully performed on this instance
     * since the object was created (or, if its backend has been changed, since the last change).
     * @return true if authenticated, false otherwise.
     */
    public boolean isAuthenticated() {
        return (token != null);
    }

    /**
     * Returns the student name in the format it is provided in by the Guichet Étudiant (title case).
     * @return The name of the student currently logged in.
     * @throws GEError In case of a network, parsing, or general error.
     * @throws GEAuthenticationError In case of an authentication error.
     */
    public String getStudentName() throws GEError {
        if (token == null) throw new GEAuthenticationError("No credentials provided!");

        ParametersMultimap parameters = new ParametersMultimap();
        parameters.put("__RequestVerificationToken", token);

        return networkBackend.post(urlPrefix + "getInfosEtudiant", parameters);
    }

    /**
     * Returns a list of student programs the student may be enrolled in.
     * @return A java.util.List of {@link lu.uni.avatov.guichetetudiant.GEStudyProgram} objects
     * @throws GEError In case of a network, parsing, or general error.
     * @throws GEAuthenticationError In case of an authentication error.
     */
    public List<GEStudyProgram> getStudyPrograms() throws GEError {
        if (token == null) throw new GEAuthenticationError("No credentials provided!");

        ParametersMultimap params = new ParametersMultimap();
        params.put("__RequestVerificationToken", token); //Use the token obtained during authentication

        try {
            JSONArray inArray = new JSONArray(networkBackend.post(urlPrefix + "getStudentFormation", params));
            List<GEStudyProgram> outArray = new ArrayList<GEStudyProgram>(inArray.length());
            for (int i = 0; i < inArray.length(); ++i) {
                JSONObject object = inArray.getJSONObject(i);
                GEStudyProgram s = new GEStudyProgram();
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

    /**
     * Returns a list of {@link lu.uni.avatov.guichetetudiant.GEEvent}'s for the specified date range.
     * Note that only the time parameters of the passed Date objects are not considered; events
     * statrting from 00:00 on startDate up to 23:59 on endDate will be considered. This mirrors the
     * behaviour of Guichet Étudiant, but is enforced here in order to prevent changes in the behaviour
     * of software using this library in case this is changed on the side of the Guichet Ëtudiant.
     *
     * @param startDate The first day of the time period for which events are to be fetched.
     * @param endDate The last day of the time period for which events are to be fetched.
     * @param studyProgramIds A list of strings with the ID of every study program for which events are to be fetched.
     * @return A list of {@link lu.uni.avatov.guichetetudiant.GEEvent}'s
     * @throws GEError In case of a network, parsing, or general error.
     * @throws GEAuthenticationError In case of an authentication error.
     */
    public List<GEEvent> getEvents(Date startDate, Date endDate, List<String> studyProgramIds) throws GEError {
        if (token == null) throw new GEAuthenticationError("No credentials provided!");

        //Prepare parameters
        //Erase time information; GuichetEtudiant does not distinguish it anyway
        //If Android Studio complains about SimpleDateFormat
        DateFormat requestDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'00:00:00");
        requestDateFormat.setTimeZone(TimeZone.getTimeZone("Europe/Luxembourg"));
        DateFormat responseDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        responseDateFormat.setTimeZone(TimeZone.getTimeZone("Europe/Luxembourg"));


        ParametersMultimap parameters = new ParametersMultimap();
        for (String s : studyProgramIds)
            parameters.put("formations", s);
        parameters.put("start", requestDateFormat.format(startDate));
        parameters.put("end", requestDateFormat.format(endDate));
        parameters.put("groupFilter", "all");
        parameters.put("__RequestVerificationToken", token); //Use the token obtained during authentication

        //Make request and parse JSON
        String json="";
        try {
            json = networkBackend.post(urlPrefix + "GetEventInPeriode", parameters);
            JSONArray inArray  = new JSONArray(json);

            int length = inArray.length();
            ArrayList<GEEvent> outArray = new ArrayList<GEEvent>(length);
            for (int i = 0; i < length; ++i) {
                JSONObject o = inArray.getJSONObject(i);
                GEEvent e = new GEEvent();

                //These parameters are essential. The omission of any of them should cause a failure.
                e.start = responseDateFormat.parse(o.getString("DateDebut"));
                e.end = responseDateFormat.parse(o.getString("DateFin"));
                e.id = o.getString("Id");

                //These parameters are less critical. A default value is therefore accepted in case
                //of omission.
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
            System.err.println("Guichet Étudiant: JSON Exception: " + e.getMessage());
            System.err.println("Server response:");
            System.err.println(json);
            throw new GEError("Guichet Étudiant: JSON Exception: " + e.getMessage());
        } catch (ParseException e) {
            //Date was parsed incorrectly
            throw new GEError("Guichet Étudiant: Error parsing date:  " + e.getMessage());
        }
    }

    /**
     * Returns a list of {@link lu.uni.avatov.guichetetudiant.GEEvent}'s for the specified date range.
     * Note that only the time parameters of the passed Date objects are not considered; events
     * statrting from 00:00 on startDate up to 23:59 on endDate will be considered. This mirrors the
     * behaviour of Guichet Étudiant, but is enforced here in order to prevent changes in the behaviour
     * of software using this library in case this is changed on the side of the Guichet Ëtudiant.
     *
     * @param startDate The first day of the time period for which events are to be fetched.
     * @param endDate The last day of the time period for which events are to be fetched.
     * @param studyProgramList A list of study programs for which events are to be fetched.
     * @return A list of {@link lu.uni.avatov.guichetetudiant.GEEvent}'s
     * @throws GEError In case of a network, parsing, or general error.
     * @throws GEAuthenticationError In case of an authentication error.
     */
    public List<GEEvent> getEvents(Date startDate, Date endDate, ArrayList<GEStudyProgram> studyProgramList) throws GEError {
        ArrayList<String> studyProgramIDs = new ArrayList<String>(studyProgramList.size());
        for (GEStudyProgram s : studyProgramList) studyProgramIDs.add(s.id);
        return getEvents(startDate, endDate, studyProgramIDs);
    }

}
