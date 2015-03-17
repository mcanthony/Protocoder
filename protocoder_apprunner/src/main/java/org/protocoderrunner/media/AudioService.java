/*
 * Protocoder 
 * A prototyping platform for Android devices 
 * 
 * Victor Diaz Barrales victormdb@gmail.com
 *
 * Copyright (C) 2014 Victor Diaz
 * Copyright (C) 2013 Motorola Mobility LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions: 
 * 
 * The above copyright notice and this permission notice shall be included in all 
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN 
 * THE SOFTWARE.
 * 
 */

package org.protocoderrunner.media;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import org.protocoderrunner.R;
import org.protocoderrunner.apprunner.AppRunnerFragment;
import org.protocoderrunner.utils.MLog;
import org.puredata.android.io.AudioParameters;
import org.puredata.android.service.PdService;
import org.puredata.core.PdBase;
import org.puredata.core.utils.IoUtils;

import java.io.File;
import java.io.IOException;

public class AudioService {

    public static final String TAG = "PdAudioService";

    public static PdService pdService = null;
	public static String file;

    public static int settingsSampleRate = -1;
    public static int settingsMicChannels = -1;
    public static int settingsOutputChannels = -1;
    public static int settingsBuffer = -1;

    public static final ServiceConnection pdConnection = new ServiceConnection() {

        @Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			pdService = ((PdService.PdBinder) service).getService();

			MLog.d(TAG, "service connected");

			try {
				initPd();
				// loadPatchFromResources();
				loadPatchFromDirectory(file);
			} catch (IOException e) {
                MLog.d(TAG, "error loading pd file");
				finish();
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			stop();
		}

		public void stop() {
			MLog.d(TAG, "stopping audio");
			try {
                MLog.d(TAG, "stopping audio 1");

                if (pdService != null) {
                    MLog.d(TAG, "stopping audio2");

                    pdService.stopAudio();
                    MLog.d(TAG, "stopping audio 3");

                    PdBase.release();
                    MLog.d(TAG, "stopping audio 4");

                    if (pdConnection != null) {
                        pdService.unbindService(pdConnection);
                        MLog.d(TAG, "stopping audio 5");

                    }
                    MLog.d(TAG, "stopping audio 6");


                }
			} catch (IllegalArgumentException e) {
				// already unbound
				pdService = null;
                MLog.d(TAG, "stopping audio 7");
                MLog.d(TAG, e.toString());
            }
		}

	};

    private static void initPd() throws IOException {

		// configure audio glue

		if (settingsSampleRate == -1); settingsSampleRate = AudioParameters.suggestSampleRate();
		if (settingsMicChannels == -1) settingsMicChannels = AudioParameters.suggestInputChannels();
        if (settingsOutputChannels == -1) settingsOutputChannels = 2;
        if (settingsBuffer == -1) settingsBuffer = 8;

		MLog.d(TAG, "mic channels" + settingsMicChannels);
		pdService.initAudio(settingsSampleRate, settingsMicChannels, settingsOutputChannels, settingsBuffer);


		start();

	}

	public static void start() {
		if (!pdService.isRunning()) {
			Intent intent = new Intent(pdService, AppRunnerFragment.class);
			pdService.startAudio();
		}
	}

	protected static void sendMessage(String message, String value) {
		if (value.isEmpty()) {
			PdBase.sendBang(message);
		} else if (value.matches("[0-9]+")) {
			PdBase.sendFloat(message, Float.parseFloat(value));
		} else {
			PdBase.sendSymbol(message, value);
		}
	}

	protected static void triggerNote(int value) {
		int m = (int) (Math.random() * 5);
		MLog.d(TAG, "triggerNote " + m);
		PdBase.sendFloat("midinote", value); // m);
		PdBase.sendBang("trigger");
	}

	protected static void sendBang(int value) {
		PdBase.sendBang("button" + value);
	}

	protected static void changeSpeed(int value) {
		PdBase.sendFloat("tempo", value); // m);

	}

	protected static void finish() {
	}

	private static void loadPatchFromResources() throws IOException {

		File dir = pdService.getFilesDir();
		IoUtils.extractZipResource(pdService.getResources().openRawResource(R.raw.tuner), dir, true);
		File patchFile = new File(dir, "tuner/sampleplay.pd");
		MLog.d(TAG, patchFile.getAbsolutePath());
		PdBase.openPatch(patchFile.getAbsolutePath());

	}

	private static void loadPatchFromDirectory(String file2) throws IOException {
		PdBase.openPatch(file);
	}

}
