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
public final class WhenNot {
    private final Rule rule;

    private final Collection<Fact> facts = new ArrayList<>();

    WhenNot(final Rule rule) {
        this.rule = rule;
    }

    public WhenNot and(final Fact fact) {
        facts.add(fact);
        return this;
    }

    @SuppressWarnings("UnusedReturnValue")
    public WhenNot or(final Fact fact) {
        if (!facts.isEmpty()) {
            rule.addNegCondition(facts);
        }
        facts.add(fact);
        return this;
    }

    @SuppressWarnings("unused")
    public Rule then(final RuleAction ruleAction) {
        if (!facts.isEmpty()) {
            rule.addNegCondition(facts);
        }
        rule.setRuleAction(ruleAction);
        return rule;
    }
}
