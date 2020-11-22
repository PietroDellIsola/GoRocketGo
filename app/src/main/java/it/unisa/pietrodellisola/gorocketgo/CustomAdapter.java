package it.unisa.pietrodellisola.gorocketgo;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;

import java.util.List;

/**
 * Created by PietroDell'Isola on 23/12/2017.
 */

public class CustomAdapter extends ArrayAdapter<Button> {
    private int resource;
    private LayoutInflater inflater;
    private SharedPreferences prefs;

    public CustomAdapter(Context context, int resourceId, List<Button> objects) {
        super(context, resourceId, objects);
        resource = resourceId;
        inflater = LayoutInflater.from(context);


        prefs = PreferenceManager.getDefaultSharedPreferences(context);

    }

    @Override
    public View getView(int position, View v, ViewGroup parent){

        if (v == null) {
            v = inflater.inflate(it.unisa.pietrodellisola.gorocketgo.R.layout.list_element, null);
        }

        Button buttonSelezionato = getItem(position);

        Button b1 = (Button)v.findViewById(it.unisa.pietrodellisola.gorocketgo.R.id.buttonNomeOggetto);
        Button b2 = (Button)v.findViewById(it.unisa.pietrodellisola.gorocketgo.R.id.buttonSalvaOggetto);

        /*USO LO SWITCH PER COLORARE IL BOTTONE E SCRIVERE "SELEZIONATO", SE L'ELEMENTO
        * E' STATO SELEZIONATO PRECEDENTEMENTE*/
        boolean procedi=false;
        switch (position){
            case 0:
                if(prefs.getString("ROCKET","rocket1").equals("rocket1"))
                    procedi=true;
                break;

            case 1:
                if(prefs.getString("ROCKET","rocket1").equals("rocket2"))
                    procedi=true;
                break;

            case 2:
                if(prefs.getString("ROCKET","rocket1").equals("rocket3"))
                    procedi=true;
                break;

            case 3:
                if(prefs.getString("ROCKET","rocket1").equals("rocket4"))
                    procedi=true;
                break;

            case 4:
                if(prefs.getString("ROCKET","rocket1").equals("rocket5"))
                    procedi=true;
                break;

            case 5:
                if(prefs.getString("ROCKET","rocket1").equals("rocket6"))
                    procedi=true;
                break;

            case 6:
                if(prefs.getString("ROCKET","rocket1").equals("rocket7"))
                    procedi=true;
                break;

            case 7:
                if(prefs.getString("TERRAIN","mars").equals("mars"))
                    procedi=true;
                break;

            case 8:
                if(prefs.getString("TERRAIN","mars").equals("neptune"))
                    procedi=true;
                break;

            case 9:
                if(prefs.getString("TERRAIN","mars").equals("moon"))
                    procedi=true;
                break;

            case 10:
                if(prefs.getString("TERRAIN","mars").equals("earth"))
                    procedi=true;
                break;
        }


        if(procedi)
        {
            b2.setText("Selezionato");
            b2.setBackgroundColor(Color.GREEN);
        }
        else
        {
            b2.setText("Seleziona");
            b2.setBackgroundColor(Color.YELLOW);
        }

        b1.setText(buttonSelezionato.getText());

        b1.setTag(position);
        b2.setTag(position);

        return v;
    }

    /*RIDISEGNO LA LISTA*/
    public void aggiorna(){
        notifyDataSetChanged();
    }

}
