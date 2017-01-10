/*
 * Copyright (c) 2015-2017 Uli Bubenheimer. All rights reserved.
 */

package org.bubenheimer.android.rulez;

/**
 * Represents the action (right-hand side) of a rule with code to execute if the left-hand side
 * matches during rule evaluation.
 */
public interface RuleAction {
    /**
     * A rule action with code to execute.
     * @param oldState the old fact state. Only valid for the duration of this call. Do not store
     *                 a reference to this object.
     * @param newState the new fact state. Only valid for the duration of this call. Do not store
     *                 a reference to this object.
     */
    void fire(ReadableState oldState, WritableState newState);
}
