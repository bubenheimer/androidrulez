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

import androidx.annotation.IntDef
import org.bubenheimer.rulez.NamedFact
import org.bubenheimer.rulez.base.FactBase

/**
 * A persistable named fact
 * @param id the internal fact id. All facts in a rule base must have distinct IDs.
 * @param name a unique fact name
 * @param persistence fact state persistence type
 */
class PersistableFact private constructor(
    id: Int,
    name: String,
    @Persistence val persistence: Int = PERSISTENCE_NONE
) : NamedFact(id, name) {
    /**
     * Persistence type. Specifies whether fact state is persistent or not.
     */
    @Retention(AnnotationRetention.SOURCE)
    @IntDef(PERSISTENCE_NONE, PERSISTENCE_DISK)
    annotation class Persistence

    companion object {
        /**
         * No fact state persistence.
         */
        const val PERSISTENCE_NONE = 0

        /**
         * Fact state is persistent for the life of the app installation. Uninstalling the app or
         * clearing app data clears fact state.
         */
        const val PERSISTENCE_DISK = 1
    }

    class FactCreator(
        private val name: String,
        @Persistence private val persistence: Int = PERSISTENCE_NONE
    ) :
        FactBase.FactCreator<PersistableFact> {
        override fun create(id: Int) = PersistableFact(id, name, persistence)
    }
}
