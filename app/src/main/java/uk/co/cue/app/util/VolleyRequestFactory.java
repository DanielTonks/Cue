package uk.co.cue.app.util;

/**
 * Created by danieltonks on 26/02/2018.
 */

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by danieltonks on 25/02/2018.
 * <p>
 * Class that abstracts away some of the behaviour of volley so that it is simpler to write across multiple classes.
 * Any class which wishes to make a Volley request should implement VolleyRequest. URL endpoints given to
 * doRequest() should be placed inside CueApp
 */

public class VolleyRequestFactory {

    private VolleyRequest callback;
    private Context c;
    private RequestQueue requestQueue;

    public VolleyRequestFactory(VolleyRequest callback, Context c) {
        this.callback = callback;
        this.requestQueue = Volley.newRequestQueue(c);
        this.c = c;
    }

    public void doRequest(String url, Map<String, String> params, int method) {
        final Map<String, String> reqParams = params;

        JSONObject jsonObj = null;
        if (method != Request.Method.GET) {
            jsonObj = new JSONObject(params);
        } else {
            url += "?";
            int counter = 0;
            for (Map.Entry<String, String> me : params.entrySet()) {
                counter++;
                url += me.getKey() + "=" + me.getValue();
                if (params.size() != counter) {
                    url += "&";
                }
            }
        }

        final String given_url = url;

        JsonObjectRequest req = new JsonObjectRequest(method, url, jsonObj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println(given_url + ": " + response);
                        callback.requestFinished(response, given_url);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println(error.getMessage());
                        if (error.networkResponse == null) {
                            callback.requestFailed(999);
                        } else {
                            callback.requestFailed(error.networkResponse.statusCode);
                        }
                    }
                }

        );

        requestQueue.add(req);
    }

    public interface VolleyRequest {

        void requestFinished(JSONObject response, String url);

        void requestFailed(int statusCode);
    }


}
