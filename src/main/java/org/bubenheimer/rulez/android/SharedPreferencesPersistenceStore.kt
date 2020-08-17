/*
 * Copyright (c) 2015-2020 Uli Bubenheimer
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
import org.bubenheimer.rulez.facts.Fact
import org.bubenheimer.rulez.state.State

/**
 * Persistence store to support [Persistable] [Fact]s (e.g. [PersistableFact]) based on
 * [SharedPreferences].
 *
 * @param facts set of [Fact]s belonging to the same [org.bubenheimer.rulez.facts.FactBase]
 * @param sharedPreferences storage to use
 * @param keyPrefix key name prefix to keep [Fact] keys unique in shared [sharedPreferences]
 * storage
 */
open class SharedPreferencesPersistenceStore(
    private val facts: Iterable<Fact>,
    private val sharedPreferences: SharedPreferences,
    private val keyPrefix: String
) {
    /**
     * Restores [Fact] [State] for all [facts]
     *
     * @param initialState base [Fact] [State]. Restored [State] is applied on top.
     * @return the restored [State]
     */
    fun restoreState(initialState: State = State.VOID): State {
        return facts.fold(initialState) { state, fact ->
            if (fact is Persistable) {
                val prefixedKey = prefixedKey(fact)
                if (contains(prefixedKey)) state(fact, get(prefixedKey))
                else state
            } else {
                state
            }
        }
    }

    /**
     * Saves [Fact] state for all [facts]
     */
    fun saveState(state: State) {
        sharedPreferences.edit {
            facts.forEach {
                if (it is Persistable) putBoolean(prefixedKey(it), state[it])
            }
        }
    }

    /**
     * Whether the store contains a value for a specific key
     */
    open fun contains(key: String): Boolean = sharedPreferences.contains(key)

    /**
     * Gets a value for a specific key. Returns `false` is key is not present in store.
     */
    open fun get(key: String): Boolean = sharedPreferences.getBoolean(key, false)

    /**
     * Creates a prefixed [fact] key
     */
    private fun prefixedKey(fact: Persistable) = keyPrefix + fact.key
}
