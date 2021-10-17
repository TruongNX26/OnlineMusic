package com.example.onlinemusic;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.List;
import java.util.Map;

public class SongAdapter extends BaseAdapter {

    private String path = "http://10.0.2.2:8080/songs/";

    private MainActivity mainActivity;

    private RequestQueue requestQueue;
    private List<Song> songs;

    public SongAdapter(List<Song> songs, MainActivity mainActivity) {
        this.songs = songs;
        this.mainActivity = mainActivity;
        this.requestQueue = Volley.newRequestQueue(mainActivity);
    }

    @Override
    public int getCount() {
        return songs.size();
    }

    @Override
    public Object getItem(int i) {
        return songs.get(i);
    }

    @Override
    public long getItemId(int i) {
        return songs.get(i).getId();
    }

    private class Holder {
        TextView txtName, txtSinger;
        ImageButton imbDelete;

        public Holder(TextView txtName, TextView txtSinger, ImageButton imbDelete) {
            this.txtName = txtName;
            this.txtSinger = txtSinger;
            this.imbDelete = imbDelete;
        }

        public TextView getTxtName() {
            return txtName;
        }

        public TextView getTxtSinger() {
            return txtSinger;
        }

        public ImageButton getImbDelete() {
            return imbDelete;
        }
    }

    @Override
    public View getView(int index, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = LayoutInflater.from(mainActivity);

        Holder holder;
        if (view == null) {
            view = inflater.inflate(R.layout.item_song, viewGroup, false);

            TextView txtName, txtSinger;
            ImageButton imbDelete;

            txtName = view.findViewById(R.id.txtName);
            txtSinger = view.findViewById(R.id.txtSinger);
            imbDelete = view.findViewById(R.id.imbDelete);

            holder = new Holder(txtName, txtSinger, imbDelete);
            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }

        holder.getTxtName().setText(songs.get(index).getName());
        holder.getTxtSinger().setText(songs.get(index).getSinger());
        holder.getImbDelete().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlertDialog(songs.get(index));
            }
        });

        return view;
    }

    private void deleteSong(Long id) {
        String url = path + id;

        StringRequest request = new StringRequest(Request.Method.DELETE, url,
                response -> { mainActivity.fetchSongs(); },
                error -> {});

        requestQueue.add(request);

    }

    private void showAlertDialog(Song song) {
        AlertDialog dialog = new AlertDialog.Builder(mainActivity)
                .setTitle("Delete")
                .setMessage(String.format("Xóa %s ?", song.getName()))
                .setPositiveButton("OK", (dialogInterface, i) -> {
                    deleteSong(song.getId());
                    dialogInterface.dismiss();
                })
                .setNegativeButton("Cancel", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                }).create();

        dialog.show();
    }
}
