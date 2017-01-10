/*
 * Copyright (c) 2015-2017 Uli Bubenheimer. All rights reserved.
 */

package org.bubenheimer.android.rulez;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;

/**
 * <p>A variation of {@link BreadthFirstRuleEngine} where rule evaluations are scheduled for a
 * {@link Handler} to execute at the next opportunity.</p>
 *
 * <p>Also provides functionality to save and restore rule engine state.</p>
 *
 * <p>Not thread-safe. Typically used on the UI thread.</p>
 */
@SuppressWarnings("unused")
public class AndroidRuleEngine extends BreadthFirstRuleEngine {
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
     * Start rule base evaluation. Should be invoked from {@link Activity#onStart()} or equivalent
     * methods in {@code Activity} or {@code Fragment} classes.
     */
    @SuppressWarnings("unused")
    public void start() {
        scheduleEvaluation();
    }

    /**
     * Ends rule base evaluation. Should be invoked from {@link Activity#onStop()} or equivalent
     * methods in {@code Activity} or {@code Fragment} classes.
     */
    public void stop() {
        handler.removeCallbacks(evaluator);
        evaluationScheduled = false;
    }

    @Override
    protected void scheduleEvaluation() {
        if (evaluationScheduled) {
            return;
        }

        handler.post(evaluator);
        evaluationScheduled = true;
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
