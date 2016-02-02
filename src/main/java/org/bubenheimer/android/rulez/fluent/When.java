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

package org.bubenheimer.android.rulez.fluent;

import org.bubenheimer.android.rulez.Fact;
import org.bubenheimer.android.rulez.Rule;
import org.bubenheimer.android.rulez.RuleBody;

import java.util.ArrayList;
import java.util.Collection;

public final class When {
    private final Rule rule;

    private Collection<Fact> facts = new ArrayList<>();

    When(final Rule rule) {
        this.rule = rule;
    }

    public When and(final Fact fact) {
        facts.add(fact);
        return this;
    }

    public When or(final Fact fact) {
        if (!facts.isEmpty()) {
            rule.addPremise(facts);
        }
        facts.clear();
        facts.add(fact);
        return this;
    }

    public WhenNot andNot(final Fact fact) {
        if (!facts.isEmpty()) {
            rule.addPremise(facts);
        }
        final WhenNot whenNot = new WhenNot(rule);
        whenNot.or(fact);
        return whenNot;
    }

    public Rule then(final RuleBody ruleBody) {
        if (!facts.isEmpty()) {
            rule.addPremise(facts);
        }
        rule.setRuleBody(ruleBody);
        return rule;
    }
}
