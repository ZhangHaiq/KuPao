package com.example.admin.kupao;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.kupao.pedomemter.StepService;
import com.example.admin.kupao.util.AppUtils;
import com.example.admin.kupao.util.Constant;
import com.example.admin.kupao.util.DensityUtil;
import com.example.admin.kupao.view.CircleButton;
import com.example.admin.kupao.view.CircleWaveButton;
import com.example.admin.kupao.view.HintDialog;
import com.example.admin.kupao.view.WaveView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by adu on 2016/10/21.
 */

public class WalkingActivity extends AppCompatActivity implements  Handler.Callback{

    //@BindView(R.id.rl_back) RelativeLayout rlBack;//返回
    @BindView(R.id.tv_top) TextView tvTop;
    //@BindView(R.id.rl_Right) RelativeLayout rlRight;
    @BindView(R.id.ll_top) RelativeLayout llTop;
    @BindView(R.id.top_bar_linear) LinearLayout topBarLinear;

    @BindView(R.id.step_count) TextView stepCount;      //计算步数
    @BindView(R.id.calories) TextView calories;         //热量千卡
    @BindView(R.id.tv_calories) TextView tvCalories;
    @BindView(R.id.iv_time) ImageView ivTime;
    @BindView(R.id.time) TextView time;                 //分钟
    @BindView(R.id.tv_time) TextView tvTime;
    @BindView(R.id.wave_view)
    WaveView waveView;        //
    @BindView(R.id.stop)
    CircleButton stop;             //停止
    @BindView(R.id.start)
    CircleWaveButton start;       //开始
    @BindView(R.id.bt_continue) CircleButton btContinue;    //暂停

    public static final String WALKCAMPID = "walkCampId";
    private long step = 0;
    private long lastStep = 0;
    private boolean isPause = false;
    private boolean isStart = false;
    private long timemm = 0;
    private long thisTime = 0;
    private SharedPreferences sp;
    //循环获取当前时刻的步数中间的间隔时间
    private long TIME_INTERVAL = 500;
    public static final int TIMECOM = 30;

    private Messenger messenger;
    private Messenger mGetReplyMessenger = new Messenger(new Handler(this));
    private Handler delayHandler;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (!isPause && isStart) {
                thisTime = thisTime + TIME_INTERVAL;
                timemm = thisTime / 60000;
                delayHandler.sendEmptyMessageDelayed(TIMECOM, TIME_INTERVAL);
            }

        }
    };
    ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            try {
                messenger = new Messenger(service);
                Message msg = Message.obtain(null, Constant.Config.MSG_FROM_CLIENT);
                msg.replyTo = mGetReplyMessenger;
                messenger.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walking);
        ButterKnife.bind(this);
        initView();
    }


    private void initView() {
        sp = getSharedPreferences(Constant.Config.FILE_NAME, MODE_PRIVATE);
        lastStep = sp.getInt(Constant.Config.stepNum, 0);
        delayHandler = new Handler(this);
        if (sp.getBoolean(Constant.Config.isStepServiceRunning, false)) {
            setupService();
        }
//        topBarLinear.setBackgroundColor(0);
        tvTop.setText("天天酷跑");
        if (android.os.Build.VERSION.SDK_INT > 18) {
            AppUtils.initSystemBar(this);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) llTop.getLayoutParams();
            params.height = DensityUtil.Dp2Px(this, 35);
            llTop.setLayoutParams(params);
            topBarLinear.setPadding(0, AppUtils.getStatusBarHeight(this), 0, 0);
        }

        btContinue.setPaintColor(R.color.text_color_1e78be);
        start.setPaintColor(R.color.circle_bule_bbd4e7);
        stop.setPaintColor(R.color.circle_red_cd3a33);
        start.start();
        stepCount.setText(String.valueOf(step));
        calories.setText(String.valueOf(stepToKcal(step)));
    }


    @OnClick({ R.id.stop, R.id.start, R.id.bt_continue })
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.stop:
                if (step >= 100) {
                    if (sp.getBoolean(Constant.Config.isStepServiceRunning, false)) {
                        sendBroadcast(new Intent(Constant.Config.stopStepService));
                        if (conn != null) {
                            unbindService(conn);
                        }
                        SharedPreferences.Editor stop_editor = sp.edit();
                        stop_editor.putBoolean(Constant.Config.isStepServiceRunning, false);
                        stop_editor.commit();
                    }
                }
                isStart = false;
                waveView.stop();
                runnable.run();

                new HintDialog.Builder(WalkingActivity.this).
                        setTitle("提示").
                        setMessage("当前步数太少，是否退出？").
                        setConfirmBtnListener(new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (sp.getBoolean(Constant.Config.isStepServiceRunning, false)) {
                                    sendBroadcast(new Intent(Constant.Config.stopStepService));
                                    if (conn != null) {
                                        unbindService(conn);
                                    }
                                    SharedPreferences.Editor stop_editor = sp.edit();
                                    stop_editor.putBoolean(Constant.Config.isStepServiceRunning, false);
                                    stop_editor.commit();
                                }
                                WalkingActivity.this.finish();
                                System.exit(0);
                            }
                        }).onCreate().show();

                break;
            case R.id.start:
                if (!sp.getBoolean(Constant.Config.isStepServiceRunning, false)) {
                    setupService();
                    SharedPreferences.Editor start_editor = sp.edit();
                    start_editor.putBoolean(Constant.Config.isStepServiceRunning, true);
                    start_editor.commit();
                }
                startAnimation();
                waveView.start();
                isStart = true;
                runnable.run();
                break;
            case R.id.bt_continue:
                if (isPause) {

                    btContinue.setText("暂停");
                    isPause = false;
                    waveView.start();
                    if (!sp.getBoolean(Constant.Config.isStepServiceRunning, false)) {
                        setupService();
                        SharedPreferences.Editor starteditor = sp.edit();
                        starteditor.putBoolean(Constant.Config.isStepServiceRunning, true);

                        starteditor.commit();
                    }
                } else {
                    btContinue.setText("继续");
                    isPause = true;
                    waveView.stop();
                    if (sp.getBoolean(Constant.Config.isStepServiceRunning, false)) {
                        sendBroadcast(new Intent(Constant.Config.stopStepService));
                        if (conn != null) {
                            unbindService(conn);
                        }

                        SharedPreferences.Editor stop_editor = sp.edit();
                        stop_editor.putBoolean(Constant.Config.isStepServiceRunning, false);
                        stop_editor.commit();
                    }
                    runnable.run();
                    break;
                }
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {

            case Constant.Config.MSG_FROM_SERVER:
                step = Long.valueOf(String.valueOf(msg.getData().get(Constant.Config.stepNum))) - lastStep;
                stepCount.setText(String.valueOf(step));
                calories.setText(String.valueOf(stepToKcal(step)));
                time.setText(String.valueOf(this.timemm));
                delayHandler.sendEmptyMessageDelayed(Constant.Config.REQUEST_SERVER, TIME_INTERVAL);
                break;
            case Constant.Config.REQUEST_SERVER:
                try {
                    Message msg1 = Message.obtain(null, Constant.Config.MSG_FROM_CLIENT);
                    msg1.replyTo = mGetReplyMessenger;
                    messenger.send(msg1);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case TIMECOM:
                runnable.run();
                break;
        }
        return false;
    }

    private void startAnimation() {
        btContinue.setVisibility(View.VISIBLE);
        stop.setVisibility(View.VISIBLE);

        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;

        Animator animatorStart = ObjectAnimator.ofFloat(start, "alpha", 1.0f, 0f);
        AnimatorSet animatorSetStart = new AnimatorSet();
        animatorSetStart.playTogether(animatorStart);
        animatorSetStart.setInterpolator(new DecelerateInterpolator());
        animatorSetStart.setDuration(1000);
        animatorSetStart.start();

        Animator animatorContinue1 = ObjectAnimator.ofFloat(btContinue, "alpha", 0f, 1.0f);
        animatorContinue1.setDuration(3000);
        Animator animatorContinue2 = ObjectAnimator.ofFloat(btContinue, "translationX", -width / 3, btContinue.getX());
        animatorContinue2.setDuration(2000);
        AnimatorSet animatorSetContinue = new AnimatorSet();
        animatorSetContinue.playTogether(animatorContinue1, animatorContinue2);
        animatorSetContinue.setInterpolator(new DecelerateInterpolator());

        Animator animatorStop1 = ObjectAnimator.ofFloat(stop, "alpha", 0f, 1.0f);
        animatorStop1.setDuration(3000);
        Animator animatorStop2 = ObjectAnimator.ofFloat(stop, "translationX", width / 3, 0);
        animatorStop2.setDuration(2000);
        AnimatorSet animatorSetStop = new AnimatorSet();
        animatorSetStop.playTogether(animatorStop1, animatorStop2);
        animatorSetStop.setInterpolator(new DecelerateInterpolator());

        animatorSetStart.start();
        animatorSetStop.start();
        animatorSetContinue.start();

        start.setVisibility(View.GONE);

    }
    private void setupService() {
        Intent intent = new Intent(this, StepService.class);
        bindService(intent, conn, Context.BIND_AUTO_CREATE);
        startService(intent);
    }
    //步数与热量的交
    private float stepToKcal(float step) {
        float i = 388.5f;
        return Math.round(100 * step * i / 10000.0f) / 100.0f;
    }
    @Override
    public void onBackPressed() {
        if (!isStart) {
            WalkingActivity.this.finish();
            super.onBackPressed();
        } else {
            new HintDialog.Builder(WalkingActivity.this).
                    setTitle("提示").
                    setMessage("确定退出跑步吗？您已跑步数将不会进行兑换！").
                    setConfirmBtnListener(new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //if (sp.getBoolean(Constant.Config.isStepServiceRunning, false)) {
                            sendBroadcast(new Intent(Constant.Config.stopStepService));
                            if (conn != null) {
                                unbindService(conn);
                            }
                            //SharedPreferences.Editor stop_editor = sp.edit();
                            //stop_editor.putBoolean(Constant.Config.isStepServiceRunning, false);
                            //stop_editor.commit();
                            //}
                            //WalkingActivity.this.finish();
                        }
                    }).onCreate().show();
        }

    }
}