package com.dima.emmeggi95.jaycaves.me.layout;

import android.support.v7.widget.RecyclerView;

public interface OnStartDragListener {

    void onStartDrag(RecyclerView.ViewHolder viewHolder);

    void move(int x, int y);

    void remove(int x);
}
