/*
 * Copyright (c) 2015-2017 Uli Bubenheimer. All rights reserved.
 */

package org.bubenheimer.android.rulez;

/**
 * Listener to be invoked when rule evaluation ends.
 * Set this via {@link RuleEngine#setEvalEndListener(EvalEndListener)}.
 */
@SuppressWarnings("WeakerAccess")
public interface EvalEndListener {
    /**
     * Invoked when rule evaluation ends.
     * @param engine the rule engine
     */
    void onEvalEnd(final RuleEngine engine);
}
