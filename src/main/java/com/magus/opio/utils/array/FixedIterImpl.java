package com.magus.opio.utils.array;

public class FixedIterImpl implements Iter {
    private int start;
    private int cursor;
    private int total;
    private int step;

    public FixedIterImpl(int start, int step, int total) {
        this.start = start;
        this.step = step;
        this.total = total;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public void setCursor(int cursor) {
        this.cursor = cursor;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public void setStep(int step) {
        this.step = step;
    }

    @Override
    public void seekToFirst() {
        cursor = start;
    }

    @Override
    public int currentStart() {
        return cursor;
    }

    @Override
    public int currentEnd() {
        return cursor + step;
    }

    @Override
    public boolean valid() {
        return cursor != total;
    }

    @Override
    public void next() {
        cursor += step;
    }

    @Override
    public void at(int pos) {
        int curr = start + step * pos;
        if (curr >= total || curr + step > total) {
            cursor = start;
            return;
        }
        cursor = curr;
    }

    @Override
    public int number() {
        return (total - start) / step;
    }
}
