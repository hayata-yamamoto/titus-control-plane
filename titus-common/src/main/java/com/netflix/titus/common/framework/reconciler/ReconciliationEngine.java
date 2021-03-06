/*
 * Copyright 2018 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.netflix.titus.common.framework.reconciler;

import java.util.List;

import rx.Observable;

/**
 */
public interface ReconciliationEngine<EVENT> {

    interface DifferenceResolver<EVENT> {
        List<ChangeAction> apply(ReconciliationEngine<EVENT> engine);
    }

    /**
     * Change reference entity. The return observable completes successfully if the reference update was
     * successful. The action itself may include calls to external system to make the change persistent.
     * Examples of actions:
     * <ul>
     * <li>Job scale up</li>
     * <li>User requested task termination</li>
     * </ul>
     * Task completion can take some time, but it is always guarded by a timeout. If timeout triggers, the result is unknown.
     * Multiple change requests are processed in order of arrival, one at a time. If action execution deadline is
     * crossed, it is rejected. The deadline value must be always greater than the execution timeout.
     */
    Observable<Void> changeReferenceModel(ChangeAction changeAction);

    /**
     * Change reference entity with the given id. Change actions for non-overlapping entities can be executed in parallel.
     */
    Observable<Void> changeReferenceModel(ChangeAction changeAction, String entityHolderId);

    /**
     * Returns immutable reference model.
     */
    EntityHolder getReferenceView();

    /**
     * Returns immutable running model.
     */
    EntityHolder getRunningView();

    /**
     * Returns immutable persisted model.
     */
    EntityHolder getStoreView();

    <ORDER_BY> List<EntityHolder> orderedView(ORDER_BY orderingCriteria);

    /**
     * Emits an event for each requested system change , and reconciliation action.
     */
    Observable<EVENT> events();
}
