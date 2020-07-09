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
package org.bubenheimer.rulez.android

import android.os.Bundle
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.savedstate.SavedStateRegistry

/**
 * A variation of the rule engine leveraging Lifecycle and SavedState.
 */
open class LifecycleRuleEngine(
    private val savedStateRegistry: SavedStateRegistry,
    evaluationListener: (LooperRuleEngine.() -> Unit)? = null
) : LooperRuleEngine(evaluationListener), DefaultLifecycleObserver {
    private companion object {
        private const val SAVED_STATE_KEY = "LifecycleRuleEngine.key"
    }

    override fun onCreate(owner: LifecycleOwner) {
        val bundle = savedStateRegistry.consumeRestoredStateForKey(SAVED_STATE_KEY)
        bundle?.let { restoreInstanceState(it) }

        savedStateRegistry.registerSavedStateProvider(SAVED_STATE_KEY) {
            val b = Bundle(2)
            saveInstanceState(b)
            b
        }
        scheduleEvaluation()
    }

    override fun onDestroy(owner: LifecycleOwner) =
        savedStateRegistry.unregisterSavedStateProvider(SAVED_STATE_KEY)

    override fun onStart(owner: LifecycleOwner) = resumeEvaluation()

    override fun onStop(owner: LifecycleOwner) = pauseEvaluation()
}
