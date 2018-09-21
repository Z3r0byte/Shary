package eu.z3r0byteapps.shary.Services;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.util.Date;

import eu.z3r0byteapps.shary.MagisterLibrary.Magister;
import eu.z3r0byteapps.shary.MagisterLibrary.container.School;
import eu.z3r0byteapps.shary.MagisterLibrary.container.User;
import eu.z3r0byteapps.shary.MagisterLibrary.util.HttpUtil;
import eu.z3r0byteapps.shary.MagisterLibrary.util.LogUtil;
import eu.z3r0byteapps.shary.MagisterLibrary.util.SchoolUrl;
import eu.z3r0byteapps.shary.SharyLibrary.Result;
import eu.z3r0byteapps.shary.SharyLibrary.Urls;
import eu.z3r0byteapps.shary.Util.ConfigUtil;
import eu.z3r0byteapps.shary.Util.DateUtils;
import eu.z3r0byteapps.shary.Util.JobUtil;

public class RenewSessionKey extends JobService {
    private static final String TAG = "Shary";

    ConfigUtil configUtil;
    JobParameters jobParameters;

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        JobUtil.scheduleJob(getApplicationContext());

        this.jobParameters = jobParameters;
        Context context = getApplicationContext();
        if (!isOnline(context)) {
            Log.e(TAG, "Session Renewal: Not connected to wifi, aborting");
            return false;
        }

        configUtil = new ConfigUtil(context);
        if (!configUtil.getBoolean("loggedIn", false)) return false;

        if (configUtil.getString("lastSessionUpdate", null) == null) {
            updateSession();
        } else {
            String dateStr = configUtil.getString("lastSessionUpdate", null);
            Date lastDate = DateUtils.parseDate(dateStr, "dd-MM-yyyy");
            Date now = DateUtils.parseDate(DateUtils.formatDate(DateUtils.getToday(), "dd-MM-yyyy"), "dd-MM-yyyy");
            if (lastDate.before(now)) {
                updateSession();
            } else {
                Log.d(TAG, "Session Renewal: Not at least one day ago, aborting");
            }
        }
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }

    private void updateSession() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Magister magister = login();
                if (magister == null) {
                    Log.e(TAG, "Session Renewal: Could not get magister, aborting");
                    return;
                }
                String profile = getProfile(magister);
                if (profile == null) {
                    Log.e(TAG, "Session Renewal: Could not get profile, aborting");
                    return;
                }
                Integer personID = magister.profile.id;
                String token = generateToken(profile);
                Boolean success = createUser(token, HttpUtil.getSessionToken(), personID);
                if (success) {
                    configUtil.setString("lastSessionUpdate", DateUtils.formatDate(new Date(), "dd-MM-yyyy"));
                    Log.d(TAG, "Session Renewal: Session succesfully updated");
                } else {
                    Log.e(TAG, "Session Renewal: Failed to update session, aborting");
                }
                onStopJob(jobParameters);
            }
        }).start();
    }

    private Boolean createUser(String token, String sessionid, Integer personID) {
        School school = new Gson().fromJson(configUtil.getString("School"), School.class);
        String schoolPrefix = school.url.substring(8, school.url.indexOf("."));
        eu.z3r0byteapps.shary.SharyLibrary.User user = new eu.z3r0byteapps.shary.SharyLibrary.User(sessionid, token, personID, schoolPrefix);
        try {
            InputStreamReader inputStreamReader = HttpUtil.httpPost(Urls.createUser, new Gson().toJson(user));
            Result result = new Gson().fromJson(LogUtil.getStringFromInputStream(inputStreamReader), Result.class);
            if (result.error == null) {
                configUtil.setString("sessionId", sessionid);
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Magister login() {
        School school = new Gson().fromJson(configUtil.getString("School"), School.class);
        User user = new Gson().fromJson(configUtil.getString("User"), User.class);
        try {
            return Magister.login(school, user.username, user.password);
        } catch (IOException e) {
            return null;
        }
    }

    private String getProfile(Magister magister) {
        SchoolUrl url = new SchoolUrl(magister.school);
        try {
            return LogUtil.getStringFromInputStream(HttpUtil.httpGet(url.getAccountUrl()));
        } catch (IOException e) {
            return null;
        }
    }

    private String generateToken(String profile) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(profile.getBytes("UTF-8"));
            StringBuffer hexString = new StringBuffer();

            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if (hex.length() == 1)
                    hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        //should check null because in airplane mode it will be null
        return (netInfo != null && netInfo.isConnected());
    }
}
