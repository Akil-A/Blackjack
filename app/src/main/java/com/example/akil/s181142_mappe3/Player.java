package com.example.akil.s181142_mappe3;

import java.util.ArrayDeque;


public class Player {

    private String name;
    private int refills, bet = 0;
    private double money = 0;
    private boolean busted = false, isBet = false, blackjack, broke = false, won = false, stand = false;
    private ArrayDeque<Card> hand;

    public Player(String name) {
        this.name = name;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public int getBet(){
        return bet;
    }

    public void setBet(int bet){
        this.bet = bet;
    }
    public boolean isBusted() {
        return busted;
    }

    public void setBusted(boolean busted) {
        this.busted = busted;
    }

    public boolean isBet() {
        return isBet;
    }

    public void setIsBet(boolean isBet) {
        this.isBet = isBet;
    }

    public boolean getBlackjack() {
        return blackjack;
    }

    public void setBlackjack(boolean blackjack) {
        this.blackjack = blackjack;
    }

    public boolean isBroke() {
        return broke;
    }

    public ArrayDeque<Card> getHand() {
        return hand;
    }

    public void setHand(ArrayDeque<Card> hand) {
        this.hand = hand;
    }

    public boolean isWon() {
        return won;
    }

    public void setWon(boolean won) {
        this.won = won;
    }

    public boolean isStand() {
        return stand;
    }

    public void setStand(boolean stand) {
        this.stand = stand;
    }

    public int getRefills() {
        return refills;
    }

    public void setRefills(int refills) {
        this.refills = refills;
    }

}
