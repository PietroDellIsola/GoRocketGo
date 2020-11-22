package it.unisa.pietrodellisola.gorocketgo;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class MainActivity extends Activity {

    private TextView tvRecord;

    private MediaPlayer mediaPlayer; /*PER GESTIRE DUE TRACCE AUDIO UTILIZZO UN MEDIAPLAYER
                                POICHÈ NON VENGONO RIPRODOTTI ASSIEME*/
    private Intent intent;

    public SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());


        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(it.unisa.pietrodellisola.gorocketgo.R.layout.activity_main);

        tvRecord=(TextView)findViewById(it.unisa.pietrodellisola.gorocketgo.R.id.tvRecordMain);
        //AGGIORNO LA TEXTVIEW DEL RECORD IN ON RESUME POICHÈ POTREBBE SUBIRE VARIAZIONI, DOPO
        //IL LANCIO DI GAMEOVERACTIVITY

        mediaPlayer=MediaPlayer.create(getApplicationContext(), it.unisa.pietrodellisola.gorocketgo.R.raw.fanfare_for_space);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        intent = new Intent();
    }

    public void bottonePremuto(){
        mediaPlayer.stop();
        mediaPlayer=MediaPlayer.create(getApplicationContext(), it.unisa.pietrodellisola.gorocketgo.R.raw.click_on_button);
        mediaPlayer.start();
    }

    public void giocaOnClick(View v){
        bottonePremuto();
        intent.setClass(MainActivity.this,PlayActivity.class);

        /* LEGGO LE SHARED PREFERENCES, SE PRESENTI E LE PASSO ALL'ACTIVITY GIOCA
        PRIMO VALORE = NOME DELLA SHARED PREFERENCES, SECONDO VALORE = SE NESSUNA SHARED PREFERENCES E' PRESENTE*/
        intent.putExtra("ROCKET", prefs.getString("ROCKET", "rocket1"));
        intent.putExtra("TERRAIN", prefs.getString("TERRAIN", "mars"));
        intent.putExtra("COMANDI", prefs.getString("COMANDI", "Manuale"));
        intent.putExtra("DIFFICOLTA", prefs.getString("DIFFICOLTA", "Facile"));
        //prefs.getString("FLAG", "italia");

        startActivity(intent);
    }

    public void personalizzazioneOnClick(View v){
        bottonePremuto();
        intent.setClass(MainActivity.this,CustomizationActivity.class);
        startActivity(intent);
    }

    public void comandiOnClick(View v){
        bottonePremuto();
        intent.setClass(MainActivity.this,ControlsActivity.class);
        startActivity(intent);
    }

    public void esciOnClick(View view){
        bottonePremuto();
        finishAffinity();
    }

    @Override
    protected void onResume() {
        super.onResume();

        //AGGIORNO LA TEXTVIEW DEL RECORD
        int record=prefs.getInt("RECORD", 0);
        if(record==0)
        {
            tvRecord.setText("Nessun record");
        }
        else tvRecord.setText(record+" Km");

        if(!mediaPlayer.isPlaying())
            {
                mediaPlayer=MediaPlayer.create(getApplicationContext(), it.unisa.pietrodellisola.gorocketgo.R.raw.fanfare_for_space);
                mediaPlayer.start();
            }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer.isPlaying())
                mediaPlayer.stop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer.isPlaying())
                mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer=null;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mediaPlayer.isPlaying())
                mediaPlayer.stop();
        finishAffinity();
    }
}
