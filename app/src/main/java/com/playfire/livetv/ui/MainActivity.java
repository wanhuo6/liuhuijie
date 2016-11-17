package com.playfire.livetv.ui;

import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.playfire.livetv.R;
import com.playfire.livetv.base.BaseActivity;
import com.playfire.livetv.common.listener.MyClickListener;
import com.playfire.livetv.utils.ScreenUtils;

import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import master.flame.danmaku.controller.DrawHandler;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.DanmakuTimer;
import master.flame.danmaku.danmaku.model.IDanmakus;
import master.flame.danmaku.danmaku.model.android.DanmakuContext;
import master.flame.danmaku.danmaku.model.android.Danmakus;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import master.flame.danmaku.ui.widget.DanmakuView;

public class MainActivity extends BaseActivity implements DrawHandler.Callback {

    private static final String TAG = MainActivity.class.getSimpleName();
    @BindView(R.id.video_view)
    VideoView mVideoView;
    @BindView(R.id.danmaku_view)
    DanmakuView mDanmakuView;
    @BindView(R.id.edit_text)
    EditText mEditText;
    @BindView(R.id.tv_send)
    TextView mTvSend;
    @BindView(R.id.ll_operation)
    LinearLayout mLlOperation;
    @BindView(R.id.rl_parent)
    RelativeLayout mRlParent;

    private boolean showDanmaku;

    private DanmakuContext danmakuContext;

    private BaseDanmakuParser parser = new BaseDanmakuParser() {
        @Override
        protected IDanmakus parse() {
            return new Danmakus();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        //Uri uri = Uri.parse("http://kuaikuai.oss-cn-beijing.aliyuncs.com/upload/d2a226b1-3344-499c-8acc-d08831334a77.mp4");
        // String url="http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4";
        //String url="http://kuaikuai.oss-cn-beijing.aliyuncs.com/upload/d2a226b1-3344-499c-8acc-d08831334a77.mp4";
        // String mUrl = "http://221.174.125.8:9009/live/chid=29";
        //videoView.setVideoURI(Uri.parse(url));
        mTvSend.setText("你好");
        mVideoView.setMediaController(new MediaController(this));
        mVideoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.test));
        // videoView.seekTo(2530000);
        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mVideoView.start();
            }
        });
        mVideoView.start();
        mDanmakuView.enableDanmakuDrawingCache(true);
        mDanmakuView.setCallback(this);
        danmakuContext = DanmakuContext.create();
        mDanmakuView.prepare(parser, danmakuContext);
        mDanmakuView.setOnClickListener(mMyClickListener);
        mTvSend.setOnClickListener(mMyClickListener);
        getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if (visibility == View.SYSTEM_UI_FLAG_VISIBLE) {
                    onWindowFocusChanged(true);
                }
            }
        });
    }

    /**
     * 向弹幕View中添加一条弹幕
     *
     * @param content    弹幕的具体内容
     * @param withBorder 弹幕是否有边框
     */
    private void addDanmaku(String content, boolean withBorder) {
        BaseDanmaku danmaku = danmakuContext.mDanmakuFactory.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL);
        danmaku.text = content;
        danmaku.padding = 5;
        danmaku.textSize = ScreenUtils.sp2px(20);
        danmaku.textColor = Color.WHITE;
        danmaku.setTime(mDanmakuView.getCurrentTime());
        if (withBorder) {
            danmaku.borderColor = Color.GREEN;
        }
        mDanmakuView.addDanmaku(danmaku);
    }

    /**
     * 随机生成一些弹幕
     */
    private void generateSomeDanmaku() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (showDanmaku) {
                    int time = new Random().nextInt(300);
                    String content = "" + time + time;
                    addDanmaku(content, false);
                    try {
                        Thread.sleep(time);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (mDanmakuView != null && mDanmakuView.isPrepared()) {
            mDanmakuView.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mDanmakuView != null && mDanmakuView.isPrepared() && mDanmakuView.isPaused()) {
            mDanmakuView.resume();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        showDanmaku = false;
        if (mDanmakuView != null) {
            mDanmakuView.release();
            mDanmakuView = null;
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && Build.VERSION.SDK_INT >= 19) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }


    @Override
    public void prepared() {
        showDanmaku = true;
        mDanmakuView.start();
        generateSomeDanmaku();
    }

    @Override
    public void updateTimer(DanmakuTimer timer) {

    }

    @Override
    public void danmakuShown(BaseDanmaku danmaku) {

    }

    @Override
    public void drawingFinished() {

    }

    private MyClickListener mMyClickListener = new MyClickListener() {
        @Override
        protected void onKKClick(View v) {

            switch (v.getId()) {
                case R.id.ll_operation:
                    if (mLlOperation.getVisibility() == View.GONE) {
                        mLlOperation.setVisibility(View.VISIBLE);
                    } else {
                        mLlOperation.setVisibility(View.GONE);
                    }
                    break;
                case R.id.tv_send:
                    String content = mEditText.getText().toString();
                    if (!TextUtils.isEmpty(content)) {
                        addDanmaku(content, true);
                        mEditText.setText("");
                    }
                    break;

                default:
                    break;


            }

        }
    };

}
