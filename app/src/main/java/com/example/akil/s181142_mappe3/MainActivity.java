package com.example.akil.s181142_mappe3;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayDeque;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity {

    private Button mHitButton, mStandButton, mDealButton;
    private boolean mHitPressed, mFirstTimeDealer = true, mRoundFinished = false;
    private TextView mBetHolder, mPlayerPointHolder, mDealerPointHolder, mMessageHolder;
    private Player mPlayer, mDealer;
    private Deck mDeck;
    private RelativeLayout mPlayerCardHolder, mDealerCardHolder;
    private ImageView mDealerHiddenCard;
    private ArrayDeque<Card> mPlayerHand, mDealerHand;
    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(getString(R.string.blackjack_title));

        mPlayerCardHolder = (RelativeLayout)findViewById(R.id.player_card_holder);
        mDealerCardHolder = (RelativeLayout)findViewById(R.id.dealer_card_holder);
        mPlayerPointHolder = (TextView)findViewById(R.id.point_holder);
        mDealerPointHolder = (TextView)findViewById(R.id.dealer_point_holder);
        mDealerHiddenCard = (ImageView)findViewById(R.id.imageView12);
        mBetHolder = (TextView)findViewById(R.id.bet_holder);

        //Get money and refills from sharedpreferences
        mSharedPreferences = getSharedPreferences("com.example.s181142_mappe3.STATISTIC_DATA", MODE_PRIVATE);
        int refills = mSharedPreferences.getInt("com.example.s181142_mappe3.Refills", 0);
        int money = mSharedPreferences.getInt("com.example.s181142_mappe3.Money", 1500);
        mPlayer = new Player("player");
        mDealer = new Player("dealer");
        mPlayer.setRefills(refills);
        mPlayer.setMoney(money);
        mBetHolder.setText(String.valueOf(0));
        mMessageHolder = (TextView)findViewById(R.id.message_holder);

        mHitButton = (Button)findViewById(R.id.hit_button);
        mHitButton.setVisibility(View.INVISIBLE);
        mHitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHitPressed = true;
                decision(mPlayer, mDeck);
            }
        });

        mStandButton = (Button)findViewById(R.id.stand_button);
        mStandButton.setVisibility(View.INVISIBLE);
        mStandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRoundFinished = true;
                mPlayer.setStand(true);
                mHitButton.setVisibility(View.INVISIBLE);
                mStandButton.setVisibility(View.INVISIBLE);
                whoWon(mPlayer, mDeck);
            }
        });

        mDealButton = (Button)findViewById(R.id.deal_button);
        mDealButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDealButton.setVisibility(View.INVISIBLE);
                start();
            }
        });
    }

    public int score(Player player){ //Calculates players/dealers score

        int playerScore = 0;
        int aces = 0;
        int value;
        ArrayDeque<Card> hand = player.getHand();

        for(Iterator iterator = hand.iterator(); iterator.hasNext();){
            Card card = (Card)iterator.next();
            value = card.getValue();

            if(value > 11)
                playerScore +=10;
            else
                playerScore += value;

            if(value == 11) //Check how many aces player have
                aces++;

            while(playerScore > 21 && aces != 0){ //If player has more points than 21
                playerScore -=10;                 //then change ace value from 11 to 1
                aces--;
            }

            if(playerScore > 21)
                player.setBusted(true);

            if(playerScore == 21 && player.getHand().size() == 2)
                player.setBlackjack(true);
        }
        return playerScore;
    }

    public void setHand(Player player, RelativeLayout layout) {
        score(player);

        int i = 0;
        ArrayDeque<Card> hand = player.getHand();
        for (Card card : hand) {

            int cardResource = card.getCard(getApplicationContext()); //Draws card from drawables

            if (i < layout.getChildCount()) {
                View v = layout.getChildAt(i);  //Loops through all ImageViews
                if (v instanceof ImageView) {
                    //Hide Dealers second card
                    if(player.getName().equals("dealer") && player.getHand().size() == 2 && i == 1 && mFirstTimeDealer) {
                        ((ImageView) v).setImageResource(getResources().getIdentifier("facedown", "drawable", getPackageName()));
                        mFirstTimeDealer = false;
                    }
                    else
                        ((ImageView) v).setImageResource(cardResource);  //Sets drawn card on right place
                }
                i++;
            }
        }
    }

    public void bet(Player player) {

        int playerBet = 0;

        if (player.getMoney() != 0) {  //If player has money show Bet button and get the bet
            mBetHolder.setVisibility(View.VISIBLE);
            double tempBet = mBetHolder.getText().toString().equals("") ? 0 : Double.parseDouble(mBetHolder.getText().toString());
            playerBet = (int)tempBet;
        }

        if(playerBet > 0 && playerBet <= player.getMoney()){  //If it's correctly sized bet
            player.setBet(playerBet);                         //then update players variables
            player.setMoney((player.getMoney() - (player.getBet())));
            invalidateOptionsMenu();
            player.setIsBet(true);
            mBetHolder.setEnabled(false);
        }
    }

    public void dealer(Player dealer, Deck deck) { //When it's dealers time to get cards
                                                   //deal the cards while dealers points are < 17
        while(score(dealer) < 17)
            dealer.getHand().addLast(deck.drawCard());

        setHand(dealer, mDealerCardHolder);
    }

    public void whoWon(Player player, Deck deck){ //Check who won the round

        dealer(mDealer, deck);
        int dealerScore = score(mDealer);
        int playerScore = score(player);

        if(mDealer.getBlackjack()) { //Dealer has Blackjack

            if(!player.isBusted()){
                player.setBusted(true);
                mDealButton.setVisibility(View.VISIBLE);
                mMessageHolder.setText(R.string.dealer_blackjack_message);
                mRoundFinished = true;
            }
        } else if(dealerScore > 21) { //Dealer has busted

            if(!player.isWon() && !player.isBusted()) {
                player.setWon(true);
                player.setMoney((player.getMoney()) + (player.getBet() * 2));
                mDealButton.setVisibility(View.VISIBLE);
                mMessageHolder.setText(R.string.dealer_busted_message);
                mRoundFinished = true;
            }

        } else {
                //Player has less score than Dealer and has lost
            if( (playerScore < dealerScore) && dealerScore <= 21 && !player.isWon() && !player.isBusted()){
                mDealButton.setVisibility(View.VISIBLE);
                player.setWon(false);
                mMessageHolder.setText(R.string.you_lost_less_points_message);
                mRoundFinished = true;

                //Player and Dealer has drawn
            } else if( (playerScore == dealerScore) && !player.isWon() && !player.isBusted()){
                mDealButton.setVisibility(View.VISIBLE);
                player.setMoney((player.getMoney()) + (player.getBet()));
                mMessageHolder.setText(R.string.draw_message);
                mRoundFinished = true;

                //Player has more points than Dealer and has won
            } else if( (playerScore > dealerScore) && !player.isWon() && !player.isBusted()){
                mDealButton.setVisibility(View.VISIBLE);
                player.setWon(true);
                player.setMoney((player.getMoney()) + (player.getBet() * 2));
                mMessageHolder.setText(R.string.you_won_more_points_message);
                mRoundFinished = true;
            }
        }
        if(mRoundFinished) { //Round is finished reset variables
            resetVariables(player);
        }
    }

    public void decision(Player player, Deck deck) { //If player is not busted deal him/her card

        int playerScore;

        if(!player.isBroke()){

                playerScore = score(player);

                if(player.getBlackjack()) { //Player has blackjack and round is over
                    player.setMoney((player.getMoney()) + (player.getBet() * 2.5));
                    player.setWon(true);
                    mMessageHolder.setText(R.string.you_have_blackjack);
                    mRoundFinished = true;
                } else if(playerScore == 21 && !player.isWon()) { //Player has 21 points and stands auto
                    player.setStand(true);
                    mMessageHolder.setText(R.string.auto_stand_message);
                    whoWon(player, deck);
                }
            //Player is not busted so deal one more card
            if(!player.isStand() && !player.isBusted() && !player.isWon() && mHitPressed) {
                player.getHand().addLast(deck.drawCard());
                setHand(player, mPlayerCardHolder);
                if(player.isBusted() && !player.isWon()) {
                    player.setBusted(true);
                    mMessageHolder.setText(R.string.you_busted_lost_message);
                    mRoundFinished = true;
                }
                mPlayerPointHolder.setText(getString(R.string.points) + String.valueOf(score(player)));
                mHitPressed = false;
            }
        }
        if(mRoundFinished) { //Round has finished, reset variables
            resetVariables(player);
            mStandButton.setVisibility(View.INVISIBLE);
            mHitButton.setVisibility(View.INVISIBLE);

        }
    }

    public void start(){ //Starts the round and resets values

        bet(mPlayer);

        if(!mPlayer.isBet()) { //Tells to bet correctly
            mMessageHolder.setText(getString(R.string.bet_message) + ((int) mPlayer.getMoney()));
            mDealButton.setVisibility(View.VISIBLE);
            return;
        }

        if(mRoundFinished) { //When round is finished remove all the cards from view and cache
            for (int i = 0; i < mPlayerHand.size(); i++) {
                View v = mPlayerCardHolder.getChildAt(i);
                ((ImageView) v).setImageDrawable(null);
                v.destroyDrawingCache();
            }

            for (int i = 0; i < mDealerHand.size(); i++) {
                View v = mDealerCardHolder.getChildAt(i);
                ((ImageView) v).setImageDrawable(null);
                v.destroyDrawingCache();
            }
            //Reset all variables
            mPlayer.setBlackjack(false);
            mPlayer.setBusted(false);
            mPlayer.setStand(false);
            mPlayer.setWon(false);
            mDealer.setBlackjack(false);
            mDealer.setBusted(false);
        }

        mFirstTimeDealer = true;
        mRoundFinished = false;
        mMessageHolder.setText("");

        mHitButton.setVisibility(View.VISIBLE);
        mStandButton.setVisibility(View.VISIBLE);

        mDeck = new Deck();
        mDeck.shuffle();
        mPlayerHand = new ArrayDeque<>();
        mDealerHand = new ArrayDeque<>();

        //Deal first two cards to Dealer and Player clockwise
        mPlayerHand.add(mDeck.drawCard());
        mDealerHand.add(mDeck.drawCard());
        mPlayerHand.add(mDeck.drawCard());
        mDealerHand.add(mDeck.drawCard());
        mPlayer.setHand(mPlayerHand);
        mDealer.setHand(mDealerHand);
        mPlayer.setMoney(mPlayer.getMoney());
        setHand(mPlayer, mPlayerCardHolder);
        setHand(mDealer, mDealerCardHolder);
        invalidateOptionsMenu();
        mPlayerPointHolder.setText(getString(R.string.points) + String.valueOf(score(mPlayer)));

        int tempValue = mDealer.getHand().getFirst().getValue();
        if( tempValue > 10 && tempValue != 11){ //Set correct value for dealers first card
            tempValue = 10;
        }
        mDealerPointHolder.setText(getString(R.string.points) + String.valueOf(tempValue));

        decision(mPlayer, mDeck);
    }

    public void broke(){  //Show SnackBar when you are broke and add up refills and reset money
        if(mPlayer.getMoney() == 0 && !mPlayer.isBet()){
            View layout = findViewById(R.id.main_layout);
            Snackbar snackbar = Snackbar.make(layout, R.string.broke_message, Snackbar.LENGTH_LONG);
            View view = snackbar.getView();
            view.setBackgroundColor((Color.parseColor(getString(R.string.snackbar_color_red))));
            snackbar.show();
            mPlayer.setMoney(1500);
            mPlayer.setRefills(mPlayer.getRefills() + 1);
            saveRefills();
            invalidateOptionsMenu();
            mDealButton.setVisibility(View.VISIBLE);
            return;
        }
    }

    public void showDealersHiddenCard(){  //Shows Dealers hidden card
        if(mRoundFinished && mDealer.getHand().size() == 2) {
            Card card = mDealer.getHand().getLast();
            String suit = card.getSuit();
            int value = card.getValue();
            int resource = getResources().getIdentifier(suit+value, "drawable", getPackageName());
            mDealerHiddenCard.setImageResource(resource);
        }
    }

    public void resetVariables(Player player){
        mDealerPointHolder.setText(getString(R.string.points) + String.valueOf(score(mDealer)));
        player.setIsBet(false);
        mBetHolder.setEnabled(true);
        mBetHolder.setText(String.valueOf(0));
        invalidateOptionsMenu();
        player.setBet(0);
        showDealersHiddenCard();
        mDealButton.setVisibility(View.VISIBLE);
        broke();
        saveMoney();
    }

    public void saveMoney() { //Save money in sharedPreferences
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt("com.example.s181142_mappe3.Money", (int) mPlayer.getMoney()).apply();
    }

    public void saveRefills() { //Save refills in sharedPreferences
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt("com.example.s181142_mappe3.Refills", mPlayer.getRefills()).apply();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);

        //Sets money in ActionBar
        int money = (int) mPlayer.getMoney();
        TextView textViewMoney = new TextView(this);
        textViewMoney.setText(getString(R.string.dollar_sign) + String.valueOf(money));
        textViewMoney.setPadding(10, 0, 20, 0);
        textViewMoney.setTypeface(null, Typeface.BOLD);
        textViewMoney.setTextSize(18);
        textViewMoney.setTextColor(Color.parseColor(getString(R.string.white_color)));
        menu.add(0, 0, 0, "1").setActionView(textViewMoney).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        //Sets refills in ActionBar
        int refills = mPlayer.getRefills();
        TextView textViewRefill = new TextView(this);
        textViewRefill.setText(getString(R.string.refills) + refills);
        textViewRefill.setPadding(10, 0, 10, 0);
        textViewRefill.setTypeface(null, Typeface.BOLD);
        textViewRefill.setTextSize(14);
        textViewRefill.setTextColor(Color.parseColor(getString(R.string.white_color)));
        menu.add(0, 0, 0, "1").setActionView(textViewRefill).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_rules) {
            Intent i = new Intent(getApplicationContext(), RulesActivity.class);
            startActivity(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
