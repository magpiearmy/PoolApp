package com.example.adam.poolapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.JsonReader;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class PlayerSummaryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_players_summary);
    }

    @Override
    protected void onStart() {
        super.onStart();
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                callApi();
            }
        });
    }

    private void callApi() {
        try {
            URL getPlayersEndpoint = new URL("http://10.0.2.2:8081/players");
            HttpURLConnection connection = (HttpURLConnection) getPlayersEndpoint.openConnection();

            if (connection.getResponseCode() == 200) {
                final List<Player> players = new ArrayList<>();

                InputStreamReader bodyReader = new InputStreamReader(connection.getInputStream(), "UTF-8");
                JsonReader jsonReader = new JsonReader(bodyReader);
                jsonReader.beginArray();
                while (jsonReader.hasNext()) {
                    jsonReader.beginObject();
                    players.add(parsePlayer(jsonReader));
                    jsonReader.endObject();
                }
                jsonReader.close();

                addPlayersToTable(players);
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Player parsePlayer(JsonReader jsonReader) throws IOException {
        jsonReader.skipValue();
        int id = jsonReader.nextInt();

        jsonReader.skipValue();
        String name = jsonReader.nextString();

        jsonReader.skipValue();
        int teamId = jsonReader.nextInt();

        return new Player(id, name, teamId);
    }

    private void addPlayersToTable(final List<Player> players) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (Player player : players) {
                    TableLayout tableLayout = findViewById(R.id.playerSummaryTable);
                    TableRow row = buildPlayerTableRow(player.name);
                    tableLayout.addView(row, new TableLayout.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                }
            }
        });
    }

    private TableRow buildPlayerTableRow(String playerName) {
        TextView nameTextView = new TextView(this);
        nameTextView.setText(playerName);
        nameTextView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        nameTextView.setTextSize(28);

        TableRow row = new TableRow(this);
        row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        row.addView(nameTextView);
        return row;
    }
}
