package com.heshicaihao.popstars.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
//import android.graphics.AvoidXfermode.Mode;
import android.view.animation.AccelerateInterpolator;

import com.heshicaihao.popstars.gamesoundpool.GameSoundPool;
import com.heshicaihao.popstars.R;


public class GoodSocre {

	private final String TAG = this.getClass().getSimpleName();

	final float screenWidthMeasure = 720;

	private Paint mPaint;
	private int duration = 500;
	GameSoundPool sounds;
	private ValueAnimator animator;
	private float animatorValue;
	private AnimationEndListener listener;
	float srceenWidth;
	float screenHeight;
	Location location;
	int mode;
	Bitmap bitmap;
	Context context;
	
	public GoodSocre(Location location, int mode,Context context, GameSoundPool sounds, float width,
			float height) {
		srceenWidth = width;
		screenHeight = height;
		this.sounds = sounds;
		this.location =location;
		this.mode = mode;
		mPaint = new Paint();
		this.context = context;
		init();
	}
	
	public void init(){
		if(mode == 1){
			bitmap =  BitmapFactory.decodeResource(context.getResources(), R.drawable.combo_cool);
		}else if(mode == 2){
			bitmap =  BitmapFactory.decodeResource(context.getResources(), R.drawable.combo_awesome);
		}else{
			bitmap =  BitmapFactory.decodeResource(context.getResources(), R.drawable.combo_fantastic);
		}
		location.x-= bitmap.getWidth()/2;
	}
	
	private float timeCount = 10;

	public void flash() {
		animator = ValueAnimator.ofFloat(1, 0);
		animator.setDuration(duration);
		//从头开始动画
		animator.setRepeatMode(ValueAnimator.RESTART);
		animator.setInterpolator(new AccelerateInterpolator());
		animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator valueAnimator) {
				
			}
		});
		animator.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				listener.onAnimationEnd(GoodSocre.this);
			}
		});
	}
	int time = 40;
	int alpha = 255;
	int dis =30;
	public void setDuration(int duration) {
		this.duration = duration;
	}

	public void addAnimationEndListener(AnimationEndListener listener) {
		this.listener = listener;
	}

	public void draw(Canvas canvas) {
		if(time-- <0)
			listener.onAnimationEnd(GoodSocre.this);
		if(time <30)
			alpha-=dis;
		mPaint.setAlpha(alpha);
		canvas.drawBitmap(bitmap, location.x,location.y, mPaint);
	}

	public void release() {
		if(bitmap != null && bitmap.isRecycled()){
			bitmap.recycle();
		}
	}

	public interface AnimationEndListener {
		void onAnimationEnd(GoodSocre mFirework);
	}

	public static class Location {
		public float x;
		public float y;

		public Location(float x, float y) {
			this.x = x;
			this.y = y;
		}
	}
}
