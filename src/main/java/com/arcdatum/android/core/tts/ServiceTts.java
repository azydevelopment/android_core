/*
* Copyright (c) 2017 Andrew Yeung
*
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
*
* The above copyright notice and this permission notice shall be included in all
* copies or substantial portions of the Software.
*
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
* SOFTWARE.
*/

package com.arcdatum.android.core.tts;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.arcdatum.android.core.helpers.HlprDbg;
import com.arcdatum.android.core.interfaces.ITtsEngine;
import com.arcdatum.android.core.interfaces.ITtsSrc;
import com.arcdatum.android.core.asyncTasks.AsyncTaskListening.OnCompletedListener;
import com.arcdatum.android.core.interfaces.ITtsEngine.IDelTtsEngine;

import java.lang.ref.WeakReference;
import java.util.List;

public class ServiceTts extends Service implements
        ITtsEngine,
        IDelTtsEngine {

    private static final String TAG = HlprDbg.TAG_PREFIX + ServiceTts.class.getSimpleName();

    protected TtsEngine mTtsEngine;

    protected WeakReference<IDelTtsEngine> mDel = new WeakReference<>(null);

    // Service

    @Override
    public void onCreate() {
        HlprDbg.log(TAG);
        super.onCreate();
        mTtsEngine = new TtsEngine(this);
        mTtsEngine.setDelegate(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        HlprDbg.log(TAG);
        BinderTts binder = new BinderTts(this);
        setDelegate(binder);
        return binder;
    }

    @Override
    public void onDestroy() {
        HlprDbg.log(TAG);
        super.onDestroy();
        mTtsEngine.destroy();
    }

    // ITtsEngine

    @Override
    public boolean isInitialized() {
        return mTtsEngine.isInitialized();
    }

    @Override
    public boolean isPlaying() {
        return mTtsEngine.isPlaying();
    }

    @Override
    public boolean isFlushing() {
        return mTtsEngine.isFlushing();
    }

    @Override
    public List<EngineInfo> getEngines() {
        return mTtsEngine.getEngines();
    }

    @Override
    public List<VoiceInfo> getVoices() {
        return mTtsEngine.getVoices();
    }

    @Override
    public void getVoicesAsync(OnCompletedListener<List<VoiceInfo>> listener) {
        mTtsEngine.getVoicesAsync(listener);
    }

    @Override
    public void setDelegate(IDelTtsEngine del) {
        mDel = new WeakReference<>(del);
    }

    @Override
    public void setEngine(String packageName, IListenerTtsEngineOnInit listener) {
        mTtsEngine.setEngine(packageName, listener);
    }

    @Override
    public void setVoice(VoiceInfo voiceInfo) {
        mTtsEngine.setVoice(voiceInfo);
    }

    @Override
    public void setPitch(float pitch) {
        // TODO HACK: Hardcoded tts pitch coefficient
        mTtsEngine.setPitch(pitch * 2);
    }

    @Override
    public void setSpeed(float speed) {
        // TODO HACK: Hardcoded tts speed coefficient
        mTtsEngine.setSpeed(speed * 2);
    }

    @Override
    public void setSrc(ITtsSrc src) {
        mTtsEngine.setSrc(src);
    }

    @Override
    public int getNumTtsItems() {
        return mTtsEngine.getNumTtsItems();
    }

    @Override
    public int getTtsIndexCur() {
        return mTtsEngine.getTtsIndexCur();
    }

    @Override
    public void restartItem() {
        mTtsEngine.restartItem();
    }

    @Override
    public void togglePlaying() {
        mTtsEngine.togglePlaying();
    }

    @Override
    public void play() {
        mTtsEngine.play();
    }

    @Override
    public void stop() {
        mTtsEngine.stop();
    }

    @Override
    public void seekTo(int index, boolean playImmediately) {
        mTtsEngine.seekTo(index, playImmediately);
    }

    @Override
    public void destroy() {
        mTtsEngine.destroy();
    }

    // IDelTtsEngine

    @Override
    public void onPlay(ITtsEngine engine) {
        IDelTtsEngine del = mDel.get();
        if (del != null) {
            del.onPlay(this);
        }
    }

    @Override
    public void onPause(ITtsEngine engine) {
        IDelTtsEngine del = mDel.get();
        if (del != null) {
            del.onPause(this);
        }
    }

    @Override
    public void onSeekedTo(ITtsEngine engine, int indexSeekedTo) {
        IDelTtsEngine del = mDel.get();
        if (del != null) {
            del.onSeekedTo(this, indexSeekedTo);
        }
    }

    @Override
    public void onDoneUtterance(ITtsEngine engine, String utteranceId) {
        IDelTtsEngine del = mDel.get();
        if (del != null) {
            del.onDoneUtterance(this, utteranceId);
        }
    }

    @Override
    public void onDoneTtsSrc(ITtsEngine engine) {
        IDelTtsEngine del = mDel.get();
        if (del != null) {
            del.onDoneTtsSrc(this);
        }
    }
}
