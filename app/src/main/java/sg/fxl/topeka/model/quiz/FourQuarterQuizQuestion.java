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

import java.util.Arrays;

import sg.fxl.topeka.helper.AnswerHelper;

public final class FourQuarterQuizQuestion extends OptionsQuizQuestion<String> {

    public FourQuarterQuizQuestion(String question, int[] answer, String[] options, boolean solved) {
        super(question, answer, options, solved);
    }

    @Override
    public QuizType getType() {
        return QuizType.FOUR_QUARTER;
    }

    @Override
    public String getStringAnswer() {
        return AnswerHelper.getAnswer(getAnswer(), getOptions());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FourQuarterQuizQuestion)) {
            return false;
        }

        FourQuarterQuizQuestion quiz = (FourQuarterQuizQuestion) o;
        final int[] answer = getAnswer();
        final String question = getQuestion();
        if (answer != null ? !Arrays.equals(answer, quiz.getAnswer()) : quiz.getAnswer() != null) {
            return false;
        }
        if (!question.equals(quiz.getQuestion())) {
            return false;
        }

        //noinspection RedundantIfStatement
        if (!Arrays.equals(getOptions(), quiz.getOptions())) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + Arrays.hashCode(getOptions());
        result = 31 * result + Arrays.hashCode(getAnswer());
        return result;
    }

}
