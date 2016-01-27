package sg.fxl.topekaport;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import sg.fxl.topeka.model.CategoryJson;
import sg.fxl.topeka.model.Quiz;
import sg.fxl.topeka.model.Theme;
import sg.fxl.topeka.model.quiz.QuizQuestion;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private static final int REQUEST_CATEGORY = 0x2300;
    private Quiz quiz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startQuiz();
            }
        });
    }

    /**
     * Start the quiz
     */
    public void startQuiz() {
        // Create Quiz Question & Quiz
        quiz = new QuizBuilder()
                .name("Sample Quiz")
                .theme(Theme.yellow)
                .addVideoQuestion("Record a video of something.")
                .addImageQuestion("Take a image of a something.")
                .addSelectQuestion("Sample Questions 1", new int[]{0}, new String[]{"Option 1", "Option 2", "Option 3", "Option 4"})
                .addFillBlankQuestion("Sample Questions 2", "Answer")
                .addPickerQuestion("Sample Question 3", 5, 1, 10, 1)
                .create();

        // Setup Quiz Setting
        QuizSetting quizSetting = new QuizSetting();
        quizSetting.showStartScreen = false;
        quizSetting.showEndScreen = false;
        quizSetting.showTrueAnimationOnly = true;

        // Send it through json with intent
        Intent intent = new Intent(this, QuizActivity.class);
        CategoryJson.to(intent, quiz);
        QuizSetting.Json.to(intent, quizSetting);
        ActivityCompat.startActivityForResult(this, intent, REQUEST_CATEGORY, null);
    }

    /**
     * On QuizQuestion end
     *
     * @param requestCode requestCode
     * @param resultCode  resultCode
     * @param intent      data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == R.id.solved) {
            Quiz quiz = CategoryJson.from(intent);
            for (QuizQuestion quizQuestion : quiz.getQuizzes()) {
                Log.d("Result", quizQuestion.getSelectedAnswerString());
            }
        }
    }
}
