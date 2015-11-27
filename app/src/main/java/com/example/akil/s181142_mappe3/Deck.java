package com.example.akil.s181142_mappe3;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Deck {

    private ArrayDeque<Card> mDeck;
    private List<Card> mTempDeck;
    private Card card = new Card();

    public Deck(){
        String[] suits = card.getSuits();
        mTempDeck = new ArrayList<>();

        for(int i = 0; i < 4; i++)
            for(int j = 2; j < 15; j++)
                mTempDeck.add(new Card(suits[i],j));
    }

    public void shuffle(){
        Collections.shuffle(mTempDeck);
        mDeck = new ArrayDeque<>(mTempDeck);
        mTempDeck.clear();
    }

    public Card drawCard(){
        Card card = mDeck.getFirst();
        mDeck.removeFirst();
        return card;
    }
}
