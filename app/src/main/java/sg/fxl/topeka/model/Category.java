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

package sg.fxl.topeka.model;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.Arrays;
import java.util.List;

import sg.fxl.topeka.model.quiz.QuizQuestion;

public class Category{

    public static final String TAG = "Category";
    private static final int SCORE = 8;
    private static final int NO_SCORE = 0;

    private String name;
    private String id;
    private Theme theme;
    private int[] scores;
    private List<QuizQuestion> quizzes;
    private boolean solved;

    public Category() {
    }

    public Category(String name, String id, Theme theme, List<QuizQuestion> quizzes, boolean solved) {
        this.name = name;
        this.id = id;
        this.theme = theme;
        this.quizzes = quizzes;
        scores = new int[quizzes.size()];
        this.solved = solved;
    }

    public Category(String name, String id, Theme theme, List<QuizQuestion> quizzes, int[] scores, boolean solved) {
        this.name = name;
        this.id = id;
        this.theme = theme;
        if (quizzes.size() == scores.length) {
            this.quizzes = quizzes;
            this.scores = scores;
        } else {
            throw new IllegalArgumentException("Quizzes and scores must have the same length");
        }
        this.solved = solved;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public Theme getTheme() {
        return theme;
    }

    @NonNull
    public List<QuizQuestion> getQuizzes() {
        return quizzes;
    }

    /**
     * Updates a score for a provided quiz within this category.
     *
     * @param which The quiz to rate.
     * @param correctlySolved <code>true</code> if the quiz was solved else <code>false</code>.
     */
    public void setScore(QuizQuestion which, boolean correctlySolved) {
        int index = quizzes.indexOf(which);
        Log.d(TAG, "Setting score for " + which + " with index " + index);
        if (-1 == index) {
            return;
        }
        scores[index] = correctlySolved ? SCORE : NO_SCORE;
    }

    public boolean isSolvedCorrectly(QuizQuestion quizQuestion) {
        return getScore(quizQuestion) == SCORE;
    }

    /**
     * Gets the score for a single quiz.
     *
     * @param which The quiz to look for
     * @return The score if found, else 0.
     */
    public int getScore(QuizQuestion which) {
        try {
            return scores[quizzes.indexOf(which)];
        } catch (IndexOutOfBoundsException ignore) {
            return 0;
        }
    }

    /**
     * @return The sum of all quiz scores within this category.
     */
    public int getScore() {
        int categoryScore = 0;
        for (int quizScore : scores) {
            categoryScore += quizScore;
        }
        return categoryScore;
    }

    public int[] getScores() {
        return scores;
    }

    public boolean isSolved() {
        return solved;
    }

    public void setSolved(boolean solved) {
        this.solved = solved;
    }

    /**
     * Checks which quiz is the first unsolved within this category.
     *
     * @return The position of the first unsolved quiz.
     */
    public int getFirstUnsolvedQuizPosition() {
        if (quizzes == null) {
            return -1;
        }
        for (int i = 0; i < quizzes.size(); i++) {
            if (!quizzes.get(i).isSolved()) {
                return i;
            }
        }
        return quizzes.size();
    }

    @Override
    public String toString() {
        return "Category{" +
                "name='" + name + '\'' +
                ", id='" + id + '\'' +
                ", theme=" + theme +
                ", quizzes=" + quizzes +
                ", scores=" + Arrays.toString(scores) +
                ", solved=" + solved +
                '}';
    }

    @SuppressWarnings("RedundantIfStatement")
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Category category = (Category) o;

        if (!id.equals(category.id)) {
            return false;
        }
        if (!name.equals(category.name)) {
            return false;
        }
        if (!quizzes.equals(category.quizzes)) {
            return false;
        }
        if (theme != category.theme) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + id.hashCode();
        result = 31 * result + theme.hashCode();
        result = 31 * result + quizzes.hashCode();
        return result;
    }

}
