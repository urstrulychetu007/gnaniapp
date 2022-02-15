package com.gnani.ai;

import android.app.Application;

import com.gnani.speechtotext.Recorder;

public class SpeechToText extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Recorder.init("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJ1c2VyX25hbWUiOiJiJ3pkbVo1M1gxSWhMSC9pMTZCbmdnMGl1bm5OcFIvWFVUeGJZbForeWtuYmM9JyIsImtleV9kYXRlIjoiMjAyMC0wMS0yOSJ9.6otHkdm0D3YSs43W9BiLij0tuNObN54k0RicIlJWkM2w_6gcT8deHSEA0Az7zUuJOPbYsExhBsnbp6Zy0pv7gA","8adf9dacff38e70af2b0cd12c57b1e30ec23d405");
    }
}
