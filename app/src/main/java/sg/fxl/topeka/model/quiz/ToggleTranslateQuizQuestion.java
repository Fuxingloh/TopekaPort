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

public final class ToggleTranslateQuizQuestion extends OptionsQuizQuestion<String[]> {

    private String[] mReadableOptions;

    public ToggleTranslateQuizQuestion(String question, int[] answer, String[][] options, boolean solved) {
        super(question, answer, options, solved);
    }

    @Override
    public QuizType getType() {
        return QuizType.TOGGLE_TRANSLATE;
    }

    @Override
    public String getStringAnswer() {
        return AnswerHelper.getAnswer(getAnswer(), getReadableOptions());
    }

    public String[] getReadableOptions() {
        //lazily initialize
        if (null == mReadableOptions) {
            final String[][] options = getOptions();
            mReadableOptions = new String[options.length];
            //iterate over the options and create readable pairs
            for (int i = 0; i < options.length; i++) {
                mReadableOptions[i] = createReadablePair(options[i]);
            }
        }
        return mReadableOptions;
    }

    private String createReadablePair(String[] option) {
        // results in "Part one <> Part two"
        return option[0] + " <> " + option[1];
    }

    @SuppressWarnings("RedundantIfStatement")
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ToggleTranslateQuizQuestion)) {
            return false;
        }

        ToggleTranslateQuizQuestion that = (ToggleTranslateQuizQuestion) o;

        if (!Arrays.equals(getAnswer(), that.getAnswer())) {
            return false;
        }

        if (!Arrays.deepEquals(getOptions(), that.getOptions())) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + Arrays.hashCode(getOptions());
        return result;
    }
}
