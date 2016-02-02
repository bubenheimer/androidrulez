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

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Collection;

/**
 * A rule
 */
public final class Rule {

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({EXECUTION_TYPE_ONCE, EXECUTION_TYPE_RESET, EXECUTION_TYPE_ALWAYS})
    public @interface ExecutionType {}

    /**
     * Specifies to evaluate and execute a rule no more than once
     */
    public static final int EXECUTION_TYPE_ONCE = 0;

    /**
     * Specifies to re-evaluate and re-execute a rule once its left-hand side no longer matches
     */
    public static final int EXECUTION_TYPE_RESET = 1;

    /**
     * Specifies to always re-evaluate and re-execute a rule
     */
    public static final int EXECUTION_TYPE_ALWAYS = 2;

    /**
     * Rule name
     */
    final String name;

    /**
     * Rule execution type. Specifies if a rule should run no more than once, or under what
     * conditions it becomes eligible to re-run.
     */
    @ExecutionType
    final int executionType;

    /**
     * The positive facts of the rule's left-hand side.
     */
    final ArrayList<Integer> premises = new ArrayList<>();

    /**
     * The negated facts of the rule's left-hand side.
     */
    final ArrayList<Integer> negPremises = new ArrayList<>();

    /**
     * The rule body to execute if the rule matches.
     */
    RuleBody ruleBody;

    /**
     * Create a rule.
     * @param name             the rule name for debugging
     * @param executionType    the rule execution type
     */
    public Rule(final String name, @ExecutionType final int executionType) {
        this.name = name;
        this.executionType = executionType;
    }

    /**
     * @return the rule name for debugging
     */
    public String getName() {
        return name;
    }

    /**
     * Add a conjunction of facts to the rule's left-hand side.
     * @param facts the conjunction of facts
     */
    public void addPremise(final Collection<Fact> facts) {
        int factVector = 0;
        for (final Fact fact : facts) {
            factVector |= 1 << fact.id;
        }
        premises.add(factVector);
    }

    /**
     * Add a conjunction of negated facts to the rule's left-hand side.
     * @param facts the conjunction of facts
     */
    public void addNegPremise(final Collection<Fact> facts) {
        int factVector = 0;
        for (final Fact fact : facts) {
            factVector |= 1 << fact.id;
        }
        negPremises.add(factVector);
    }

    /**
     * Specifies the rule's executable body (right-hand side)
     * @param ruleBody the rule body
     */
    public void setRuleBody(final RuleBody ruleBody) {
        this.ruleBody = ruleBody;
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
    boolean eval(final int state) {
        for (final int premise : premises) {
            if ((state & premise) != premise) {
                return false;
            }
        }
        //TODO optimization possible for the common case of disjoint negated premises
        for (final int negPremise : negPremises) {
            if ((state & negPremise) == negPremise) {
                return false;
            }
        }
        return true;
    }
}
