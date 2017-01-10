/*
 * Copyright (c) 2015-2017 Uli Bubenheimer. All rights reserved.
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
