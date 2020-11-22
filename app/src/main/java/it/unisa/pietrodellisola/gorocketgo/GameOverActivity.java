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
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

public class GameOverActivity extends Activity {

    private TextView tvrecord,tvNuovoRecord;
    private MediaPlayer gameoverSound;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private int recordAttuale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(it.unisa.pietrodellisola.gorocketgo.R.layout.activity_game_over);

        recordAttuale=getIntent().getExtras().getInt("CURRENTRECORD");

        tvNuovoRecord=(TextView) findViewById(it.unisa.pietrodellisola.gorocketgo.R.id.tvNuovoRecord);
        tvrecord=(TextView) findViewById(it.unisa.pietrodellisola.gorocketgo.R.id.tvSignalLostRecord);
        tvrecord.setText(recordAttuale+" Km");

        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = prefs.edit();
        if(recordAttuale > prefs.getInt("RECORD", 0)) //LEGGE 0 SE NON C'E' NESSUN RECORD
        {
            /*SE NON E' SALVATO ALCUN RECORD SALVO DIRETTAMENTE QUELLO ATTUALE
            * ALTRIMENTI SALVO QUELLO ATTUALE POICHE' IL RECORD CORRENTE E' MAGGIORE DI QUELLO SALVATO*/
            editor.putInt("RECORD", recordAttuale);
            tvNuovoRecord.setVisibility(View.VISIBLE);
            Animation anim = new AlphaAnimation(0.0f, 1.0f);
            anim.setDuration(200);
            anim.setStartOffset(0);
            anim.setRepeatCount(Animation.INFINITE);
            tvNuovoRecord.startAnimation(anim);

            editor.commit();
        }

        //AUDIO CARICATO IN ONRESUME

        //QUI FARO' STAMPARE IL RECORD APPENA STABILITO E LO EVIDENZIERO' NEL CASO SI SUPERI QUELLO PRECEDENTE
        //CLICCANDO SU RIENTRA, O BACK, VERRO' RIPORTATO ALLA SCHERMATA PRINCIPALE
    }

    public void rientra(View v){
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);

        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);

        finish(); //STESSA COSA DELLA FUNZIONE RIENTRA
                    //NON POSSO RICHIAMARE RIENTRA PERCHÈ NON HO VIEW!!
    }

    @Override
    protected void onResume() {
        super.onResume();

        gameoverSound=MediaPlayer.create(getApplicationContext(), it.unisa.pietrodellisola.gorocketgo.R.raw.gameover_sound);
        gameoverSound.setLooping(true);
        gameoverSound.start();


    }

    @Override
    protected void onPause() {
        super.onPause();

        gameoverSound.stop();
        gameoverSound.release();
        gameoverSound=null;

    }
}
