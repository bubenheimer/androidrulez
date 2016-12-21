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

import android.support.annotation.IntDef;
import android.support.annotation.RestrictTo;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * A fact
 */
public final class Fact {
    /**
     * Persistence type. Specifies whether fact state is persistent or not.
     */
    @SuppressWarnings("WeakerAccess")
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({PERSISTENCE_NONE, PERSISTENCE_DISK})
    public @interface Persistence {}
    /**
     * No fact state persistence.
     */
    @SuppressWarnings("WeakerAccess")
    public static final int PERSISTENCE_NONE = 0;
    /**
     * Fact state is persistent for the life of the app installation. Uninstalling the app or
     * clearing app data clears fact state.
     */
    @SuppressWarnings("WeakerAccess")
    public static final int PERSISTENCE_DISK = 1;

    /**
     * The internal fact id
     */
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    final int id;

    /**
     * Fact name
     */
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    final String name;

    /**
     * Persistence type. Specifies whether fact state is persistent or not.
     */
    @Persistence
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    final int persistence;

    /**
     * Creates a new fact
     * @param id            the internal fact id. All facts in a rule base must have distinct IDs.
     * @param name          a unique fact name
     * @param persistence   fact state persistence type
     */
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    Fact(final int id, final String name, @Persistence final int persistence) {
        this.id = id;
        this.name = name;
        this.persistence = persistence;
    }

    /**
     * @return the fact's ID within a given rule base.
     */
    @SuppressWarnings("unused")
    public int getId() {
        return id;
    }
}
