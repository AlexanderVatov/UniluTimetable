package lu.uni.avatov.guichetetudiant;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.Authenticator;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

public class GEHTTPBackend implements GuichetEtudiant.NetworkBackend {
    public GEHTTPBackend() {
        authenticator = new GEAuthenticator();
        System.err.println("Setting default authenticator...");
        Authenticator.setDefault(authenticator);
        cookieManager = new CookieManager();
        this.urlPrefix = GuichetEtudiant.urlPrefix;
    }

    public GEHTTPBackend(String urlPrefix, String username, String password) {
        authenticator = new GEAuthenticator(username, password);
        System.err.println("Setting default authenticator...");
        Authenticator.setDefault(authenticator);
        cookieManager = new CookieManager();
        //CookieHandler.setDefault(cookieManager);
        this.urlPrefix = urlPrefix;
    }

    public String get (URL url) throws GEError {
        try {

            System.out.println("GET Request to: " + url.toString());


            HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                Authenticator.setDefault(authenticator);
                injectCookies(con); //Retrieve stored cookies and add them to the request

                con.connect();
                extractCookies(con); //Extract cookies and store them
                int status = con.getResponseCode();
                if(status == 200) {
                    //Read and return response
                    Reader in = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
                    StringBuilder response = new StringBuilder();
                    for (int ch; (ch = in.read()) >= 0; )
                        response.append((char) ch);

                    return response.toString();
                }
                else if (status == 401) {
                    throw new GEAuthenticationError("Authentication to Guichet Étudiant server was unsuccessful!");
                }
                else {
                    throw new GEError("Guichet Étudiant Error: Status code " + status);
                }

        }
        catch (IOException e) {
            throw new GEError("Guichet Étudiant: An IOException occurred: " + e.toString());
        }
    }

    @Override
    public String get(String urlSuffix) throws GEError {
        try {
            return get(new URL(urlPrefix + urlSuffix));
        }
        catch(MalformedURLException e) {
            throw new GEError("Guichet Étudiant Error: Malformed URL");
        }
    }

    public String post(URL url, ParametersMultimap parameters) throws GEError {
        try {
            //URL-encode params into a single "&"-separated string
            StringBuilder data = new StringBuilder();
            for (ParametersMultimap.Entry entry : parameters) {
                if (data.length() != 0) data.append('&');
                data.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                data.append("=");
                data.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            }

            System.out.println("POST Request to: " + url.toString());
            System.out.println("Data:\n\"" + data.toString() + "\"\n");

            byte[] dataBytes = data.toString().getBytes("UTF-8"); //Bytes to send to server

            //Send the bytes and make the request
            HttpURLConnection c = (HttpURLConnection) url.openConnection();
            Authenticator.setDefault(authenticator);
            c.setRequestMethod("POST");
            c.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            c.setRequestProperty("Content-Length", String.valueOf(dataBytes.length));
            injectCookies(c); //Retrieve stored cookies and add them to the request
            c.setDoOutput(true);
            c.connect();
            c.getOutputStream().write(dataBytes);
            c.getOutputStream().close();

            extractCookies(c); //Extract cookies and store them

            //Handle response
            int status = c.getResponseCode();
            if (status == 200) {
                //Read and return response
                Reader in = new BufferedReader(new InputStreamReader(c.getInputStream(), "UTF-8"));
                StringBuilder response = new StringBuilder();
                for (int ch; (ch = in.read()) >= 0; )
                    response.append((char) ch);
                return response.toString();

            } else if (status == 401) {
                throw new GEAuthenticationError("Authentication to Guichet Étudiant server was unsuccessful!");

            }
            else {
                throw new GEError("Guichet Etudiant Error: Status code " + status);
            }
        }
        catch (IOException e) {
            throw new GEError("Guichet Étudiant: An IOException occurred: " + e.toString());
        }
    }
    @Override
    public String post(String urlSuffix, ParametersMultimap parameters) throws GEError {
        try {
            return post(new URL(urlPrefix + urlSuffix), parameters);
        }
        catch(MalformedURLException e) {
            throw new GEError("Guichet Étudiant Error: Malformed URL");
        }
    }

    @Override
    public void setCredentials(String username, String password) {
        authenticator.setCredentials(username, password);
    }

    protected void extractCookies(HttpURLConnection con) {
        Map<String, List<String>> headerFields = con.getHeaderFields();
        List<String> cookiesHeader = headerFields.get("Set-Cookie");

        if (cookiesHeader != null) {
            for (String cookie : cookiesHeader) {
                cookieManager.getCookieStore().add(null, HttpCookie.parse(cookie).get(0));
            }
        }
    }

    protected void injectCookies(HttpURLConnection con) {
        StringBuilder cookiesHeader = new StringBuilder();
        Boolean first = true;
        for (HttpCookie cookie: cookieManager.getCookieStore().getCookies()) {
            if (!first) {
                cookiesHeader.append(";");
            }
            first = false;
            cookiesHeader.append(cookie.toString());
        }
        con.setRequestProperty("Cookie",cookiesHeader.toString());
    }

    public GEAuthenticator getAuthenticator() {
        return authenticator;
    }

    public void setAuthenticator(GEAuthenticator authenticator) {
        this.authenticator = authenticator;
        Authenticator.setDefault(authenticator);
    }


    public String getUrlPrefix() {
        return urlPrefix;
    }

    public void setUrlPrefix(String urlPrefix) {
        this.urlPrefix = urlPrefix;
    }

    public CookieManager getCookieManager() {
        return cookieManager;
    }

    public void setCookieManager(CookieManager cookieManager) {
        this.cookieManager = cookieManager;
    }

    private String urlPrefix;
    private GEAuthenticator authenticator;
    private CookieManager cookieManager;

}
