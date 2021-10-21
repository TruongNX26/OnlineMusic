package com.example.onlinemusic;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static Activity instance;

    public static Activity getInstance() {
        return instance;
    }

    private RequestQueue requestQueue;

    private ImageButton btnRenew, btnAdd;

    private ListView listView;
    private ArrayList<Song> songs;
    private SongAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        instance = this;

        AppUtil.makeStatusBarTransparent(this);
        mapping();

        requestQueue = Volley.newRequestQueue(this);
        //

        initListView();
        fetchSongs();
        setListViewListener();
    }

    private void initListView() {
        songs = new ArrayList<>();
        adapter = new SongAdapter(songs, this);
        listView.setAdapter(adapter);
    }

    private void setListViewListener() {
        listView.setOnItemClickListener((adapterView, view, index, id) -> {
            Intent intent = new Intent(this, PlayerActivity.class);
            intent.putExtra("position", index);
            intent.putExtra("songs", songs);
            startActivity(intent);
        });
    }

    private void setButtonRenewListener() {
        Animation animRotate = AnimationUtils.loadAnimation(this, R.anim.anim_rotate);
        btnRenew.setOnClickListener(view -> {
            btnRenew.startAnimation(animRotate);
            fetchSongs();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void setButtonAddListener() {
        btnAdd.setOnClickListener(view -> {
            Intent intent = new Intent(this, UploadActivity.class);
            startActivity(intent);
        });
    }

    public void fetchSongs() {

        String url = "http://10.0.2.2:8080/songs";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    songs.clear();
                    Long id;
                    String name, singer, dataLink;
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject object = response.getJSONObject(i);
                            id = object.getLong("id");
                            name = i + ". " + object.getString("name");
                            singer = object.getString("singer");
                            JSONObject link = object.getJSONArray("links").getJSONObject(0);
                            dataLink = link.getString("href");
                            songs.add(new Song(id, name, singer, dataLink));

                            //  Toast.makeText(this, String.format("%d / %s / %s / %s", id, name, singer, dataLink), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) { e.printStackTrace(); }
                    }
                    adapter.notifyDataSetChanged();

                },
                error -> {
                    Toast.makeText(this, "ERROR", Toast.LENGTH_SHORT).show();
                });

        requestQueue.add(jsonArrayRequest);
        adapter.notifyDataSetChanged();
    }

    private void mapping() {
        listView = findViewById(R.id.listView);
        btnRenew = findViewById(R.id.imbRenew);
        btnAdd = findViewById(R.id.imbAdd);

        setButtonRenewListener();
        setButtonAddListener();
    }
}