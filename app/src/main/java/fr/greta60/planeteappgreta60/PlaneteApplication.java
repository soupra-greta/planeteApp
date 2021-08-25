package fr.greta60.planeteappgreta60;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class PlaneteApplication extends Application {
    @Override
    public void onCreate()
    {
        super.onCreate();
        //initialisation de la BDD
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .allowWritesOnUiThread(true)
                .build();
        Realm.setDefaultConfiguration(config);


    }
}
