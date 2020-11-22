package it.unisa.pietrodellisola.gorocketgo;

import java.util.Random;

/**
 * Created by pietr on 21/12/2017.
 */

public class ObstaclesLogic {
    private Random random;
    public ObstaclesLogic(){

    }

    /*
    QUESTO METODO RITORNA UN ARRAY DI VARIABILI BOOLEANE, INDICA QUALI OSTACOLI DEI 5 DOVRANNO
    ESSERE NASCOSTI E QUALI VISIBILI, TUTTO GENERATO IN MANIERA CASUALE
    */
    public boolean[] ostacoliVisibili(){
        boolean[] array = new boolean[5];
        array[0]=false;
        array[1]=false;
        array[2]=false;
        array[3]=false;
        array[4]=false;
        random = new Random();
        int scelta=random.nextInt(17)+0;

        switch (scelta){
            case 0: array[0]=true;
                    array[1]=true;
                    array[3]=true;
                    break;
            case 1: array[1]=true;
                    array[2]=true;
                    array[4]=true;
                    break;

            case 2: array[0]=true;
                    array[2]=true;
                    array[3]=true;
                    break;
            case 3: array[1]=true;
                    array[3]=true;
                    array[4]=true;
                    break;
            case 4: array[0]=true;
                    array[1]=true;
                    array[2]=true;
                    break;
            case 5: array[1]=true;
                    array[2]=true;
                    array[3]=true;
                    break;
            case 6: array[2]=true;
                    array[3]=true;
                    array[4]=true;
                    break;
            case 7: array[0]=true;
                    array[1]=true;
                    break;
            case 8: array[0]=true;
                    array[2]=true;
                    break;
            case 9: array[0]=true;
                    array[3]=true;
                    break;
            case 10:array[0]=true;
                    array[4]=true;
                    break;
            case 11:array[1]=true;
                    array[2]=true;
                    break;
            case 12:array[1]=true;
                    array[3]=true;
                    break;
            case 13:array[1]=true;
                    array[4]=true;
                    break;
            case 14:array[2]=true;
                    array[3]=true;
                    break;
            case 15:array[3]=true;
                    array[4]=true;
                    break;
            case 16:array[2]=true;
                    array[4]=true;
                    break;
            case 17:array[3]=true;
                    array[4]=true;
                    break;
        }

        return array;
    }

}
