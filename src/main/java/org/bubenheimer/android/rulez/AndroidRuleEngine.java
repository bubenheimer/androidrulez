/*
 * Copyright (c) 2016 Uli Bubenheimer
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

package org.bubenheimer.android.rulez;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;

/**
 * <p>A variation of {@link BreadthFirstRuleEngine} where rule evaluations are scheduled for a
 * {@link Handler} to execute at the next opportunity.</p>
 *
 * <p>Also provides functionality to save and restore rule engine state.</p>
 *
 * <p>Not thread-safe. Typically used on the UI thread.</p>
 */
public final class AndroidRuleEngine extends BreadthFirstRuleEngine {
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
    private final Runnable evaluator = new Evaluator();
    /**
     * The {@link Handler} to post to.
     */
    private final Handler handler = new Handler();

    /**
     * Restores rule engine state.
     * @param savedInstanceState the saved state
     */
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
    public void saveInstanceState(@NonNull final Bundle outState) {
        outState.putInt(INSTANCE_STATE_RULE_ENGINE_FACTS, getFactState().getState());
        outState.putInt(INSTANCE_STATE_RULE_ENGINE_EVAL, getRuleMatchState());
    }

    /**
     * Start rule base evaluation. Should be invoked from {@link Activity#onStart()} or equivalent
     * methods in {@code Activity} or {@code Fragment} classes.
     */
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
    private final class Evaluator implements Runnable {
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
