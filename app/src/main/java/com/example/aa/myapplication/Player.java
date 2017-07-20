package com.example.aa.myapplication;

import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class Player extends AppCompatActivity implements View.OnClickListener{
    static MediaPlayer mp;
    ArrayList<File> mySongs;
    SeekBar sb;
    int position;
    MediaMetadataRetriever mmr;
    Uri u;
    Thread th;
    Button btPlay, btNxt, btPv;
    TextView singer,title,album;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        btPlay=(Button)findViewById(R.id.btPlay);
        btPv=(Button)findViewById(R.id.btPv);
        btNxt=(Button)findViewById(R.id.btNxt);
        singer=(TextView)findViewById(R.id.singerValue);
        title=(TextView)findViewById(R.id.titleValue);
        album=(TextView)findViewById(R.id.albumValue);
        btPlay.setOnClickListener(this);
        btNxt.setOnClickListener(this);
        btPv.setOnClickListener(this);

        sb=(SeekBar)findViewById(R.id.seekBar);
        th=new Thread(){
            @Override
            public void run() {
                int totalDuration=mp.getDuration();
                int currentPosition=mp.getCurrentPosition();
                sb.setMax(totalDuration);
                while (currentPosition < totalDuration)
                {
                    try{
                        sleep(500);
                        currentPosition=mp.getCurrentPosition();
                        sb.setProgress(currentPosition);
                    }
                    catch (Exception e)
                    {

                    }
                }
                //super.run();
            }
        };

        if(mp!=null)
        {
            mp.stop();
            mp.release();
        }
        Intent i=getIntent();
        Bundle b=i.getExtras();
        mySongs =(ArrayList) b.getParcelableArrayList("songlist");
        position=b.getInt("pos");
        mmr=new MediaMetadataRetriever();
        mmr.setDataSource(mySongs.get(position).getPath().toString());
        String av=mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
        if(av==null)
            av="Unknown";
        album.setText(av);
        String sv=mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
        if(sv==null)
            sv="Unknown";
        singer.setText(sv);
        String tv=mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        if(tv==null)
            tv="Unknown";
        title.setText(tv);
        try
        {
            u=Uri.parse(mySongs.get(position).toString());
            mp=MediaPlayer.create(getApplicationContext(),u);
            mp.start();
        }
        catch (Exception e)
        {
            toast("File is corrupt.");
        }
        sb.setMax(mp.getDuration());
        th.start();
        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mp.seekTo(seekBar.getProgress());
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        if(id== R.id.action)
            return true;
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btNxt:
                    mp.stop();
                    mp.release();
                    position=(position+1)%mySongs.size();
                    u=Uri.parse(mySongs.get(position).toString());
                    mp=MediaPlayer.create(getApplicationContext(),u);
                    mp.start();
                    sb.setMax(mp.getDuration());
                    sb.setProgress(0);
                break;
            case R.id.btPlay:
                    if(mp.isPlaying())
                        mp.pause();
                    else
                        {
                            mp.start();
                            sb.setProgress(0);
                            sb.setMax(mp.getDuration());
                        }
                break;
            case R.id.btPv:
                mp.stop();
                mp.release();
                position= (position-1)<0 ? mySongs.size()-1 : position-1;
                u=Uri.parse(mySongs.get(position).toString());
                mp=MediaPlayer.create(getApplicationContext(),u);
                mp.start();
                sb.setMax(mp.getDuration());
                sb.setProgress(0);
                break;
        }
    }

    public void toast(String text){
        Toast.makeText(getApplicationContext(),text,Toast.LENGTH_SHORT).show();
    }
}
