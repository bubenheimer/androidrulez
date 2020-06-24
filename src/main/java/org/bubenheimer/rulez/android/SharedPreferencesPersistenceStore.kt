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
package org.bubenheimer.rulez.android

import android.content.SharedPreferences
import androidx.core.content.edit
import org.bubenheimer.rulez.PersistenceStore

/**
 * Persistence store to support [Fact.PERSISTENCE_DISK] based on [SharedPreferences].
 */
open class SharedPreferencesPersistenceStore
/**
 * Constructor
 * @param sharedPreferences store to use. Ideally this should be a dedicated
 * [SharedPreferences] file to prevent key name conflicts.
 */
(private val sharedPreferences: SharedPreferences) : PersistenceStore {
    /**
     * Retrieve fact value from [SharedPreferences]
     * @param id    unique fact ID
     * @param name  unique fact name
     * @return fact value
     */
    override fun get(id: Int, name: String): Boolean = sharedPreferences.getBoolean(name, false)

    /**
     * Set fact value in [SharedPreferences]
     * @param id    unique fact ID
     * @param name  unique fact name
     * @param value fact value
     */
    override fun set(id: Int, name: String, value: Boolean) =
            sharedPreferences.edit { putBoolean(name, value) }
}