package it.unisa.pietrodellisola.gorocketgo;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class PlayActivity extends Activity implements SensorEventListener {

    private ValueAnimator animator;

    private Point size;
    private DisplayMetrics metrics;

    private Toast countdownToast;
    private ImageView terrain, rocket, fire, smoke,shield, crash;
    private Animation[] animations;
    private MediaPlayer rocket_launcher_sound,space_sound;
    private Handler handler1,handler2;
    private int altezzaForma,larghezzaForma; //altezza ed altezza delle forme rispetto alla grandezza dello schermo
    private ImageView[] ostacoli;

    private ObstaclesLogic ol;
    private boolean[] arrayOstacoliVisibili;
    private boolean[] updateFascia;//NECESSARI PER SINCRONIZZARE
            //ALTRIMENTI FA OGNI VOLTA LE OPERAZIONI CHE DOVREBBE FARE UNA VOLTA OGNI TOT SECONDI

    public int x = 0;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private boolean partenza=false,fine=false,stopAnimOnBack=false;

    //PER AGGIORNARE LA DISTANZA PERCORSA
    //Ma = ms * 0.0029411764705872
    private TextView distanzaPercorsa;
    private int dist;

    private String controllo;
    private RelativeLayout relativeLayout;

    private boolean scudoAttivo;
    private int resIdShield, resIdAsteroid;  //resIdShield = blueShield
    private TextView tvcountdown;
    private int countdownValue;
    private int resIdVioletShield;          //resIdVioletShield = violetShield

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(it.unisa.pietrodellisola.gorocketgo.R.layout.activity_play);
        handler1=new Handler();
        handler2=new Handler();
        rocket_launcher_sound=null;
        space_sound=null;
        scudoAttivo=false;

        ol = new ObstaclesLogic();
        arrayOstacoliVisibili = new boolean[5];
        updateFascia = new boolean[3];
        updateFascia[0]=true;
        updateFascia[1]=true;
        updateFascia[2]=true;

        dimensioneSchermo(); // AGGIORNO LE VARIABILI CHE FANNO RIFERIMENTO ALLE PROPRIETA' DELLO SCHERMO

        terrain=(ImageView)findViewById(it.unisa.pietrodellisola.gorocketgo.R.id.idbg_terrain);
        rocket=(ImageView)findViewById(it.unisa.pietrodellisola.gorocketgo.R.id.rocket);

        //RECUPERO L'ID DELLA RISORSA GRAZIE AD IL NOME E L'AGGIORNO
        int resIdRocket = getResources().getIdentifier((getIntent().getExtras().getString("ROCKET"))
                , "drawable", getPackageName());
        int resIdTerrain = getResources().getIdentifier((getIntent().getExtras().getString("TERRAIN"))
                , "drawable", getPackageName());
        rocket.setBackgroundResource(resIdRocket);
        terrain.setBackgroundResource(resIdTerrain);

        /*AGGIORNO LA DIMESIONE DEL RAZZO RISPETTO ALLE DIMENSIONI DELLO SCHERMO*/
        altezzaForma=(size.y)/6;
        larghezzaForma=(size.x)/5;
        rocket.getLayoutParams().height=altezzaForma;
        rocket.getLayoutParams().width=larghezzaForma;

        /*AGGIORNO LA DIMESIONE DELLO SCUDO RISPETTO ALLE DIMENSIONI DELLO SCHERMO*/
        shield=(ImageView)findViewById(it.unisa.pietrodellisola.gorocketgo.R.id.shield);
        shield.getLayoutParams().height=altezzaForma;
        shield.getLayoutParams().width=altezzaForma;

        tvcountdown=(TextView)findViewById(it.unisa.pietrodellisola.gorocketgo.R.id.tvcountdown);
        distanzaPercorsa=(TextView)findViewById(it.unisa.pietrodellisola.gorocketgo.R.id.tvDistanzaPercorsa);
        ostacoli=new ImageView[15];
        for (int i = 0; i < 15; i++) {
            int id = getResources().getIdentifier("ostacolo"+(i+1), "id", getPackageName());
            ostacoli[i] = (ImageView) findViewById(id);
            ostacoli[i].getLayoutParams().height=larghezzaForma;
            ostacoli[i].getLayoutParams().width=larghezzaForma;
            ostacoli[i].setY(-larghezzaForma); //LI AGGINGO FUORI DALLO SCHERMO
        }

        fire=(ImageView)findViewById(it.unisa.pietrodellisola.gorocketgo.R.id.fire);
        smoke=(ImageView)findViewById(it.unisa.pietrodellisola.gorocketgo.R.id.smoke);
        /*AGGIORNO LA DIMESIONE DELL'IMMAGINE CRASH RISPETTO ALLE DIMENSIONI DELLO SCHERMO*/
        crash=(ImageView)findViewById(it.unisa.pietrodellisola.gorocketgo.R.id.crash);
        crash.getLayoutParams().height=altezzaForma;
        crash.getLayoutParams().width=altezzaForma;

        animations = new Animation[3];
        animations[0]= AnimationUtils.loadAnimation(getApplicationContext(), it.unisa.pietrodellisola.gorocketgo.R.anim.terrain_animation);
        animations[1]= AnimationUtils.loadAnimation(getApplicationContext(), it.unisa.pietrodellisola.gorocketgo.R.anim.fire_animation);
        animations[2]= AnimationUtils.loadAnimation(getApplicationContext(), it.unisa.pietrodellisola.gorocketgo.R.anim.smoke_animation);

        //NECESSARIO PER AGGIUNGERE LO SCUDO AL POSTO DELL'ASTEROIDE INVISIBILE
        resIdShield = getResources().getIdentifier(("shield")
                , "drawable", getPackageName());
        //NECESSARIO PER SOSTITUIRE LO SCUDO DI NUOVO IN ASTEROIDE
        resIdAsteroid = getResources().getIdentifier(("asteroid2")
                , "drawable", getPackageName());
        //NECESSARIO PER SOSTITUIRE LO SCUDO BLU IN VIOLA
        resIdVioletShield = getResources().getIdentifier(("shield2")
                , "drawable", getPackageName());

        relativeLayout=(RelativeLayout)findViewById(it.unisa.pietrodellisola.gorocketgo.R.id.relativeLayout);

        countdown();

        inizioScorrimentoBackground();

    } //fine onCreate



    class LogicTask extends AsyncTask<Void,Void,Void> {

        @Override
        protected void onPreExecute() {
            dist=0;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            while (!fine) {
                /* AGGIORNO DISTANZA PERCORSA */
			    publishProgress();
                sleep();
            }

            return null;
        }


        @Override
        protected void onProgressUpdate(Void... values) {  //PARAMETRO ESPLICITO RECORD
            dist+=3;//AGGIORNO DISTANZA PERCORSA

            //AGGIORNO DISTANZA PERCORSA
            distanzaPercorsa.setText("DISTANZA PERCORSA: "+dist+" km");
            //Ma = ms * 0.0029411764705872

            if(scudoAttivo)
            {
                if(countdownValue>2)
                    {
                        countdownValue--;
                        tvcountdown.setText(""+countdownValue);
                    }
                else {
                    tvcountdown.setVisibility(View.INVISIBLE); //RENDO INVISIBILE IL COUNTDOWN PER LO SCUDO

                    shield.setVisibility(View.INVISIBLE); //DISATTIVO LO SCUDO SOTTO IL RAZZO
                    scudoAttivo=false; //DISATTIVO LO SCUDO A LIVELLO LOGICO
                    for(ImageView iv: ostacoli){
                        if(iv.getTag().equals("scudo"))
                            iv.setBackgroundResource(resIdShield);
                    }
                }
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            crash.setX(rocket.getX());
            crash.setY(rocket.getY());
            crash.setVisibility(View.VISIBLE);

            animator.end(); //FERMO L'ANIMAZIONE
            if(!stopAnimOnBack) {
                /*SE VIENE RICHIAMATO IL POSTEXECUTE DAL RAZZO CHE SBATTE CONTRO IL METEORITE ESEGUO GAMEOVER DOPO 0.4 sec*/

                handler1.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        gameOver();

                    }
                }, 400);
            }
            /* SE È ONBACKPRESSED A FERMARE IL SECONDO THREAD NON CHAMERA' GAMEOVER, E TORNERA' ALLA
            * SCHERMATA PRINCIPALE VISUALIZZANDO SOLTANTO IL "CRASH" NEL CASO IL RAZZO SIA PARTITO*/

        }

        private void sleep() {
			/* Ritardo di 0,1 secondi */
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    public void countdown(){

        /*AUDIO E STAMPA NUMERI PER CONTO ALLA ROVESCIA CON GESTIONE ANIMAZIONI FUOCO E FUMO*/
        rocket_launcher_sound = MediaPlayer.create(getApplicationContext(), it.unisa.pietrodellisola.gorocketgo.R.raw.rocket_launcher_sound);
        rocket_launcher_sound.start();

        countdownToast=toastPersonalizzato("3");

        countdownToast.show();

        handler1.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(fine)return; //SE PREMO IL PULSANTE BACK ANNULLO TUTTO
                fire.setVisibility(View.VISIBLE);
                fire.startAnimation(animations[1]);
                countdownToast.cancel();
                countdownToast=toastPersonalizzato("2");
                countdownToast.show();
            }
        }, 1000);

        handler1.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(fine)return; //SE PREMO IL PULSANTE BACK ANNULLO TUTTO
                countdownToast.cancel();
                countdownToast=toastPersonalizzato("1");
                countdownToast.show();
            }
        }, 2000);

        handler1.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(fine)return; //SE PREMO IL PULSANTE BACK ANNULLO TUTTO
                fire.setVisibility(View.INVISIBLE);
                smoke.setVisibility(View.VISIBLE);
                smoke.startAnimation(animations[2]);
                countdownToast.cancel();
            }
        }, 3000);

        handler1.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(fine)return; //SE PREMO IL PULSANTE BACK ANNULLO TUTTO
                countdownToast=null;
                rocket_launcher_sound.stop();
                rocket_launcher_sound.release();
                rocket_launcher_sound=null;
                space_sound = MediaPlayer.create(getApplicationContext(), it.unisa.pietrodellisola.gorocketgo.R.raw.deep_space);
                space_sound.setLooping(true);
                space_sound.start();
                //INIZIO A MUOVERMI E CONTO LA SITANZA PERCORSA
                new LogicTask().execute();

            }
        }, 4100);

    }


    public void inizioScorrimentoBackground(){

        /*INIZIO ANIMAZIONE SCORRIMENTO BACKGROUND STELLE E TERRENO*/

        handler1.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(fine==true)return;
                cieloStellato();
                terrain.startAnimation(animations[0]);


                handler2.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        partenza=true;  // ABILITO IL CONTROLLO ALL'UTENTE SOLO DOPO IL COUNTDOWN
                        terrain.setVisibility(View.INVISIBLE);
                        smoke.setVisibility(View.INVISIBLE);

                    }
                }, 1500); //fine delay interno


            }
        }, 3100);

    }


    public Toast toastPersonalizzato(String numero){

        Toast toast = new Toast(getApplicationContext());
        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        View layout = inflater.inflate(it.unisa.pietrodellisola.gorocketgo.R.layout.custom_toast, null);
        TextView tvnumero=(TextView)layout.findViewById(it.unisa.pietrodellisola.gorocketgo.R.id.countdown);
        tvnumero.setText(numero);

        toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);

        return toast;
    } //fine toastPersonalizzato


    public void cieloStellato(){
        if(fine==true)return;

        final ImageView backgroundOne = (ImageView) findViewById(it.unisa.pietrodellisola.gorocketgo.R.id.bg1);
        final ImageView backgroundTwo = (ImageView) findViewById(it.unisa.pietrodellisola.gorocketgo.R.id.bg2);

        animator = ValueAnimator.ofFloat(0.0f, 1.0f);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());
        final int durataAnimator=calcoloDurataAnimator();
        animator.setDuration(durataAnimator);//in milliseoondi
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                final float progress = (float) animation.getAnimatedValue();
                final float height = backgroundOne.getHeight();
                final float translationY = height * progress;
                backgroundOne.setTranslationY(translationY);
                backgroundTwo.setTranslationY(translationY - height);

                //ANIMAZIONI PER OSTACOLI
                final float translationY2 = height * progress;

                ostacoli[0].setTranslationY(translationY2);
                ostacoli[1].setTranslationY(translationY2);
                ostacoli[2].setTranslationY(translationY2);
                ostacoli[3].setTranslationY(translationY2);
                ostacoli[4].setTranslationY(translationY2);

                handler1.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        //questi si muovono successivamente
                        ostacoli[5].setTranslationY(translationY2);
                        ostacoli[6].setTranslationY(translationY2);
                        ostacoli[7].setTranslationY(translationY2);
                        ostacoli[8].setTranslationY(translationY2);
                        ostacoli[9].setTranslationY(translationY2);

                    }
                }, (int)durataAnimator/3);

                handler1.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        //questi si muovono successivamente
                        ostacoli[10].setTranslationY(translationY2);
                        ostacoli[11].setTranslationY(translationY2);
                        ostacoli[12].setTranslationY(translationY2);
                        ostacoli[13].setTranslationY(translationY2);
                        ostacoli[14].setTranslationY(translationY2);

                    }
                }, (int)((durataAnimator/3)*2)); //fine delay interno

                //AGGIORNLO OSTACOLI
                aggiornamentoOstacoli(0,5, 0);
                aggiornamentoOstacoli(5,10, 1);
                aggiornamentoOstacoli(10,15, 2);

                //CONTROLLO SE IL RAZZO URTA CON GLI OSTACOLI

                int i=0;
                int partizioneLarghezzaForma=larghezzaForma/4;//AGGIUNGO partizioneLarghezzaForma PER NON FARLO "SBATTERE " SUBITO
                //PRIMA FASCIA
                //SE SI TROVA SOPRA E NON SBATTE CON LA PARTE BASSA SULL'ASTEROIDE
                if((rocket.getY()>((ostacoli[0].getY())+(partizioneLarghezzaForma-larghezzaForma)))&&   //MEGLIO LARGHEZZA FORMA AL
                                                                                                // POSTO DI ALTEZZA FORMA PER DARE PIU' CHANCE
                        //SE IL RAZZO SI TROVA POCO SOTTO LA FASCIA DI OSTACOLI PRESA IN CONSIDERAZIONE
                        (rocket.getY()<((ostacoli[0].getY()+larghezzaForma)-partizioneLarghezzaForma)))
                    for (i=0;i<5;i++){
                        if(ostacoli[i].getVisibility()==View.VISIBLE) //SOLTANTO SE E' VISIBILE
                            if( // LATO SINISTRO
                                    ((rocket.getX()>=(ostacoli[i].getX()+partizioneLarghezzaForma))&& //AGGIUNGO partizioneLarghezzaForma PER NON FARLO "SBATTERE " SUBITO
                                            (rocket.getX()<(ostacoli[i].getX()+larghezzaForma)-partizioneLarghezzaForma))||
                                            //CENTRALE
                                            ((rocket.getX()+(larghezzaForma/2)>=(ostacoli[i].getX()+partizioneLarghezzaForma))&&
                                                    (rocket.getX()+(larghezzaForma/2)<(ostacoli[i].getX()+larghezzaForma)-partizioneLarghezzaForma))||
                                            // LATO DESTRO
                                            ((rocket.getX()+larghezzaForma>=(ostacoli[i].getX()+partizioneLarghezzaForma))&& //AGGIUNGO partizioneLarghezzaForma PER NON FARLO "SBATTERE " SUBITO
                                                    (rocket.getX()+larghezzaForma<(ostacoli[i].getX()+larghezzaForma)-partizioneLarghezzaForma))
                                    )

                            {
                                //SE L'OSTACOLO RAPPRESENTA TEMPORANEAMENTE UNO SCUDO ED IL RAZZO CI PASSA SOPRA
                                if((!scudoAttivo)&&(ostacoli[i].getTag().equals("scudo")))
                                    {
                                        tvcountdown.setVisibility(View.VISIBLE);
                                        shield.setY(rocket.getY());
                                        shield.setX((rocket.getX()+larghezzaForma/2)-(altezzaForma/2));
                                        shield.setVisibility(View.VISIBLE);

                                        countdownValue=42; //MODIFICO IL COUNTDOWN QUANDO PASSO SULLO SCUDO
                                                            //SE MENTRE E' ANCORA ATTIVO LO SCUDO E PASSO SU UN ALTRO SCUDO,
                                                            //IL COUNTDOWN NON SI RINNOVA
                                        scudoAttivo=true;
                                        for(ImageView iv: ostacoli){
                                            if(iv.getTag().equals("scudo"))
                                                iv.setBackgroundResource(resIdVioletShield);
                                        }
                                    }

                                if(!scudoAttivo)
                                    fine=true;
                            }

                    }

                //SECONDA FASCIA
                //SE SI TROVA SOPRA E NON SBATTE CON LA PARTE BASSA SULL'ASTEROIDE
                if((rocket.getY()>((ostacoli[5].getY())+(partizioneLarghezzaForma-larghezzaForma)))&&   //MEGLIO LARGHEZZA FORMA AL
                                                                                                // POSTO DI ALTEZZA FORMA PER DARE PIU' CHANCE
                        //SE IL RAZZO SI TROVA POCO SOTTO LA FASCIA DI OSTACOLI PRESA IN CONSIDERAZIONE
                        (rocket.getY()<((ostacoli[5].getY()+larghezzaForma)-partizioneLarghezzaForma)))
                    for (i=5;i<10;i++){
                        if(ostacoli[i].getVisibility()==View.VISIBLE) //SOLTANTO SE E' VISIBILE
                            if( // LATO SINISTRO
                                    ((rocket.getX()>=(ostacoli[i].getX()+partizioneLarghezzaForma))&& //AGGIUNGO partizioneLarghezzaForma PER NON FARLO "SBATTERE " SUBITO
                                            (rocket.getX()<(ostacoli[i].getX()+larghezzaForma)-partizioneLarghezzaForma))||
                                            //CENTRALE
                                            ((rocket.getX()+(larghezzaForma/2)>=(ostacoli[i].getX()+partizioneLarghezzaForma))&&
                                                    (rocket.getX()+(larghezzaForma/2)<(ostacoli[i].getX()+larghezzaForma)-partizioneLarghezzaForma))||
                                            // LATO DESTRO
                                            ((rocket.getX()+larghezzaForma>=(ostacoli[i].getX()+partizioneLarghezzaForma))&& //AGGIUNGO partizioneLarghezzaForma PER NON FARLO "SBATTERE " SUBITO
                                                    (rocket.getX()+larghezzaForma<(ostacoli[i].getX()+larghezzaForma)-partizioneLarghezzaForma))
                                    )

                            {
                                //SE L'OSTACOLO RAPPRESENTA TEMPORANEAMENTE UNO SCUDO ED IL RAZZO CI PASSA SOPRA
                                if((!scudoAttivo)&&(ostacoli[i].getTag().equals("scudo")))
                                    {
                                        tvcountdown.setVisibility(View.VISIBLE);
                                        shield.setY(rocket.getY());
                                        shield.setX((rocket.getX()+larghezzaForma/2)-(altezzaForma/2));
                                        shield.setVisibility(View.VISIBLE);

                                        countdownValue=42; //MODIFICO IL COUNTDOWN QUANDO PASSO SULLO SCUDO
                                                            //SE MENTRE E' ANCORA ATTIVO LO SCUDO E PASSO SU UN ALTRO SCUDO,
                                                            //IL COUNTDOWN NON SI RINNOVA
                                        scudoAttivo=true;
                                        for(ImageView iv: ostacoli){
                                            if(iv.getTag().equals("scudo"))
                                                iv.setBackgroundResource(resIdVioletShield);
                                        }
                                    }

                                if(!scudoAttivo)
                                    fine=true;
                            }

                    }

                //TERZA FASCIA
                //SE SI TROVA SOPRA E NON SBATTE CON LA PARTE BASSA SULL'ASTEROIDE
                if((rocket.getY()>((ostacoli[10].getY())+(partizioneLarghezzaForma-larghezzaForma)))&&      //MEGLIO LARGHEZZA FORMA AL
                                                                                                    // POSTO DI ALTEZZA FORMA PER DARE PIU' CHANCE
                        //SE IL RAZZO SI TROVA POCO SOTTO LA FASCIA DI OSTACOLI PRESA IN CONSIDERAZIONE
                        (rocket.getY()<((ostacoli[10].getY()+larghezzaForma)-partizioneLarghezzaForma)))
                    for (i=10;i<15;i++){
                        if(ostacoli[i].getVisibility()==View.VISIBLE) //SOLTANTO SE E' VISIBILE
                            if( // LATO SINISTRO
                                    (((rocket.getX()>=(ostacoli[i].getX()+partizioneLarghezzaForma))&& //AGGIUNGO partizioneLarghezzaForma PER NON FARLO "SBATTERE " SUBITO
                                            (rocket.getX()<(ostacoli[i].getX()+larghezzaForma)-partizioneLarghezzaForma))||
                                            //CENTRALE
                                            ((rocket.getX()+(larghezzaForma/2)>=(ostacoli[i].getX()+partizioneLarghezzaForma))&&
                                                    (rocket.getX()+(larghezzaForma/2)<(ostacoli[i].getX()+larghezzaForma)-partizioneLarghezzaForma))||
                                            // LATO DESTRO
                                            ((rocket.getX()+larghezzaForma>=(ostacoli[i].getX()+partizioneLarghezzaForma))&& //AGGIUNGO partizioneLarghezzaForma PER NON FARLO "SBATTERE " SUBITO
                                                    (rocket.getX()+larghezzaForma<(ostacoli[i].getX()+larghezzaForma)-partizioneLarghezzaForma)))
                                    )

                            {
                                //SE L'OSTACOLO RAPPRESENTA TEMPORANEAMENTE UNO SCUDO ED IL RAZZO CI PASSA SOPRA
                                if((!scudoAttivo)&&(ostacoli[i].getTag().equals("scudo")))
                                    {
                                        tvcountdown.setVisibility(View.VISIBLE);
                                        shield.setY(rocket.getY());
                                        shield.setX((rocket.getX()+larghezzaForma/2)-(altezzaForma/2));
                                        shield.setVisibility(View.VISIBLE);

                                        countdownValue=42; //MODIFICO IL COUNTDOWN QUANDO PASSO SULLO SCUDO
                                                            //SE MENTRE E' ANCORA ATTIVO LO SCUDO E PASSO SU UN ALTRO SCUDO,
                                                            //IL COUNTDOWN NON SI RINNOVA
                                        scudoAttivo=true;
                                        for(ImageView iv: ostacoli){
                                            if(iv.getTag().equals("scudo"))
                                                iv.setBackgroundResource(resIdVioletShield);
                                        }
                                    }

                                if(!scudoAttivo)
                                    fine=true;
                            }

                    }


            }
        });
        animator.start();

    } //fine cieloStellato

    /*QUESTO METODO AGGIORNA GI OSTACOLI VISIBILI E QUELLI NON VISIBILI*/
    public void aggiornamentoOstacoli(int i, int dim, int numeroFasciaDaAggiornare){

        if ((ostacoli[i].getY()>=(size.y-5))) {
            updateFascia[numeroFasciaDaAggiornare] = true;
        }

        if ((ostacoli[i].getY()<10) && updateFascia[numeroFasciaDaAggiornare]==true ){
            //AGGIORNO LA FASCIA STAMPANDO ASTEROIDI PER GLI ELEMENTI VISIBILI
            arrayOstacoliVisibili=ol.ostacoliVisibili();
            int indiceArrayOstacoliVisibili=0; //SERVE PER SCORRERE L'ARRAY RITORNATO DALL'ISTRUZIONE PRECEDENTE

            for(int k=i;k<dim;k++){
                if(arrayOstacoliVisibili[indiceArrayOstacoliVisibili]==true){

                    if(ostacoli[k].getTag().equals("scudo"))
                    {
                        //METTO L'OSTACOLO AL POSTO DELLO SCUDO
                        ostacoli[k].setTag("ostacolo");
                        ostacoli[k].setBackgroundResource(resIdAsteroid);
                    }

                    ostacoli[k].setVisibility(View.VISIBLE);
                }
                else  {
                    /*AGGIORNO LA FASCIA AGGIUNGEDO SCUDI AD ELEMENTI NON VISIBILI NEL CASO
                    * IL METODO aggiungiScudo PRELEVI IL NUMERO 1*/
                    ostacoli[k].setVisibility(View.INVISIBLE);
                    aggiungiScudo(ostacoli[k]); //DECIDE SE AGGIUNGERE UNO SCUDO AL POSTO DELL'ASTEROIDE NON PRESENTE

                }

                indiceArrayOstacoliVisibili++;
            }

            updateFascia[numeroFasciaDaAggiornare]=false;
        }

    }

    public void aggiungiScudo(ImageView ostacolo){
        if(((new Random()).nextInt(7)+0)==1){
            //AGGIUNGO LO SCUDO, RIMUOVO L'OSTACOLO, RENDO L'IMMMAGINE VISIBILE E CAMBIO IL TAG IN SCUDO

            ostacolo.setVisibility(View.VISIBLE);
            ostacolo.setTag("scudo");

            if(!scudoAttivo)ostacolo.setBackgroundResource(resIdShield); //SCUDO BLU
            else ostacolo.setBackgroundResource(resIdVioletShield); //SCUDO VIOLA
        }

    }

    public void dimensioneSchermo(){

        //Controlla la dimensione del display
        Display display = getWindowManager().getDefaultDisplay();
        metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        size = new Point();
        display.getSize(size);

        int screen_w = size.x;
        int screen_h = size.y;
        int dp_w = (int)(screen_w/metrics.density);
        int dp_h = (int)(screen_h/metrics.density);  //IN PX
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(controllo.equals("Manuale"))return;

        if ((partenza)&&(sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER)&&(!fine)) {

            if (
                    (rocket.getX() >= 5) && (sensorEvent.values[0] > 0) ||
                            (((rocket.getX() + larghezzaForma) <= (size.x + 5)) && (sensorEvent.values[0] < 0))
                    )
            {

                x = (int) rocket.getX(); //PRENDO L'ATTUALE POSIZIONE DEL RAZZO
                x -= (int) (sensorEvent.values[0]) * 3; //AGGIUNGO O SOTTRAGGO ALLA POSIZIONE ATTUALE, LO SPOSTAMENTO
                rocket.setX(x); //AGGIORNO LA POSSIZIONE DELL'IMMAGINE
                if(scudoAttivo)shield.setX((rocket.getX()+larghezzaForma/2)-(altezzaForma/2));
            }

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public void gameOver(){
        space_sound.stop();
        space_sound.release();
        space_sound=null;

        Intent i = new Intent(PlayActivity.this,GameOverActivity.class);
        i.putExtra("CURRENTRECORD",dist);
        startActivity(i);
        finish();
    }

    private int calcoloDurataAnimator(){
        int durata=0;
        switch(getIntent().getExtras().getString("DIFFICOLTA")){
            case "Facile":
                durata=5000;
                break;
            case "Medio":
                durata=4000;
                break;
            case "Difficile":
                durata=3000;
                break;
        }
        return durata;
    }

    @Override
    protected void onResume() {
        super.onResume();

        controllo=getIntent().getExtras().getString("COMANDI");

        if(controllo.equals("Accelerometro")) {
            //CONTROLLO
            if ((null==sensorManager)||(null==accelerometer)){
                sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
                accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            }

            sensorManager.registerListener(this, accelerometer,
                    SensorManager.SENSOR_DELAY_GAME); //  Velocità di campionamento 0,02sec
            fine=false; //NON HO PREMUTO IL TASTO BACK

        }
        else
        {
            //if(controllo.equals("Manuale"))

            relativeLayout.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if((motionEvent.getActionMasked() == MotionEvent.ACTION_MOVE)&&(partenza)&&(!fine)){

                        if(((motionEvent.getX()- (larghezzaForma / 2))>5)&&
                                (motionEvent.getX()<(size.x-5- (larghezzaForma / 2))))
                        {
                            rocket.setX(motionEvent.getX() - (larghezzaForma / 2));
                            if(scudoAttivo)shield.setX((rocket.getX()+larghezzaForma/2)-(altezzaForma/2));
                            //AGGIORNO LA X DEL RAZZO
                        }
                    }

                    return true;
                }
            });


        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if(controllo.equals("Accelerometro")) {
            sensorManager.unregisterListener(this);
        }
        else relativeLayout.setOnTouchListener(null);

        //RIMUOVERE TUTTO, NON SALVO LO STATO
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        stopAnimOnBack=true; //NON FA ESEGUIRE LA FUNZIONE GAMEOVER ALLA TERMINAZIONE DEL SECONDO THREAD
        fine=true; //CICCO SUL PULSANTE BACK, BLOCCO TUTTI I TOAST CHE STO PER STAMPARE

        if ((rocket_launcher_sound!=null)&&(rocket_launcher_sound.isPlaying())) {
            countdownToast.cancel();
            countdownToast = null;
            rocket_launcher_sound.stop();
            rocket_launcher_sound.release();
            rocket_launcher_sound = null;
        }

        if((space_sound!=null)&&(space_sound.isPlaying()))
        {
            space_sound.stop();
            space_sound.release();
            space_sound=null;
        }

       finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

} //fine PlayActivity
