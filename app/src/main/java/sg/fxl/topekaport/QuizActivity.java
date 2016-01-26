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

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListenerAdapter;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.Window;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import sg.fxl.topeka.helper.ApiLevelHelper;
import sg.fxl.topeka.helper.ViewUtils;
import sg.fxl.topeka.model.CategoryJson;
import sg.fxl.topeka.model.Quiz;
import sg.fxl.topeka.widget.TextSharedElementCallback;

public class QuizActivity extends AppCompatActivity {

    private static final String TAG = "QuizActivity";
    private static final String STATE_IS_PLAYING = "isPlaying";
    private static final String FRAGMENT_TAG = "QuizQuestion";

    private Interpolator interpolator;
    private Quiz quiz;
    private QuizFragment quizFragment;
    private FloatingActionButton quizFab;
    private boolean savedStateIsPlaying;
    private ImageView icon;
    private Animator circularReveal;
    private ObjectAnimator colorChange;
    private View toolbarBack;

    final View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            switch (v.getId()) {
                case R.id.fab_quiz:
                    startQuizFromClickOn(v);
                    break;
                case R.id.submitAnswer:
                    submitAnswer();
                    break;
                case R.id.quiz_done:
                    ActivityCompat.finishAfterTransition(QuizActivity.this);
                    break;
                case R.id.back:
                    onBackPressed();
                    break;
                default:
                    throw new UnsupportedOperationException("OnClick has not been implemented for " + getResources().getResourceName(v.getId()));
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        interpolator = new FastOutSlowInInterpolator();
        if (null != savedInstanceState) {
            savedStateIsPlaying = savedInstanceState.getBoolean(STATE_IS_PLAYING);
        }
        super.onCreate(savedInstanceState);
        populate(CategoryJson.from(getIntent()));
        int categoryNameTextSize = getResources()
                .getDimensionPixelSize(R.dimen.category_item_text_size);
        int paddingStart = getResources().getDimensionPixelSize(R.dimen.spacing_double);
        final int startDelay = getResources().getInteger(R.integer.toolbar_transition_duration);
        ActivityCompat.setEnterSharedElementCallback(this,
                new TextSharedElementCallback(categoryNameTextSize, paddingStart) {
                    @Override
                    public void onSharedElementStart(List<String> sharedElementNames,
                                                     List<View> sharedElements,
                                                     List<View> sharedElementSnapshots) {
                        super.onSharedElementStart(sharedElementNames,
                                sharedElements,
                                sharedElementSnapshots);
                        toolbarBack.setScaleX(0f);
                        toolbarBack.setScaleY(0f);
                    }

                    @Override
                    public void onSharedElementEnd(List<String> sharedElementNames,
                                                   List<View> sharedElements,
                                                   List<View> sharedElementSnapshots) {
                        super.onSharedElementEnd(sharedElementNames,
                                sharedElements,
                                sharedElementSnapshots);
                        // Make sure to perform this animation after the transition has ended.
                        ViewCompat.animate(toolbarBack)
                                .setStartDelay(startDelay)
                                .scaleX(1f)
                                .scaleY(1f)
                                .alpha(1f);
                    }
                });
    }

    @Override
    protected void onResume() {
        if (savedStateIsPlaying) {
            quizFragment = (QuizFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG);
            if (!quizFragment.hasSolvedStateListener()) {
                quizFragment.setSolvedStateListener(getSolvedStateListener());
            }
            findViewById(R.id.quiz_fragment_container).setVisibility(View.VISIBLE);
            quizFab.hide();
        } else {
            initQuizFragment();
        }
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putBoolean(STATE_IS_PLAYING, quizFab.getVisibility() == View.GONE);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        if (icon == null || quizFab == null) {
            // Skip the animation if icon or fab are not initialized.
            super.onBackPressed();
            return;
        }

        ViewCompat.animate(toolbarBack)
                .scaleX(0f)
                .scaleY(0f)
                .alpha(0f)
                .setDuration(100)
                .start();

        // Scale the icon and fab to 0 size before calling onBackPressed if it exists.
        ViewCompat.animate(icon)
                .scaleX(.7f)
                .scaleY(.7f)
                .alpha(0f)
                .setInterpolator(interpolator)
                .start();

        ViewCompat.animate(quizFab)
                .scaleX(0f)
                .scaleY(0f)
                .setInterpolator(interpolator)
                .setStartDelay(100)
                .setListener(new ViewPropertyAnimatorListenerAdapter() {
                    @SuppressLint("NewApi")
                    @Override
                    public void onAnimationEnd(View view) {
                        if (isFinishing() ||
                                (ApiLevelHelper.isAtLeast(Build.VERSION_CODES.JELLY_BEAN_MR1)
                                        && isDestroyed())) {
                            return;
                        }
                        QuizActivity.super.onBackPressed();
                    }
                })
                .start();
    }

    private void startQuizFromClickOn(final View clickedView) {
        initQuizFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.quiz_fragment_container, quizFragment, FRAGMENT_TAG)
                .commit();
        final FrameLayout container = (FrameLayout) findViewById(R.id.quiz_fragment_container);
        container.setBackgroundColor(ContextCompat.
                getColor(this, quiz.getTheme().getWindowBackgroundColor()));
        revealFragmentContainer(clickedView, container);
        // the toolbar should not have more elevation than the content while playing
        setToolbarElevation(false);
    }

    private void revealFragmentContainer(final View clickedView, final FrameLayout fragmentContainer) {
        if (ApiLevelHelper.isAtLeast(Build.VERSION_CODES.LOLLIPOP)) {
            revealFragmentContainerLollipop(clickedView, fragmentContainer);
        } else {
            fragmentContainer.setVisibility(View.VISIBLE);
            clickedView.setVisibility(View.GONE);
            icon.setVisibility(View.GONE);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void revealFragmentContainerLollipop(final View clickedView, final FrameLayout fragmentContainer) {
        prepareCircularReveal(clickedView, fragmentContainer);

        ViewCompat.animate(clickedView)
                .scaleX(0)
                .scaleY(0)
                .alpha(0)
                .setInterpolator(interpolator)
                .setListener(new ViewPropertyAnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(View view) {
                        fragmentContainer.setVisibility(View.VISIBLE);
                        clickedView.setVisibility(View.GONE);
                    }
                })
                .start();

        fragmentContainer.setVisibility(View.VISIBLE);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(circularReveal).with(colorChange);
        animatorSet.start();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void prepareCircularReveal(View startView, FrameLayout targetView) {
        int centerX = (startView.getLeft() + startView.getRight()) / 2;
        // Subtract the start view's height to adjust for relative coordinates on screen.
        int centerY = (startView.getTop() + startView.getBottom()) / 2 - startView.getHeight();
        float endRadius = (float) Math.hypot((double) centerX, (double) centerY);
        circularReveal = ViewAnimationUtils.createCircularReveal(
                targetView, centerX, centerY, startView.getWidth(), endRadius);
        circularReveal.setInterpolator(new FastOutLinearInInterpolator());

        circularReveal.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                icon.setVisibility(View.GONE);
                circularReveal.removeListener(this);
            }
        });
        // Adding a color animation from the FAB's color to transparent creates a dissolve like
        // effect to the circular reveal.
        int accentColor = ContextCompat.getColor(this, quiz.getTheme().getAccentColor());
        colorChange = ObjectAnimator.ofInt(targetView,
                ViewUtils.FOREGROUND_COLOR, accentColor, Color.TRANSPARENT);
        colorChange.setEvaluator(new ArgbEvaluator());
        colorChange.setInterpolator(interpolator);
    }

    @SuppressLint("NewApi")
    public void setToolbarElevation(boolean shouldElevate) {
        if (ApiLevelHelper.isAtLeast(Build.VERSION_CODES.LOLLIPOP)) {
            toolbarBack.setElevation(shouldElevate ?
                    getResources().getDimension(R.dimen.elevation_header) : 0);
        }
    }

    private void initQuizFragment() {
        if (quizFragment != null) {
            return;
        }
        quizFragment = QuizFragment.newInstance(quiz, getSolvedStateListener());
        setToolbarElevation(false);
    }

    @NonNull
    private QuizFragment.SolvedStateListener getSolvedStateListener() {
        return new QuizFragment.SolvedStateListener() {
            @Override
            public void onCategorySolved() {
                setResultSolved();
                setToolbarElevation(true);
                displayDoneFab();
            }

            private void displayDoneFab() {
                /* We're re-using the already existing fab and give it some
                 * new values. This has to run delayed due to the queued animation
                 * to hide the fab initially.
                 */
                if (null != circularReveal && circularReveal.isRunning()) {
                    circularReveal.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            showQuizFabWithDoneIcon();
                            circularReveal.removeListener(this);
                        }
                    });
                } else {
                    showQuizFabWithDoneIcon();
                }
            }

            private void showQuizFabWithDoneIcon() {
                quizFab.setImageResource(R.drawable.ic_tick);
                quizFab.setId(R.id.quiz_done);
                quizFab.setVisibility(View.VISIBLE);
                quizFab.setScaleX(0f);
                quizFab.setScaleY(0f);
                ViewCompat.animate(quizFab)
                        .scaleX(1)
                        .scaleY(1)
                        .setInterpolator(interpolator)
                        .setListener(null)
                        .start();
            }
        };
    }

    private void setResultSolved() {
        // Send it through json with intent
        Intent intent = new Intent();
        CategoryJson.to(intent, quiz);
        setResult(R.id.solved, intent);
    }

    /**
     * Proceeds the quiz to it's next state.
     */
    public void proceed() {
        submitAnswer();
    }

    private void submitAnswer() {
        if (!quizFragment.showNextPage()) {
            quizFragment.showSummary();
            setResultSolved();
            return;
        }
        setToolbarElevation(false);
    }

    @SuppressLint("NewApi")
    private void populate(Quiz quiz) {
        this.quiz = quiz;
        setTheme(this.quiz.getTheme().getStyleId());
        if (ApiLevelHelper.isAtLeast(Build.VERSION_CODES.LOLLIPOP)) {
            Window window = getWindow();
            window.setStatusBarColor(ContextCompat.getColor(this,
                    this.quiz.getTheme().getPrimaryDarkColor()));
        }
        initLayout();
        initToolbar(this.quiz);
    }

    private void initLayout() {
        setContentView(R.layout.activity_quiz);
        //noinspection PrivateResource
        icon = (ImageView) findViewById(R.id.icon);
        ViewCompat.animate(icon)
                .scaleX(1)
                .scaleY(1)
                .alpha(1)
                .setInterpolator(interpolator)
                .setStartDelay(300)
                .start();
        quizFab = (FloatingActionButton) findViewById(R.id.fab_quiz);
        quizFab.setImageResource(R.drawable.ic_play);
        if (savedStateIsPlaying) {
            quizFab.hide();
        } else {
            quizFab.show();
        }
        quizFab.setOnClickListener(onClickListener);
    }

    private void initToolbar(Quiz quiz) {
        toolbarBack = findViewById(R.id.back);
        toolbarBack.setOnClickListener(onClickListener);
        TextView titleView = (TextView) findViewById(R.id.category_title);
        titleView.setText(quiz.getName());
        titleView.setTextColor(ContextCompat.getColor(this,
                quiz.getTheme().getTextPrimaryColor()));
        if (savedStateIsPlaying) {
            // the toolbar should not have more elevation than the content while playing
            setToolbarElevation(false);
        }
    }
}
