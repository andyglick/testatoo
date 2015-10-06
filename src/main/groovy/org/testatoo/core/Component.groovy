/**
 * Copyright (C) 2014 Ovea (dev@ovea.com)
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
package org.testatoo.core

import org.testatoo.core.action.Action
import org.testatoo.core.action.MouseClick
import org.testatoo.core.action.MouseDrag
import org.testatoo.core.action.support.Clickable
import org.testatoo.core.action.support.Draggable
import org.testatoo.core.evaluator.Evaluator
import org.testatoo.core.property.Property
import org.testatoo.core.property.PropertyEvaluator
import org.testatoo.core.property.matcher.PropertyMatcher
import org.testatoo.core.state.*
import org.testatoo.bundle.html5.traits.GenericSupport

import java.util.concurrent.TimeoutException

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 */
class Component implements GenericSupport, Clickable, Draggable {

    private static final PropertyEvaluator DEFAULT_PE = {} as PropertyEvaluator
    private static final StateEvaluator DEFAULT_SE = {} as StateEvaluator

    private Map<Class<? extends Property>, PropertyEvaluator> _supportedProperties = new IdentityHashMap<>()
    private Map<Class<? extends State>, StateEvaluator> _supportedStates = new IdentityHashMap<>()
    private Set<Class<? extends Action>> _supportedActions = new HashSet<>()

    CachedMetaData meta = new CachedMetaData()

    Component() {
        support Enabled, Disabled, Available, Missing, Hidden, Visible
        support MouseClick, MouseDrag
    }

    Component(Evaluator evaluator, IdProvider idProvider) {
        this()
        meta = new CachedMetaData(
            evaluator: evaluator,
            idProvider: idProvider
        )
    }

    String eval(String jqueryExpr) { return evaluator.eval(getId(), jqueryExpr) }

    boolean check(String jqueryExpr) { return evaluator.getBool(getId(), jqueryExpr) }

    String getId() throws ComponentException { meta.getMetaInfo(this).id }

    Evaluator getEvaluator() { meta.evaluator }

    Block be(State matcher) { block 'be', matcher }

    boolean is(State state) {
        StateEvaluator se = _supportedStates.get(state.class)
        if (se == null) {
            throw new ComponentException("Component ${this} does not support state ${state.class.simpleName}")
        }
        return (se == DEFAULT_SE ? state.class.newInstance().evaluator : se).getState(this)
    }

    Block have(PropertyMatcher matcher) { block 'have', matcher }

    Object has(Property property) {
        PropertyEvaluator pe = _supportedProperties.get(property.class)
        if (pe == null) {
            throw new ComponentException("Component ${this} does not support property ${property.class.simpleName}")
        }
        return (pe == DEFAULT_PE ? property.class.newInstance().evaluator : pe).getValue(this)
    }

    Block contain(Component... components) {
        Blocks.block "matching ${this} contains ${components}", {
            List ret = evaluator.getJson("\$._contains('${id}', [${components.collect { "'${it.id}'" }.join(', ')}])")
            if (ret) {
                throw new ComponentException("Component ${this} does not contain expected component(s): ${components.findAll { it.id in ret }}");
            }
        }
    }

    Block display(Component... components) {
        Blocks.block "matching ${this} display ${components}", {
            List ret = evaluator.getJson("\$._contains('${id}', [${components.collect { "'${it.id}'" }.join(', ')}])")
            if (ret) {
                throw new ComponentException("Component ${this} does not display expected component(s): ${components.findAll { it.id in ret }}");
            } else {
                components.findAll { !it.is(new Visible()) }
            }
        }
    }

    void should(Closure c) {
        c.delegate = this
        c(this)
        waitUntil(c)
    }

    protected <T extends Component> List<T> find(String expression, Class<T> type = Component) {
        evaluator.getMetaInfo("\$('#${id}').find('${expression}')").collect { it.asType(type) } as List<T>
    }

    protected <T extends Component> List<T> findjs(String expression, Class<T> type = Component) {
        evaluator.getMetaInfo(expression).collect { it.asType(type) } as List<T>
    }

    private block(String type, Matcher m) { Blocks.block "matching ${this} ${type} ${m}", { m.matches(this) } }

    @Override
    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false
        Component component = (Component) o
        if (id != component.id) return false
        return true
    }

    @Override
    int hashCode() { id.hashCode() }

    @Override
    String toString() {
        String str
        try {
            str = id
        } catch (ComponentException ignored) {
            str = meta.metaInfo
        }
        getClass().simpleName + ":${str}"
    }

    Object asType(Class clazz) {
        if (Component.isAssignableFrom(clazz)) {
            Component c = (Component) clazz.newInstance()
            c.meta = this.meta
            return c
        }
        return super.asType(clazz)
    }

    void support(Class<?>... types) {
        for (Class<?> type : types) {
            if (Property.isAssignableFrom(type)) {
                support(type as Class<? extends Property>, DEFAULT_PE)
            } else if (State.isAssignableFrom(type)) {
                support(type as Class<? extends State>, DEFAULT_SE)
            } else if (Action.isAssignableFrom(type)) {
                _supportedActions.add(type as Class<? extends Action>)
            }
        }
    }

    void support(Class<? extends Property> type, PropertyEvaluator e) {
        _supportedProperties.put(type, e)
    }

    void support(Class<? extends State> type, StateEvaluator e) {
        _supportedStates.put(type, e)
    }

    void support(Class<?> type, Closure<?> c) {
        if (Property.isAssignableFrom(type)) {
            _supportedProperties.put(type as Class<? extends Property>, c as PropertyEvaluator)
        } else if (State.isAssignableFrom(type)) {
            _supportedStates.put(type as Class<? extends State>, c as StateEvaluator)
        }
    }

    void support(Class<?> type, String exp) {
        support(type, { eval(exp) })
    }

    void execute(Action action) {
        Class<?> c = action.getClass()
        while (c != Object) {
            if (_supportedActions.contains(c)) {
                // TODO mathieu where is th use of the override
                action.execute(this)
                return
            } else {
                c = c.superclass
            }
        }
        throw new ComponentException("Unsupported action '${action.class.simpleName}' on component ${this}")
    }

    static class CachedMetaData {

        @Delegate
        private MetaInfo metaInfo

        private Evaluator evaluator

        IdProvider idProvider

        MetaInfo getMetaInfo(Component c) {
            if (!metaInfo) {
                MetaInfo info = idProvider.getMetaInfos(evaluator)[0]
                if (c.class != Component) {
                    String identifyingExpr = Identifiers.getIdentifyingExpression(c.class)
                    if (!(evaluator.getBool(info.id, identifyingExpr))) {
                        Class<Component> type = Testatoo.componentTypes.find {
                            evaluator.getBool(info.id, Identifiers.getIdentifyingExpression(it))
                        }
                        throw new ComponentException("Expected a ${c.class.simpleName} for component with id '${info.id}', but was: ${type?.simpleName ?: 'unknown'}")
                    }
                }
                metaInfo = info
            }
            return metaInfo
        }
    }

    static Component $(String jQuery, long timeout = 2000) { new Component(Testatoo.evaluator, new jQueryIdProvider(jQuery, timeout, true)) }

    private static void waitUntil(Closure c) {
        c()
        try {
            _waitUntil Testatoo.waitUntil.toMilliseconds(), 200, {
                Log.testatoo "waitUntil: ${c}"
                Blocks.run(Blocks.compose(Blocks.pending()))
            }
        } catch (TimeoutException e) {
            throw new ComponentException("${e.message}")
        }
    }

    private static <V> V _waitUntil(final long timeout, long interval, Closure<V> c) throws TimeoutException {
        Throwable ex = null
        long t = timeout
        for (; t > 0; t -= interval) {
            try {
                return c.call()
            } catch (Throwable e) {
                ex = e
            }
            Thread.sleep(interval)
        }
        throw new ComponentException("${ex.message}")
    }
}
