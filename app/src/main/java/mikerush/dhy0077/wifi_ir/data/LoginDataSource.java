package mikerush.dhy0077.wifi_ir.data;

import android.util.Log;

import mikerush.dhy0077.wifi_ir.MyApp;
import mikerush.dhy0077.wifi_ir.data.model.LoggedInUser;

import java.io.IOException;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {
    private final String TAG="LoginDataSource";

    public Result<LoggedInUser> login(String username, String password) {

        try {

            // TODO: handle loggedInUser authentication
            if (1==1) {
                LoggedInUser User =
                        new LoggedInUser(
                                java.util.UUID.randomUUID().toString(),
                                username);
                Log.i(TAG,"Log In Successful");
                return new Result.Success<>(User);
            } else {
                Log.i(TAG,"Log In Failed");
                return new Result.Error(new Exception("MQTT Login Error"));
            }
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public void logout() {
        // TODO: revoke authentication
    }
}