/*
 * Copyright (c) 2015-2017 Uli Bubenheimer. All rights reserved.
 */

package org.bubenheimer.android.rulez.fluent;

import org.bubenheimer.android.rulez.Fact;
import org.bubenheimer.android.rulez.Rule;
import org.bubenheimer.android.rulez.RuleAction;

import java.util.ArrayList;
import java.util.Collection;

@SuppressWarnings("WeakerAccess")
public final class When {
    private final Rule rule;

    private final Collection<Fact> facts = new ArrayList<>();

    When(final Rule rule) {
        this.rule = rule;
    }

    public When and(final Fact fact) {
        facts.add(fact);
        return this;
    }

    @SuppressWarnings("UnusedReturnValue")
    public When or(final Fact fact) {
        if (!facts.isEmpty()) {
            rule.addCondition(facts);
        }
        facts.clear();
        facts.add(fact);
        return this;
    }

    @SuppressWarnings("unused")
    public WhenNot andNot(final Fact fact) {
        if (!facts.isEmpty()) {
            rule.addCondition(facts);
        }
        final WhenNot whenNot = new WhenNot(rule);
        whenNot.or(fact);
        return whenNot;
    }

    @SuppressWarnings("unused")
    public Rule then(final RuleAction ruleAction) {
        if (!facts.isEmpty()) {
            rule.addCondition(facts);
        }
        rule.setRuleAction(ruleAction);
        return rule;
    }
}
