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
 * This abstract class provides general structure for quizzes.
 *
 * @see sg.fxl.topeka.model.quiz.QuizType
 * @see sg.fxl.topeka.widget.quiz.AbsQuizView
 */
public abstract class QuizQuestion<A>{

    private static final String TAG = "QuizQuestion";

    private String question;
    private String quizType;
    private A answer;
    private A selectedAnswer;
    /**
     * Flag indicating whether this quiz has already been solved.
     * It does not give information whether the solution was correct or not.
     */
    private boolean solved;

    protected QuizQuestion(String question, A answer, boolean solved) {
        this.question = question;
        this.answer = answer;
        this.quizType = getType().getJsonName();
        this.solved = solved;
    }

    /**
     * @return The {@link QuizType} that represents this quiz.
     */
    public abstract QuizType getType();

    /**
     * Implementations need to return a human readable version of the given answer.
     */
    public abstract String getStringAnswer();

    public String getQuestion() {
        return question;
    }

    public A getAnswer() {
        return answer;
    }

    protected void setAnswer(A answer) {
        this.answer = answer;
    }

    public A getSelectedAnswer() {
        return selectedAnswer;
    }

    public void setSelectedAnswer(A selectedAnswer) {
        this.selectedAnswer = selectedAnswer;
    }

    public boolean isAnswerCorrect(A answer) {
        return this.answer.equals(answer);
    }

    public boolean isSolved() {
        return solved;
    }

    public void setSolved(boolean solved) {
        this.solved = solved;
    }

    /**
     * @return The id of this quiz.
     */
    public int getId() {
        return getQuestion().hashCode();
    }

    @SuppressWarnings("RedundantIfStatement")
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof QuizQuestion)) {
            return false;
        }

        QuizQuestion quizQuestion = (QuizQuestion) o;

        if (solved != quizQuestion.solved) {
            return false;
        }
        if (!answer.equals(quizQuestion.answer)) {
            return false;
        }
        if (!question.equals(quizQuestion.question)) {
            return false;
        }
        if (!quizType.equals(quizQuestion.quizType)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = question.hashCode();
        result = 31 * result + answer.hashCode();
        result = 31 * result + quizType.hashCode();
        result = 31 * result + (solved ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return getType() + ": \"" + getQuestion() + "\"";
    }
}
