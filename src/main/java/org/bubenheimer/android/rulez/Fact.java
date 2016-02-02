/*
 * Copyright (c) 2016 Uli Bubenheimer
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
 */

package org.bubenheimer.android.rulez;

/**
 * A fact
 */
public final class Fact {
    /**
     * The internal fact id
     */
    final int id;

    /**
     * Creates a new fact
     * @param id the internal fact id. All facts in a rule base must have distinct IDs.
     */
    Fact(final int id) {
        this.id = id;
    }

    /**
     * @return the fact's ID within a given rule base.
     */
    public int getId() {
        return id;
    }
}
