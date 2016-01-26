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

    private final int min;
    private final int max;
    private final int step;

    public PickerQuizQuestion(String question, Integer answer, int min, int max, int step, boolean solved) {
        super(question, answer, solved);
        this.min = min;
        this.max = max;
        this.step = step;
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    public int getStep() {
        return step;
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

        if (min != that.min) {
            return false;
        }
        //noinspection SimplifiableIfStatement
        if (max != that.max) {
            return false;
        }
        return step == that.step;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + min;
        result = 31 * result + max;
        result = 31 * result + step;
        return result;
    }
}
