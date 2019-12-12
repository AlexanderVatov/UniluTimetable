package lu.uni.timetable;

import java.util.Collections;
import java.util.List;

import lu.uni.avatov.guichetetudiant.Credentials;

/**
 * Stores and retrieves user settings
 */
public class Settings {
    public Settings() {
        //Initialise settings
    }

    public static String username() {
        return Credentials.username;
    }

    public static String password() {
        return Credentials.password;
    }

    public static List<String> studyProgramIds() {
        return Collections.singletonList("0001o3032");
    }
}
