package lu.uni.timetable.data;

import java.io.IOException;

import lu.uni.avatov.guichetetudiant.GEError;
import lu.uni.avatov.guichetetudiant.GuichetEtudiant;
import lu.uni.timetable.App;
import lu.uni.timetable.data.model.LoggedInUser;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    public Result<LoggedInUser> login(String username, String password) {

        try {
            System.err.println("Attempting authentication...");
            // TODO: handle loggedInUser authentication
            GuichetEtudiant g = App.guichetEtudiant();
            g.authenticate(username, password);
//            LoggedInUser fakeUser =
//                    new LoggedInUser(
//                            java.util.UUID.randomUUID().toString(),
//                            "Jane Doe");

            LoggedInUser user = new LoggedInUser(username, g.getStudentName());
            return new Result.Success<>(user);
        } catch (GEError e) {
            System.err.println("Error authenticating");
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public void logout() {
        // TODO: revoke authentication
    }
}
