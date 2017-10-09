package com.androiddev.dragsortinrecycleview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
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
    private int viewHandleId = -1;
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
                debugLog("make view visible incase invisible");
                int middleY = floatingItemBound.top + floatingItemBound.height()/2;
                //Moving down the list
                //These will auto-animate if the device continually sends touch motion events
                if(itemPos > selectedDragItemPos && view.getTop() < middleY){
                    float amoutUp = (middleY - view.getTop())/ (float) view.getHeight();
                    if(amoutUp>1)
                        amoutUp = 1;
                    outRect.top = -(int) (floatingItemBound.height() * amoutUp);
                    outRect.bottom = (int) (floatingItemBound.height() * amoutUp);

                }else if(itemPos < selectedDragItemPos && view.getBottom() > middleY) {
                    float amoutDown = (view.getBottom()-middleY)/(float) view.getHeight();
                    if(amoutDown > 1) amoutDown = 1;
                    outRect.bottom = -(int)(floatingItemBound.height()*amoutDown);
                    outRect.top = (int)(floatingItemBound.height()*amoutDown);
                }
            }
        }else {
            outRect.bottom = 0;
            outRect.top = 0;
            view.setVisibility(View.VISIBLE);
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

    public void setViewHandleId(int id){
        viewHandleId = id;
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        View itemView = rv.findChildViewUnder(e.getX(), e.getY());
        if(itemView == null) return false;
        boolean dragging = false;
        debugLog("onInterceptTouchEvent");
        if((dragHandlerWidth > 0) && (e.getX() < dragHandlerWidth)){
            dragging = true;
        }else if(viewHandleId != -1){
            View handleView = itemView.findViewById(viewHandleId);
            if(handleView == null){
                Log.e(TAG, "The view ID " + viewHandleId + " was not found in the RecycleView item");
                return false;
            }

            if(handleView.getVisibility() != View.VISIBLE){
                return false;
            }

            int[] parentItemPos = new int[2];
            itemView.getLocationInWindow(parentItemPos);

            int[] handlePos = new int[2];
            handleView.getLocationInWindow(handlePos);

            int xRel = handlePos[0] - parentItemPos[0];
            int yRel = handlePos[1] - parentItemPos[1];
            Rect touchBounds = new Rect(itemView.getLeft() + xRel, itemView.getTop() + yRel,
                    itemView.getLeft() + xRel + handleView.getWidth(), itemView.getTop() + yRel + handleView.getHeight());

            if(touchBounds.contains((int)e.getX(), (int)e.getY())){
                dragging = true;
            }

            debugLog("parentItemPos = " + parentItemPos[0] + " " + parentItemPos[1]);
            debugLog("handlePos = " + handlePos[0] + " " + handlePos[1]);
        }

        if(dragging){
            debugLog("starting drag");
            setIsDragging(true);
            mFloatingItem = createFloatingBitmap(itemView);
            fingerAchorY = (int) e.getY();
            fingerOffsetInViewY = fingerAchorY - itemView.getTop();
            fingerY = fingerAchorY;
            selectedDragItemPos = rv.getChildLayoutPosition(itemView);
            debugLog("selectedDragItemPos = " + selectedDragItemPos);
            return true;
        }

        return false;
    }

    private BitmapDrawable createFloatingBitmap(View view) {
        floatingItemStatingBound = new Rect(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
        floatingItemBound = new Rect(floatingItemStatingBound);
        Bitmap bitmap = Bitmap.createBitmap(
                floatingItemStatingBound.width(), floatingItemStatingBound.height(), Bitmap.Config.ARGB_8888
        );
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        BitmapDrawable drawable = new BitmapDrawable(view.getResources(), bitmap);
        drawable.setBounds(floatingItemBound);

        return drawable;
    }

    private void setIsDragging(boolean dragging) {
        if(dragging != isDragging){
            isDragging = dragging;
            if(mOnDragStateChangedListener!= null) {
                if(isDragging){
                    mOnDragStateChangedListener.onDragStart();
                }else {
                    mOnDragStateChangedListener.onDragEnd();
                }
            }
        }
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        debugLog("onTouchEvent");
        if(e.getAction() == MotionEvent.ACTION_UP || e.getAction() == MotionEvent.ACTION_CANCEL){
            if(e.getAction() == MotionEvent.ACTION_UP && selectedDragItemPos != -1){
                int newPos = getNewPosition(rv);
                if(mOnItemMovedListener!=null){
                    mOnItemMovedListener.onItemMovedListener(selectedDragItemPos, newPos);
                }
            }
            setIsDragging(false);
            selectedDragItemPos = -1;
            mFloatingItem = null;
            rv.invalidateItemDecorations();
            return;
        }

        fingerY = (int) e.getY();

        if(mFloatingItem != null){
            floatingItemBound.top = fingerY - fingerOffsetInViewY;

            if (floatingItemBound.top < -floatingItemStatingBound.height() / 2) //Allow half the view out the top
                floatingItemBound.top = -floatingItemStatingBound.height() / 2;

            floatingItemBound.bottom = floatingItemBound.top + floatingItemStatingBound.height();

            mFloatingItem.setBounds(floatingItemBound);
        }
        //Do auto scrolling at end of list
        float scrollAmount = 0;
        if (fingerY > (rv.getHeight() * (1 - autoScrollWindow))) {
            scrollAmount = (fingerY - (rv.getHeight() * (1 - autoScrollWindow)));
        } else if (fingerY < (rv.getHeight() * autoScrollWindow)) {
            scrollAmount = (fingerY - (rv.getHeight() * autoScrollWindow));
        }
        debugLog("Scroll: " + scrollAmount);

        scrollAmount *= autoScrollSpeed;
        rv.scrollBy(0, (int) scrollAmount);

        rv.invalidateItemDecorations();// Redraw
    }

    private int getNewPosition(RecyclerView recyclerView) {
        int itemsOnScreen = recyclerView.getLayoutManager().getItemCount();
        float floatmiddleY = floatingItemBound.top + floatingItemBound.height()/2;
        int above = 0;
        int below = Integer.MAX_VALUE;
        for (int i = 0; i < itemsOnScreen; i++) {
            View view = recyclerView.getLayoutManager().getChildAt(i);
            if(view.getVisibility()!=View.VISIBLE)
                continue;

            int itemPos = recyclerView.getChildLayoutPosition(view);
            if(itemPos == selectedDragItemPos){
                continue;
            }
            float viewMiddleY = view.getTop() + view.getHeight() / 2;
            if (floatmiddleY > viewMiddleY) //Is above this item
            {
                if (itemPos > above)
                    above = itemPos;
            } else if (floatmiddleY <= viewMiddleY) //Is below this item
            {
                if (itemPos < below)
                    below = itemPos;
            }
        }
        debugLog("above = " + above + " below = " + below);

        if (below != Integer.MAX_VALUE) {
            if (below < selectedDragItemPos) //Need to count itself
                below++;
            return below - 1;
        } else {
            if (above < selectedDragItemPos)
                above++;

            return above;
        }
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        if (mFloatingItem != null) {
            mFloatingItem.setAlpha((int) (255 * floatingItemAlpha));
            paint.setColor(floatingItemBgColor);
            c.drawRect(floatingItemBound, paint);
            mFloatingItem.draw(c);
        }
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }
}
