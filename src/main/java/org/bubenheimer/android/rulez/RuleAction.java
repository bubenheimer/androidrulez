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
 * Represents the action (right-hand side) of a rule with code to execute if the left-hand side
 * matches during rule evaluation.
 */
public interface RuleAction {
    /**
     * A rule action with code to execute.
     * @param oldState the old fact state. Only valid for the duration of this call. Do not store
     *                 a reference to this object.
     * @param newState the new fact state. Only valid for the duration of this call. Do not store
     *                 a reference to this object.
     */
    void fire(ReadableState oldState, WritableState newState);
}
