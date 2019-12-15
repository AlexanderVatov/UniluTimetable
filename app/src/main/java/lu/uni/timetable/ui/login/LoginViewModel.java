package lu.uni.timetable.ui.login;

import android.content.SharedPreferences;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import lu.uni.avatov.guichetetudiant.GEError;
import lu.uni.avatov.guichetetudiant.GuichetEtudiant;
import lu.uni.timetable.App;
import lu.uni.timetable.R;
import lu.uni.timetable.Settings;
import lu.uni.timetable.data.LoginRepository;

public class LoginViewModel extends ViewModel {

    private MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
    private LoginRepository loginRepository;

    LoginViewModel(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    public void login(String username, String password) {
        // can be launched in a separate asynchronous job
//        Result<LoggedInUser> result = loginRepository.login(username, password);
//
//        if (result instanceof Result.Success) {
//            LoggedInUser data = ((Result.Success<LoggedInUser>) result).getData();
//            loginResult.setValue(new LoginResult(new LoggedInUserView(data.getDisplayName())));
//        } else {
//            loginResult.setValue(new LoginResult(R.string.login_failed));
//        }
        new AsyncLogin(username, password).execute();
    }

    public void loginDataChanged(String username, String password) {
        if (!isUserNameValid(username)) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_username, null));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_password));
        } else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }

    // A placeholder username validation check
    private boolean isUserNameValid(String username) {
        return username!= null & username.matches("[a-zA-Z0-9]+");
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && !password.isEmpty();
    }

    private class AsyncLogin extends AsyncTask<Void, Void, Boolean> {
        protected String username, password, fullName;
        AsyncLogin(String username, String password) {
            this.username = username;
            this.password = password;
        }

        @Override
        protected Boolean doInBackground(Void... emptiness) {
            try {
                System.err.println("Attempting authentication...");
                GuichetEtudiant g = App.guichetEtudiant();
                g.authenticate(username, password);
                fullName = g.getStudentName();
                return true;

            } catch (GEError e) {
                System.err.println("Error authenticating");
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean successful) {
            if(successful) {
                loginResult.setValue(
                        new LoginResult(new LoggedInUserView(fullName))
                );
                SharedPreferences.Editor e = Settings.preferences().edit();
                e.putBoolean(Settings.USER_LOGGED_IN, true);
                e.putBoolean(Settings.MAIN_UPDATE_NEEDED, true);
                e.apply();
            }
            else {
                loginResult.setValue(new LoginResult(1));
            }
        }
    }
}
