package com.bbel.eatnow.utils;

import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.view.View;

import com.bbel.eatnow.adapter.CardAdapter1;


public class ShadowTransformer1 implements ViewPager.OnPageChangeListener, ViewPager.PageTransformer {

    private ViewPager mViewPager;
    private CardAdapter1 mAdapter;
    private float mLastOffset;
    private boolean mScalingEnabled;

    public ShadowTransformer1(ViewPager viewPager, CardAdapter1 adapter) {
        mViewPager = viewPager;
        viewPager.addOnPageChangeListener(this);
        mAdapter = adapter;
    }

    public void enableScaling(boolean enable) {
        if (mScalingEnabled && !enable) {
            // shrink main card
            CardView currentCard = mAdapter.getCardViewAt(mViewPager.getCurrentItem());
            if (currentCard != null) {
                currentCard.animate().scaleY(1);
                currentCard.animate().scaleX(1);
            }
        }else if(!mScalingEnabled && enable){
            // grow main card
            CardView currentCard = mAdapter.getCardViewAt(mViewPager.getCurrentItem());
            if (currentCard != null) {
                currentCard.animate().scaleY(1.1f);
                currentCard.animate().scaleX(1.1f);
            }
        }

        mScalingEnabled = enable;
    }

    @Override
    public void transformPage(View page, float position) {
        if (position > -1 && position < 1) {

            page.setTranslationX(-position * page.getWidth());
            float scaleFactor;
            float aPostion = Math.abs(position);
            if (aPostion < 0.5) {
                scaleFactor = 1 - aPostion;
            } else {
                scaleFactor = aPostion;
            }
            page.setScaleX(0.75f + 0.25f * scaleFactor);
            page.setScaleY(0.7f + 0.3f * scaleFactor);

            page.setRotationY(180 * position);

            if (Math.abs(position) < 0.5) {
                page.setVisibility(View.VISIBLE);
            } else {
                page.setVisibility(View.INVISIBLE);
            }
        }

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        int realCurrentPosition;
        int nextPosition;
        float baseElevation = mAdapter.getBaseElevation();
        float realOffset;
        boolean goingLeft = mLastOffset > positionOffset;

        // If we're going backwards, onPageScrolled receives the last position
        // instead of the current one
        if (goingLeft) {
            realCurrentPosition = position + 1;
            nextPosition = position;
            realOffset = 1 - positionOffset;
        } else {
            nextPosition = position + 1;
            realCurrentPosition = position;
            realOffset = positionOffset;
        }

        // Avoid crash on overscroll
        if (nextPosition > mAdapter.getCount() - 1
                || realCurrentPosition > mAdapter.getCount() - 1) {
            return;
        }

        CardView currentCard = mAdapter.getCardViewAt(realCurrentPosition);

        // This might be null if a fragment is being used
        // and the views weren't created yet
        if (currentCard != null) {
            if (mScalingEnabled) {
                currentCard.setScaleX((float) (1 + 0.1 * (1 - realOffset)));
                currentCard.setScaleY((float) (1 + 0.1 * (1 - realOffset)));
            }
            currentCard.setCardElevation((baseElevation + baseElevation
                    * (CardAdapter1.MAX_ELEVATION_FACTOR - 1) * (1 - realOffset)));
        }

        CardView nextCard = mAdapter.getCardViewAt(nextPosition);

        // We might be scrolling fast enough so that the next (or previous) card
        // was already destroyed or a fragment might not have been created yet
        if (nextCard != null) {
            if (mScalingEnabled) {
                nextCard.setScaleX((float) (1 + 0.1 * (realOffset)));
                nextCard.setScaleY((float) (1 + 0.1 * (realOffset)));
            }
            nextCard.setCardElevation((baseElevation + baseElevation
                    * (CardAdapter1.MAX_ELEVATION_FACTOR - 1) * (realOffset)));
        }

        mLastOffset = positionOffset;
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}