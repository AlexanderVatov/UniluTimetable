package lu.uni.avatov.guichetetudiant;

import java.net.Authenticator;
import java.net.PasswordAuthentication;


public class GEAuthenticator extends Authenticator {

    protected String username = "", password = "";

    public GEAuthenticator() {

    }

    public GEAuthenticator(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public void setCredentials(String username, String password) {
        System.err.println("Setting credentials...");
        this.username = username;
        this.password = password;
    }


    public PasswordAuthentication getPasswordAuthentication() {
        if(username.isEmpty()) {
            System.err.println("GEAuthenticator: No credentials to provide!");
            return null;
        }
        if(getRequestingHost().equals(GuichetEtudiant.hostname) && getRequestorType() == RequestorType.SERVER) {
            System.err.println("GEAuthenticator: Providing " + getRequestingScheme() + " credentials");
            return (new PasswordAuthentication(username, password.toCharArray()));
        }
        else {
            System.err.println("GEAuthenticator: Unknown system, not providing credentials.");
            System.err.println("   (hostname: " + getRequestingHost() + ", scheme: " + getRequestingScheme() + ").");
            return null;
        }
    }
}

