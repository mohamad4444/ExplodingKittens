package com.gruppe14.explodingkitten.common.data.Game;

import java.io.Serializable;

public enum Card implements Serializable {
    // 0=ExplodingKitten,1=DEFUSE,2=Favor,3=Nope,4=AttackCard,5=ShuffleCard,6=SkipCard,7-11=normal,12=cardBack,13=SeeTheFuture

    EXPLODINGKITTEN(0),
    ATTACK(4),
    NOPE(3),
    SKIP(6),
    DEFUSE(1),
    FAVOR(2),
    SHUFFLE(5),
    SEETHEFUTURE(13),
    CATCARD1(7),
    CATCARD2(8),CATCARD3(9),CATCARD4(10),CATCARD5(11);
    public final int label;
    private static final long serialVersionUID = 1L;
    Card(int label) {
        this.label = label;
    }

}