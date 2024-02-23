package com.sforge.quotes.animation;

import android.view.animation.LinearInterpolator;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;

public class FadeInItemAnimator extends DefaultItemAnimator {

    private long duration = 1000; // Default fade-in duration

    public FadeInItemAnimator() {}

    public FadeInItemAnimator(long duration) {
        this.duration = duration;
    }

    @Override
    public boolean animateAdd(RecyclerView.ViewHolder holder) {
        holder.itemView.setAlpha(0f);
        holder.itemView.animate()
                .alpha(1f)
                .setDuration(duration)
                .setInterpolator(new LinearInterpolator())
                .start();
        return super.animateAdd(holder);
    }
}
