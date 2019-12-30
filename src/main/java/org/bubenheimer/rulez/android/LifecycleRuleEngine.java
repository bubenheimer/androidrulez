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

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.savedstate.SavedStateRegistry;

/**
 * A variation of the rule engine leveraging Lifecycle and SavedState.
 */
@SuppressWarnings("unused")
public class LifecycleRuleEngine extends LooperRuleEngine implements DefaultLifecycleObserver {
    private static final String SAVED_STATE_KEY = "LifecycleRuleEngine.key";

    private final SavedStateRegistry savedStateRegistry;

    public LifecycleRuleEngine(
            final SavedStateRegistry savedStateRegistry
    ) {
        this.savedStateRegistry = savedStateRegistry;
    }

    @Override
    public void onCreate(
            final @NonNull LifecycleOwner owner
    ) {
        final Bundle bundle = savedStateRegistry.consumeRestoredStateForKey(SAVED_STATE_KEY);
        if (bundle != null) {
            restoreInstanceState(bundle);
        }

        savedStateRegistry.registerSavedStateProvider(SAVED_STATE_KEY, () -> {
            final Bundle b = new Bundle(2);
            saveInstanceState(b);
            return b;
        });

        scheduleEvaluation();
    }

    @Override
    public void onDestroy(
            final @NonNull LifecycleOwner owner
    ) {
        savedStateRegistry.unregisterSavedStateProvider(SAVED_STATE_KEY);
    }

    @Override
    public void onStart(
            final @NonNull LifecycleOwner owner
    ) {
        resumeEvaluation();
    }

    @Override
    public void onStop(
            final @NonNull LifecycleOwner owner
    ) {
        pauseEvaluation();
    }
}
