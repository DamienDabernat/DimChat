package fr.dabernat.dimchat;

import android.app.Application;
import android.content.Context;

public class ApplicationManager extends Application {

    private static Context context;

    public void onCreate() {
        super.onCreate();
        ApplicationManager.context = getApplicationContext();
    }

    public static Context getContext() {
        return ApplicationManager.context;
    }
}
