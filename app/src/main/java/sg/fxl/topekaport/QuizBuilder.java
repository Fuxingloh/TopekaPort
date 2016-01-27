package sg.fxl.topekaport;

import java.util.ArrayList;
import java.util.List;

import sg.fxl.topeka.model.Quiz;
import sg.fxl.topeka.model.Theme;
import sg.fxl.topeka.model.quiz.FillBlankQuizQuestion;
import sg.fxl.topeka.model.quiz.ImageQuizQuestion;
import sg.fxl.topeka.model.quiz.MultiSelectQuizQuestion;
import sg.fxl.topeka.model.quiz.PickerQuizQuestion;
import sg.fxl.topeka.model.quiz.QuizQuestion;
import sg.fxl.topeka.model.quiz.SelectItemQuizQuestion;
import sg.fxl.topeka.model.quiz.VideoQuizQuestion;

/**
 * Created by: Fuxing
 * Date: 26/1/2016
 * Time: 7:23 PM
 * Project: Topeka Port
 */
public class QuizBuilder {
    private String name;
    private Theme theme;
    private List<QuizQuestion> quizQuestionList = new ArrayList<>();

    public QuizBuilder() {
    }

    public QuizBuilder name(String name){
        this.name = name;
        return this;
    }

    public QuizBuilder theme(Theme theme){
        this.theme = theme;
        return this;
    }

    // Add Questions
    public QuizBuilder addSelectQuestion(String title, int[] answers, String[] options){
        this.quizQuestionList.add(new SelectItemQuizQuestion(title, answers, options, false));
        return this;
    }

    public QuizBuilder addSelectMultiQuestion(String title, int[] answers, String[] options){
        this.quizQuestionList.add(new MultiSelectQuizQuestion(title, answers, options, false));
        return this;
    }

    public QuizBuilder addFillBlankQuestion(String title, String answer){
        this.quizQuestionList.add(new FillBlankQuizQuestion(title, answer, null, null, false));
        return this;
    }

    public QuizBuilder addPickerQuestion(String title, Integer answer, int min, int max, int steps){
        this.quizQuestionList.add(new PickerQuizQuestion(title, answer, min, max, steps, false));
        return this;
    }

    public QuizBuilder addImageQuestion(String title){
        this.quizQuestionList.add(new ImageQuizQuestion(title, false));
        return this;
    }

    public QuizBuilder addVideoQuestion(String title){
        this.quizQuestionList.add(new VideoQuizQuestion(title, false));
        return this;
    }

    public Quiz create(){
        return new Quiz(name, name, theme, quizQuestionList, false);
    }
}
