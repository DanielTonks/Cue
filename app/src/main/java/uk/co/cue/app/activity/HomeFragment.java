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

import uk.co.cue.app.R;

public class HomeFragment extends Fragment {

    private View welcome;
    private View venues;
    private View queue;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragment = inflater.inflate(R.layout.fragment_home, container, false);
        getActivity().setTitle("Home");

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
                startActivity(i);
            }
        });

//        Button pub_details = fragment.findViewById(R.id.btn_pub);
//        pub_details.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent a = new Intent(getActivity().getApplicationContext(), VenueDetails.class);
//                startActivity(a);
//            }
//        });

        return fragment;
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
}
