package fr.dabernat.dimchat.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import fr.dabernat.dimchat.R;
import fr.dabernat.dimchat.model.CurrentUser;
import fr.dabernat.dimchat.server.OnServiceListener;
import fr.dabernat.dimchat.server.ServiceInterface;


public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final SharedPreferences prefs = this.getSharedPreferences(
                "fr.dabernat.dimchat", Context.MODE_PRIVATE);

        final CurrentUser currentUser = new CurrentUser();

        if(prefs.contains("token")) {
            currentUser.setPseudo(prefs.getString("username", ""));
            currentUser.setPassword(prefs.getString("password", ""));
            currentUser.setToken(prefs.getString("token", ""));
            Log.w(TAG, currentUser.toString());
            connect(prefs, currentUser);
        }

        Button btConnexion = (Button) findViewById(R.id.btConnexion);

        btConnexion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText etPseudo = (EditText) findViewById(R.id.etPseudo);
                EditText etPassword = (EditText) findViewById(R.id.etPassword);

                currentUser.setPseudo(etPseudo.getText().toString());
                currentUser.setPassword(etPassword.getText().toString());

                connect(prefs, currentUser);
            }
        });


    }

    private void connect(final SharedPreferences prefs, final CurrentUser currentUser) {
        if( !currentUser.getPseudo().isEmpty() && !currentUser.getPassword().isEmpty()) {
            HashMap<String, String> params = new HashMap<>();
            params.put("username", currentUser.getPseudo());
            params.put("password", currentUser.getPassword());

            prefs.edit().putString("username", currentUser.getPseudo()).apply();
            prefs.edit().putString("password", currentUser.getPassword()).apply();

            ServiceInterface serviceInterface = new ServiceInterface("POST", "messaging/", "connect", params);
            serviceInterface.setOnServiceListener(new OnServiceListener() {
                @Override
                public void onResult(String response) {
                    try {
                        JSONObject json = new JSONObject(response);
                        int statusCode = Integer.parseInt(json.get("code").toString());
                        if (statusCode == 200) {
                            Log.w(TAG, "onResult: " + response );
                            prefs.edit().putString("token", json.get("accesstoken").toString()).apply();
                            currentUser.setToken(json.get("accesstoken").toString());
                            Intent channelListIntent = new Intent(LoginActivity.this, ChannelListActivity.class);
                            channelListIntent.putExtra("currentUser", currentUser);
                            startActivity(channelListIntent);
                            finish();
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
