package it.unisa.pietrodellisola.gorocketgo;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;

public class CustomizationActivity extends Activity {
    private ListView listaOggetti;
    private CustomAdapter customAdapter;
    private ImageView VisualizzatoreOggettiSelezionatiRazzo, VisualizzatoreOggettiSelezionatiTerreno;

    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(it.unisa.pietrodellisola.gorocketgo.R.layout.activity_customization);

        String[] oggettiPersonalizzabili = {
                "Razzo rosso", "Razzo blu", "Razzo verde", "Razzo bordeaux", "Razzo oro",
                 "Razzo viola",  "Razzo rosa", "Marte", "Nettuno", "Luna", "Terra"
        };

        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        VisualizzatoreOggettiSelezionatiRazzo=(ImageView)findViewById(it.unisa.pietrodellisola.gorocketgo.R.id.ivVisualizzatoreImmaginiRazzo);
        int resIdRocket = getResources().getIdentifier((prefs.getString("ROCKET", "rocket1"))
                , "drawable", getPackageName());
        VisualizzatoreOggettiSelezionatiRazzo.setBackgroundResource(resIdRocket);

        VisualizzatoreOggettiSelezionatiTerreno=(ImageView)findViewById(it.unisa.pietrodellisola.gorocketgo.R.id.ivVisualizzatoreImmaginiTerreno);
        int resIdTerrain = getResources().getIdentifier((prefs.getString("TERRAIN", "mars"))
                , "drawable", getPackageName());
        VisualizzatoreOggettiSelezionatiTerreno.setBackgroundResource(resIdTerrain);

        listaOggetti=(ListView)findViewById(it.unisa.pietrodellisola.gorocketgo.R.id.listaOggetti);

        customAdapter = new CustomAdapter(this, it.unisa.pietrodellisola.gorocketgo.R.layout.list_element, new ArrayList<Button>());

        listaOggetti.setAdapter(customAdapter);


        for (int i=0; i<oggettiPersonalizzabili.length; i++) {
            Button b = new Button(this);
            b.setText(oggettiPersonalizzabili[i]);
            customAdapter.add(b);

        }

    }

    public void visualizzaOggettoSelezionato(View v) {

        int position = Integer.parseInt(v.getTag().toString());
        Button b = customAdapter.getItem(position);
        switch(position) {
            case 0:
                VisualizzatoreOggettiSelezionatiRazzo.setBackgroundResource(it.unisa.pietrodellisola.gorocketgo.R.drawable.rocket1);
                break;

            case 1:
                VisualizzatoreOggettiSelezionatiRazzo.setBackgroundResource(it.unisa.pietrodellisola.gorocketgo.R.drawable.rocket2);
                break;

            case 2:
                VisualizzatoreOggettiSelezionatiRazzo.setBackgroundResource(it.unisa.pietrodellisola.gorocketgo.R.drawable.rocket3);
                break;

            case 3:
                VisualizzatoreOggettiSelezionatiRazzo.setBackgroundResource(it.unisa.pietrodellisola.gorocketgo.R.drawable.rocket4);
                break;

            case 4:
                VisualizzatoreOggettiSelezionatiRazzo.setBackgroundResource(it.unisa.pietrodellisola.gorocketgo.R.drawable.rocket5);
                break;

            case 5:
                VisualizzatoreOggettiSelezionatiRazzo.setBackgroundResource(it.unisa.pietrodellisola.gorocketgo.R.drawable.rocket6);
                break;

            case 6:
                VisualizzatoreOggettiSelezionatiRazzo.setBackgroundResource(it.unisa.pietrodellisola.gorocketgo.R.drawable.rocket7);
                break;

            case 7:
                VisualizzatoreOggettiSelezionatiTerreno.setBackgroundResource(it.unisa.pietrodellisola.gorocketgo.R.drawable.mars);
                break;

            case 8:
                VisualizzatoreOggettiSelezionatiTerreno.setBackgroundResource(it.unisa.pietrodellisola.gorocketgo.R.drawable.neptune);
                break;

            case 9:
                VisualizzatoreOggettiSelezionatiTerreno.setBackgroundResource(it.unisa.pietrodellisola.gorocketgo.R.drawable.moon);
                break;

            case 10:
                VisualizzatoreOggettiSelezionatiTerreno.setBackgroundResource(it.unisa.pietrodellisola.gorocketgo.R.drawable.earth);
                break;
        }
    }


    public void salvaOggettoSelezionato(View v) {

        int position = Integer.parseInt(v.getTag().toString());
        Button b = customAdapter.getItem(position);

        //SHARED PREFERENCES
        SharedPreferences.Editor editor = prefs.edit();
        String nomeDellaRisorsaScelta=null;

        switch (position){
            case 0:
                nomeDellaRisorsaScelta="rocket1";
                VisualizzatoreOggettiSelezionatiRazzo.setBackgroundResource(it.unisa.pietrodellisola.gorocketgo.R.drawable.rocket1);
                break;
            case 1:
                nomeDellaRisorsaScelta="rocket2";
                VisualizzatoreOggettiSelezionatiRazzo.setBackgroundResource(it.unisa.pietrodellisola.gorocketgo.R.drawable.rocket2);
                break;
            case 2:
                nomeDellaRisorsaScelta="rocket3";
                VisualizzatoreOggettiSelezionatiRazzo.setBackgroundResource(it.unisa.pietrodellisola.gorocketgo.R.drawable.rocket3);
                break;
            case 3:
                nomeDellaRisorsaScelta="rocket4";
                VisualizzatoreOggettiSelezionatiRazzo.setBackgroundResource(it.unisa.pietrodellisola.gorocketgo.R.drawable.rocket4);
                break;
            case 4:
                nomeDellaRisorsaScelta="rocket5";
                VisualizzatoreOggettiSelezionatiRazzo.setBackgroundResource(it.unisa.pietrodellisola.gorocketgo.R.drawable.rocket5);
                break;
            case 5:
                nomeDellaRisorsaScelta="rocket6";
                VisualizzatoreOggettiSelezionatiRazzo.setBackgroundResource(it.unisa.pietrodellisola.gorocketgo.R.drawable.rocket6);
                break;
            case 6:
                nomeDellaRisorsaScelta="rocket7";
                VisualizzatoreOggettiSelezionatiRazzo.setBackgroundResource(it.unisa.pietrodellisola.gorocketgo.R.drawable.rocket7);
                break;
            case 7:
                nomeDellaRisorsaScelta="mars";
                VisualizzatoreOggettiSelezionatiTerreno.setBackgroundResource(it.unisa.pietrodellisola.gorocketgo.R.drawable.mars);
                break;
            case 8:
                nomeDellaRisorsaScelta="neptune";
                VisualizzatoreOggettiSelezionatiTerreno.setBackgroundResource(it.unisa.pietrodellisola.gorocketgo.R.drawable.neptune);
                break;
            case 9:
                nomeDellaRisorsaScelta="moon";
                VisualizzatoreOggettiSelezionatiTerreno.setBackgroundResource(it.unisa.pietrodellisola.gorocketgo.R.drawable.moon);
                break;
            case 10:
                nomeDellaRisorsaScelta="earth";
                VisualizzatoreOggettiSelezionatiTerreno.setBackgroundResource(it.unisa.pietrodellisola.gorocketgo.R.drawable.earth);
                break;

        }
        if((position>=0)&&(position<=6)){
            editor.putString("ROCKET", nomeDellaRisorsaScelta);
        }
        if((position>=7)&&(position<=10)) {
            editor.putString("TERRAIN", nomeDellaRisorsaScelta);
        }
        editor.commit();

        customAdapter.aggiorna();//RIDISEGNO LA LISTA

    }


    public void onClickindietro(View v){
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        listaOggetti=null;
        customAdapter=null;
        VisualizzatoreOggettiSelezionatiRazzo=null;
        VisualizzatoreOggettiSelezionatiTerreno=null;
        finish();
    }

}
