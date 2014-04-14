/**
 * Copyright (C) 2013 Ovea (dev@ovea.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.testatoo.core.input

import org.testatoo.core.Testatoo

/**
 * @author David Avenante (d.avenante@gmail.com)
 */
class Keyboard {

    static void type(Collection<?> keys)  { Testatoo.evaluator.enter(keys) }
    static void type(Key key)  { type([key]) }
    static void type(String text)  { type([text]) }

}