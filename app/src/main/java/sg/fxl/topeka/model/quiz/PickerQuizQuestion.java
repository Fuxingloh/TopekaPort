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

public final class PickerQuizQuestion extends QuizQuestion<Integer> {

    private final int mMin;
    private final int mMax;
    private final int mStep;

    public PickerQuizQuestion(String question, Integer answer, int min, int max, int step, boolean solved) {
        super(question, answer, solved);
        mMin = min;
        mMax = max;
        mStep = step;
    }

    public int getMin() {
        return mMin;
    }

    public int getMax() {
        return mMax;
    }

    public int getStep() {
        return mStep;
    }

    @Override
    public QuizType getType() {
        return QuizType.PICKER;
    }

    @Override
    public String getStringAnswer() {
        return getAnswer().toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PickerQuizQuestion)) {
            return false;
        }
        //noinspection EqualsBetweenInconvertibleTypes
        if (!super.equals(o)) {
            return false;
        }

        PickerQuizQuestion that = (PickerQuizQuestion) o;

        if (mMin != that.mMin) {
            return false;
        }
        //noinspection SimplifiableIfStatement
        if (mMax != that.mMax) {
            return false;
        }
        return mStep == that.mStep;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + mMin;
        result = 31 * result + mMax;
        result = 31 * result + mStep;
        return result;
    }
}
