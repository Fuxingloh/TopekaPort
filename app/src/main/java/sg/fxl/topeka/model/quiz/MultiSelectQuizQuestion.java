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

package sg.fxl.topeka.model.quiz;


import sg.fxl.topeka.helper.AnswerHelper;

public final class MultiSelectQuizQuestion extends OptionsQuizQuestion<String> {

    public MultiSelectQuizQuestion(String question, int[] answer, String[] options, boolean solved) {
        super(question, answer, options, solved);
    }

    @Override
    public QuizType getType() {
        return QuizType.MULTI_SELECT;
    }

    @Override
    public String getStringAnswer() {
        return AnswerHelper.getAnswer(getAnswer(), getOptions());
    }

    @Override
    public String getSelectedAnswerString() {
        return AnswerHelper.getAnswer(getSelectedAnswer(), getOptions());
    }

}
