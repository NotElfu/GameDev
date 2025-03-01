package com.sinulog.gamedev.graphics;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import com.sinulog.gamedev.R;

public class SpriteSheet {
    //private Bitmap bitmap;
    private Bitmap spriteRight;
    private Bitmap spriteLeft;


    public SpriteSheet(Context context) {
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inScaled = false;
        spriteRight = BitmapFactory.decodeResource(context.getResources(), R.drawable.player_sprite1, bitmapOptions);
        spriteLeft = BitmapFactory.decodeResource(context.getResources(), R.drawable.player_sprite2, bitmapOptions);
    }
    public Sprite[] getPlayerSpriteArray(boolean facingRight){
        Sprite[] spriteArray = new Sprite [6];
        Bitmap currentSpriteSheet = facingRight ? spriteRight :spriteLeft;

        //Moving Loop
        for (int i = 0; i<6; i++) {
            spriteArray[i] = new Sprite(this, new Rect(i*32, 0, (i+1) * 32, 32), facingRight);
        }
        return spriteArray;
    }

    public Bitmap getBitmap(boolean facingRight) {
        return facingRight ? spriteRight:spriteLeft;
    }
}
