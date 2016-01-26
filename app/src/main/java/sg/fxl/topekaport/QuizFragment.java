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

package sg.fxl.topekaport;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterViewAnimator;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import sg.fxl.topeka.adapter.QuizAdapter;
import sg.fxl.topeka.adapter.ScoreAdapter;
import sg.fxl.topeka.helper.ApiLevelHelper;
import sg.fxl.topeka.model.Quiz;
import sg.fxl.topeka.model.Theme;
import sg.fxl.topeka.model.quiz.QuizQuestion;
import sg.fxl.topeka.widget.AvatarView;
import sg.fxl.topeka.widget.quiz.AbsQuizView;

/**
 * Encapsulates QuizQuestion solving and displays it to the user.
 */
public class QuizFragment extends android.support.v4.app.Fragment {

    private static final String KEY_USER_INPUT = "USER_INPUT";
    private TextView progressText;
    private int quizSize;
    private ProgressBar progressBar;
    private Quiz quiz;
    private AdapterViewAnimator quizView;
    private ScoreAdapter scoreAdapter;
    private QuizAdapter quizAdapter;
    private SolvedStateListener solvedStateListener;

    public static QuizFragment newInstance(Quiz quiz, SolvedStateListener solvedStateListener) {
        if (quiz == null) {
            throw new IllegalArgumentException("The quiz can not be null");
        }
        QuizFragment fragment = new QuizFragment();
        if (solvedStateListener != null) {
            fragment.solvedStateListener = solvedStateListener;
        }
        fragment.quiz = quiz;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Create a themed Context and custom LayoutInflater
        // to get nicely themed views in this Fragment.
        final Theme theme = quiz.getTheme();
        final ContextThemeWrapper context = new ContextThemeWrapper(getActivity(),
                theme.getStyleId());
        final LayoutInflater themedInflater = LayoutInflater.from(context);
        return themedInflater.inflate(R.layout.fragment_quiz, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        quizView = (AdapterViewAnimator) view.findViewById(R.id.quiz_view);
        decideOnViewToDisplay();
        setQuizViewAnimations();
        final AvatarView avatar = (AvatarView) view.findViewById(R.id.avatar);
        setAvatarDrawable(avatar);
        initProgressToolbar(view);
        super.onViewCreated(view, savedInstanceState);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setQuizViewAnimations() {
        if (ApiLevelHelper.isLowerThan(Build.VERSION_CODES.LOLLIPOP)) {
            return;
        }
        quizView.setInAnimation(getActivity(), R.animator.slide_in_bottom);
        quizView.setOutAnimation(getActivity(), R.animator.slide_out_top);
    }

    private void initProgressToolbar(View view) {
        final int firstUnsolvedQuizPosition = quiz.getFirstUnsolvedQuizPosition();
        final List<QuizQuestion> quizzes = quiz.getQuizzes();
        quizSize = quizzes.size();
        progressText = (TextView) view.findViewById(R.id.progress_text);
        progressBar = ((ProgressBar) view.findViewById(R.id.progress));
        progressBar.setMax(quizSize);

        setProgress(firstUnsolvedQuizPosition);
    }

    private void setProgress(int currentQuizPosition) {
        if (!isAdded()) {
            return;
        }
        progressText
                .setText(getString(R.string.quiz_of_quizzes, currentQuizPosition, quizSize));
        progressBar.setProgress(currentQuizPosition);
    }

    @SuppressWarnings("ConstantConditions")
    private void setAvatarDrawable(AvatarView avatarView) {
        avatarView.setAvatar(R.drawable.avatar_1);
        ViewCompat.animate(avatarView)
                .setInterpolator(new FastOutLinearInInterpolator())
                .setStartDelay(500)
                .scaleX(1)
                .scaleY(1)
                .start();
    }

    private void decideOnViewToDisplay() {
        final boolean isSolved = quiz.isSolved();
        if (isSolved) {
            showSummary();
            if (null != solvedStateListener) {
                solvedStateListener.onCategorySolved();
            }
        } else {
            quizView.setAdapter(getQuizAdapter());
            quizView.setSelection(quiz.getFirstUnsolvedQuizPosition());
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        View focusedChild = quizView.getFocusedChild();
        if (focusedChild instanceof ViewGroup) {
            View currentView = ((ViewGroup) focusedChild).getChildAt(0);
            if (currentView instanceof AbsQuizView) {
                outState.putBundle(KEY_USER_INPUT, ((AbsQuizView) currentView).getUserInput());
            }
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        restoreQuizState(savedInstanceState);
        super.onViewStateRestored(savedInstanceState);
    }

    private void restoreQuizState(final Bundle savedInstanceState) {
        if (null == savedInstanceState) {
            return;
        }
        quizView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom,
                                       int oldLeft,
                                       int oldTop, int oldRight, int oldBottom) {
                quizView.removeOnLayoutChangeListener(this);
                View currentChild = quizView.getChildAt(0);
                if (currentChild instanceof ViewGroup) {
                    final View potentialQuizView = ((ViewGroup) currentChild).getChildAt(0);
                    if (potentialQuizView instanceof AbsQuizView) {
                        ((AbsQuizView) potentialQuizView).setUserInput(savedInstanceState.
                                getBundle(KEY_USER_INPUT));
                    }
                }
            }
        });

    }

    private QuizAdapter getQuizAdapter() {
        if (null == quizAdapter) {
            quizAdapter = new QuizAdapter(getActivity(), quiz);
        }
        return quizAdapter;
    }

    /**
     * Displays the next page.
     *
     * @return <code>true</code> if there's another quiz to solve, else <code>false</code>.
     */
    public boolean showNextPage() {
        if (null == quizView) {
            return false;
        }
        int nextItem = quizView.getDisplayedChild() + 1;
        setProgress(nextItem);
        final int count = quizView.getAdapter().getCount();
        if (nextItem < count) {
            quizView.showNext();
            return true;
        }
        //Mark category as solved
        quiz.setSolved(true);
        return false;
    }

    public void showSummary() {
        @SuppressWarnings("ConstantConditions")
        final ListView scorecardView = (ListView) getView().findViewById(R.id.scorecard);
        scoreAdapter = getScoreAdapter();
        scorecardView.setAdapter(scoreAdapter);
        scorecardView.setVisibility(View.VISIBLE);
        quizView.setVisibility(View.GONE);
    }

    public boolean hasSolvedStateListener() {
        return solvedStateListener != null;
    }

    public void setSolvedStateListener(SolvedStateListener solvedStateListener) {
        this.solvedStateListener = solvedStateListener;
        if (quiz.isSolved() && null != this.solvedStateListener) {
                this.solvedStateListener.onCategorySolved();
            }
    }

    private ScoreAdapter getScoreAdapter() {
        if (null == scoreAdapter) {
            scoreAdapter = new ScoreAdapter(quiz);
        }
        return scoreAdapter;
    }

    /**
     * Interface definition for a callback to be invoked when the quiz is started.
     */
    public interface SolvedStateListener {

        /**
         * This method will be invoked when the category has been solved.
         */
        void onCategorySolved();
    }
}