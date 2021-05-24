package com.example.adam.poolapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class PlayersFragment extends Fragment {

    public static final String PROTOCOL = "http";
    public static final String SERVER_HOST = "192.168.0.17";
    public static final int SERVER_PORT = 5000;
    public static final String PLAYERS_ENDPOINT = "/players";

    public PlayersFragment() {
        super(R.layout.fragment_players);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AsyncTask.execute(this::fetchPlayers);
    }

    private void fetchPlayers() {
        HttpURLConnection connection = connectToServer();

        try {
            if (connection.getResponseCode() == 200) {
                BufferedReader bodyReader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
                String response = getJsonResponse(bodyReader);
                List<Player> players = parsePlayersFromResponse(response);

                addPlayersToTable(players);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getJsonResponse(BufferedReader bodyReader) throws IOException {
        StringBuilder response = new StringBuilder();
        String currentLine;
        while ((currentLine = bodyReader.readLine()) != null) {
            response.append(currentLine);
        }
        return response.toString();
    }

    private HttpURLConnection connectToServer() {
        try {
            URL playersEndpoint = new URL(PROTOCOL, SERVER_HOST, SERVER_PORT, PLAYERS_ENDPOINT);
            return (HttpURLConnection) playersEndpoint.openConnection();
        } catch (MalformedURLException e) {
            throw new RuntimeException("Malformed URL", e);
        } catch (IOException e) {
            throw new RuntimeException("Could not connect to server", e);
        }
    }

    private List<Player> parsePlayersFromResponse(String response) {
        List<Player> players = new ArrayList<>();

        try {
            JSONObject json = new JSONObject(response);
            JSONArray playersArray = json.getJSONArray("players");
            for (int i = 0; i < playersArray.length(); i++) {
                players.add(parsePlayer(playersArray.getJSONObject(i)));
            }

        } catch (JSONException ignored) {
        }

        return players;
    }

    private Player parsePlayer(JSONObject playerObject) throws JSONException {
        return new Player(
                playerObject.getInt("player_id"),
                playerObject.getString("first_name"),
                playerObject.getString("last_name"),
                playerObject.getInt("team_id")
        );
    }

    private void addPlayersToTable(final List<Player> players) {
        getActivity().runOnUiThread(() -> {
            for (Player player : players) {
                TableLayout tableLayout = getActivity().findViewById(R.id.playerSummaryTable);
                TableRow row = buildTableRow(String.format("%s %s", player.first_name, player.last_name));
                tableLayout.addView(row, new TableLayout.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            }
        });
    }

    private TableRow buildTableRow(String playerName) {
        TextView nameTextView = new TextView(getActivity());
        nameTextView.setText(playerName);
        nameTextView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        nameTextView.setTextSize(36);

        TableRow row = new TableRow(getActivity());
        row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        row.addView(nameTextView);
        return row;
    }
}
