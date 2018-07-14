package com.example.adam.poolapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
            Unirest.get("http://10.0.2.2:8081/players");

            JsonNode body = response.getBody();
            JSONArray playerArray = body.getArray();

            for (int i = 0; i < playerArray.length(); i++) {
                JSONObject playerJson = playerArray.getJSONObject(i);
                int id = playerJson.getInt("player_id");
                String name = playerJson.getString("player_name");
                addPlayerToTable(id, name);
            }

        } catch (UnirestException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void addPlayerToTable(int id, String name) {
        TableLayout tableLayout = findViewById(R.id.playerSummaryTable);
        TableRow row = buildPlayerTableRow(name);
        tableLayout.addView(row, new TableLayout.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
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
