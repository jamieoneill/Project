package com.mismatched.nowyouretalking;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

/**
 * Created by jamie on 09/03/2017.
 */

public class ImageHelper {

    public int getImageForView(String imageText){

        if (imageText.equals("Man")|| imageText.equals("Mann")){

            int Selected = R.drawable.man;

            return Selected;
        }else if (imageText.equals("Boy")|| imageText.equals("Junge")){

            int Selected = R.drawable.boy;

            return Selected;
        }
        else if (imageText.equals("Woman")|| imageText.equals("Frau")){

            int Selected = R.drawable.woman;

            return Selected;
        }
        else if (imageText.equals("Bread")|| imageText.equals("Brot")){

            int Selected = R.drawable.bread;

            return Selected;
        } else if (imageText.equals("Water")|| imageText.equals("Wasser")){

            int Selected = R.drawable.water;

            return Selected;
        }
        else{
            return 0;
        }

    }
}
