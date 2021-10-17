package com.example.onlinemusic;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class PlayerActivity extends AppCompatActivity {

    private TextView txtSongName, txtSinger, txtCurrentTime, txtTimeTotal;
    private SeekBar seekBar;
    private ImageButton btnPrev, btnPlayPause, btnStop, btnNext;
    private ImageView imvDisc;

    private ArrayList<Song> songs;
    private int position;

    private Handler handler = new Handler();

    private MediaPlayer mediaPlayer;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("mm:ss");

    private ObjectAnimator anim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        AppUtil.makeStatusBarTransparent(this);
        mapping();
        loadExtras();
        setSeekBarListener();
        //
        mediaPlayer = new MediaPlayer();
        setMediaPlayerListener();

        createAnimator();

        setButtonPlayPauseListener();
        setButtonPrevListener();
        setButtonNextListener();
        setButtonStopListener();

        prepareMediaPlayer();
        btnPlayPause.performClick();


    }

    private void setMediaPlayerListener() {
        mediaPlayer.setOnCompletionListener(player -> {
            position++;
            if (position == songs.size()) position = 0;

            prepareMediaPlayer();
            mediaPlayer.start();
        });
    }

    private void loadExtras() {
        songs = (ArrayList<Song>) getIntent().getSerializableExtra("songs");
        position = getIntent().getIntExtra("position", 0);
    }

    private void setButtonPlayPauseListener() {
        btnPlayPause.setOnClickListener(view -> {

            if(anim.isPaused()) anim.resume();
            else anim.pause();

            if (mediaPlayer.isPlaying()) {
                handler.removeCallbacks(updater);
                mediaPlayer.pause();
                btnPlayPause.setImageResource(R.drawable.ic_baseline_play_arrow);
                
            } else {
                mediaPlayer.start();
                btnPlayPause.setImageResource(R.drawable.ic_baseline_pause);
                updateSeekBar();
            }
        });
    }

    private void setButtonPrevListener() {
        btnPrev.setOnClickListener(view -> {
            seekBar.setProgress(0);
            position--;
            if (position < 0) position = songs.size() - 1;

            if (mediaPlayer.isPlaying()) {
                prepareMediaPlayer();
                mediaPlayer.start();
            } else {
                prepareMediaPlayer();
            }
        });

    }

    private void setButtonNextListener() {

        btnNext.setOnClickListener(view -> {
            seekBar.setProgress(0);
            position++;
            if (position >= songs.size()) position = 0;

            if (mediaPlayer.isPlaying()) {
                prepareMediaPlayer();
                mediaPlayer.start();
            } else {
                prepareMediaPlayer();
            }
        });
    }

    private void setButtonStopListener() {
        btnStop.setOnClickListener(view -> {
            seekBar.setProgress(0);
            if (mediaPlayer.isPlaying()) {
                btnPlayPause.performClick();
                mediaPlayer.seekTo(0);
            }
        });

    }

    private void setSeekBarListener() {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });
    }

    private void prepareMediaPlayer() {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(songs.get(position).getDataLink());
            mediaPlayer.prepare();

            preparePlayerUI();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void preparePlayerUI() {
        txtSongName.setText(songs.get(position).getName());
        txtSinger.setText(songs.get(position).getSinger());

        txtTimeTotal.setText(dateFormat.format(mediaPlayer.getDuration()));
        seekBar.setMax(mediaPlayer.getDuration());
    }

    private Runnable updater = new Runnable() {
        @Override
        public void run() {
            updateSeekBar();
            updateCurrentTime();
        }
    };

    private void updateCurrentTime() {
        long currentDuration = mediaPlayer.getCurrentPosition();
        txtCurrentTime.setText(dateFormat.format(new Date(currentDuration)));
    }

    private void updateSeekBar() {
        if (mediaPlayer.isPlaying()) {
            seekBar.setProgress(mediaPlayer.getCurrentPosition());
            handler.postDelayed(updater, 100);
        }
    }

    private void mapping() {
        txtCurrentTime = findViewById(R.id.textViewCurrentTime);
        txtTimeTotal = findViewById(R.id.textViewTimeTotal);
        txtSongName = findViewById(R.id.txtPlayerSongName);
        txtSinger = findViewById(R.id.txtPlayerSinger);

        imvDisc = findViewById(R.id.imvDisc);

        seekBar = findViewById(R.id.seekBarSong);
        btnNext = findViewById(R.id.imageButtonNext);
        btnPrev = findViewById(R.id.imageButtonPrev);
        btnStop = findViewById(R.id.imageButtonStop);
        btnPlayPause = findViewById(R.id.imageButtonPlay);
    }

    private void createAnimator() {
        anim = ObjectAnimator.ofFloat(imvDisc, "rotation", 0, 360);
        anim.setDuration(2000);
        anim.setRepeatCount(ObjectAnimator.INFINITE);
        anim.setRepeatMode(ObjectAnimator.RESTART);
        anim.setInterpolator(new LinearInterpolator());
        anim.start();
        anim.pause();
    }

    @Override
    protected void onDestroy() {
        mediaPlayer.pause();
        mediaPlayer.stop();
        super.onDestroy();
    }
}