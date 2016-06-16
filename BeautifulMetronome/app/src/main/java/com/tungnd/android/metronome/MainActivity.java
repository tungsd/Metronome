package com.tungnd.android.metronome;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import com.tungnd.android.beat.BeatView;
import com.tungnd.android.beat.metronome;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

/**
 * Main activity to start app
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener, metronome, DiscreteSeekBar.OnProgressChangeListener {

    private BeatView beatView;
    private boolean start;
    private static PlayerService playerService;
    private PlayerService.LocalBinder localBinder;
    private Intent svc;
    private boolean doubleBackToExitPressedOnce;
    private DiscreteSeekBar tempoSeekBar;
    private DiscreteSeekBar volumeSeekBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_metronome_paralax);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tempoSeekBar = (DiscreteSeekBar) findViewById(R.id.tempo_slider);
        tempoSeekBar.setOnProgressChangeListener(this);
        volumeSeekBar = (DiscreteSeekBar) findViewById(R.id.tempo_slider);
        volumeSeekBar.setOnProgressChangeListener(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        this.beatView = (BeatView) findViewById(R.id.visual);
        beatView.setOnClickListener(this);

        //this.appbar = (AppBarLayout) findViewById(R.id.app_bar);

        //Table view
        setupTableLayout();

//        GridView gridview = (GridView) findViewById(R.id.gridview);
//        gridview.setAdapter(new ButtonAdapter(this));
    }

    private void setupTableLayout() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TableLayout tableLayout = (TableLayout) findViewById(R.id.table);
                LayoutInflater layoutInflater = getLayoutInflater();
                for (int i = 0; i < 3; i++) {
                    TableRow r = (TableRow) layoutInflater.inflate(R.layout.table_row, null);
                    for (int j = 0; j < 4; j++) {
                        Button b = (Button) layoutInflater.inflate(R.layout.button, null);
                        b.setOnClickListener(MainActivity.this);
                        b.setText("" + metronome.tempos[i * 4 + j]);
                        r.addView(b);
                    }
                    tableLayout.addView(r);
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        svc = new Intent(this, PlayerService.class);
        bindService(svc, mConnection, Context.BIND_AUTO_CREATE);
        startService(svc);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from the service
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, R.string.confirm_exit, Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopBeat();
    }

    /**
     * In general, click on the Beat View will start the beat
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.visual: {
                start = !start;
                startBeat();
            }
            default:
                break;
        }
    }

    @Override
    public void startBeat() {
        if (start) {
            playerService.startBeat();
            beatView.setBeatLock(playerService);//sync with sound
            beatView.startBeat();
        } else {
            stopBeat();
        }
    }

    @Override
    public void stopBeat() {
        if (svc != null) {
            playerService.stopBeat();
            beatView.stopBeat();
        }
    }

    private boolean mBound;
    /**
     * Defines callbacks for service binding, passed to bindService()
     */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            Log.d("Service connected", this.toString());
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            PlayerService.LocalBinder binder = (PlayerService.LocalBinder) service;
            playerService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };


    @Override
    public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
        switch (seekBar.getId()){
            case R.id.volumn_slider:
                //change tempo
                break;
            case R.id.tempo_slider:
                //change volume
                break;
        }

    }

    @Override
    public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(DiscreteSeekBar seekBar) {

    }
}
