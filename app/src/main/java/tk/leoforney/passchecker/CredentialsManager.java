package tk.leoforney.passchecker;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.apache.commons.codec.binary.Base64;

import java.io.UnsupportedEncodingException;

import androidx.annotation.NonNull;

class CredentialsManager {

    private SharedPreferences pref;
    private static CredentialsManager instance = null;
    private Base64 base64;
    private Context context;
    TextView navEmail, navName;

    static CredentialsManager getInstance(Context context) {
        if (instance == null) {
            instance = new CredentialsManager(context);
        }
        return instance;
    }

    CredentialsManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(getClass().getCanonicalName(), Context.MODE_PRIVATE);
        base64 = new Base64();
    }

    String getToken() {
        return pref.getString("token", "");
    }

    String getName() {
        return pref.getString("name", "");
    }

    String getEmail() {
        return pref.getString("email", "");
    }

    boolean alreadyExists() {
        return !(getToken().equals("")) && !(getName().equals(""));
    }

    void setDisplayData(MainActivity mainActivity) {
        Log.d("CredentialsManager", "Display data requested");

        View headerLayout = mainActivity.navigationView.getHeaderView(0); // 0-index header

        navName = headerLayout.findViewById(R.id.nav_name);
        navEmail = headerLayout.findViewById(R.id.nav_email);

        if (alreadyExists()) {
            Log.d(CredentialsManager.class.getName(), getName() + " : " + getEmail());
            navName.setText(getName());
            navEmail.setText(getEmail());
        }
    }

    void setData(@NonNull String name, @NonNull String token) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("name", name);
        editor.putString("token", token);
        String tokenDecoded = null;
        try {
            tokenDecoded = new String(Base64.decodeBase64(token.getBytes("UTF-8")), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String[] split = tokenDecoded.split(":");
        editor.putString("email", split[0]);
        editor.apply();
    }

    void clearData() {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("name", null);
        editor.putString("token", null);
        editor.apply();
    }

    void setIP(@NonNull String ip) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("ip", ip);
        editor.apply();
        Log.d("CredentialsManager", "IP set to: " + getIP());
    }

    String getIP() {
        return pref.getString("ip", context.getResources().getString(R.string.server_url));
    }
}
