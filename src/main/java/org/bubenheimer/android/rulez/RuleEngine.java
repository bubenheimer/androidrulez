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

import android.content.SharedPreferences;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;

import org.bubenheimer.android.log.Log;

import java.lang.ref.WeakReference;

/**
 * Abstract rule engine missing an evaluation strategy.
 */
@SuppressWarnings("WeakerAccess")
public abstract class RuleEngine {
    private static final String TAG = RuleEngine.class.getSimpleName();

    /**
     * The fact state (bit vector).
     */
    private final FactState factState = new FactState(this);

    /**
     * Listener to be invoked when rule evaluation ends.
     */
    @Nullable private EvalEndListener evalEndListener;

    /**
     * A weak reference to the rule base.
     */
    private WeakReference<RuleBase> ruleBaseRef;

    /**
     * @return the fact state (what's true and what's false)
     */
    @SuppressWarnings("WeakerAccess")
    public final FactState getFactState() {
        return factState;
    }

    /**
     * Clears the rule base and clears the rule engine state.
     */
    @SuppressWarnings("unused")
    @CallSuper
    public void clear() {
        if (ruleBaseRef != null) {
            ruleBaseRef.clear();
            ruleBaseRef = null;
        }
        clearState();
    }

    /**
     * Clears the rule engine state
     */
    @CallSuper
    public void clearState() {
        factState.clear();
    }

    /**
     * @return the listener to be invoked when rule evaluation ends. May be null.
     */
    @SuppressWarnings("unused")
    @Nullable
    public final EvalEndListener getEvalEndListener() {
        return evalEndListener;
    }

    /**
     * @param listener the listener to be invoked when rule evaluation ends. May be null.
     */
    @SuppressWarnings("WeakerAccess")
    public final void setEvalEndListener(@Nullable final EvalEndListener listener) {
        evalEndListener = listener;
    }

    /**
     * @return the rule base. May be null.
     */
    @SuppressWarnings("WeakerAccess")
    @Nullable
    public final RuleBase getRuleBase() {
        return ruleBaseRef.get();
    }

    /**
     * Sets the rule base. If the rule base is not {@code null},
     * it needs to be completely initialized.
     *
     * @param ruleBase the rule base. May be {@code null}.
     */
    public void setRuleBase(final RuleBase ruleBase) {
        ruleBaseRef = new WeakReference<>(ruleBase);
        if (ruleBase != null) {
            final SharedPreferences sharedPreferences = ruleBase.sharedPreferences;
            if (sharedPreferences != null) {
                final int factCount = ruleBase.getFactCount();
                int initState = factState.getState();
                for (int i = 0; i < factCount; ++i) {
                    final Fact fact = ruleBase.facts[i];
                    if (fact.persistence == Fact.PERSISTENCE_DISK) {
                        if (sharedPreferences.getBoolean(fact.name, false)) {
                            initState |= 1 << fact.id;
                        }
                    }
                }
                factState.setState(initState);
            }
        }
    }

    /**
     * Schedules a rule evaluation step.
     */
    protected abstract void scheduleEvaluation();

    /**
     * To be called by subclasses at the end of a rule evaluation step to notify the rule engine
     * when evaluation has concluded.
     */
    @SuppressWarnings("WeakerAccess")
    @CallSuper
    protected final void handleEvaluationEnd() {
        Log.v(TAG, "Evaluation ended: ", formatState(factState.getState()));

        if (evalEndListener != null) {
            evalEndListener.onEvalEnd(this);
        }
    }

    /**
     * Convenience method to format the fact state of the rule engine or the rule base evaluation
     * state as a string in a standard manner (as a bit vector).
     * @param value the fact state or rule base
     * @return the standardized string-formatted state
     */
    @SuppressWarnings("WeakerAccess")
    protected static String formatState(final int value) {
        return Integer.toBinaryString(value);
    }
}
