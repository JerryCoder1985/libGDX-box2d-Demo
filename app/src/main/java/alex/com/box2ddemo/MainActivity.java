package alex.com.box2ddemo;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.badlogic.gdx.backends.android.AndroidFragmentApplication;
import com.badoo.mobile.util.WeakHandler;

import alex.com.box2ddemo.gift2dview.Box2DFragment;
import alex.com.box2ddemo.gift2dview.Tools.GiftParticleContants;
import alex.com.box2ddemo.gift2dview.Tools.ScreenParamUtil;
import alex.com.box2ddemo.testcode.SpringEffect;
import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends FragmentActivity implements AndroidFragmentApplication.Callbacks{

    private Box2DFragment m_box2dFgm;
    private WeakHandler m_weakHandler = new WeakHandler();
    private SystemReceiveBroadCast m_systemreceiveBroadCast;
    private boolean m_bCrazyMode = false;

    @Bind(R.id.random)
    public Button m_random;

	@Bind(R.id.lyt_container)
	public FrameLayout m_container;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

	    getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        m_systemreceiveBroadCast = new SystemReceiveBroadCast();
        IntentFilter filter1 = new IntentFilter();
        filter1.setPriority(800);
        filter1.addAction(GiftParticleContants.BROADCAST_GIFTPARTICLE_BACKKEY);
        registerReceiver(m_systemreceiveBroadCast, filter1);

        m_box2dFgm = new Box2DFragment();
	    getSupportFragmentManager().beginTransaction().add(R.id.lyt_container, m_box2dFgm).commit();

	    showBox2dFgmFullScreen();

        SpringEffect.doEffectSticky(findViewById(R.id.random), new Runnable() {
            @Override
            public void run() {
                if (m_bCrazyMode == false) {
                    m_weakHandler.postDelayed(m_runnableCrazyMode, 50);
                    m_random.setText("Stop crazyMode");
                } else {
                    m_weakHandler.removeCallbacks(m_runnableCrazyMode);
                    m_random.setText("Start crazyMode");
                }
                m_bCrazyMode = !m_bCrazyMode;
            }
        });

    }

    private boolean m_testleft = false;
    private Runnable m_runnableCrazyMode = new Runnable() {
        @Override
        public void run() {
            m_box2dFgm.addBall(m_testleft);
            m_testleft = !m_testleft;
            m_weakHandler.postDelayed(m_runnableCrazyMode, 50);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(m_systemreceiveBroadCast);
        m_weakHandler.removeCallbacks(m_runnableCrazyMode);
        ButterKnife.unbind(this);
    }

    @Override
    public void exit() {

    }

    private long m_exitTime;
    private boolean checkquit() {

        if ((System.currentTimeMillis() - m_exitTime) > 2000) {
            Toast.makeText(this, "再次点击退出", Toast.LENGTH_SHORT).show();
            m_exitTime = System.currentTimeMillis();
        } else {
            finish();
        }
        return true;
    }

    protected void dialogTest() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("确认退出应用吗？");

        builder.setTitle("提示");

        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                MainActivity.this.finish();
            }
        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();
    }

    protected void dialog(String tip) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(tip);

        builder.setTitle("提示");

        builder.setPositiveButton("退出", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                MainActivity.this.finish();
            }
        });

        builder.create().show();
    }

    public class SystemReceiveBroadCast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            Log.d(MainActivity.class.getSimpleName(), "SystemReceiveBroadCast[^^^^^^^]play Particle Receive: " + intent.getAction());
            if (intent.getAction().equals(GiftParticleContants.BROADCAST_GIFTPARTICLE_BACKKEY)) {
                checkquit();
            }
        }
    }

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		showBox2dFgmFullScreen();
	}

	private void showBox2dFgmFullScreen(){
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)m_container.getLayoutParams();
		params.width = RelativeLayout.LayoutParams.MATCH_PARENT;
		params.height = RelativeLayout.LayoutParams.MATCH_PARENT;
		m_container.setLayoutParams(params);
	}

	private void showBox2dFgmNormalScreen(){
		int width = ScreenParamUtil.GetScreenWidthPx(this);
		int height = ScreenParamUtil.GetScreenHeightDp(this);
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)m_container.getLayoutParams();
		params.width = width;
		params.height = height;
		m_container.setLayoutParams(params);
	}
}
