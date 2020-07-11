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

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import org.bubenheimer.rulez.base.Fact
import org.bubenheimer.rulez.base.FactState
import org.bubenheimer.rulez.base.Rule
import org.bubenheimer.rulez.base.RuleBase

/**
 * A variation of the rule engine leveraging Lifecycle and SavedState.
 */
open class LifecycleRuleEngine<F : Fact, R : Rule<F>>(
    factState: FactState<F>,
    ruleBase: RuleBase<F, R>,
//    ruleMatchState: Int = 0,
    evaluationListener: (LooperRuleEngine<F, R>.() -> Unit)? = null
) : LooperRuleEngine<F, R>(factState, ruleBase, evaluationListener), DefaultLifecycleObserver {
    override fun onCreate(owner: LifecycleOwner) {
        scheduleEvaluation()
    }

    override fun onStart(owner: LifecycleOwner) = resumeEvaluation()

    override fun onStop(owner: LifecycleOwner) = pauseEvaluation()
}
