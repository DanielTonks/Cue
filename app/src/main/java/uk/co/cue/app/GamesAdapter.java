package uk.co.cue.app;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import uk.co.cue.app.objects.HistoricalGame;


/**
 * Created by danieltonks on 04/03/2018.
 */

public class GamesAdapter extends ArrayAdapter<HistoricalGame> {

    private Activity t;

    public GamesAdapter(Activity t, ArrayList<HistoricalGame> lst) {
        super(t, 0, lst);
        this.t = t;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.past_game_item, parent, false);
        }

        HistoricalGame g = getItem(position);

        TextView gameType = listItemView.findViewById(R.id.game_type);
        TextView pubName = listItemView.findViewById(R.id.pub_name);
        TextView price = listItemView.findViewById(R.id.price);
        TextView date_month = listItemView.findViewById(R.id.date_month);
        TextView date_time = listItemView.findViewById(R.id.date_time);

        gameType.setText(g.getCategory());
        pubName.setText(g.getVenueName());
        price.setText("£" + String.format("%.2f", g.getPrice()));
        date_month.setText(g.getDateMonth());
        date_time.setText(g.getDateTime());


        return listItemView;
    }
}
