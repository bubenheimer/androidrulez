/*
 * Copyright (c) 2015-2017 Uli Bubenheimer. All rights reserved.
 */

package org.bubenheimer.android.rulez;

/**
 * API for modifying the fact state
 */
@SuppressWarnings("WeakerAccess")
public interface WritableState {
    /**
     * Adds a fact to the state.
     * @param fact the fact to add
     */
    @SuppressWarnings("unused")
    void addFact(Fact fact);

    /**
     * Adds facts to the state
     * @param facts the facts to add
     */
    @SuppressWarnings("unused")
    void addFacts(Fact... facts);

    /**
     * Removes a fact from the state.
     * @param fact the fact to remove
     */
    @SuppressWarnings("unused")
    void removeFact(Fact fact);

    /**
     * Removes facts from the state.
     * @param facts the facts to remove
     */
    @SuppressWarnings("unused")
    void removeFacts(Fact... facts);

    /**
     * Adds a fact to the state and removes a fact from the state in a single operation.
     * @param addFact       the fact to add
     * @param removeFact    the fact to remove
     */
    @SuppressWarnings("unused")
    void addRemoveFacts(Fact addFact, Fact removeFact);

    /**
     * Adds facts to the state and removes facts from the state in a single operation.
     * @param addFacts      the facts to add
     * @param removeFacts   the facts to remove
     */
    @SuppressWarnings("unused")
    void addRemoveFacts(Fact[] addFacts, Fact[] removeFacts);
}
