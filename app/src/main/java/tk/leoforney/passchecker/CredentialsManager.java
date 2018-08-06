package tk.leoforney.passchecker;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

class CredentialsManager {

    private SharedPreferences pref;
    private static CredentialsManager instance = null;

    static CredentialsManager getInstance(Context context) {
        if (instance == null) {
            instance = new CredentialsManager(context);
        }
        return instance;
    }

    CredentialsManager(Context context) {
        pref = context.getSharedPreferences(getClass().getCanonicalName(), Context.MODE_PRIVATE);
    }

    String getToken() {
        return pref.getString("token", null);
    }

    String getName() {
        return pref.getString("name", null);
    }

    void setData(@NonNull String name, @NonNull String token) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("name", name);
        editor.putString("token", token);
        editor.apply();
    }

}
