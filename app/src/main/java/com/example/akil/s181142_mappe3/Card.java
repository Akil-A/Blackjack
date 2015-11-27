package com.example.akil.s181142_mappe3;

import android.content.Context;
import android.content.res.Resources;

public class Card {

    private String mSuit;
    private int mValue;
    private String[] suits = {"clubs", "diamonds", "hearts", "spades"};

    public Card(){}

    public Card(String suit, int value){
        this.mSuit = suit;
        this.mValue = value;
    }

    public String getSuit(){
        return mSuit;
    }

    public String[] getSuits(){
        return suits;
    }

    public int getValue(){
        return mValue;
    }
    
    public int getCard(Context context){

        //Get the correct card from drawables
        return context.getResources().getIdentifier(mSuit+mValue, "drawable", context.getPackageName());
    }
}
