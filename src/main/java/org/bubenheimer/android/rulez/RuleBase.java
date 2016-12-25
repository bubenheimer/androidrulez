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
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;

import org.bubenheimer.android.log.Log;
import org.bubenheimer.android.rulez.fluent.Proposition;

import java.util.ArrayList;
import java.util.Locale;

/**
 * The rule engine's collection of rules
 */
@SuppressWarnings("WeakerAccess")
public class RuleBase {
    private static final String TAG = RuleBase.class.getSimpleName();

    /** Maximum number of facts. Change this to {@code 64} if long is used instead of int to
     * represent the fact state. */
    public static final int MAX_FACTS = 32;

    /** Maximum number of rules. Change this to {@code 64} if long is used instead of int to
     * represent the rule base state. */
    public static final int MAX_RULES = 32;

    final Fact[] facts = new Fact[MAX_FACTS];

    private int factIdCounter = 0;

    /**
     * The rules
     */
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    final ArrayList<Rule> rules = new ArrayList<>(MAX_RULES);

    //TODO provide a slightly more elaborate persistence API to eliminate tight coupling
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    @Nullable
    SharedPreferences sharedPreferences;

    /**
     * @param sharedPreferences    a dedicated sharedPreferences object for saving and restoring
     *                             persistent fact state. May be null to not save persistent state.
     */
    @SuppressWarnings("unused")
    public void setSharedPreferences(@Nullable final SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    /**
     * Create a new fact with no fact state persistence.
     * @param name fact name for debugging
     * @return the new fact
     */
    @SuppressWarnings("unused")
    public Fact newFact(final String name) {
        return newFact(name, Fact.PERSISTENCE_NONE);
    }

    /**
     * Create a new fact
     * @param name          fact name for debugging
     * @param persistence   fact state persistence
     * @return the new fact
     */
    public Fact newFact(
            final String name,
            @SuppressWarnings("SameParameterValue") @Fact.Persistence final int persistence) {
        if (factIdCounter >= MAX_FACTS) {
            throw new AssertionError("Too many facts");
        } else {
            Log.v(TAG, "ID ", String.format(Locale.getDefault(), "%2d", factIdCounter),
                    " for new fact ", name);
            final Fact fact = new Fact(factIdCounter, name, persistence);
            facts[factIdCounter++] = fact;
            return fact;
        }
    }

    public int getFactCount() {
        return factIdCounter;
    }

    /**
     * Create a rule via a fluent builder pattern with a default match type of
     * {@link Rule#MATCH_ALWAYS}.
     *
     * @param name rule name
     * @return a builder instance
     */
    public Proposition rule(final String name) {
        return rule(name, Rule.MATCH_ALWAYS);
    }

    /**
     * Create a rule via a fluent builder pattern.
     * @param name         rule name
     * @param matchType    rule match type. Specifies if a rule should run no more than once,
     *                     or under what conditions it becomes eligible to re-run.
     * @return a builder instance
     */
    public Proposition rule(
            final String name,
            @SuppressWarnings("SameParameterValue") @Rule.MatchType final int matchType) {
        if (rules.size() >= MAX_RULES) {
            throw new AssertionError("Too many rules");
        }
        final Rule rule = new Rule(name, matchType);
        rules.add(rule);
        return new Proposition(rule);
    }
}
