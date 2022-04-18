package com.example.android.BluetoothChat;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.SurfaceHolder.Callback;

public class MySurfaceView extends SurfaceView implements Callback, Runnable {

	private Thread th;
	private SurfaceHolder sfh;
	private Canvas canvas;
	private Paint paint;
	private boolean flag;
	private int RockerCircleX = 100;
	private int RockerCircleY = 100;
	private int RockerCircleR = 200;
	private float SmallRockerCircleX = 100;
	private float SmallRockerCircleY = 100;
	private float SmallRockerCircleR = 120;

	// ��Ļ����������
	private float Center_X = 0;
	private float Center_Y = 0;

	// ���尴����λ��
	private int which = 0, last_which = 0;

	// �����ж��Ƕ�
	double Pi8 = Math.PI / 8;

	public OnRockerListener getRockerListener() {
		return rockerListener;
	}

	public void setRockerListener(OnRockerListener rockerListener) {
		this.rockerListener = rockerListener;
	}

	// �趨�Զ��������
	private OnRockerListener rockerListener = null;

	public MySurfaceView(Context context) {
		super(context);
		Log.v("Himi", "MySurfaceView");
		this.setKeepScreenOn(true);
		sfh = this.getHolder();
		sfh.addCallback(this);
		paint = new Paint();
		paint.setAntiAlias(true);
		setFocusable(true);
		setFocusableInTouchMode(true);
	}

	public void surfaceCreated(SurfaceHolder holder) {

		// �趨ҡ�˵ĳ�ʼλ��

		Center_X = this.getMeasuredWidth() / 2;
		Center_Y = this.getMeasuredHeight() / 2;
		RockerCircleX = (int) Center_X;
		RockerCircleY = (int) Center_Y;
		SmallRockerCircleX = Center_X;
		SmallRockerCircleY = Center_Y;

		// �趨����ˢ���߳�
		th = new Thread(this);
		flag = true;
		th.start();
	}

	/***
	 * �õ�����֮��Ļ���
	 */
	public double getRad(float px1, float py1, float px2, float py2) {
		// �õ�����X�ľ���
		float x = px2 - px1;
		// �õ�����Y�ľ���
		float y = py1 - py2;
		// ���б�߳�
		float xie = (float) Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
		// �õ�����Ƕȵ�����ֵ��ͨ�����Ǻ����еĶ��� ���ڱ�/б��=�Ƕ�����ֵ��
		float cosAngle = x / xie;
		// ͨ�������Ҷ����ȡ����ǶȵĻ���
		float rad = (float) Math.acos(cosAngle);
		// ע�⣺��������λ��Y����<ҡ�˵�Y��������Ҫȡ��ֵ-0~-180
		if (py2 < py1) {
			rad = -rad;
		}
		return rad;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		double tempRad = .0;
		if (event.getAction() == MotionEvent.ACTION_DOWN
				|| event.getAction() == MotionEvent.ACTION_MOVE) {
			// �����������ڻ��Χ��
			// �õ�ҡ���봥�������γɵĽǶ�
			tempRad = getRad(RockerCircleX, RockerCircleY, event.getX(),
					event.getY());
			if (Math.sqrt(Math.pow((RockerCircleX - (int) event.getX()), 2)
					+ Math.pow((RockerCircleY - (int) event.getY()), 2)) >= RockerCircleR) {

				// Log.d("Rad", tempRad+"");
				// ��֤�ڲ�СԲ�˶��ĳ�������
				getXY(RockerCircleX, RockerCircleY, RockerCircleR, tempRad);
			} else {// ���С�����ĵ�С�ڻ�����������û��������ƶ�����
				SmallRockerCircleX = (int) event.getX();
				SmallRockerCircleY = (int) event.getY();
			}
		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			// ���ͷŰ���ʱҡ��Ҫ�ָ�ҡ�˵�λ��Ϊ��ʼλ��
			SmallRockerCircleX = Center_X;
			SmallRockerCircleY = Center_Y;
		}

		double lon = Math.sqrt(Math.pow(Center_X - SmallRockerCircleX, 2)
				+ Math.pow(Center_Y - SmallRockerCircleY, 2));
		if (lon >= RockerCircleR - 10) {
			if ((tempRad >= -1 * Math.PI && tempRad <= -7 * Pi8)
					|| (tempRad <= Math.PI && tempRad > 7 * Pi8))
				which = 7;
			else if (tempRad > -7 * Pi8 && tempRad <= -5 * Pi8)
				which = 8;
			else if (tempRad > -5 * Pi8 && tempRad <= -3 * Pi8)
				which = 1;
			else if (tempRad > -3 * Pi8 && tempRad <= -1 * Pi8)
				which = 2;
			else if (tempRad > -1 * Pi8 && tempRad <= 1 * Pi8) {
				which = 3;
				// System.out.println(""+tempRad);

			}
			else if (tempRad > 1 * Pi8 && tempRad <= 3 * Pi8)
				which = 4;
			else if (tempRad > 3 * Pi8 && tempRad <= 5 * Pi8)
				which = 5;
			else if (tempRad > 5 * Pi8 && tempRad <= 7 * Pi8)
				which = 6;

		}

		else {

			which = 0;
		}

		if (which != last_which) {
			// System.out.println(which+"");
			// if(rockerListener==null)
			// System.out.println("cao");
			Log.d("ymq", "" + which);
			rockerListener.onRocker(which);
			last_which = which;
		}

		return true;
	}

	/**
	 * 
	 * @param R
	 *            Բ���˶�����ת��
	 * @param centerX
	 *            ��ת��X
	 * @param centerY
	 *            ��ת��Y
	 * @param rad
	 *            ��ת�Ļ���
	 */
	public void getXY(float centerX, float centerY, float R, double rad) {
		// ��ȡԲ���˶���X����
		SmallRockerCircleX = (float) (R * Math.cos(rad)) + centerX;
		// ��ȡԲ���˶���Y����
		SmallRockerCircleY = (float) (R * Math.sin(rad)) + centerY;
	}

	public void draw() {
		try {
			canvas = sfh.lockCanvas();
			canvas.drawColor(Color.WHITE);
			// ����͸����
			paint.setColor(0xB0D20000);
			// ����ҡ�˱���
			canvas.drawCircle(RockerCircleX, RockerCircleY, RockerCircleR,
					paint);
			paint.setColor(Color.rgb(0xFE, 0xAC, 0x15));
			// ����ҡ��
			canvas.drawCircle(SmallRockerCircleX, SmallRockerCircleY,
					SmallRockerCircleR, paint);
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			try {
				if (canvas != null)
					sfh.unlockCanvasAndPost(canvas);
			} catch (Exception e2) {

			}
		}
	}

	public void run() {
		// TODO Auto-generated method stub
		while (flag) {
			draw();
			try {
				Thread.sleep(20);
			} catch (Exception ex) {
			}
		}
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		Log.v("Himi", "surfaceChanged");
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		flag = false;
		Log.v("Himi", "surfaceDestroyed");
	}
}