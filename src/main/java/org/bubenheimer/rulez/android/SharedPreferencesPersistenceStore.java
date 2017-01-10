/*
 * Copyright (c) 2015-2017 Uli Bubenheimer. All rights reserved.
 */

package org.bubenheimer.rulez.android;

import android.content.SharedPreferences;

import org.bubenheimer.rulez.Fact;
import org.bubenheimer.rulez.PersistenceStore;

/**
 * Persistence store to support {@link Fact#PERSISTENCE_DISK} based on {@link SharedPreferences}.
 */
public final class SharedPreferencesPersistenceStore implements PersistenceStore {
    private final SharedPreferences sharedPreferences;

    /**
     * Constructor
     * @param sharedPreferences store to use. Ideally this should be a dedicated
     *                          {@link SharedPreferences} file to prevent key name conflicts.
     */
    public SharedPreferencesPersistenceStore(final SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    /**
     * Retrieve fact value from {@link SharedPreferences}
     * @param id    unique fact ID
     * @param name  unique fact name
     * @return fact value
     */
    @Override
    public boolean get(final int id, final String name) {
        return sharedPreferences.getBoolean(name, false);
    }

    /**
     * Set fact value in {@link SharedPreferences}
     * @param id    unique fact ID
     * @param name  unique fact name
     * @param value fact value
     */
    @Override
    public void set(final int id, final String name, final boolean value) {
        sharedPreferences.edit().putBoolean(name, value).apply();
    }
}
