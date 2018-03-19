package uk.co.cue.app.activity.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.Request;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import uk.co.cue.app.R;
import uk.co.cue.app.activity.VenueDetails;
import uk.co.cue.app.objects.HistoricalGame;
import uk.co.cue.app.objects.Venue;
import uk.co.cue.app.util.CueApp;
import uk.co.cue.app.util.GamesAdapter;
import uk.co.cue.app.util.VolleyRequestFactory;


/**
 * A simple {@link Fragment} subclass.
 */
public class PastGamesFragment extends Fragment implements VolleyRequestFactory.VolleyRequest {


    private VolleyRequestFactory vrf;
    private CueApp app;
    private View errorView;
    private ListView listView;
    private ArrayList<HistoricalGame> games;
    private GamesAdapter gamesAdapter;
    private View spinner;

    public PastGamesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragment = inflater.inflate(R.layout.fragment_past_games, container, false);
        getActivity().setTitle("Past Games");

        app = (CueApp) getActivity().getApplication();
        vrf = new VolleyRequestFactory(this, getContext());

        Map<String, String> params = new HashMap<String, String>();
        params.put("user_id", String.valueOf(app.getUser().getUserid()));
        params.put("session_cookie", app.getUser().getSession());
        vrf.doRequest(app.POST_user_history, params, Request.Method.POST);

        spinner = fragment.findViewById(R.id.progress);
        errorView = fragment.findViewById(R.id.noResults);
        listView = (ListView) fragment.findViewById(R.id.list);

        games = new ArrayList<HistoricalGame>();
        gamesAdapter = new GamesAdapter(getActivity(), games);
        listView.setAdapter(gamesAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                HistoricalGame g = gamesAdapter.getItem(i);
                Intent intent = new Intent(getContext(), VenueDetails.class);
                intent.putExtra("venue", g.getVenue());
                startActivity(intent);
            }
        });


        // Inflate the layout for this fragment
        return fragment;
    }

    @Override
    public void requestFinished(JSONObject response, String url) {
        spinner.setVisibility(View.GONE);
        ArrayList<HistoricalGame> games = new ArrayList<>();
        try {
            JSONArray historyArr = response.getJSONArray("History");

            //Parse each object
            for (int i = 0; i < historyArr.length(); i++) {
                JSONObject obj = historyArr.getJSONObject(i);
                String time = obj.getString("time_start");
                String category = obj.getString("category");
                String venue_name = obj.getString("venue_name");
                int venue_id = obj.getInt("venue_id");
                String google_token = obj.getString("google_token");
                double lat = obj.getDouble("latitude");
                double lon = obj.getDouble("longitude");

                double price = obj.getDouble("base_price");

                Venue v = new Venue(venue_id, venue_name, lat, lon, google_token);
                HistoricalGame g = new HistoricalGame(time, category, price, v);
                games.add(g);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        if (games.size() == 0) {
            listView.setVisibility(View.GONE);
            errorView.setVisibility(View.VISIBLE);
        } else {
            listView.setVisibility(View.VISIBLE);
            errorView.setVisibility(View.GONE);
        }

        gamesAdapter.clear();
        gamesAdapter.addAll(games);
        gamesAdapter.notifyDataSetChanged();
    }

    @Override
    public void requestFailed(int statusCode) {
        listView.setVisibility(View.GONE);
        errorView.setVisibility(View.VISIBLE);
    }
}
