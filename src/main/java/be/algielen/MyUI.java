package be.algielen;

import java.util.List;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

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

	@Override
	protected void init(VaadinRequest vaadinRequest) {
		final VerticalLayout layout = new VerticalLayout();

		Button refresh = new Button("Refresh");
		refresh.addClickListener(e -> updateList());

		TextField searchfield = new TextField("Search");
		searchfield.addValueChangeListener(e -> filterList(searchfield.getValue()));

		grid.addColumn(Customer::getFirstName).setCaption("First name");
		grid.addColumn(Customer::getLastName).setCaption("Last name");
		grid.addColumn(Customer::getEmail).setCaption("Email");

		updateList();

		layout.addComponents(refresh, searchfield, grid);
		setContent(layout);
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
