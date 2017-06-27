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

import android.content.Context;
import android.media.AudioManager;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.speech.tts.UtteranceProgressListener;

import com.arcdatum.android.core.helpers.HlprDbg;
import com.arcdatum.android.core.helpers.HlprDbg.DBG_LVL;
import com.arcdatum.android.core.interfaces.ITtsEngine;
import com.arcdatum.android.core.interfaces.ITtsSrc;
import com.arcdatum.android.core.asyncTasks.AsyncTaskListening.OnCompletedListener;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

public class TtsEngine implements
        ITtsEngine {

    private static final String TAG = HlprDbg.TAG_PREFIX + TtsEngine.class.getSimpleName();

    private static final String UTTERANCE_ID_FLUSHING = Integer.toString(-1);

    private static final int LOAD_BUFFER = 3;

    private Context mContext;

    private WeakReference<IDelTtsEngine> mDel = new WeakReference<>(null);

    private TextToSpeech mTts;
    private String mPackageCur = "";

    private STATE mState;

    private ITtsSrc mITtsSrc;

    private int mTtsIndexCur = 0;
    private int mTtsIndexNextToLoad = -1;

    private HashSet<String> mHashSetUtterancesInFlight = new HashSet<>();

    public TtsEngine(Context context) {
        mContext = context;
        mState = STATE.UNINITIALIZED;
    }

    // ITtsEngine

    @Override
    public boolean isInitialized() {
        return mState != STATE.UNINITIALIZED;
    }

    @Override
    public boolean isPlaying() {
        return mState == STATE.PLAYING;
    }

    @Override
    public boolean isFlushing() {
        return (mState == STATE.FLUSHING_THEN_PLAY) || (mState == STATE.FLUSHING_THEN_STOP);
    }

    @Override
    public List<EngineInfo> getEngines() {
        List<EngineInfo> engines = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= 14) {
            List<TextToSpeech.EngineInfo> enginesRaw = mTts.getEngines();

            for (int i = 0; i < enginesRaw.size(); i++) {
                TextToSpeech.EngineInfo engineRaw = enginesRaw.get(i);
                engines.add(new EngineInfo(engineRaw.label, engineRaw.name));
            }
        } else {
            String engineDefault = mTts.getDefaultEngine();
            EngineInfo engineInfo = new EngineInfo(engineDefault, engineDefault);
            engines.add(engineInfo);
        }

        Collections.sort(engines);

        return engines;
    }

    @Override
    public List<VoiceInfo> getVoices() {
        List<VoiceInfo> voiceInfos = new ArrayList<>();

        boolean isVoiceEnumerationSupported = false;

        // TODO BUG: For some reason, this returns a lot of voices that aren't even available
//        try {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                Set<android.speech.tts.Voice> voicesRaw = mTts.getVoicesAsync();
//                if (voicesRaw != null) {
//                    for (android.speech.tts.Voice voice : voicesRaw) {
//                        voiceInfos.add(new VoiceInfo(voice));
//                    }
//                    isVoiceEnumerationSupported = true;
//                }
//            }
//        } catch (Exception e) {
//            HlprDbg.log(HlprDbg.DBG_LVL.WARN, TAG, "VoiceInfo enumeration not supported | " + e.getMessage());
//        }

        if (!isVoiceEnumerationSupported) {
            // TODO PERF: This takes a rather long time :(
            // TODO HACK: I don't like this solution of searching through all available locales...
            Locale[] locales = Locale.getAvailableLocales();
            for (Locale locale : locales) {
                String localeString = locale.toString();
                if(localeString.contains("POSIX")){
                    // TODO HACK: Bug seems to be a Samsung OS bug
                    continue;
                }
                int res = mTts.isLanguageAvailable(locale);
                if (res == TextToSpeech.LANG_COUNTRY_AVAILABLE) {
                    VoiceInfo voiceInfo = new VoiceInfo(locale, locale.getDisplayName());
                    voiceInfos.add(voiceInfo);
                }
            }
        }

        Collections.sort(voiceInfos);

        return voiceInfos;
    }

    @Override
    public void getVoicesAsync(OnCompletedListener<List<VoiceInfo>> listener) {
        AsyncTaskGetVoices task = new AsyncTaskGetVoices(this);
        task.setListenerCompleted(listener);
        task.execute();
    }

    @Override
    public void setDelegate(IDelTtsEngine del) {
        mDel = new WeakReference<>(del);
    }

    @Override
    public void setEngine(String packageName, final IListenerTtsEngineOnInit listener) {
        final WeakReference<TtsEngine> weakSelf = new WeakReference<>(this);

        if (isPlaying()) {
            stop();
        }

        // TODO BUG: If not init this probably doesn't play nice with voice setting since this is async.\

        // TODO BUG: mTts is null at this point on first start

        OnInitListener initListener = new OnInitListener() {
            @Override
            public void onInit(int status) {
                HlprDbg.log(TAG, "TTS initialized | Status: " + Integer.toString(status));
                if (status == TextToSpeech.SUCCESS) {
                    registerListeners();
                    setState(STATE.STOPPED);
                    TtsEngine engine = weakSelf.get();
                    if (listener != null && engine != null) {
                        listener.onInit(engine);
                    }
                } else {
                    HlprDbg.log(DBG_LVL.ERROR, TAG, "Initialization failed!");
                    setState(STATE.UNINITIALIZED);
                }
            }
        };


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH
                && !packageName.equals(mPackageCur)
                && packageName.length() > 0) {
            mTts = new TextToSpeech(mContext, initListener, packageName);
        } else {
            mTts = new TextToSpeech(mContext, initListener);
        }

    }

    @Override
    public void setVoice(VoiceInfo voiceInfo) {
        // TODO FEATURE: Prevent redundant setting of voice.

        int result = -1;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && voiceInfo.hasVoice()) {
            result = mTts.setVoice(voiceInfo.getVoice());
        } else {
            result = mTts.setLanguage(voiceInfo.getLocale());
        }

        HlprDbg.log(TAG, "Set voice result: " + Integer.toString(result));

        restartItem();
    }

    @Override
    public void setPitch(float pitch) {
        mTts.setPitch(pitch);
        restartItem();
    }

    @Override
    public void setSpeed(float speed) {
        mTts.setSpeechRate(speed);
        restartItem();
    }

    @Override
    public void setSrc(ITtsSrc src) {
        mITtsSrc = src;
    }

    @Override
    public int getNumTtsItems() {
        return mITtsSrc.getNumTtsItems();
    }

    @Override
    public int getTtsIndexCur() {
        return mTtsIndexCur;
    }

    @Override
    public void restartItem() {
        if (isPlaying()) {
            // TODO BUG: Sometimes this replays the current item.  Sometimes it goes to the next one.
            seekTo(getTtsIndexCur(), true);
        }
    }

    @Override
    public void togglePlaying() {
        if (isPlaying()) {
            stop();
        } else {
            play();
        }
    }

    @Override
    public void play() {
        HlprDbg.log(DBG_LVL.INFO, TAG, "Index: " + mTtsIndexCur);
        setState(STATE.PLAYING);
        mTtsIndexNextToLoad = mTtsIndexCur;
        queueSpeech(mTtsIndexCur);

        IDelTtsEngine del = mDel.get();
        if (del != null) {
            del.onPlay(this);
        }
    }

    @Override
    public void stop() {
        flush(false);
        IDelTtsEngine del = mDel.get();
        if (del != null) {
            del.onPause(this);
        }
    }

    @Override
    public void seekTo(int index, boolean playImmediately) {
        if (!isFlushing()) {
            mTtsIndexCur = index;
            mTtsIndexNextToLoad = index;

            IDelTtsEngine del = mDel.get();
            if (del != null) {
                del.onSeekedTo(this, index);
            }

            if (playImmediately) {
                flush(playImmediately);
                play();
            } else {
                stop();
            }
        }
    }

    public void destroy() {
        if (mTts != null) {
            mTts.stop();
            mTts.shutdown();
        }
    }

    // Private API

    private void registerListeners(){
        if (mTts != null) {
            final WeakReference<TtsEngine> weakSelf = new WeakReference<>(this);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                mTts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                    @Override
                    public void onStart(String utteranceId) {
                    }

                    @Override
                    public void onStop(String utteranceId, boolean interrupted) {
                    }

                    @Override
                    public void onDone(String utteranceId) {
                        TtsEngine engine = weakSelf.get();
                        if (engine != null) {
                            engine.onDone(utteranceId);
                        }
                    }

                    @Override
                    public void onError(String utteranceId) {
                    }

                    @Override
                    public void onError(String utteranceId, int errorCode) {
                    }
                });
            } else {
                mTts.setOnUtteranceCompletedListener(new TextToSpeech.OnUtteranceCompletedListener() {
                    @Override
                    public void onUtteranceCompleted(String utteranceId) {
                        TtsEngine engine = weakSelf.get();
                        if (engine != null) {
                            engine.onDone(utteranceId);
                        }
                    }
                });
            }
        }
    }

    private void setState(STATE state) {
        mState = state;
    }

    @SuppressWarnings("deprecation")
    private void flush(boolean playImmediately) {
        if (playImmediately) {
            setState(STATE.FLUSHING_THEN_PLAY);
        } else {
            setState(STATE.FLUSHING_THEN_STOP);
        }

        mHashSetUtterancesInFlight.clear();

        // TODO HACK: Use a silent utterance to stop everything
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mTts.playSilentUtterance(30, TextToSpeech.QUEUE_FLUSH, UTTERANCE_ID_FLUSHING);
        } else {
            HashMap map = new HashMap<String, String>();
            map.put(TextToSpeech.Engine.KEY_PARAM_STREAM, String.valueOf(AudioManager.STREAM_MUSIC));
            map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, UTTERANCE_ID_FLUSHING);
            mTts.playSilence(30, TextToSpeech.QUEUE_FLUSH, map);
        }
    }

    @SuppressWarnings("deprecation")
    private void queueSpeech(int index) {

        if (mState != STATE.PLAYING) {
            HlprDbg.log(DBG_LVL.ERROR, TAG, "Not in the PLAYING state");
            return;
        }

        if (mITtsSrc == null) {
            HlprDbg.log(DBG_LVL.ERROR, TAG, "No TTS source");
            return;
        }

        int numItems = mITtsSrc.getNumTtsItems();

        while (mTtsIndexNextToLoad < numItems && mTtsIndexNextToLoad < mTtsIndexCur + LOAD_BUFFER) {

            String text = mITtsSrc.getTtsItem(mTtsIndexNextToLoad);

            String utteranceId = Integer.toString(mTtsIndexNextToLoad);

            mHashSetUtterancesInFlight.add(utteranceId);

            int result = 0;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                result = mTts.speak(text, TextToSpeech.QUEUE_ADD, null, utteranceId);
            } else {
                HashMap map = new HashMap<String, String>();
                map.put(TextToSpeech.Engine.KEY_PARAM_STREAM, String.valueOf(AudioManager.STREAM_MUSIC));
                map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceId);
                result = mTts.speak(text, TextToSpeech.QUEUE_ADD, map);
            }

            if (result != TextToSpeech.SUCCESS) {
                HlprDbg.log(DBG_LVL.ERROR, TAG, "Error enqueuing item ID: " + mTtsIndexNextToLoad);
            }

            mTtsIndexNextToLoad++;

        }

    }

    private void onDone(String utteranceId) {
        switch (mState) {
            case FLUSHING_THEN_STOP:
                setState(STATE.STOPPED);
                break;

            case FLUSHING_THEN_PLAY:
                setState(STATE.PLAYING);
                break;

            case PLAYING:

                // Only do anything if the utterance is marked as in flight
                if (mHashSetUtterancesInFlight.contains(utteranceId)) {

                    IDelTtsEngine del = mDel.get();

                    // Notify everyone this utterance is done
                    if (del != null) {
                        // TODO BUG: Some of these might be marked as done when seeking because of a flush
                        del.onDoneUtterance(this, utteranceId);
                    }

                    // Play the next thing
                    mTtsIndexCur++;
                    queueSpeech(mTtsIndexCur);

                    if (del != null) {
                        del.onSeekedTo(this, mTtsIndexCur);
                    }

                    // Check if we're at the end of this TTS source
                    if (Integer.parseInt(utteranceId) >= mITtsSrc.getNumTtsItems() - 1) {
                        HlprDbg.log(DBG_LVL.INFO, TAG, "Finished reading TTS source");
                        if (del != null) {
                            del.onDoneTtsSrc(this);
                        }
                    }

                }

                break;
        }
    }

}
