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

import android.support.annotation.IntDef;
import android.support.annotation.RestrictTo;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * A rule
 */
public final class Rule {

    /**
     * Match eligibility type. Specifies whether a rule may match repeatedly or only once.
     */
    @SuppressWarnings("WeakerAccess")
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({MATCH_ONCE, MATCH_RESET, MATCH_ALWAYS})
    public @interface MatchType {}

    /**
     * Specifies to match and fire a rule no more than once
     */
    @SuppressWarnings("WeakerAccess")
    public static final int MATCH_ONCE = 0;

    /**
     * Specifies to re-match and re-fire a rule after its left-hand side no longer matches
     */
    @SuppressWarnings("WeakerAccess")
    public static final int MATCH_RESET = 1;

    /**
     * Specifies to always re-match and re-fire a rule
     */
    @SuppressWarnings("WeakerAccess")
    public static final int MATCH_ALWAYS = 2;

    /**
     * Rule name
     */
    private final String name;

    /**
     * Rule match type. Specifies if a rule should match no more than once, or under what
     * conditions it becomes eligible to re-match.
     */
    @MatchType
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    final int matchType;

    /**
     * The positive facts of the rule's left-hand side.
     */
    private final ArrayList<Integer> conditions = new ArrayList<>();

    /**
     * The negated facts of the rule's left-hand side.
     */
    private final ArrayList<Integer> negConditions = new ArrayList<>();

    /**
     * The rule action to execute when the rule fires.
     */
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    RuleAction ruleAction;

    /**
     * Create a rule.
     * @param name         the rule name for debugging
     * @param matchType    the rule match type
     */
    @SuppressWarnings("WeakerAccess")
    public Rule(final String name, @MatchType final int matchType) {
        this.name = name;
        this.matchType = matchType;
    }

    /**
     * @return the rule name
     */
    @SuppressWarnings("unused")
    public String getName() {
        return name;
    }

    /**
     * @return the match type
     */
    @SuppressWarnings("unused")
    public int getMatchType() {
        return matchType;
    }

    /**
     * Add a conjunction of facts to the rule's left-hand side.
     * @param facts the conjunction of facts
     */
    public void addCondition(final Collection<Fact> facts) {
        int factVector = 0;
        for (final Fact fact : facts) {
            factVector |= 1 << fact.id;
        }
        conditions.add(factVector);
    }

    /**
     * Add a conjunction of negated facts to the rule's left-hand side.
     * @param facts the conjunction of facts
     */
    public void addNegCondition(final Collection<Fact> facts) {
        int factVector = 0;
        for (final Fact fact : facts) {
            factVector |= 1 << fact.id;
        }
        negConditions.add(factVector);
    }

    /**
     * Specifies the rule's executable action (right-hand side)
     * @param ruleAction the rule action
     */
    public void setRuleAction(final RuleAction ruleAction) {
        this.ruleAction = ruleAction;
    }

    /**
     * Retrieve the conjunctions of facts from the rule's left-hand side in the native
     * integer format. Not recommended for performance-critical operations.
     *
     * @return the rule's conjunctions of facts
     */
    @SuppressWarnings("unused")
    public List<Integer> getNativeConditions() {
        return Collections.unmodifiableList(conditions);
    }

    /**
     * Retrieve the conjunctions of negated facts from the rule's left-hand side in the native
     * integer format. Not recommended for performance-critical operations.
     *
     * @return the rule's conjunctions of negated facts
     */
    @SuppressWarnings("unused")
    public List<Integer> getNativeNegConditions() {
        return Collections.unmodifiableList(negConditions);
    }

    @Override
    public String toString() {
        return name;
    }

    /**
     * Evaluates the rule's left-hand side
     * @param state the fact state to use for evaluation
     * @return whether the left-hand side matches the fact state
     */
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    boolean eval(final int state) {
        for (final int condition : conditions) {
            if ((state & condition) != condition) {
                return false;
            }
        }
        //TODO optimization possible for the common case of disjoint negated conditions
        for (final int negCondition : negConditions) {
            if ((state & negCondition) == negCondition) {
                return false;
            }
        }
        return true;
    }
}
