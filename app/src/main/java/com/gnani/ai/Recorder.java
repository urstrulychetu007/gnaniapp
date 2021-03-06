package com.gnani.ai;

import static android.content.Context.BIND_AUTO_CREATE;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.preference.PreferenceManager;

import androidx.core.app.ActivityCompat;

import com.gnani.ai.SpeechService;

public class Recorder {

    private static SpeechService mSpeechService;
    private static boolean mStartRecording = false;
    private static final int RECORDER_SAMPLERATE = 16000;
    private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
    private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    private static AudioRecord recorder = null;
    private static int bufferSize = AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE,
            RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING);
    private static Thread recordingThread = null;
    private static SpeechService.Listener listener;
    private static RecordingStatusListener listener1;
    private static CountDownTimer countDownTimer;
    private static String TOKEN = null;
    private static String ACCESS_KEY = null;
    private static String UID = null;

    public static void bind(Context context) {

        listener = (SpeechService.Listener) context;
        listener1 = (RecordingStatusListener) context;
        context.bindService(new Intent(context, SpeechService.class), mServiceConnection, BIND_AUTO_CREATE);

    }

    public static void unbind(Context context) {
        mSpeechService.removeListener(mSpeechServiceListener);
        context.unbindService(mServiceConnection);
        mSpeechService = null;

        stopCounter();
    }

    public static void onRecord(String lang) {

        if (!mStartRecording) {
            mSpeechService.startRecognizing(UID, TOKEN, lang, ACCESS_KEY, mSpeechService.getString(com.gnani.ai.R.string.audio_format_value), mSpeechService.getString(com.gnani.ai.R.string.encoding_value), mSpeechService.getString(com.gnani.ai.R.string.sad_value), mSpeechService.getString(com.gnani.ai.R.string.stt_api_value), mSpeechService.getResources().getInteger(com.gnani.ai.R.integer.stt_api_port_value), mSpeechService.getResources().getBoolean(com.gnani.ai.R.bool.tls_value));
            startRecording();

        } else {
            stopCounter();
        }
    }


    public static void setDefaults(String key, String value, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getDefaults(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, null);
    }

    private static void startRecording() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        recorder = new AudioRecord(MediaRecorder.AudioSource.VOICE_RECOGNITION,
                RECORDER_SAMPLERATE,
                RECORDER_CHANNELS,
                RECORDER_AUDIO_ENCODING,
                bufferSize);
        int i = recorder.getState();
        if (i == 1)
            recorder.startRecording();

        if (listener1 != null) {
            listener1.onRecordingStatus(true);
        }

        startCounter();

        mStartRecording = true;

        recordingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                byte[] data = new byte[bufferSize];
                int read = 0;
                while (mStartRecording) {

                    read = recorder.read(data, 0, bufferSize);
                    if (read > 0) {
                    }

                    if (AudioRecord.ERROR_INVALID_OPERATION != read) {

                        try {
                            mSpeechService.recognize(data, bufferSize);

                        } catch (Exception e) {
                        }

                    }
                }
            }
        }, "AudioRecorder Thread");

        recordingThread.start();
    }


    private static void stopRecording() {
        if (recorder != null) {

            int i = recorder.getState();

            if (i == 1)
                recorder.stop();
            recorder.release();

            mStartRecording = false;

            if (listener1 != null) {
                listener1.onRecordingStatus(false);
            }
            recorder = null;
            recordingThread = null;
        }

    }

    private static final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder binder) {
            mSpeechService = SpeechService.from(binder);
            mSpeechService.addListener(mSpeechServiceListener);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mSpeechService = null;
        }

    };

    private static final SpeechService.Listener mSpeechServiceListener =
            new SpeechService.Listener() {
                @Override
                public void onSpeechRecognized(final String text, final String asr, final boolean isFinal) {

                    if (asr != null) {
                        if (asr.equalsIgnoreCase(mSpeechService.getString(com.gnani.ai.R.string.app_name))) {
                            listener.onSpeechRecognized(text, asr, isFinal);

                        } else if (asr.equalsIgnoreCase(mSpeechService.getString(com.gnani.ai.R.string.yes))) {

                            stopCounter();
                            listener.onSpeechRecognized(text, asr, isFinal);

                        }
                    }
                }

                @Override
                public void onError(Throwable t) {

                    stopCounter();
                    listener.onError(t);

                }

            };


    public interface RecordingStatusListener {

        /**
         * Called when a recording starts and stops.
         * <p>
         * //         * @param boolean status    true if recording is started and false if recording is stopped.
         */
        void onRecordingStatus(boolean status);
    }

    private static void startCounter() {

        countDownTimer = new CountDownTimer(16000, 1000) {

            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                stopCounter();
            }
        }.start();
    }

    private static void stopCounter() {

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        if (mSpeechService != null) {
            mSpeechService.finishRecognizing();
        }
        stopRecording();
    }

    public static void init(String token, String accessKey,String uid) {

        TOKEN = token;
        ACCESS_KEY = accessKey;
        UID=uid;

    }

}