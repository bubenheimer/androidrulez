/*
 * Copyright (c) 2015-2017 Uli Bubenheimer. All rights reserved.
 */

package org.bubenheimer.android.rulez;

/**
 * API for checking the fact state
 */
@SuppressWarnings("WeakerAccess")
public interface ReadableState {
    /**
     * @return whether the fact is valid (true)
     */
    @SuppressWarnings("unused")
    boolean isValid(Fact fact);
}
