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

import org.bubenheimer.rulez.facts.FactBase
import org.bubenheimer.rulez.facts.NamedFact

/**
 * A persistable named fact
 *
 * @param factBase scope of [PersistableFact] for [PersistableFact.id] allocation and association
 * with [RuleBase]
 * @param name a fact name. Should be unique within [factBase] to properly support key
 * generation.
 */
@Suppress("KDocUnresolvedReference") // Android Studio bug workaround
class PersistableFact constructor(
    factBase: FactBase,
    name: String
) : NamedFact(factBase, name), Persistable {
    override val key
        get() = name

    override fun toString() = "$id: $name (persistable)"
}

/**
 * Creates a new [PersistableFact]
 *
 * @receiver scope of [PersistableFact] for [PersistableFact.id] allocation and association with a
 * rulebase.
 * @param name a fact name. Should be unique within receiver [FactBase] to properly support key
 * generation.
 */
fun FactBase.newPersistableFact(name: String) = PersistableFact(this, name)
