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
import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;

import org.bubenheimer.android.log.Log;

import static org.bubenheimer.android.rulez.RuleEngine.formatState;

@SuppressWarnings("WeakerAccess")
public final class FactState implements ReadableState, WritableState {
    private static final String TAG = FactState.class.getSimpleName();

    /**
     * The state (bit vector).
     */
    private int state = 0;

    /**
     * The associated rule engine.
     */
    @NonNull
    private final RuleEngine ruleEngine;

    /**
     * @param ruleEngine the associated rule engine
     */
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    FactState(@NonNull final RuleEngine ruleEngine) {
        this.ruleEngine = ruleEngine;
    }

    @Override
    public boolean isValid(final Fact fact) {
        return (state & (1 << fact.id)) != 0;
    }

    /**
     * @return the raw state bit vector (indicating what's true and what's false)
     */
    public int getState() {
        return state;
    }

    /**
     * @param state the state
     */
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    void setState(final int state) {
        this.state = state;
    }

    /**
     * Resets the state, all facts turn false (no facts added).
     */
    @SuppressWarnings("WeakerAccess")
    public void clear() {
        state = 0;
    }

    @Override
    public void addFact(final Fact fact) {
        final int factMask = 1 << fact.id;
        checkFactChange(fact, factMask, true);
        addFactsInternal(factMask);
    }

    @Override
    public void addFacts(final Fact... facts) {
        int factVector = 0;
        for (final Fact fact : facts) {
            final int factMask = 1 << fact.id;
            checkFactChange(fact, factMask, true);
            factVector |= factMask;
        }
        addFactsInternal(factVector);
    }

    /**
     * Adds the facts from a fact bit vector.
     * @param factVector the fact bit vector
     */
    private void addFactsInternal(final int factVector) {
        final int oldState = state;
        state |= factVector;
        Log.v(TAG, "State change: ", formatState(oldState), " + ", formatState(factVector), " = ",
                formatState(state));
        stateChangeEval(oldState);
    }

    @Override
    public void removeFact(final Fact fact) {
        final int factMask = 1 << fact.id;
        checkFactChange(fact, factMask, false);
        removeFactsInternal(factMask);
    }

    @Override
    public void removeFacts(final Fact... facts) {
        int factVector = 0;
        for (final Fact fact : facts) {
            final int factMask = 1 << fact.id;
            checkFactChange(fact, factMask, false);
            factVector |= factMask;
        }
        removeFactsInternal(factVector);
    }

    /**
     * Removes the facts from a fact bit vector.
     * @param factVector the fact bit vector
     */
    private void removeFactsInternal(final int factVector) {
        final int oldState = state;
        state &= ~factVector;
        Log.v(TAG, "State change: ", formatState(oldState), " - ", formatState(factVector), " = ",
                formatState(state));
        stateChangeEval(oldState);
    }

    @Override
    public void addRemoveFacts(final Fact addFact, final Fact removeFact) {
        final int addFactMask = 1 << addFact.id;
        checkFactChange(addFact, addFactMask, true);
        final int removeFactMask = 1 << removeFact.id;
        checkFactChange(removeFact, removeFactMask, false);
        addRemoveFactsInternal(addFactMask, removeFactMask);
    }

    @Override
    public void addRemoveFacts(final Fact[] addFacts, final Fact[] removeFacts) {
        int addFactVector = 0;
        for (final Fact fact : addFacts) {
            final int factMask = 1 << fact.id;
            checkFactChange(fact, factMask, true);
            addFactVector |= factMask;
        }
        int removeFactVector = 0;
        for (final Fact fact : removeFacts) {
            final int factMask = 1 << fact.id;
            checkFactChange(fact, factMask, false);
            removeFactVector |= factMask;
        }
        addRemoveFactsInternal(addFactVector, removeFactVector);
    }

    /**
     * Adds facts to the state and removes facts from the state via two fact bit vectors.
     * @param addFactVector      the facts to add
     * @param removeFactVector   the facts to remove
     */
    private void addRemoveFactsInternal(final int addFactVector, final int removeFactVector) {
        final int oldState = state;
        state = (state | addFactVector) & ~removeFactVector;
        Log.v(TAG, "State change: ", formatState(oldState), " + ", formatState(addFactVector),
                " - ", formatState(removeFactVector), " = ", formatState(state));
        stateChangeEval(oldState);
    }

    /**
     * Schedules an evaluation if the current state is different from the passed previous one.
     * @param oldState the previous state
     */
    private void stateChangeEval(final int oldState) {
        if (oldState != state) {
            ruleEngine.scheduleEvaluation();
        }
    }

    private void checkFactChange(final Fact fact, final int factMask, final boolean isSet) {
        if (fact.persistence == Fact.PERSISTENCE_DISK) {
            final RuleBase ruleBase = ruleEngine.getRuleBase();
            assert ruleBase != null;
            final SharedPreferences sharedPreferences = ruleBase.sharedPreferences;
            if (sharedPreferences != null) {
                if (isSet && (state | factMask) != state ||
                        !isSet && (state & ~factMask) != state) {
                    sharedPreferences.edit().putBoolean(fact.name, isSet).apply();
                }
            }
        }
    }
}
