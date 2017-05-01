package com.mismatched.nowyouretalking;

/**
 * Created by jamie on 09/03/2017.
 */

public class ImageHelper {

    public int getImageForView(String imageText){

        // image order... English or German or Spanish or French
        switch (imageText) {
            case "Man":
            case "Mann":
            case "Hombre":
            case "Homme": {

                return R.drawable.man;
            }
            case "Boy":
            case "Junge":
            case "el nino":
            case "Garçon": {

                return R.drawable.boy;
            }
            case "Woman":
            case "Frau":
            case "Mujer":
            case "Femme": {

                return R.drawable.woman;
            }
            case "Bread":
            case "Brot":
            case "El pan":
            case "Pain": {

                return R.drawable.bread;
            }
            case "Water":
            case "Wasser":
            case "Agua":
            case "Eau": {

                return R.drawable.water;
            }
            case "Girl":
            case "Madchen":
            case "Nina":
            case "Fille": {

                return R.drawable.girl;
            }
            default:
                return 0;
        }

    }
}
