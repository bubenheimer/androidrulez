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

import android.util.Log;

/**
 * <p>A rule engine where conceptually the evaluation strategy checks for each rule whether its
 * left-hand side matches the current state, and saves the rule away for later execution.
 * Once all rules have been evaluated, the actions of all matched rules are executed sequentially.
 * Then the engine checks if the state has changed and starts over.
 * This results in forward-chaining breadth-first rule evaluation.</p>
 *
 * <p>The actual implementation may use optimizations such that the conceptual strategy is
 * implemented in a non-literal, yet equivalent manner.</p>
 *
 * <p>Not thread-safe.</p>
 *
 */
public class BreadthFirstRuleEngine extends RuleEngine {
    private static final String TAG = BreadthFirstRuleEngine.class.getSimpleName();

    /**
     * A bit mask for the match state of all rules. Indicates whether a rule has already fired.
     */
    private int ruleMatchState = 0;

    /**
     * Indicates whether an evaluation of the rule base has been scheduled due to changed state.
     */
    @SuppressWarnings("FieldCanBeLocal")
    private boolean evaluationScheduled = false;

    /**
     * Indicates whether we are currently evaluating the rule base.
     */
    private boolean isEvaluating = false;

    /**
     * A {@link ReadableState} representing the current rule base state to pass to rule bodies.
     * There is just a single one to avoid garbage collection issues.
     */
    private final BaseState baseState = new BaseState();

    /**
     * Represents the current rule base state to pass to rule bodies.
     */
    private static final class BaseState implements ReadableState {
        /**
         * the current state
         */
        int state;

        @Override
        public boolean isValid(final Fact fact) {
            return false;
        }
    }

    /**
     * @return a bit mask for the match state of all rules. Indicates whether a rule has
     * already fired.
     */
    protected final int getRuleMatchState() {
        return ruleMatchState;
    }

    /**
     *
     * @param state A bit mask for the match state of all rules.
     */
    protected final void setRuleMatchState(final int state) {
        ruleMatchState = state;
    }

    @Override
    public void clearState() {
        super.clearState();
        ruleMatchState = 0;
    }

    @Override
    public void setRuleBase(final RuleBase ruleBase) {
        super.setRuleBase(ruleBase);
        ruleMatchState = 0;
    }

    @Override
    protected void scheduleEvaluation() {
        if (isEvaluating) {
            evaluationScheduled = true;
            return;
        }

        isEvaluating = true;
        do {
            evaluationScheduled = false;
            evaluate();
        } while (evaluationScheduled);

        isEvaluating = false;

        handleEvaluationEnd();
    }

    /**
     * Evaluates the rule base.
     */
    protected final void evaluate() {
        final RuleBase ruleBase = getRuleBase();
        if (ruleBase == null) {
            return;
        }
        baseState.state = getFactState().getState();
        Log.v(TAG, "Evaluating: " + formatState(baseState.state));
        int evaluatedMask = 1;
        final int ruleCount = ruleBase.rules.size();
        for (int i = 0; i < ruleCount; ++i) {
            final Rule rule = ruleBase.rules.get(i);
            if (rule.matchType != Rule.MATCH_ONCE
                    || (ruleMatchState & evaluatedMask) == 0) {
                if (rule.eval(baseState.state)) {
                    if (rule.matchType == Rule.MATCH_ALWAYS
                            || (ruleMatchState & evaluatedMask) == 0) {
                        Log.v(TAG, "Rule fired: " + rule);
                        ruleMatchState |= evaluatedMask;
                        rule.ruleAction.fire(baseState, getFactState());
                    }
                } else if (rule.matchType == Rule.MATCH_RESET
                        && (ruleMatchState & evaluatedMask) != 0) {
                    Log.v(TAG, "Rule reset: " + rule);
                    ruleMatchState ^= evaluatedMask;
                }
            }
            evaluatedMask <<= 1;
        }
    }
}
