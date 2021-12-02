package be.algielen;

import java.util.List;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

/**
 * This UI is the application entry point. A UI may either represent a browser window
 * (or tab) or some part of a html page where a Vaadin application is embedded.
 * <p>
 * The UI is initialized using {@link #init(VaadinRequest)}. This method is intended to be
 * overridden to add component to the user interface and initialize non-component functionality.
 */
@Theme("mytheme")
public class MyUI extends UI {

	private CustomerService service = CustomerService.getInstance();

	private Grid<Customer> grid = new Grid<>();

	private CustomerForm form = new CustomerForm(this);

	@Override
	protected void init(VaadinRequest vaadinRequest) {
		final VerticalLayout layout = new VerticalLayout();

		HorizontalLayout toolbar = new HorizontalLayout();
		CssLayout filter = new CssLayout();
		filter.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);

		TextField searchField = setupSearchField();

		Button clear = new Button(VaadinIcons.CLOSE);
		clear.addClickListener(e -> searchField.clear());

		setupGrid();

		updateList();

		Button addCustomerButton = setupAddButton();

		filter.addComponents(searchField, clear);

		HorizontalLayout main = new HorizontalLayout(grid, form);
		form.setVisible(false);
		main.setSizeFull();
		grid.setSizeFull();
		main.setExpandRatio(grid, 1);

		toolbar.addComponents(filter, addCustomerButton);
		layout.addComponents(toolbar, main);
		setContent(layout);
	}

	private TextField setupSearchField() {
		TextField searchField = new TextField();
		searchField.setPlaceholder("Filter the result");
		searchField.addValueChangeListener(e -> filterList(searchField.getValue()));
		searchField.setValueChangeMode(ValueChangeMode.LAZY);
		return searchField;
	}

	private Button setupAddButton() {
		Button addCustomerButton = new Button("Add customer");
		addCustomerButton.addClickListener(event -> {
			grid.asSingleSelect().clear();
			form.setCustomer(new Customer());
		});
		addCustomerButton.setStyleName(ValoTheme.BUTTON_FRIENDLY);
		return addCustomerButton;
	}

	private void setupGrid() {
		grid.addColumn(Customer::getFirstName).setCaption("First name");
		grid.addColumn(Customer::getLastName).setCaption("Last name");
		grid.addColumn(Customer::getEmail).setCaption("Email");
		grid.addColumn(Customer::getBirthDate).setCaption("Date of birth");

		grid.asSingleSelect().addValueChangeListener(event -> {
			if (event.getValue() == null) {
				form.setVisible(false);
			} else {
				form.setCustomer(event.getValue());
			}
		});
	}

	void updateList() {
		List<Customer> customers = service.findAll();
		grid.setItems(customers);
	}

	void filterList(String filter) {
		List<Customer> customers = service.findAll(filter);
		grid.setItems(customers);
	}

	@WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
	@VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
	public static class MyUIServlet extends VaadinServlet {
	}

}
