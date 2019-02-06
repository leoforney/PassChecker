package tk.leoforney.passchecker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import androidx.annotation.ColorInt;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ThemeColors {

    private static final String NAME = "ThemeColors", KEY = "color";
    private Context context;
    private OkHttpClient client;

    @ColorInt
    public int color;

    public ThemeColors(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        String stringColor = sharedPreferences.getString(KEY, "004bff");
        color = Color.parseColor("#" + stringColor);

        this.context = context;

        client = new OkHttpClient.Builder()
                .writeTimeout(3L, TimeUnit.SECONDS)
                .build();

        if (isLightActionBar()) context.setTheme(R.style.AppTheme);
        context.setTheme(context.getResources().getIdentifier("T_" + stringColor, "style", context.getPackageName()));
    }

    public void setAppColors(Activity activity) {
        Request colorRequest = new Request.Builder()
                .get()
                .url("http://" + CredentialsManager.getInstance(context).getIP() + "/getProperty/primaryColor")
                .addHeader("Token", CredentialsManager.getInstance(context).getToken())
                .build();
        client.newCall(colorRequest).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseString = response.body().string();
                Log.d("ThemeColors", "Retrieved theme color " + responseString);
                //setNewThemeColor(activity, responseString);
            }
        });
    }

    /**
     * Set theme color from HEX
     *
     * @param activity Activity
     * @param hex      hex color (with or without #)
     */
    public static void setNewThemeColor(Activity activity, String hex) {
        SharedPreferences.Editor editor = activity.getSharedPreferences(NAME, Context.MODE_PRIVATE).edit();
        editor.putString(KEY, hex.replace("#", ""));
        editor.apply();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            activity.runOnUiThread(activity::recreate);
        else {
            Intent i = activity.getPackageManager().getLaunchIntentForPackage(activity.getPackageName());
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            activity.startActivity(i);
        }
    }

    public static void setNewThemeColor(Activity activity, int red, int green, int blue) {
        int colorStep = 15;
        red = Math.round(red / colorStep) * colorStep;
        green = Math.round(green / colorStep) * colorStep;
        blue = Math.round(blue / colorStep) * colorStep;

        String stringColor = Integer.toHexString(Color.rgb(red, green, blue)).substring(2);
        setNewThemeColor(activity, stringColor);
    }

    private boolean isLightActionBar() {// Checking if title text color will be black
        int rgb = (Color.red(color) + Color.green(color) + Color.blue(color)) / 3;
        return rgb > 210;
    }
}