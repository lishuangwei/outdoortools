package com.v.smartassistant.outdoortool.Activities;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.v.smartassistant.outdoortool.CustomView.NoiseProgressView;
import com.v.smartassistant.outdoortool.R;

import java.text.DecimalFormat;

public class NoiseActivity extends AppCompatActivity {
    String TAG = "NoiseActivity";
    private NoiseProgressView mProgress;
    private TextView mLevel1, mLevel2, mLevel3, mLevel4, mLevel5;

    public static final int SAMPLE_RATE_IN_HZ = 8000;
    public static final int BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLE_RATE_IN_HZ,
            AudioFormat.CHANNEL_IN_DEFAULT, AudioFormat.ENCODING_PCM_16BIT);
    private AudioRecord mAudioRecord;
    private boolean mStart;
    private Object mLock;
    private double mVolume;
    private DecimalFormat df;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int num = Integer.parseInt(df.format(mVolume));
            Log.d("shuang", "handleMessage: " + mVolume + "-----" + num);
            mProgress.setPercent(num * 100 / 150);
            setTextColor(num);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.noise_layout);
        init();
        getNoiseLevel();
    }

    private void init() {
        mProgress = (NoiseProgressView) findViewById(R.id.noise_progress);
        mLock = new Object();
        df = new DecimalFormat("#");
        mStart = true;

        mLevel1 = (TextView) findViewById(R.id.level1);
        mLevel2 = (TextView) findViewById(R.id.level2);
        mLevel3 = (TextView) findViewById(R.id.level3);
        mLevel4 = (TextView) findViewById(R.id.level4);
        mLevel5 = (TextView) findViewById(R.id.level5);
        mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE_IN_HZ, AudioFormat.CHANNEL_IN_DEFAULT,
                AudioFormat.ENCODING_PCM_16BIT, BUFFER_SIZE);
    }

    private void setTextColor(int db) {
        if (db < 45 && db >= 0) {
            resetColor();
            mLevel1.setTextColor(getColor(R.color.noise_text_active_color));
        } else if (db >= 45 && db < 60) {
            resetColor();
            mLevel2.setTextColor(getColor(R.color.noise_text_active_color));
        } else if (db >= 60 && db < 80) {
            resetColor();
            mLevel3.setTextColor(getColor(R.color.noise_text_active_color));
        } else if (db >= 80 && db < 115) {
            resetColor();
            mLevel4.setTextColor(getColor(R.color.noise_text_active_color));
        } else if (db >= 115 && db <= 150) {
            resetColor();
            mLevel5.setTextColor(getColor(R.color.noise_text_active_color));
        }
    }

    private void resetColor() {
        mLevel1.setTextColor(getColor(R.color.noise_text_normal_color));
        mLevel2.setTextColor(getColor(R.color.noise_text_normal_color));
        mLevel3.setTextColor(getColor(R.color.noise_text_normal_color));
        mLevel4.setTextColor(getColor(R.color.noise_text_normal_color));
        mLevel5.setTextColor(getColor(R.color.noise_text_normal_color));
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mStart = false;
    }

    public void getNoiseLevel() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mAudioRecord.startRecording();
                short[] buffer = new short[BUFFER_SIZE];
                while (mStart) {
                    if (mAudioRecord != null) {
                        int r = mAudioRecord.read(buffer, 0, BUFFER_SIZE);
                        long v = 0;
                        for (int i = 0; i < buffer.length; i++) {
                            v += buffer[i] * buffer[i];
                        }
                        double mean = v / (double) r;
                        double volume = 10 * Math.log10(mean);
                        mVolume = volume;
                        mHandler.sendEmptyMessage(0);

                        synchronized (mLock) {
                            try {
                                mLock.wait(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                }
                mAudioRecord.stop();
                mAudioRecord.release();
                mAudioRecord = null;
            }
        }).start();
    }
}
