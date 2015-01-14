package com.agenth.extremedigger;

import com.agenth.extremedigger.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class JoystickView extends View {

	public static final int INVALID_POINTER_ID = -1;

	public static final double STICK_PROPORTION = 0.45;

	private int pointerId = INVALID_POINTER_ID;
	private float touchX, touchY;

	private JoystickMovedListener mMoveListener;
	private Drawable mStickDrawable;
	private int mSize;
	private int mStickOrigin;
	private int mStickSize;

	private int offsetX;
	private int offsetY;

	// Center of the view in view coordinates
	private int cX, cY;

	public JoystickView(Context context) {
		super(context);
		initJoystickView(context);
	}

	public JoystickView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initJoystickView(context);
	}

	public JoystickView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initJoystickView(context);
	}

	private void initJoystickView(Context context) {
		setFocusable(true);

		setBackgroundColor(Color.TRANSPARENT);
		setBackgroundResource(R.drawable.joystick_pad);

		mStickDrawable = context.getResources().getDrawable(
				R.drawable.joystick_stick);
	}

	@Override
	protected void onDraw(Canvas canvas) {

		canvas.save();
		canvas.translate(
				Math.min(Math.max(0, mStickOrigin + touchX * cX), mSize
						- mStickSize),
				Math.min(Math.max(0, mStickOrigin + touchY * cY), mSize
						- mStickSize));
		mStickDrawable.draw(canvas);
		canvas.restore();
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);

		mSize = Math.min(getWidth(), getHeight());

		cX = cY = mSize / 2;

		mStickSize = (int) (mSize * STICK_PROPORTION);

		mStickDrawable.setBounds(0, 0, mStickSize, mStickSize);

		mStickOrigin = (int) (mSize * (1 - STICK_PROPORTION) / 2);
	}

	@Override
	public boolean onTouchEvent(MotionEvent evt) {
		final int action = evt.getAction();
		switch (action & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_MOVE: {
			return processMoveEvent(evt);
		}
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP: {
			if (pointerId != INVALID_POINTER_ID) {
				// Log.d(TAG, "ACTION_UP");
				returnHandleToCenter();
				setPointerId(INVALID_POINTER_ID);
			}
			break;
		}
		case MotionEvent.ACTION_POINTER_UP: {
			if (pointerId != INVALID_POINTER_ID) {
				final int pointerIndex = (action & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
				final int pointerId = evt.getPointerId(pointerIndex);
				if (pointerId == this.pointerId) {
					// Log.d(TAG, "ACTION_POINTER_UP: " + pointerId);
					returnHandleToCenter();
					setPointerId(INVALID_POINTER_ID);
					return true;
				}
			}
			break;
		}
		case MotionEvent.ACTION_DOWN: {
			if (pointerId == INVALID_POINTER_ID) {
				int x = (int) evt.getX();
				if (x >= offsetX && x < offsetX + mSize) {
					setPointerId(evt.getPointerId(0));
					// Log.d(TAG, "ACTION_DOWN: " + getPointerId());
					return true;
				}
			}
			break;
		}
		case MotionEvent.ACTION_POINTER_DOWN: {
			if (pointerId == INVALID_POINTER_ID) {
				final int pointerIndex = (action & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
				final int pointerId = evt.getPointerId(pointerIndex);
				int x = (int) evt.getX(pointerId);
				if (x >= offsetX && x < offsetX + mSize) {
					// Log.d(TAG, "ACTION_POINTER_DOWN: " + pointerId);
					setPointerId(pointerId);
					return true;
				}
			}
			break;
		}
		}
		return false;
	}

	private boolean processMoveEvent(MotionEvent ev) {
		if (pointerId != INVALID_POINTER_ID) {
			final int pointerIndex = ev.findPointerIndex(pointerId);

			// Translate touch position to center of view
			float x = ev.getX(pointerIndex);
			touchX = (x - cX - offsetX) / cX;
			float y = ev.getY(pointerIndex);
			touchY = (y - cY - offsetY) / cY;

			// Log.d(TAG,
			// String.format("ACTION_MOVE: (%03.0f, %03.0f) => (%03.0f, %03.0f)",
			// x, y, touchX, touchY));

			reportOnMoved();
			invalidate();

			// touchPressure = ev.getPressure(pointerIndex);
			// reportOnPressure();

			return true;
		}
		return false;
	}

	private void reportOnMoved() {
		constrainBox();

		if (mMoveListener != null) {
			mMoveListener.OnMoved(touchX, touchY);
		}
	}

	private void constrainBox() {
		touchX = Math.max(Math.min(touchX, 1), -1);
		touchY = Math.max(Math.min(touchY, 1), -1);
	}

	public void setPointerId(int id) {
		this.pointerId = id;
	}

	private void returnHandleToCenter() {
		final int numberOfFrames = 5;
		final double intervalsX = (0 - touchX) / numberOfFrames;
		final double intervalsY = (0 - touchY) / numberOfFrames;

		for (int i = 0; i < numberOfFrames; i++) {
			final int j = i;
			postDelayed(new Runnable() {
				@Override
				public void run() {
					touchX += intervalsX;
					touchY += intervalsY;

					reportOnMoved();
					invalidate();

					if (mMoveListener != null && j == numberOfFrames - 1) {
						mMoveListener.OnReturnedToCenter();
					}
				}
			}, i * 40);
		}

		if (mMoveListener != null) {
			mMoveListener.OnReleased();
		}
	}

	public interface JoystickMovedListener {
		public void OnMoved(float touchX, float touchY);

		public void OnReleased();

		public void OnReturnedToCenter();
	}

	public void setOnJostickMovedListener(JoystickMovedListener listener) {
		mMoveListener = listener;
	}
}

