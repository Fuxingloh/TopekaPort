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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import sg.fxl.topeka.model.Quiz;
import sg.fxl.topeka.model.quiz.VideoQuizQuestion;
import sg.fxl.topeka.util.VideoThumbnail;
import sg.fxl.topeka.widget.OnActivityResult;
import sg.fxl.topekaport.QuizActivity;
import sg.fxl.topekaport.R;

@SuppressLint("ViewConstructor")
public class VideoQuizView extends AbsQuizView<VideoQuizQuestion> implements OnActivityResult {

    private static final int REQUEST_VIDEO_CAPTURE = 1;

    private Button button;
    private ImageView imageView;
    private Uri videoUri;

    public VideoQuizView(Context context, Quiz quiz, VideoQuizQuestion question) {
        super(context, quiz, question);
    }

    @Override
    protected View createQuizContentView() {
        ViewGroup view = (ViewGroup) getLayoutInflater().inflate(R.layout.quiz_button_image, this, false);
        button = (Button) view.findViewById(R.id.button);
        imageView = (ImageView) view.findViewById(R.id.imageView);
        imageView.setVisibility(GONE);
        button.setText("Take Video");
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                if (takeVideoIntent.resolveActivity(getContext().getPackageManager()) != null) {
                    QuizActivity activity = ((QuizActivity) getContext());
                    activity.registerOnActivityResult(VideoQuizView.this);
                    activity.startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
                }
            }
        });
        return view;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == Activity.RESULT_OK) {
            button.setVisibility(GONE);
            imageView.setVisibility(VISIBLE);

            videoUri = data.getData();
            // Get video thumbnail
            String imagePath = VideoThumbnail.getThumbnailPathForLocalFile((Activity) getContext(), videoUri);

            // Convert to 500 by 500
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);
            imageView.setImageBitmap(ThumbnailUtils.extractThumbnail(bitmap, 500, 500));

            allowAnswer();
        }
    }

    @Override
    protected boolean isAnswerCorrect() {
        getQuiz().setSelectedAnswer(videoUri.getPath());
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
