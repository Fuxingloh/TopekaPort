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

/**
 * Available types of quizzes.
 * Maps {@link Names} to subclasses of {@link QuizQuestion}.
 */
public enum QuizType {
    ALPHA_PICKER(Names.ALPHA_PICKER, AlphaPickerQuizQuestion.class),
    FILL_BLANK(Names.FILL_BLANK, FillBlankQuizQuestion.class),
    FILL_TWO_BLANKS(Names.FILL_TWO_BLANKS, FillTwoBlanksQuizQuestion.class),
    FOUR_QUARTER(Names.FOUR_QUARTER, FourQuarterQuizQuestion.class),
    MULTI_SELECT(Names.MULTI_SELECT, MultiSelectQuizQuestion.class),
    PICKER(Names.PICKER, PickerQuizQuestion.class),
    SINGLE_SELECT(Names.SINGLE_SELECT, SelectItemQuizQuestion.class),
    SINGLE_SELECT_ITEM(Names.SINGLE_SELECT_ITEM, SelectItemQuizQuestion.class),
    TOGGLE_TRANSLATE(Names.TOGGLE_TRANSLATE, ToggleTranslateQuizQuestion.class),
    IMAGE(Names.IMAGE, ImageQuizQuestion.class),
    TRUE_FALSE(Names.TRUE_FALSE, TrueFalseQuizQuestion.class);

    private final String jsonName;
    private final Class<? extends QuizQuestion> type;

    QuizType(final String jsonName, final Class<? extends QuizQuestion> type) {
        this.jsonName = jsonName;
        this.type = type;
    }

    public String getJsonName() {
        return jsonName;
    }

    public Class<? extends QuizQuestion> getType() {
        return type;
    }

    /**
     * Created by: Fuxing
     * Date: 26/1/2016
     * Time: 5:35 PM
     * Project: Topeka Port
     */
    public interface Names {
        String ALPHA_PICKER = "alpha-picker";
        String FILL_BLANK = "fill-blank";
        String FILL_TWO_BLANKS = "fill-two-blanks";
        String FOUR_QUARTER = "four-quarter";
        String MULTI_SELECT = "multi-select";
        String PICKER = "picker";
        String SINGLE_SELECT = "single-select";
        String SINGLE_SELECT_ITEM = "single-select-item";
        String TOGGLE_TRANSLATE = "toggle-translate";
        String TRUE_FALSE = "true-false";
        String IMAGE = "image";
    }
}
