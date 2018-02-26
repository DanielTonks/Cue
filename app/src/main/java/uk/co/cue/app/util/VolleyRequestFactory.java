package uk.co.cue.app.util;

/**
 * Created by danieltonks on 26/02/2018.
 */

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

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
        final String given_url = url;

        StringRequest req = new StringRequest(method, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        callback.requestFinished(response, given_url);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.requestFailed(error);
                    }
                }

        ) {
            @Override
            protected Map<String, String> getParams() {
                return reqParams;
            }
        };
        requestQueue.add(req);
    }

    public interface VolleyRequest {

        void requestFinished(String response, String url);

        void requestFailed(VolleyError error);
    }


}
