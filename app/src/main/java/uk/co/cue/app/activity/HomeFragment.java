package uk.co.cue.app.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import uk.co.cue.app.R;
import uk.co.cue.app.objects.Game;
import uk.co.cue.app.util.CueApp;

import static android.app.Activity.RESULT_OK;

public class HomeFragment extends Fragment {

    private View welcome;
    private View venues;
    private View queue;
    private CueApp app;
    private View card_inQueue;
    private TextView est_time;
    private TextView queue_pos;
    private TextView queue_description;
    private TextView pub_name;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragment = inflater.inflate(R.layout.fragment_home, container, false);
        getActivity().setTitle("Home");

        app = (CueApp) getActivity().getApplication();

        if (app.getUser().getGame() != null) {
            //User has a game
            updateGameCard(app.getUser().getGame());
        }

        welcome = fragment.findViewById(R.id.card_intro);
        venues = fragment.findViewById(R.id.card_venues);
        queue = fragment.findViewById(R.id.card_queue);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        boolean show_welcome = sharedPref.getBoolean("show_welcome", true);
        boolean show_venues = sharedPref.getBoolean("show_venues", true);

        if (show_welcome) {
            welcome.setVisibility(View.VISIBLE);
        } else {
            welcome.setVisibility(View.GONE);
        }

        if (show_venues) {
            venues.setVisibility(View.VISIBLE);
        } else {
            venues.setVisibility(View.GONE);
        }

        fragment.findViewById(R.id.close_welcome).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeCard("Welcome");
            }
        });

        fragment.findViewById(R.id.close_venue).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeCard("Venue");
            }
        });


        CardView requestTable = fragment.findViewById(R.id.btn_join);
        requestTable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity().getApplicationContext(), ReserveTableActivity.class);
                startActivityForResult(i, 0);
            }
        });

        card_inQueue = fragment.findViewById(R.id.card_in_queue);
        queue_description = fragment.findViewById(R.id.queue_description);
        queue_pos = fragment.findViewById(R.id.queue_position);
        est_time = fragment.findViewById(R.id.estimated_time);
        pub_name = fragment.findViewById(R.id.pub_name);



        return fragment;
    }

    private void updateGameCard(Game g) {
        pub_name.setText(g.getVenueName());
        queue_description.setText("You are queueing for " + g.getCategory() + " at " + g.getVenueName());
        //queue_pos.setText(g.getPosition());
        est_time.setText("The estimated time before your game is 0 minutes");

        queue.setVisibility(View.GONE);
        card_inQueue.setVisibility(View.VISIBLE);
    }

    private void closeCard(String card) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = settings.edit();

        switch (card) {
            case "Welcome":
                editor.putBoolean("show_welcome", false);
                welcome.setVisibility(View.GONE);
                break;
            case "Venue":
                editor.putBoolean("show_venues", false);
                venues.setVisibility(View.GONE);
                break;
        }

        editor.apply();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == 0) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                System.out.println("HELLO");
                Game g = (Game) data.getSerializableExtra("game");
                app.getUser().setGame(g);


                updateGameCard(g);

            }
        }
    }
}
