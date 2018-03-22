package uk.co.cue.app.activity.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import uk.co.cue.app.R;
import uk.co.cue.app.activity.ReserveTableActivity;
import uk.co.cue.app.activity.nfc.NFCDetectedActivity;
import uk.co.cue.app.activity.nfc.SetupTagActivity;
import uk.co.cue.app.objects.Game;
import uk.co.cue.app.util.CueApp;
import uk.co.cue.app.util.VolleyRequestFactory;

import static android.app.Activity.RESULT_OK;

public class HomeFragment extends Fragment implements VolleyRequestFactory.VolleyRequest, Game.GameChanged {

    private View welcome;
    private View venues;
    private View queue;
    private CueApp app;
    private View card_inQueue;
    private TextView est_time;
    private TextView queue_pos;
    private TextView queue_description;
    private TextView pub_name;
    private View requestTable;
    private VolleyRequestFactory vrf;
    private View quit;
    private boolean ready = false;
    private boolean inProgress = false; // used to denote when the game is in progress
    private View add;
    private View machineFunctions;
    private LinearLayout add_button, edit_button, delete_button;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragment = inflater.inflate(R.layout.fragment_home, container, false);
        getActivity().setTitle("Home");

        app = (CueApp) getActivity().getApplication();

        add_button = fragment.findViewById(R.id.btn_add);
        edit_button = fragment.findViewById(R.id.btn_edit);
        delete_button = fragment.findViewById(R.id.btn_delete);
        welcome = fragment.findViewById(R.id.card_intro);
        venues = fragment.findViewById(R.id.card_venues);
        add = fragment.findViewById(R.id.card_add);
        queue = fragment.findViewById(R.id.card_queue);
        machineFunctions = fragment.findViewById(R.id.card_buttons);

        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addMachine();
            }
        });

        edit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editMachine();
            }
        });

        delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteMachine();
            }
        });

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        boolean show_welcome = sharedPref.getBoolean("show_welcome", true);
        boolean show_venues = sharedPref.getBoolean("show_venues", true);
        boolean show_add = sharedPref.getBoolean("show_add", true);

        vrf = new VolleyRequestFactory(this, getContext());

        if (sharedPref.getBoolean("isGame", false)) {
            System.out.println("This user had a game");
            Map<String, String> params = new HashMap<String, String>();
            params.put("user_id", String.valueOf(app.getUser().getUserid()));
            vrf.doRequest(app.GET_user_queue, params, Request.Method.GET);
            venues.setVisibility(View.GONE);
        } else {
            venues.setVisibility(View.VISIBLE);
        }

        boolean isBusiness = app.getUser().isBusiness();

        if (show_welcome && !isBusiness) {
            welcome.setVisibility(View.VISIBLE);
        } else {
            welcome.setVisibility(View.GONE);
        }

        if (show_venues && !isBusiness) {
            venues.setVisibility(View.VISIBLE);
        } else {
            venues.setVisibility(View.GONE);
        }

        if (show_add && isBusiness) {
            add.setVisibility(View.VISIBLE);
        } else {
            add.setVisibility(View.GONE);
        }

        if (isBusiness) {
            queue.setVisibility(View.GONE);
            machineFunctions.setVisibility(View.VISIBLE);
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

        fragment.findViewById(R.id.close_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeCard("Add");
            }
        });

        requestTable = fragment.findViewById(R.id.btn_join);
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

        quit = fragment.findViewById(R.id.btn_quit);

        quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ready && !inProgress) {
                    Intent intent = new Intent(getContext(), NFCDetectedActivity.class);
                    intent.putExtra("type", "Start");
                    startActivityForResult(intent, 1);
                } else if (ready && inProgress) {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("user_id", String.valueOf(app.getUser().getUserid()));
                    params.put("session_cookie", app.getUser().getSession());
                    vrf.doRequest(app.POST_game_end, params, Request.Method.POST);
                    card_inQueue.setVisibility(View.GONE);
                    queue.setVisibility(View.VISIBLE);
                    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
                    sharedPref.edit().putBoolean("isGame", false).commit();
                } else {
                    card_inQueue.setVisibility(View.GONE);
                    queue.setVisibility(View.VISIBLE);

                    Snackbar sb = Snackbar.make(view, "You have been removed from the queue", Snackbar.LENGTH_LONG);
                    sb.addCallback(new Snackbar.Callback() {
                        @Override
                        public void onDismissed(Snackbar transientBottomBar, int event) {
                            if (event == DISMISS_EVENT_TIMEOUT) {
                                Map<String, String> params = new HashMap<String, String>();
                                params.put("user_id", String.valueOf(app.getUser().getUserid()));
                                params.put("session_cookie", app.getUser().getSession());
                                vrf.doRequest(app.POST_leave_queue, params, Request.Method.POST);
                            }
                        }
                    });


                    sb.setAction("Undo", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            card_inQueue.setVisibility(View.VISIBLE);
                            queue.setVisibility(View.GONE);
                        }
                    });
                    sb.show();
                }

            }
        });


        if (app.getUser().getGame() != null) {
            //User has a game
            updateGameCard(app.getUser().getGame());
        }

        return fragment;
    }

    private void updateGameCard(Game g) {
        pub_name.setText(g.getVenueName());
        queue_description.setText("You are queueing for " + g.getCategory() + " at " + g.getVenueName());

        queue.setVisibility(View.GONE);
        queue_pos.setText(String.valueOf(g.getPosition()));
        card_inQueue.setVisibility(View.VISIBLE);

        if (g.getPosition() == 0) {
            est_time.setText("You can now begin your game");
            queue_pos.setText("Ready");
            ((TextView) quit.findViewById(R.id.btn_text)).setText("Start Game");
            ready = true;
        } else {
            est_time.setText("The estimated time before your game is " + g.getEstimatedTime() + " minutes");
            queue_pos.setText(String.valueOf(g.getPosition()));
            ((TextView) quit.findViewById(R.id.btn_text)).setText("Quit Queue");
            ready = false;
        }
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
            case "Add":
                editor.putBoolean("show_add", false);
                add.setVisibility(View.GONE);
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
                g.setOnGameChangedListener(this);
                app.getUser().setGame(g);

                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
                sharedPref.edit().putBoolean("isGame", true).commit();

                updateGameCard(g);
            }
        } else if (requestCode == 1) {
            //Now the game is in progress
            //est_time.setText("The estimated time before your game is 0 minutes");
            queue_pos.setText("In Progress");
            inProgress = true;
            ((TextView) quit.findViewById(R.id.btn_text)).setText("End Game");
        } else if(requestCode == 2) {
            if (resultCode == RESULT_OK) {
                System.out.println("BACK HERE AFTER DELETE");
                Toast.makeText(getContext(), "Machine deleted", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void requestFinished(JSONObject response, String url) {
        if (url.contains(app.POST_leave_queue)) {
            System.out.println("Removed from the queue successfully");
        } else if (url.contains(app.GET_user_queue)) {
            try {
                JSONObject queueInfo = response.getJSONObject("Queue");
                JSONObject queueStats = response.getJSONObject("Stats");

                String venueName = queueInfo.getString("venue_name");
                String category = queueInfo.getString("category");
                int queueID = queueInfo.getInt("queue_id");
                int venueID = queueInfo.getInt("venue_id");

                int avgTime = queueStats.getInt("wait");
                int position = queueStats.getInt("position");

                Game g = new Game(venueID, queueID, venueName, category, position, avgTime);
                g.setOnGameChangedListener(this);

                app.getUser().setGame(g);
                updateGameCard(g);


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void requestFailed(int statusCode) {

    }

    @Override
    public void gameChanged(Game g) {
        final Game g1 = g;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateGameCard(g1);
            }
        });

    }

    public void addMachine() {
        Intent i = new Intent(getContext(), SetupTagActivity.class);
        startActivity(i);
    }

    public void editMachine() {
        Intent intent = new Intent(getContext(), EditMachineActivity.class);
        startActivity(intent);
    }

    public void deleteMachine() {
        Intent intent3 = new Intent(getContext(), NFCDetectedActivity.class);
        intent3.putExtra("type", "Delete");
        startActivityForResult(intent3, 2);
    }
}
