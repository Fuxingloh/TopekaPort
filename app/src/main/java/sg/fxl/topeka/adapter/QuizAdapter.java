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

package sg.fxl.topeka.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import sg.fxl.topeka.model.Quiz;
import sg.fxl.topeka.model.quiz.AlphaPickerQuizQuestion;
import sg.fxl.topeka.model.quiz.FillBlankQuizQuestion;
import sg.fxl.topeka.model.quiz.FillTwoBlanksQuizQuestion;
import sg.fxl.topeka.model.quiz.FourQuarterQuizQuestion;
import sg.fxl.topeka.model.quiz.MultiSelectQuizQuestion;
import sg.fxl.topeka.model.quiz.PickerQuizQuestion;
import sg.fxl.topeka.model.quiz.QuizQuestion;
import sg.fxl.topeka.model.quiz.SelectItemQuizQuestion;
import sg.fxl.topeka.model.quiz.ToggleTranslateQuizQuestion;
import sg.fxl.topeka.model.quiz.TrueFalseQuizQuestion;
import sg.fxl.topeka.widget.quiz.AbsQuizView;
import sg.fxl.topeka.widget.quiz.AlphaPickerQuizView;
import sg.fxl.topeka.widget.quiz.FillBlankQuizView;
import sg.fxl.topeka.widget.quiz.FillTwoBlanksQuizView;
import sg.fxl.topeka.widget.quiz.FourQuarterQuizView;
import sg.fxl.topeka.widget.quiz.MultiSelectQuizView;
import sg.fxl.topeka.widget.quiz.PickerQuizView;
import sg.fxl.topeka.widget.quiz.SelectItemQuizView;
import sg.fxl.topeka.widget.quiz.ToggleTranslateQuizView;
import sg.fxl.topeka.widget.quiz.TrueFalseQuizView;
import sg.fxl.topekaport.QuizSetting;

/**
 * Adapter to display quizzes.
 */
public class QuizAdapter extends BaseAdapter {

    private final Context context;
    private final List<QuizQuestion> quizzes;
    private final Quiz quiz;
    private final QuizSetting quizSetting;
    private final int viewTypeCount;
    private List<String> quizTypes;

    public QuizAdapter(Context context, Quiz quiz, QuizSetting quizSetting) {
        this.context = context;
        this.quiz = quiz;
        this.quizzes = quiz.getQuizzes();
        this.viewTypeCount = calculateViewTypeCount();
        this.quizSetting = quizSetting;
    }

    private int calculateViewTypeCount() {
        Set<String> tmpTypes = new HashSet<>();
        for (int i = 0; i < quizzes.size(); i++) {
            tmpTypes.add(quizzes.get(i).getType().getJsonName());
        }
        quizTypes = new ArrayList<>(tmpTypes);
        return quizTypes.size();
    }

    @Override
    public int getCount() {
        return quizzes.size();
    }

    @Override
    public QuizQuestion getItem(int position) {
        return quizzes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return quizzes.get(position).getId();
    }

    @Override
    public int getViewTypeCount() {
        return viewTypeCount;
    }

    @Override
    public int getItemViewType(int position) {
        return quizTypes.indexOf(getItem(position).getType().getJsonName());
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final QuizQuestion quizQuestion = getItem(position);
        if (convertView instanceof AbsQuizView) {
            AbsQuizView absQuizView = (AbsQuizView) convertView;
            if (absQuizView.getQuiz().equals(quizQuestion)) {
                return convertView;
            }
        }
        convertView = getViewInternal(quizQuestion);
        return convertView;
    }

    private AbsQuizView getViewInternal(QuizQuestion quizQuestion) {
        if (null == quizQuestion) {
            throw new IllegalArgumentException("QuizQuestion must not be null");
        }
        AbsQuizView absQuizView = createViewFor(quizQuestion);
        absQuizView.setQuizSetting(quizSetting);
        return absQuizView;
    }

    private AbsQuizView createViewFor(QuizQuestion quizQuestion) {
        switch (quizQuestion.getType()) {
            case ALPHA_PICKER:
                return new AlphaPickerQuizView(context, quiz, (AlphaPickerQuizQuestion) quizQuestion);
            case FILL_BLANK:
                return new FillBlankQuizView(context, quiz, (FillBlankQuizQuestion) quizQuestion);
            case FILL_TWO_BLANKS:
                return new FillTwoBlanksQuizView(context, quiz, (FillTwoBlanksQuizQuestion) quizQuestion);
            case FOUR_QUARTER:
                return new FourQuarterQuizView(context, quiz, (FourQuarterQuizQuestion) quizQuestion);
            case MULTI_SELECT:
                return new MultiSelectQuizView(context, quiz, (MultiSelectQuizQuestion) quizQuestion);
            case PICKER:
                return new PickerQuizView(context, quiz, (PickerQuizQuestion) quizQuestion);
            case SINGLE_SELECT:
            case SINGLE_SELECT_ITEM:
                return new SelectItemQuizView(context, quiz, (SelectItemQuizQuestion) quizQuestion);
            case TOGGLE_TRANSLATE:
                return new ToggleTranslateQuizView(context, quiz,
                        (ToggleTranslateQuizQuestion) quizQuestion);
            case TRUE_FALSE:
                return new TrueFalseQuizView(context, quiz, (TrueFalseQuizQuestion) quizQuestion);
        }
        throw new UnsupportedOperationException(
                "QuizQuestion of type " + quizQuestion.getType() + " can not be displayed.");
    }
}
