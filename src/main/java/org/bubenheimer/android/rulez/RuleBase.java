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

import org.bubenheimer.android.rulez.fluent.Proposition;

import java.util.ArrayList;

/**
 * The rule engine's collection of rules
 */
public final class RuleBase {
    private static final String TAG = RuleBase.class.getSimpleName();

    /** Maximum number of facts. Change this to {@code 64} if long is used instead of int to
     * represent the fact state. */
    public static final int MAX_FACTS = 32;

    /** Maximum number of rules. Change this to {@code 64} if long is used instead of int to
     * represent the rule base state. */
    public static final int MAX_RULES = 32;

    private int factIdCounter = 0;

    /**
     * The rules
     */
    final ArrayList<Rule> rules = new ArrayList<>(MAX_RULES);

    /**
     * Create a new fact
     * @param name fact name for debugging
     * @return the new fact
     */
    public Fact newFact(final String name) {
        if (factIdCounter >= MAX_FACTS) {
            throw new AssertionError("Too many facts");
        } else {
            Log.v(TAG, "ID " + String.format("%2d", factIdCounter) + " for new fact " + name);
            return new Fact(factIdCounter++);
        }
    }

    /**
     * Create a rule via a fluent builder pattern with a default match type of
     * {@link Rule#TYPE_MATCH_ONCE}.
     *
     * @param name             rule name
     * @return a builder instance
     */
    public Proposition rule(final String name) {
        return rule(name, Rule.TYPE_MATCH_ONCE);
    }

    /**
     * Create a rule via a fluent builder pattern.
     * @param name         rule name
     * @param matchType    rule match type. Specifies if a rule should run no more than once,
     *                     or under what conditions it becomes eligible to re-run.
     * @return a builder instance
     */
    public Proposition rule(final String name, @Rule.MatchType final int matchType) {
        if (rules.size() >= MAX_RULES) {
            throw new AssertionError("Too many rules");
        }
        final Rule rule = new Rule(name, matchType);
        rules.add(rule);
        return new Proposition(rule);
    }
}
