/*
 * Copyright 2019 Netflix, Inc.
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

package com.netflix.titus.supplementary.relocation.endpoint.rest;

import com.netflix.titus.supplementary.relocation.workflow.RelocationWorkflowException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class TaskRelocationExceptionHandler {

    @ExceptionHandler(value = {RelocationWorkflowException.class})
    @Order(Ordered.HIGHEST_PRECEDENCE)
    protected ResponseEntity<Object> handleRelocationWorkflowException(RelocationWorkflowException ex, WebRequest request) {
        switch (ex.getErrorCode()) {
            case NotReady:
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Not ready yet");
            case StoreError:
            default:
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }
}
