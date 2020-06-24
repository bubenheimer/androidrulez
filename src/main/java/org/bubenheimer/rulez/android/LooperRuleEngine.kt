/*
 * Copyright (c) 2015-2019 Uli Bubenheimer
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
 *
 */
package org.bubenheimer.rulez.android

import android.os.Bundle
import android.os.Handler
import org.bubenheimer.android.threading.HandlerUtil.createAsync
import org.bubenheimer.rulez.BreadthFirstRuleEngine

/**
 *
 * A variation of [BreadthFirstRuleEngine] where rule evaluations are scheduled via the
 * current thread's [android.os.Looper]. This allows running the rule engine on the UI thread
 * without blocking, as long as rule actions are non-blocking.
 *
 *
 * Also provides functionality to save and restore rule engine state.
 *
 *
 * Not thread-safe. Typically used on the UI thread.
 */
open class LooperRuleEngine : BreadthFirstRuleEngine() {
    private companion object {
        /**
         * Instance state key for saving fact state.
         */
        private const val INSTANCE_STATE_RULE_ENGINE_FACTS = "rule_engine_fact_state"

        /**
         * Instance state key for saving rule execution state.
         */
        private const val INSTANCE_STATE_RULE_ENGINE_EVAL = "rule_engine_eval_state"
    }

    /**
     * Indicates whether an evaluation of the rule base has been scheduled due to changed state.
     */
    private var evaluationScheduled = false

    /**
     * Indicates whether evaluation of the rule base is paused. Evaluation is paused initially.
     */
    private var evaluationResumed = false

    /**
     * The [Runnable] to post for scheduling rule evaluation.
     */
    protected var evaluator: Runnable = Evaluator()

    /**
     * The [Handler] to post to.
     */
    private val handler = createAsync()

    /**
     * Restores rule engine state.
     * @param savedInstanceState the saved state
     */
    fun restoreInstanceState(savedInstanceState: Bundle) {
        val factState = savedInstanceState.getInt(INSTANCE_STATE_RULE_ENGINE_FACTS, 0)
        val evalState = savedInstanceState.getInt(INSTANCE_STATE_RULE_ENGINE_EVAL, 0)
        getFactState().state = factState
        ruleMatchState = evalState
    }

    /**
     * Saves rule engine state.
     * @param outState the saved state
     */
    fun saveInstanceState(outState: Bundle) {
        outState.putInt(INSTANCE_STATE_RULE_ENGINE_FACTS, factState.state)
        outState.putInt(INSTANCE_STATE_RULE_ENGINE_EVAL, ruleMatchState)
    }

    /**
     * Resumes rule base evaluation. Should be invoked from [Activity.onResume],
     * [Activity.onStart], or similar methods. Rule base evaluation is paused initially.
     */
    fun resumeEvaluation() {
        if (evaluationResumed) {
            return
        }
        evaluationResumed = true
        if (evaluationScheduled) {
            handler.post(evaluator)
        }
    }

    /**
     * Pauses rule base evaluation. Should be invoked from [Activity.onPause],
     * [Activity.onStop], or similar methods. Rule base evaluation is paused initially.
     */
    fun pauseEvaluation() {
        if (!evaluationResumed) {
            return
        }
        evaluationResumed = false
        if (evaluationScheduled) {
            handler.removeCallbacks(evaluator)
        }
    }

    /**
     * Schedules rule evaluation.
     */
    public override fun scheduleEvaluation() {
        if (evaluationScheduled) {
            return
        }
        evaluationScheduled = true
        if (evaluationResumed) {
            handler.post(evaluator)
        }
    }

    /**
     * Unschedules rule evaluation. It should not typically be necessary to call this method.
     */
    fun unscheduleEvaluation() {
        if (!evaluationScheduled) {
            return
        }
        evaluationScheduled = false
        if (evaluationResumed) {
            handler.removeCallbacks(evaluator)
        }
    }

    /**
     * The [Runnable] to execute for rule base evaluation.
     */
    protected open inner class Evaluator : Runnable {
        override fun run() {
            evaluationScheduled = false
            evaluate()
            if (!evaluationScheduled) {
                handleEvaluationEnd()
            }
        }
    }
}
