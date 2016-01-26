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

import sg.fxl.topeka.model.JsonAttributes;

/**
 * Available types of quizzes.
 * Maps {@link JsonAttributes.QuizType} to subclasses of {@link QuizQuestion}.
 */
public enum QuizType {
    ALPHA_PICKER(JsonAttributes.QuizType.ALPHA_PICKER, AlphaPickerQuizQuestion.class),
    FILL_BLANK(JsonAttributes.QuizType.FILL_BLANK, FillBlankQuizQuestion.class),
    FILL_TWO_BLANKS(JsonAttributes.QuizType.FILL_TWO_BLANKS, FillTwoBlanksQuizQuestion.class),
    FOUR_QUARTER(JsonAttributes.QuizType.FOUR_QUARTER, FourQuarterQuizQuestion.class),
    MULTI_SELECT(JsonAttributes.QuizType.MULTI_SELECT, MultiSelectQuizQuestion.class),
    PICKER(JsonAttributes.QuizType.PICKER, PickerQuizQuestion.class),
    SINGLE_SELECT(JsonAttributes.QuizType.SINGLE_SELECT, SelectItemQuizQuestion.class),
    SINGLE_SELECT_ITEM(JsonAttributes.QuizType.SINGLE_SELECT_ITEM, SelectItemQuizQuestion.class),
    TOGGLE_TRANSLATE(JsonAttributes.QuizType.TOGGLE_TRANSLATE, ToggleTranslateQuizQuestion.class),
    TRUE_FALSE(JsonAttributes.QuizType.TRUE_FALSE, TrueFalseQuizQuestion.class);

    private final String mJsonName;
    private final Class<? extends QuizQuestion> mType;

    QuizType(final String jsonName, final Class<? extends QuizQuestion> type) {
        mJsonName = jsonName;
        mType = type;
    }

    public String getJsonName() {
        return mJsonName;
    }

    public Class<? extends QuizQuestion> getType() {
        return mType;
    }
}
