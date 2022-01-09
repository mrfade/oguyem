package dev.solak.oguyem;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.appcompat.app.AppCompatDelegate;

import dev.solak.oguyem.classes.API;
import dev.solak.oguyem.classes.User;

public class MainApplication extends Application {

    private static Context appContext;

    @Override
    public void onCreate() {
        super.onCreate();

        // set application context
        appContext = getApplicationContext();

        // set dark mode as default
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        // init api
        API.init();

        // init User
        User.init(getApplicationContext());
        // register device if its not registered
        if (!User.isRegistered()) {
            Log.d("USER", "not registered");
            User.autoRegister();
        }

    }

    public static Context getAppContext() {
        return appContext;
    }
}
