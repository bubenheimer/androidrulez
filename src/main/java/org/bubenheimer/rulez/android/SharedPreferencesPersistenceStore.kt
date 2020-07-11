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
import org.bubenheimer.rulez.NamedFact
import org.bubenheimer.rulez.base.FactBase

/**
 * Persistence store to support [PersistableFact.PERSISTENCE_DISK] based on
 * [SharedPreferences].
 */
open class SharedPreferencesPersistenceStore(
    private val factBase: FactBase<PersistableFact>,
    /**
     * [SharedPreferences] to use
     */
    private val sharedPreferences: SharedPreferences,
    private val keyPrefix: String
) {
    fun restoreState(initialState: Int = 0): Int {
        return factBase.factList.fold(initialState) { factState, fact ->
            when (fact.persistence) {
                PersistableFact.PERSISTENCE_NONE -> factState
                PersistableFact.PERSISTENCE_DISK -> {
                    val prefixedKey = prefixedKey(fact)
                    if (contains(prefixedKey)) {
                        fact.applyToFactState(get(prefixedKey), factState)
                    } else {
                        factState
                    }
                }
                else -> throw AssertionError()
            }
        }
    }

    fun saveState(factState: Int) {
        sharedPreferences.edit {
            factBase.factList.forEach {
                if (it.persistence == PersistableFact.PERSISTENCE_DISK) {
                    putBoolean(prefixedKey(it), it.getFactValue(factState))
                }
            }
        }
    }

    open fun contains(key: String): Boolean = sharedPreferences.contains(key)

    open fun get(key: String): Boolean = sharedPreferences.getBoolean(key, false)

    private fun prefixedKey(fact: NamedFact) = keyPrefix + fact.name
}
