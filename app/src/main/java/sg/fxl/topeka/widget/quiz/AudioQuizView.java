/*
 * Copyright 2015 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package sg.fxl.topeka.widget.quiz;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import java.io.IOException;

import sg.fxl.topeka.model.Quiz;
import sg.fxl.topeka.model.quiz.AudioQuizQuestion;
import sg.fxl.topekaport.R;

@SuppressLint("ViewConstructor")
public class AudioQuizView extends AbsQuizView<AudioQuizQuestion> {
    private static final String TAG = "AudioQuizView";

    private Button recordButton;
    private Button playButton;
    private ImageView imageView;

    private boolean startRecording = true;
    private MediaRecorder recorder = null;

    private boolean startPlaying = true;
    private MediaPlayer player = null;

    private String fileName = null;

    public AudioQuizView(Context context, Quiz quiz, AudioQuizQuestion question) {
        super(context, quiz, question);
    }

    @Override
    protected View createQuizContentView() {
        ViewGroup view = (ViewGroup) getLayoutInflater().inflate(R.layout.quiz_2button_image, this, false);
        recordButton = (Button) view.findViewById(R.id.button);
        playButton = (Button) view.findViewById(R.id.button2);
        imageView = (ImageView) view.findViewById(R.id.imageView);
        imageView.setVisibility(GONE);

        setState(READY);
        recordButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (onRecord(startRecording)) {
                    if (startRecording) {
                        setState(RECORDING);
                    } else {
                        setState(READY_RECORDED);
                    }
                    startRecording = !startRecording;
                }
            }
        });

        playButton.setText("Start Playing");
        playButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                onPlay(startPlaying);
                if (startPlaying) {
                    setState(PLAYING);
                } else {
                    setState(READY_RECORDED);
                }
                startPlaying = !startPlaying;
            }
        });

        return view;
    }

    public static final int READY = 0;
    public static final int READY_RECORDED = 1;
    public static final int RECORDING = 2;
    public static final int PLAYING = 3;

    private void setState(int state) {
        switch (state) {
            case READY:
                playButton.setVisibility(GONE);
                recordButton.setVisibility(VISIBLE);
                playButton.setEnabled(false);
                allowAnswer(false);
                recordButton.setText("Start Recording");
                break;
            case READY_RECORDED:
                playButton.setVisibility(VISIBLE);
                recordButton.setVisibility(VISIBLE);
                playButton.setEnabled(true);
                allowAnswer(true);
                recordButton.setText("Start Recording");
                playButton.setText("Play Audio");
                break;
            case RECORDING:
                playButton.setVisibility(GONE);
                recordButton.setVisibility(VISIBLE);
                playButton.setEnabled(false);
                playButton.setText("Play Audio");
                recordButton.setText("Stop Recording");
                allowAnswer(false);
                break;
            case PLAYING:
                playButton.setVisibility(VISIBLE);
                recordButton.setVisibility(GONE);
                playButton.setEnabled(true);
                allowAnswer(false);
                recordButton.setText("Playing Audio");
                playButton.setText("Stop Playing");
                break;
        }
    }

    private boolean onRecord(boolean start) {
        if (start) {
            return startRecording();
        } else {
            stopRecording();
            return true;
        }
    }

    private void onPlay(boolean start) {
        if (start) {
            startPlaying();
        } else {
            stopPlaying();
        }
    }

    private void startPlaying() {
        player = new MediaPlayer();
        try {
            player.setDataSource(fileName);
            player.prepare();
            player.start();
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    setState(READY_RECORDED);
                    startPlaying = !startPlaying;
                }
            });
        } catch (IOException e) {
            Log.e(TAG, "prepare() failed");
        }
    }

    private void stopPlaying() {
        player.release();
        player = null;
    }

    private boolean startRecording() {
        int permission = ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int audioPermission = ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO);

        if (permission == PackageManager.PERMISSION_GRANTED && audioPermission == PackageManager.PERMISSION_GRANTED) {
            fileName = Environment.getExternalStorageDirectory().getAbsolutePath();
            fileName += "/" + System.currentTimeMillis() + ".mp4";

            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            recorder.setOutputFile(fileName);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

            try {
                recorder.prepare();
            } catch (IOException e) {
                Log.e(TAG, "prepare() failed");
            }

            recorder.start();
            return true;
        } else {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    (Activity) getContext(),
                    new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.RECORD_AUDIO
                    },
                    1
            );
            return false;
        }
    }

    private void stopRecording() {
        recorder.stop();
        recorder.release();
        recorder = null;
    }

    @Override
    protected boolean isAnswerCorrect() {
        getQuiz().setSelectedAnswer(fileName);
        return true;
    }

    @Override
    public Bundle getUserInput() {
        return new Bundle();
    }

    @Override
    public void setUserInput(Bundle savedInput) {
    }

}
