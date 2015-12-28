package alex.com.box2ddemo.gift2dview;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

/**
 * @author AleXQ
 * @Date 15/12/26
 * @Description: 控制box2d 重力感应逻辑
 */

public class Box2dSenserLogic {
	private SensorEventListener sel;
	private SensorManager sm;
	private Sensor sensor;
	private boolean m_isPortrait = true;
	private final static String TAG = Box2dSenserLogic.class.getSimpleName();
	private World m_world;


	public Box2dSenserLogic(final World world, Context context) {
		m_world = world;
		//获得重力感应硬件控制器
		sm = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		sensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		//添加重力感应侦听，并实现其方法，
		sel = new SensorEventListener() {
			public void onSensorChanged(SensorEvent se) {

				float x = se.values[SensorManager.DATA_X];
				float y = se.values[SensorManager.DATA_Y];
				Log.d(TAG, "x:" + x + ",y:" + y);

				float xSenser = -5.0f * x;
				float ySenser = (y >= -1) ? -30 : -y * 5.0f - 20f;
				Vector2 vec2 = m_isPortrait ? new Vector2(xSenser, ySenser) : new Vector2(0, xSenser);
				if (world != null)
					world.setGravity(vec2);
			}

			public void onAccuracyChanged(Sensor arg0, int arg1) {
			}
		};

	}

	public void setIsPortrait(boolean isPortrait) {
		m_isPortrait = isPortrait;
	}

	public void release() {
		sm.unregisterListener(sel);
	}

	public void startListener() {
		sm.registerListener(sel, sensor, SensorManager.SENSOR_DELAY_NORMAL);
	}

	public void stopListener() {
		sm.unregisterListener(sel);
	}
}