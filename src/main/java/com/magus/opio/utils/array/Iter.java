package com.magus.opio.utils.array;

public interface Iter {
    void seekToFirst();

    int currentStart();

    int currentEnd();

    boolean valid();

    void next();

    void at(int pos);

    int number();
}
