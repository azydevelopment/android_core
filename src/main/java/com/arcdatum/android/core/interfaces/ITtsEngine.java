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

package com.arcdatum.android.core.interfaces;

import android.annotation.TargetApi;
import android.os.Build;
import android.speech.tts.Voice;

import com.arcdatum.android.core.asyncTasks.AsyncTaskListening;
import com.arcdatum.android.core.asyncTasks.AsyncTaskListening.OnCompletedListener;

import java.util.List;
import java.util.Locale;

public interface ITtsEngine {

    interface IDelTtsEngine {
        void onPlay(ITtsEngine engine);

        void onPause(ITtsEngine engine);

        void onSeekedTo(ITtsEngine engine, int indexSeekedTo);

        void onDoneUtterance(ITtsEngine engine, String utteranceId);

        void onDoneTtsSrc(ITtsEngine engine);
    }

    interface IListenerTtsEngineOnInit {
        void onInit(ITtsEngine engine);
    }

    enum STATE {
        UNINITIALIZED, FLUSHING_THEN_STOP, FLUSHING_THEN_PLAY, STOPPED, PLAYING
    }

    boolean isInitialized();

    boolean isPlaying();

    boolean isFlushing();

    List<EngineInfo> getEngines();

    List<VoiceInfo> getVoices();

    void getVoicesAsync(OnCompletedListener<List<VoiceInfo>> listener);

    void setDelegate(IDelTtsEngine del);

    void setEngine(String packageName, IListenerTtsEngineOnInit listener);

    void setVoice(VoiceInfo voiceInfo);

    void setPitch(float pitch);

    void setSpeed(float speed);

    void setSrc(ITtsSrc src);

    int getNumTtsItems();

    int getTtsIndexCur();

    void restartItem();

    void togglePlaying();

    void play();

    void stop();

    void seekTo(int index, boolean playImmediately);

    void destroy();

    class AsyncTaskGetVoices extends AsyncTaskListening<Void, List<VoiceInfo>> {
        private ITtsEngine mTtsEngine;

        public AsyncTaskGetVoices(ITtsEngine iTtsEngine) {
            mTtsEngine = iTtsEngine;
        }

        @Override
        protected List<VoiceInfo> doInBackground(Void... params) {
            return mTtsEngine.getVoices();
        }
    }

    class EngineInfo implements
            Comparable<EngineInfo> {

        public static final int MATCH_NONE = -1;
        public static final int MATCH_EXACT = 0;

        private String mName;
        private String mPackageName;

        public EngineInfo(String name, String packageName) {
            mName = name;
            mPackageName = packageName;
        }

        public String getName() {
            return mName;
        }

        public String getPackageName() {
            return mPackageName;
        }

        public int comparePackage(String packageTarget) {
            String packageName = getPackageName();
            if (packageName.equals(packageTarget)) {
                return MATCH_EXACT;
            } else {
                return MATCH_NONE;
            }
        }

        // Comparable

        @Override
        public int compareTo(EngineInfo another) {
            return mName.compareTo(another.mName);
        }

        // Search utility

        public static int findBestMatch(List<EngineInfo> engines, String packageTarget) {
            // TODO HACK: Don't really like iterating through all the items to find the currently set one.  Use a hash of some sort perhaps?

            int index = -1;

            if (engines != null) {
                outerloop:
                for (int j = engines.size() - 1; j >= 0; j--) {
                    EngineInfo info = engines.get(j);

                    switch (info.comparePackage(packageTarget)) {
                        case EngineInfo.MATCH_EXACT:
                            // If it equals exactly, then early out
                            index = j;
                            break outerloop;
                        default:
                            break;
                    }
                }
            }

            return index;
        }
    }

    class VoiceInfo implements
            Comparable<VoiceInfo> {

        public static final int MATCH_NONE = -1;
        public static final int MATCH_EXACT = 0;
        public static final int MATCH_CLOSE = 1;

        private Locale mLocale;
        private String mName;
        private String mCode;

        private Voice mVoice;

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        public VoiceInfo(Voice voice) {
            mVoice = voice;

            Locale locale = voice.getLocale();

            mLocale = locale;
            mName = locale.getDisplayName();
            mCode = voice.getName();
        }

        public VoiceInfo(Locale locale, String name) {
            mLocale = locale;
            mName = name;
            mCode = locale.toString();
        }

        public boolean hasVoice() {
            return mVoice != null;
        }

        public Voice getVoice() {
            return mVoice;
        }

        public Locale getLocale() {
            return mLocale;
        }

        public String getName() {
            return mName;
        }

        public String getCode() {
            return mCode;
        }

        public int compareCode(String codeTarget) {
            String code = getCode();
            if (code.equals(codeTarget)) {
                return MATCH_EXACT;
            } else if (codeTarget.startsWith(codeTarget) || codeTarget.startsWith(code)) {
                return MATCH_CLOSE;
            } else {
                return MATCH_NONE;
            }
        }

        // Comparable

        @Override
        public int compareTo(VoiceInfo another) {
            return mName.compareTo(another.mName);
        }

        // Search utility

        public static int findBestMatch(List<VoiceInfo> voices, String codeTarget) {
            // TODO HACK: Don't really like iterating through all the items to find the currently set one.  Use a hash of some sort perhaps?

            int index = -1;

            if (voices != null) {
                outerloop:
                for (int j = voices.size() - 1; j >= 0; j--) {
                    VoiceInfo info = voices.get(j);

                    switch (info.compareCode(codeTarget)) {
                        case VoiceInfo.MATCH_EXACT:
                            // If it equals exactly, then early out
                            index = j;
                            break outerloop;
                        case VoiceInfo.MATCH_CLOSE:
                            // If it doesn't equal exactly, set it as selected but continue looking for a better match
                            index = j;
                            break;
                        default:
                            break;
                    }
                }
            }

            return index;
        }
    }


}
