/*
 * Copyright (c) 2015-2019 Uli Bubenheimer
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
 *
 */

package org.bubenheimer.rulez.android;

import android.content.SharedPreferences;

import org.bubenheimer.rulez.Fact;
import org.bubenheimer.rulez.PersistenceStore;

/**
 * Persistence store to support {@link Fact#PERSISTENCE_DISK} based on {@link SharedPreferences}.
 */
public class SharedPreferencesPersistenceStore implements PersistenceStore {
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
