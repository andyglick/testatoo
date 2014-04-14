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
package org.testatoo

import org.junit.*
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.openqa.selenium.firefox.FirefoxDriver
import org.testatoo.core.Testatoo
import org.testatoo.core.component.Button
import org.testatoo.core.evaluator.webdriver.WebDriverEvaluator

import static org.testatoo.core.Testatoo.*
import static org.testatoo.core.input.Mouse.clickOn
import static org.testatoo.core.state.States.*

/**
 * @author David Avenante (d.avenante@gmail.com)
 */
@RunWith(JUnit4)
class WaitTest {

    @BeforeClass
    public static void setup() {
        Testatoo.evaluator =  new WebDriverEvaluator(new FirefoxDriver())
        open 'http://localhost:8080/wait.html'
    }
    @AfterClass public static void tearDown() { evaluator.close() }

    @Test
    public void test_wait() {
        Button button = $('#add-message') as Button
        Button message = $('#msg') as Button

        assertThat button, { Button c ->
            c.is(enabled) and c.is(visible)
        }

        assertThat message is missing

        clickOn button
        assertThat button is(disabled)

        waitUntil 10.seconds, {
            button.is(enabled)
        }

        clickOn button
        waitUntil {
            button.is(enabled) or message.is(visible)
        }

        // TODO David
//        waitUntil button is enabled
//        waitFor 10.seconds that button.is(enabled)
//        waitFor 10.seconds that button is enabled
    }
}
