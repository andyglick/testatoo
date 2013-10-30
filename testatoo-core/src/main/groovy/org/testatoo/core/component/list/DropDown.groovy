package org.testatoo.core.component.list

import org.testatoo.core.component.Component
import org.testatoo.core.component.Type
import org.testatoo.core.state.*

/**
 * @author David Avenante (d.avenante@gmail.com)
 */
class DropDown extends Component {
    DropDown() {
        type Type.DROPDOWN
        support Enabled, Disabled, Available, Missing, Hidden, Visible
    }
}