package com.androiddev.dragsortinrecycleview;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by trach on 10/8/2017.
 */

public class DragSortRecycler extends RecyclerView.ItemDecoration implements RecyclerView.OnItemTouchListener {
    private final static String TAG = "DragSortRecycler";
    private static boolean DEBUG = true;
    private OnItemMovedListener mOnItemMovedListener;
    @Nullable
    private OnDragStateChangedListener mOnDragStateChangedListener;

    Paint paint = new Paint();
    private int dragHandlerWidth = 0;
    private int selectedDragItemPos = -1;
    private int fingerAchorY;
    private int fingerY;
    private int fingerOffsetInViewY;
    private float autoScrollWindow = 0.1f;
    private float autoScrollSpeed = 0.5f;
    private BitmapDrawable mFloatingItem;
    private Rect floatingItemStatingBound;
    private Rect floatingItemBound;
    private float floatingItemAlpha = 0.5f;
    private int floatingItemBgColor = 0;
    private int viewHandleid = -1;
    private boolean isDragging;

    public RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            debugLog("scrolled dx --> "+ dx + "dy ---> "+ dy);
            fingerAchorY -= dy;
        }
    };

    public RecyclerView.OnScrollListener getScrollListener() {
        return scrollListener;
    }

    public void setItemMovedListener(OnItemMovedListener itemMovedListener) {
        this.mOnItemMovedListener = itemMovedListener;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        debugLog("getItemOffset");
        debugLog("View top = " + view.getTop());
        if(selectedDragItemPos != -1){
            int itemPos = parent.getChildLayoutPosition(view);
            debugLog("child position "+ itemPos);
            if(!canDragOver(itemPos)){
                return;
            }
            if (itemPos == selectedDragItemPos){
                view.setVisibility(View.INVISIBLE);
            }else {
                view.setVisibility(View.VISIBLE);
                int middleY = floatingItemBound.top + floatingItemBound.height()/2;
            }
        }
    }

    private boolean canDragOver(int position){
        return true;
    }

    public void debugLog(String msg){
        if(DEBUG){
            Log.d(TAG, msg);
        }
    }


    public interface OnItemMovedListener{
        void onItemMovedListener(int from, int to);
    }

    public interface OnDragStateChangedListener{
        void onDragStart();
        void onDragEnd();
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }
}
