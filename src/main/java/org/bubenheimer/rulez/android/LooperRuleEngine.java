/*
 * Copyright (c) 2015-2017 Uli Bubenheimer. All rights reserved.
 */

package org.bubenheimer.rulez.android;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;

import org.bubenheimer.rulez.BreadthFirstRuleEngine;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;

/**
 * <p>A variation of {@link BreadthFirstRuleEngine} where rule evaluations are scheduled via the
 * current thread's {@link android.os.Looper}. This allows running the rule engine on the UI thread
 * without blocking, as long as rule actions are non-blocking.</p>
 *
 * <p>Also provides functionality to save and restore rule engine state.</p>
 *
 * <p>Not thread-safe. Typically used on the UI thread.</p>
 */
@SuppressWarnings("unused")
public class LooperRuleEngine extends BreadthFirstRuleEngine {
    /**
     * Instance state key for saving fact state.
     */
    private static final String INSTANCE_STATE_RULE_ENGINE_FACTS = "rule_engine_fact_state";
    /**
     * Instance state key for saving rule execution state.
     */
    private static final String INSTANCE_STATE_RULE_ENGINE_EVAL = "rule_engine_eval_state";

    /**
     * Indicates whether an evaluation of the rule base has been scheduled due to changed state.
     */
    private boolean evaluationScheduled = false;

    /**
     * Indicates whether evaluation of the rule base is paused. Evaluation is paused initially.
     */
    private boolean evaluationResumed = false;

    /**
     * The {@link Runnable} to post for scheduling rule evaluation.
     */
    private Runnable evaluator = new Evaluator();
    /**
     * The {@link Handler} to post to.
     */
    private final Handler handler = new Handler();

    @SuppressWarnings("unused")
    @RestrictTo(RestrictTo.Scope.SUBCLASSES)
    protected final void setEvaluator(final Runnable evaluator) {
        this.evaluator = evaluator;
    }

    /**
     * Restores rule engine state.
     * @param savedInstanceState the saved state
     */
    @SuppressWarnings("unused")
    public void restoreInstanceState(@NonNull final Bundle savedInstanceState) {
        final int factState = savedInstanceState.getInt(INSTANCE_STATE_RULE_ENGINE_FACTS, 0);
        final int evalState = savedInstanceState.getInt(INSTANCE_STATE_RULE_ENGINE_EVAL, 0);
        getFactState().setState(factState);
        setRuleMatchState(evalState);
    }

    /**
     * Saves rule engine state.
     * @param outState the saved state
     */
    @SuppressWarnings("unused")
    public void saveInstanceState(@NonNull final Bundle outState) {
        outState.putInt(INSTANCE_STATE_RULE_ENGINE_FACTS, getFactState().getState());
        outState.putInt(INSTANCE_STATE_RULE_ENGINE_EVAL, getRuleMatchState());
    }

    /**
     * Resumes rule base evaluation. Should be invoked from {@link Activity#onResume()},
     * {@link Activity#onStart()}, or similar methods. Rule base evaluation is paused initially.
     */
    public void resumeEvaluation() {
        if (evaluationResumed) {
            return;
        }

        evaluationResumed = true;
        if (evaluationScheduled) {
            handler.post(evaluator);
        }
    }

    /**
     * Pauses rule base evaluation. Should be invoked from {@link Activity#onPause()},
     * {@link Activity#onStop()}, or similar methods. Rule base evaluation is paused initially.
     */
    public void pauseEvaluation() {
        if (!evaluationResumed) {
            return;
        }

        evaluationResumed = false;
        if (evaluationScheduled) {
            handler.removeCallbacks(evaluator);
        }
    }

    /**
     * Schedules rule evaluation.
     */
    @Override
    public void scheduleEvaluation() {
        if (evaluationScheduled) {
            return;
        }

        evaluationScheduled = true;
        if (evaluationResumed) {
            handler.post(evaluator);
        }
    }

    /**
     * Unschedules rule evaluation. It should not typically be necessary to call this method.
     */
    public void unscheduleEvaluation() {
        if (!evaluationScheduled) {
            return;
        }

        evaluationScheduled = false;
        if (evaluationResumed) {
            handler.removeCallbacks(evaluator);
        }
    }

    /**
     * The {@link Runnable} to execute for rule base evaluation.
     */
    @SuppressWarnings("WeakerAccess")
    @RestrictTo(RestrictTo.Scope.SUBCLASSES)
    protected class Evaluator implements Runnable {
        @Override
        public void run() {
            evaluationScheduled = false;

            evaluate();

            if (!evaluationScheduled) {
                handleEvaluationEnd();
            }
        }
    }
}
