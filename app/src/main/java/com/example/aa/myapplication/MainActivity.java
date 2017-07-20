package com.example.aa.myapplication;

import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ListView lv;
    RadioGroup radioGroup;
    String[] items;
    MediaMetadataRetriever mmr=new MediaMetadataRetriever();
    String[] singer;
    String[] album;
    String search;
    String[] showitems;
    EditText sBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //toast("Start");
        lv= (ListView)findViewById(R.id.lvPlaylist);
        sBar = (EditText)findViewById(R.id.search);
        radioGroup = (RadioGroup) findViewById(R.id.radio);
        final ArrayList<File> mySongs = findSongs(Environment.getExternalStorageDirectory());
        items = new String[ mySongs.size() ];
        /*lyricWriter = new String[ mySongs.size() ];
        musicDirector = new String[ mySongs.size() ];*/
        singer = new String[ mySongs.size() ];
        album = new String[ mySongs.size() ];
        //mmr.setDataSource(mySongs.get(0).getPath().toString());
        for(int i=0; i<mySongs.size(); i++)
        {
            //toast(mySongs.get(i).getName().toString());
            items[i]=mySongs.get(i).getName().toString().replace(".mp3","").replace(".wav","");
            try{
                mmr.setDataSource(mySongs.get(i).getPath().toString());
            }
            catch (Exception e)
            {

            }
            if(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)!=null)
                singer[i]=mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            else
                singer[i]="Unknown";
            if(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM)!=null)
                album[i]=mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
            else
                album[i]="Unknown";
            /*mmr.setDataSource(mySongs.get(i).getAbsoluteFile().toString());
            singer[i] = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            musicDirector[i] = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_COMPOSER);
            lyricWriter[i] = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_WRITER);*/
        }
        sBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(sBar.getText().toString().trim()=="")
                search="";
                else
                search = sBar.getText().toString();
                //    toast(search);
                final ArrayList<File> showSongs = showSong(mySongs,search);
                showitems = new String[ showSongs.size() ];

                for(int i=0; i<showSongs.size(); i++)
                {
                    //toast(mySongs.get(i).getName().toString());
                    showitems[i]=showSongs.get(i).getName().toString().replace(".mp3","").replace(".wav","");
                }
                ArrayAdapter<String> adp=new ArrayAdapter<String>(getApplicationContext(),R.layout.song_layout,R.id.textView1,showitems);
                lv.setAdapter(adp);
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        startActivity(new Intent(getApplicationContext(),Player.class).putExtra("pos",position).putExtra("songlist",showSongs));
                    }
                });
            }
        });
        ArrayAdapter<String> adp=new ArrayAdapter<String>(getApplicationContext(),R.layout.song_layout,R.id.textView1,items);
        lv.setAdapter(adp);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(getApplicationContext(),Player.class).putExtra("pos",position).putExtra("songlist",mySongs));
            }
        });
    }

    public ArrayList<File> showSong(ArrayList<File> a, String search){
        ArrayList<File> a1=new ArrayList<File>();
            switch (radioGroup.getCheckedRadioButtonId())
            {
                case R.id.key:
                    for(int i=0;i<a.size();i++) {
                        if (search == null) {
                            a1.add(a.get(i));
                        } else if (a.get(i).getName().toString().toLowerCase().contains(search.toLowerCase())) {
                            a1.add(a.get(i));
                        }
                    }
                    break;
                case R.id.sing:
                    for(int i=0;i<a.size();i++) {
                        if (search == null) {
                            a1.add(a.get(i));
                        } else if (singer[i].toLowerCase().contains(search.toLowerCase())) {
                            a1.add(a.get(i));
                        }
                    }
                    break;
                case R.id.alb:
                    for(int i=0;i<a.size();i++) {
                        if (search == null) {
                            a1.add(a.get(i));
                        } else if (album[i].toLowerCase().contains(search.toLowerCase())) {
                            a1.add(a.get(i));
                        }
                    }
                    break;
            }
        return a1;
    }

    public ArrayList<File> findSongs(File root){
        ArrayList<File> a1=new ArrayList<File>();
        File[] files=root.listFiles();
        for(File singleFile : files){
            if(singleFile.isDirectory() && !singleFile.isHidden()){
                a1.addAll(findSongs(singleFile));
            }
            else{
                if(singleFile.getName().endsWith(".mp3") || singleFile.getName().endsWith(".wav"))
                {
                    a1.add(singleFile);
                }
            }
        }
        return a1;
    }

    public void toast(String text){
        Toast.makeText(getApplicationContext(),text,Toast.LENGTH_SHORT).show();
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
}
