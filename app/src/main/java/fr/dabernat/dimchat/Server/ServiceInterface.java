package fr.dabernat.dimchat.server;

import java.util.HashMap;

public class ServiceInterface {

    public static final String LOG_TAG = "ServerInterface";
    private static final String SERVER_URL = "http://raphaelbischof.fr/";

    private String method;
    private String function;
    private String query;
    private HashMap<String, String> params;
    private OnServiceListener listener;

    public ServiceInterface(String method, String query, String function, HashMap<String, String> params) {
        this.method = method;
        this.query = query;
        this.function = function;
        this.params = params;
    }

    public void setOnServiceListener(OnServiceListener listener) {
        this.listener = listener;
    }

    public void execute() {
         if (method == "POST" || method == "PUT" || method == "DELETE") {
            String data = query + "?";
            data += "function=" + function;

            ServiceProvider gsp = new ServiceProvider(method, SERVER_URL + data, params);
            gsp.setOnWebServiceListener(new OnServiceListener() {
                @Override
                public void onResult(String JSON) {
                        listener.onResult(JSON);
                }
            });
            gsp.execute();
        }
    }
}