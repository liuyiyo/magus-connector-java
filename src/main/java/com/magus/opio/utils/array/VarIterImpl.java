package com.magus.opio.utils.array;

public class VarIterImpl implements Iter {
    private int start;
    private int cursor;
    private int total;
    private int indexCursor;
    private int[] steps;

    public VarIterImpl(int[] steps, int start, int total) {
        this.steps = steps;
        this.start = start;
        this.total = total;
    }

    @Override
    public void seekToFirst() {
        cursor = start;
        indexCursor = 0;
    }

    @Override
    public int currentStart() {
        return cursor;
    }

    @Override
    public int currentEnd() {
        int step = steps[indexCursor];
        return cursor + step;
    }

    @Override
    public boolean valid() {
        return indexCursor != steps.length;
    }

    @Override
    public void next() {
        int step = steps[indexCursor];
        cursor += step;
        indexCursor++;
    }

    @Override
    public void at(int pos) {
        int stepNum = steps.length;
        if (pos >= stepNum || pos + steps[stepNum - 1] > total) {
            cursor = start;
            indexCursor = 0;
            return;
        }
        int curr = start;
        int i = 0;
        for (; i < stepNum; i++) {
            if (i == pos)
                break;
            curr += steps[i];
        }
        cursor = curr;
        indexCursor = i;
    }

    @Override
    public int number() {
        return steps.length;
    }
}
