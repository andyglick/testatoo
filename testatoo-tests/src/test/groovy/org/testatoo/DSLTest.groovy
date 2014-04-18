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
import org.testatoo.core.component.CheckBox
import org.testatoo.core.component.list.*
import org.testatoo.core.evaluator.webdriver.WebDriverEvaluator

import static org.testatoo.core.Testatoo.*
import static org.testatoo.core.property.Properties.label
import static org.testatoo.core.state.States.*

/**
 * @author David Avenante (d.avenante@gmail.com)
 */
@RunWith(JUnit4)
class DSLTest {

    @BeforeClass
    public static void setup() {
        Testatoo.evaluator =  new WebDriverEvaluator(new FirefoxDriver())
        open 'http://localhost:8080/component.html'
    }
    @AfterClass public static void tearDown() { evaluator.close() }

    @Test
    public void test_chaining_assert() {
        CheckBox checkBox = $('#checkbox') as CheckBox
        assertThat {
            checkBox.is enabled
            checkBox.is visible

            checkBox.is unchecked
            checkBox.has label('Check me out')
        }
    }

    @Test
    public void test_AND() {
        CheckBox checkBox = $('#checkbox') as CheckBox
        assertThat {
            checkBox.is(enabled) and checkBox.is(visible)
            checkBox.is(enabled) & checkBox.is(visible)
        }
    }

    @Test
    public void test_OR() {
        ListBox listBox = $('#cities') as ListBox
        assertThat {
            listBox.has(8.items) or listBox.has(3.visibleItems)
            listBox.has(8.items) | listBox.has(3.visibleItems)
        }
    }

    @Test
    public void test_ARE() {
        DropDown dropDown = $('#elements') as DropDown
//        CheckBox checkBox = $('#checkbox') as CheckBox

        assertThat {
            dropDown.items.are enabled
        }

//        assertThat {
//            [dropDown, checkBox]*.are visible
//        }
//        assertThat([login_email, login_password, login_button].are(visible))
    }


}
