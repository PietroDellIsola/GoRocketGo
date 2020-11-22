package it.unisa.pietrodellisola.gorocketgo;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class ControlsActivity extends Activity {

    private RadioGroup radioGroupComandi;
    private RadioButton radioButtonManuale,radioButtonSensore,radioButtonDifficolta1,
            radioButtonDifficolta2,radioButtonDifficolta3;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private ImageView immagine;  //MODIFICATO QUANDO CLICCO SUL RADIO BUTTON
    private TextView descrizioneComando; //MODIFICATO QUANDO CLICCO SUL RADIO BUTTON
    private RadioGroup radioGroupDifficolta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(it.unisa.pietrodellisola.gorocketgo.R.layout.activity_controls);

        immagine=(ImageView)findViewById(it.unisa.pietrodellisola.gorocketgo.R.id.immagineComandi);
        descrizioneComando=(TextView)findViewById(it.unisa.pietrodellisola.gorocketgo.R.id.testoComandi);

        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = prefs.edit();

        radioGroupComandi=(RadioGroup)findViewById(it.unisa.pietrodellisola.gorocketgo.R.id.radioGroupComandi);
        radioButtonManuale=(RadioButton)findViewById(it.unisa.pietrodellisola.gorocketgo.R.id.comando1);
        radioButtonSensore=(RadioButton)findViewById(it.unisa.pietrodellisola.gorocketgo.R.id.comando2);

        if(prefs.getString("COMANDI","Manuale").equals("Manuale"))
            radioButtonManuale.setChecked(true);
        else radioButtonSensore.setChecked(true);

        radioGroupComandi.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                View radioButton = radioGroupComandi.findViewById(i);
                int index = radioGroupComandi.indexOfChild(radioButton);

                // Add logic here

                switch (index) {
                    case 0:
                        editor.putString("COMANDI", "Manuale");
                        editor.commit();
                        //MODIFICA SCRITTA ED IMMAGINE DESCRIZIONE
                        int resIdComando1 = getResources().getIdentifier("comando1"
                                , "drawable", getPackageName());
                        immagine.setBackgroundResource(resIdComando1);
                        descrizioneComando.setText(it.unisa.pietrodellisola.gorocketgo.R.string.dettagli_comando_selezionato1);
                        break;
                    case 1:
                        editor.putString("COMANDI", "Accelerometro");
                        editor.commit();
                        //MODIFICA SCRITTA ED IMMAGINE DESCRIZIONE
                        int resIdComando2 = getResources().getIdentifier("comando2"
                                , "drawable", getPackageName());
                        immagine.setBackgroundResource(resIdComando2);
                        descrizioneComando.setText(it.unisa.pietrodellisola.gorocketgo.R.string.dettagli_comando_selezionato2);
                        break;
                }
            }
        });

        radioGroupDifficolta = (RadioGroup) findViewById(it.unisa.pietrodellisola.gorocketgo.R.id.radioGroupDifficolta);
        radioButtonDifficolta1 = (RadioButton)findViewById(it.unisa.pietrodellisola.gorocketgo.R.id.difficolta1);
        radioButtonDifficolta2 = (RadioButton)findViewById(it.unisa.pietrodellisola.gorocketgo.R.id.difficolta2);
        radioButtonDifficolta3 = (RadioButton)findViewById(it.unisa.pietrodellisola.gorocketgo.R.id.difficolta3);

        String difficoltaSelezionata = prefs.getString("DIFFICOLTA","Facile");

         switch(difficoltaSelezionata){
            case "Facile":
                radioButtonDifficolta1.setChecked(true);
                break;
            case "Medio":
                radioButtonDifficolta2.setChecked(true);
                break;
            case "Difficile":
                radioButtonDifficolta3.setChecked(true);
                break;
        }

        radioGroupDifficolta.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                View radioButton = radioGroupDifficolta.findViewById(i);
                int index = radioGroupDifficolta.indexOfChild(radioButton);

                // Add logic here

                switch (index) {
                    case 0:
                        editor.putString("DIFFICOLTA", "Facile");
                        editor.commit();
                        break;
                    case 1:
                        editor.putString("DIFFICOLTA", "Medio");
                        editor.commit();
                        break;
                    case 2:
                        editor.putString("DIFFICOLTA", "Difficile");
                        editor.commit();
                        break;
                }
            }
        });

    }

    public void onClickindietro(View v){
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        radioGroupDifficolta=null;
        radioGroupComandi=null;
        radioButtonManuale=null;
        radioButtonSensore=null;
        radioButtonDifficolta1=null;
        radioButtonDifficolta2=null;
        radioButtonDifficolta3=null;
        immagine=null;
        descrizioneComando=null;
        prefs=null;
        editor=null;
        finish();
    }

}
