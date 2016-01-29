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
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import sg.fxl.topeka.model.Quiz;
import sg.fxl.topeka.model.quiz.ImageQuizQuestion;
import sg.fxl.topeka.widget.OnActivityResult;
import sg.fxl.topekaport.QuizActivity;
import sg.fxl.topekaport.R;

@SuppressLint("ViewConstructor")
public class ImageQuizView extends AbsQuizView<ImageQuizQuestion> implements OnActivityResult {

    private static final int REQUEST_TAKE_PHOTO = 1;
    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };

    private Button button;
    private ImageView imageView;

    private File photoFile;
    private String photoPath;

    public ImageQuizView(Context context, Quiz quiz, ImageQuizQuestion question) {
        super(context, quiz, question);
    }

    @Override
    protected View createQuizContentView() {
        ViewGroup view = (ViewGroup) getLayoutInflater().inflate(R.layout.quiz_button_image, this, false);
        button = (Button) view.findViewById(R.id.button);
        imageView = (ImageView) view.findViewById(R.id.imageView);
        imageView.setVisibility(GONE);
        button.setText("Take Picture");
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // Ensure that there's a camera activity to handle the intent
                if (takePictureIntent.resolveActivity(getContext().getPackageManager()) != null) {
                    // Create the File where the photo should go
                    photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException e) {
                        // Error occurred while creating the File
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("ImageQuizView", e.getMessage(), e);
                    }
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                        // Register for on activity result
                        QuizActivity activity = ((QuizActivity) getContext());
                        activity.registerOnActivityResult(ImageQuizView.this);
                        activity.startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                    }
                }
            }
        });
        return view;
    }

    private File createImageFile() throws IOException {
        int permission = ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission == PackageManager.PERMISSION_GRANTED) {
            // Create an image file name
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_";
            File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            File image = File.createTempFile(imageFileName, ".jpg", storageDir);
            // Save a file: path for use with ACTION_VIEW intents
            photoPath = "file://" + image.getPath();
            return image;
        } else {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    (Activity) getContext(),
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
            return null;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
            button.setVisibility(GONE);
            imageView.setVisibility(VISIBLE);

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = BitmapFactory.decodeFile(photoFile.getPath(), options);
            imageView.setImageBitmap(ThumbnailUtils.extractThumbnail(bitmap, 500, 500));
            allowAnswer();
        }
    }

    @Override
    protected boolean isAnswerCorrect() {
        getQuiz().setSelectedAnswer(photoFile.getPath());
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
