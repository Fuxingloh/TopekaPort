package sg.fxl.topekaport;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import java.util.Collections;

import sg.fxl.topeka.model.Category;
import sg.fxl.topeka.model.CategoryJson;
import sg.fxl.topeka.model.Theme;
import sg.fxl.topeka.model.quiz.QuizQuestion;
import sg.fxl.topeka.model.quiz.SelectItemQuizQuestion;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private static final int REQUEST_CATEGORY = 0x2300;
    private Category category;

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
    public void startQuiz(){
        // Create Quiz & Category
        QuizQuestion quizQuestion = new SelectItemQuizQuestion("Sample Question", new int[]{0},
                new String[]{"Option 1", "Option 2", "Option 3", "Option 4"}, false);
        category = new Category("Quiz", "quiz", Theme.yellow, Collections.singletonList(quizQuestion), false);

        // Send it through json with intent
        Intent intent = new Intent(this, QuizActivity.class);
        CategoryJson.to(intent, category);
        ActivityCompat.startActivityForResult(this, intent, REQUEST_CATEGORY, null);
    }

    /**
     * On QuizQuestion end
     * @param requestCode requestCode
     * @param resultCode resultCode
     * @param data data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == R.id.solved) {
            Category category = data.getParcelableExtra("quiz");
            for (QuizQuestion quizQuestion : category.getQuizzes()) {
                Log.d("Result", quizQuestion.getAnswer().toString());
            }
        }
    }
}
