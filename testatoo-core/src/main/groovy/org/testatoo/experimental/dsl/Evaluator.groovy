/**
 * Copyright (C) 2008 Ovea <dev@ovea.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.testatoo.experimental.dsl

import org.testatoo.experimental.dsl.component.IdSupport

public interface Evaluator {
    void open(String url)

    boolean isEnabled(IdSupport component)

    boolean isVisible(IdSupport component)

    String[] getElementsIds(String expr)

    String getLabel(IdSupport component)

    void reset(IdSupport component)

    void setFocus(IdSupport component)

    void type(String text)

    void click(IdSupport component)

    String getText(IdSupport component)

    String getPlaceholder(IdSupport component)

    boolean isAvailable(IdSupport component)
}