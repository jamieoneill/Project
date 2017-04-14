package com.mismatched.nowyouretalking;

/**
 * Created by jamie on 09/03/2017.
 */

public class ImageHelper {

    public int getImageForView(String imageText){


        // image order... English or German or Spanish or French
        if (imageText.equals("Man")|| imageText.equals("Mann") || imageText.equals("Hombre") || imageText.equals("Homme")){

            int Selected = R.drawable.man;

            return Selected;
        }else if (imageText.equals("Boy")|| imageText.equals("Junge") || imageText.equals("el nino") || imageText.equals("Garçon")){

            int Selected = R.drawable.boy;

            return Selected;
        }
        else if (imageText.equals("Woman")|| imageText.equals("Frau")|| imageText.equals("Mujer") || imageText.equals("Femme")){

            int Selected = R.drawable.woman;

            return Selected;
        }
        else if (imageText.equals("Bread")|| imageText.equals("Brot")|| imageText.equals("El pan")|| imageText.equals("Pain")){

            int Selected = R.drawable.bread;

            return Selected;
        } else if (imageText.equals("Water")|| imageText.equals("Wasser") || imageText.equals("Agua") || imageText.equals("Eau")){

            int Selected = R.drawable.water;

            return Selected;
        }
        else if (imageText.equals("Girl")|| imageText.equals("Madchen") || imageText.equals("Nina") || imageText.equals("Fille")){

            int Selected = R.drawable.girl;

            return Selected;
        }
        else{
            return 0;
        }

    }
}
