package doc.dsl

import org.junit.Before
import org.junit.BeforeClass
import org.junit.ClassRule
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.testatoo.WebDriverConfig
import org.testatoo.bundle.html5.*
import org.testatoo.bundle.html5.heading.H1
import org.testatoo.bundle.html5.heading.H6
import org.testatoo.bundle.html5.input.*
import org.testatoo.bundle.html5.list.MultiSelect
import org.testatoo.bundle.html5.list.Select
import org.testatoo.bundle.html5.list.Ul
import org.testatoo.bundle.html5.table.Table
import org.testatoo.category.UserAgent
import org.testatoo.core.Browser
import org.testatoo.core.component.*
import org.testatoo.core.component.datagrid.Cell
import org.testatoo.core.component.datagrid.Column
import org.testatoo.core.component.datagrid.DataGrid
import org.testatoo.core.component.datagrid.Row
import org.testatoo.core.component.field.*

import static org.testatoo.WebDriverConfig.BASE_URL
import static org.testatoo.core.Testatoo.*

/**
 * @author David Avenante (d.avenante@gmail.com)
 */
@RunWith(JUnit4)
@Category(UserAgent.All)
class ComponentsTest {
    @ClassRule
    public static WebDriverConfig driver = new WebDriverConfig()

    @BeforeClass
    static void before() {
        visit BASE_URL + 'components.html'
    }

    @Before
    void setUp() {
        Browser.refresh()
    }

    @Test
    void should_have_expected_properties_and_states_supported_by_component() {
        Component component = $('#button') as Component
        // tag::component[]
        component.should {
            be available
            be enabled
            be visible
        }

        component = $('#submit') as Component
        component.should { be disabled }

        component = $('#hidden_panel') as Component
        component.should { be hidden }

        component = $('#non_existing_id') as Component
        component.should { be missing }
        // end::component[]
    }

    @Test
    void should_have_expected_properties_and_states_supported_by_button() {
        Button button = $('#btn') as Button
        // tag::button[]
        button.should { have text('My Button Text') }
        // end::button[]
    }

    @Test
    void should_have_expected_properties_and_states_supported_by_checkbox() {
        CheckBox checkbox = $('#checkbox') as CheckBox
        // tag::checkbox[]
        checkbox.should {
            have label('Check me out')
            be unchecked
        }

        check checkbox
        checkbox.should { be checked }

        uncheck checkbox
        checkbox.should { be unchecked }
        // end::checkbox[]
    }

    @Test
    void should_have_expected_properties_and_states_supported_by_radio() {
        Radio checked_radio = $('#radio_1') as Radio
        Radio unchecked_radio = $('#radio_2') as Radio
        // tag::radio[]
        checked_radio.should {
            have label('Radio checked')
            be checked
        }

        unchecked_radio.should {
            have label('Radio unchecked')
            be unchecked
        }

        check unchecked_radio

        unchecked_radio.should { be checked }
        checked_radio.should { be unchecked }
        // end::radio[]
    }

    @Test
    void should_have_expected_properties_and_states_supported_by_dropdown() {
        Dropdown os_list = $('#os') as Select
        // tag::dropdown[]
        os_list.should {
            have label('OS')
            have 8.items
            have selectedItem('None')
            have items('None', 'Ubuntu', 'Fedora', 'Gentoo', 'XP', 'Vista', 'FreeBSD', 'OpenBSD')
            have 3.groups
            have groups('linux', 'win32', 'BSD')
        }

        on os_list select 'Ubuntu'
        os_list.should { have selectedItem('Ubuntu') }
        // end::dropdown[]

        Item gentoo = os_list.item('Gentoo')
        on os_list select gentoo
        os_list.should { have selectedItem(gentoo) }

        Item[] linux_dist = new Item[os_list.items().size()]
        os_list.should { have items(os_list.items().toArray(linux_dist))}

        Group[] linux_family = new Group[os_list.groups().size()]
        os_list.should { have groups(os_list.groups().toArray(linux_family))}
    }

    @Test
    void should_have_expected_properties_and_states_supported_by_group() {
        Dropdown os_list = $('#os') as Select
        // tag::group[]
        Group linux_group = os_list.group('linux') // Or os_list.groups[0]

        linux_group.should {
            have value('linux')
            have items('Ubuntu', 'Fedora', 'Gentoo')
        }
        // end::group[]
    }

    @Test
    void should_have_expected_properties_and_states_supported_by_listbox() {
        ListBox cities = $('#cities') as MultiSelect
        // tag::listbox[]
        cities.should {
            have label('Cities list')
            have 6.items
            have items('Montreal', 'Quebec', 'Montpellier', 'New York', 'Casablanca', 'Munich')
            have selectedItems('Montreal')

            have 3.visibleItems     // See the first image at the begin of this section
        }

        on cities select 'Montpellier', 'New York'
        cities.should { have selectedItems('Montreal', 'Montpellier', 'New York') }

        // Or
        Item montreal = cities.item('Montreal')
        Item montpellier = cities.item('Montpellier')
        Item ny = cities.item('New York')
        cities.should { have selectedItems(montreal, montpellier, ny) }
        // end::listbox[]
    }

    @Test
    void should_have_expected_properties_and_states_supported_by_item() {
        Dropdown os_list = $('#os') as Select
        // tag::item[]
        Item os = os_list.item('Gentoo')  // Or os_list.items[1]
        os.should {
            have value('Gentoo')
            be unselected
        }

        select os
        os.should { be selected }

        ListBox cities = $('#cities') as MultiSelect
        Item city = cities.item('Montpellier')
        city.should {
            have value('Montpellier')
            be unselected
        }

        select city
        city.should { be selected }
        // end::item[]
    }

    @Test
    void should_have_expected_properties_and_states_supported_by_textfield() {
        TextField textfield = $('#text_field') as InputTypeText
        // tag::textfield[]
        textfield.should {
            be empty
            be required
            have placeholder('Placeholder')
            have length(20)
        }

        fill textfield with '1234'

        textfield.should {
            be filled
            have value('1234')
        }
        // end::textfield[]
    }

    @Test
    void should_have_expected_properties_and_states_supported_by_datefield() {
        DateField date = $('#date_field') as InputTypeDate
        // tag::datefield[]
        date.should {
            be inRange
            have value('')
            have step(0)
            have maximum('2012-06-25')
            have minimum('2011-08-13')
        }

        set date to '2011-06-26'
        date.should { have value('2011-06-26') }
        // end::datefield[]
    }

    @Test
    void should_have_expected_properties_and_states_supported_by_numberfield() {
        NumberField number = $('#number_field') as InputTypeNumber
        // tag::numberfield[]
        number.should {
            have minimum(0)
            have maximum(64)
            have step(8)
            have value(2)
            be inRange
        }

        fill number with 150
        number.should { be outOfRange }
        // end::numberfield[]
    }

    @Test
    void should_have_expected_properties_and_states_supported_by_rangefield() {
        RangeField range = $('#range_field') as InputTypeRange
        // tag::rangefield[]
        range.should {
            have minimum(0)
            have maximum(50)
            have value(10)
            have step(5)
            is inRange
        }

        set range to 40

        range.should { have value(40) }
        // end::rangefield[]
    }

    @Test
    void should_have_expected_properties_and_states_supported_by_form() {
        Form form = $('#form') as org.testatoo.bundle.html5.Form
        EmailField email_field = $('#email') as InputTypeEmail
        PasswordField password_field = $('#password') as InputTypePassword
        // tag::form[]
        email_field.should {
            have focus
            be focused // Or
            be optional

        }
        password_field.should { be required }

        form.should {
            contain(email_field, password_field)
            be invalid
        }

        fill email_field with 'invalid_email'
        fill password_field with 'a password'

        form.should { be invalid }

        fill email_field with 'valid@email.org'

        form.should { be valid }
        // end::form[]
    }

    @Test
    void should_have_expected_properties_and_states_supported_by_heading() {
        Heading first_heading = $('#h1') as H1
        Heading last_heading = $('#h6') as H6
        // tag::heading[]
        first_heading.should { have text('heading 1') }
        last_heading.should { have text('heading 6') }
        // end::heading[]
    }

    @Test
    void should_have_expected_properties_and_states_supported_by_image() {
        Image image = $('#image') as Img
        // tag::image[]
        image.should { have reference(BASE_URL + 'img/Montpellier.jpg') }
        // end::image[]
    }

    @Test
    void should_have_expected_properties_and_states_supported_by_link() {
        Link link = $('#link') as A
        // tag::link[]
        link.should {
            have text('Link to dsl page')
            have reference(BASE_URL + 'dsl.html')
        }
        // end::link[]
    }

    @Test
    void should_have_expected_properties_and_states_supported_by_listview() {
        ListView listview = $('#unordered_list') as Ul
        // tag::listview[]
        listview.should {
            have 5.items
            have items('Item 1', 'Item 2', 'Item 3', 'Item 4', 'Item 4')
        }
        // end::listview[]
    }

    @Test
    void should_have_expected_properties_and_states_supported_by_panel() {
        Panel panel = $('#panel') as Div
        try {
            // tag::panel[]
            panel.should { have title('My panel title') }
            // end::panel[]
        } catch (AssertionError e) {
            assert e.message != null // The title is not available in Html (Div)
        }
    }

    @Test
    void should_have_expected_properties_and_states_supported_by_datagrid() {
        DataGrid datagrid = $('#data_grid') as Table
        // tag::datagrid[]
        datagrid.should {
            have 4.columns
            have columns('', 'Column 1 title', 'Column 2 title', 'Column 3 title')

            have 4.rows
            have rows('Row 1', 'Row 2', 'Row 3', 'Row 4')
        }
        // end::datagrid[]

        // tag::column[]
        Column column = datagrid.column('Column 2 title') // Or datagrid.columns[2]
        column.should {
            have title('Column 2 title')

            have 4.cells
            have cells('cell 12', 'cell 22', 'cell 32', 'cell 42')
        }
        // end::column[]

        Column[] _columns = new Column[datagrid.columns().size()]
        datagrid.should { have columns(datagrid.columns().toArray(_columns))}

        // tag::row[]
        Row row = datagrid.row('Row 3') // Or datagrid.row[2]
        row.should {
            have title('Row 3')

            have 3.cells
            have cells('cell 31', 'cell 32', 'cell 33')
        }
        // end::row[]

        Row[] _rows = new Row[datagrid.rows().size()]
        datagrid.should { have rows(datagrid.rows().toArray(_rows))}

        // tag::cell[]
        datagrid.rows()[2].cells()[1].should {
            have value('cell 32')
        }
        // end::cell[]

        Cell[] _cells = new Cell[datagrid.rows()[0].cells().size()]
        datagrid.rows()[0].should { have cells(datagrid.rows()[0].cells().toArray(_cells))}
    }
}