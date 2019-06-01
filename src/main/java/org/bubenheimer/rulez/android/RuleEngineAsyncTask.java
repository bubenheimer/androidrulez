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
