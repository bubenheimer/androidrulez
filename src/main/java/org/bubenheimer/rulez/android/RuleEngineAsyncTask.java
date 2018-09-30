/*
 * Copyright (c) 2015-2017 Uli Bubenheimer. All rights reserved.
 */

package org.bubenheimer.rulez.android;

import android.os.AsyncTask;

import org.bubenheimer.rulez.Fact;
import org.bubenheimer.rulez.FactState;

import androidx.annotation.RestrictTo;

public abstract class RuleEngineAsyncTask<Params, Progress, Result>
        extends AsyncTask<Params, Progress, Result> {
    private final FactState factState;
    private final Fact completionFact;

    @RestrictTo(RestrictTo.Scope.SUBCLASSES)
    protected RuleEngineAsyncTask(final FactState factState, final Fact completionFact) {
        this.factState = factState;
        this.completionFact = completionFact;
    }

    public final void cancelIfIncomplete() {
        if (getStatus() != Status.FINISHED && !isCancelled()) {
            cancel(false);
        }
    }

    @RestrictTo(RestrictTo.Scope.SUBCLASSES)
    protected final void addCompletionFact() {
        factState.addFact(completionFact);
    }
}
