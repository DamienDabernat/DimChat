package fr.dabernat.dimchat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import fr.dabernat.dimchat.Model.User;
import fr.dabernat.dimchat.Server.ServiceInterface;


public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final User user = new User("ddabe", "damiendabernat");
        if( !user.getPseudo().isEmpty() && !user.getPassword().isEmpty()) {
            HashMap<String, String> params = new HashMap<>();
            params.put("username", user.getPseudo());
            params.put("password", user.getPassword());

            ServiceInterface serviceInterface = new ServiceInterface("POST", "messaging/", "connect", params);
            serviceInterface.setOnServiceListener(new OnServiceListener() {
                @Override
                public void onResult(String response) {
                    try {
                        JSONObject json = new JSONObject(response);
                        int statusCode = Integer.parseInt(json.get("code").toString());
                        if (statusCode == 200) {
                            user.setToken(json.get("accesstoken").toString());
                            user.toString();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            serviceInterface.execute();
        }
    }
}
