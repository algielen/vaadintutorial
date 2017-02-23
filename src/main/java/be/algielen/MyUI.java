package be.algielen;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.HasComponents;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
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

	@Override
	protected void init(VaadinRequest vaadinRequest) {
		final VerticalLayout layout = new VerticalLayout();

		final TextField name = new TextField();
		name.setCaption("Type your name here:");

		Button button = new Button("Click Me");
		button.addClickListener(e -> {
			layout.addComponent(new Label("Thanks " + name.getValue() + ", it works!"));
		});

		layout.addComponents(name, button);

		Button addNewButton = new Button("Click me if you dare");
		addNewButton.addClickListener(new DuplicatingClickListener());

		layout.addComponent(addNewButton);

		setContent(layout);
	}

	@WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
	@VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
	public static class MyUIServlet extends VaadinServlet {
	}

	private class DuplicatingClickListener implements Button.ClickListener {

		@Override
		public void buttonClick(Button.ClickEvent clickEvent) {
			Button sourceButton = clickEvent.getButton();
			Button newButton = new Button(sourceButton.getCaption());
			newButton.addClickListener(this);
			if (clickEvent.isAltKey()) {
				HasComponents parent = sourceButton.getParent();
				if (parent instanceof VerticalLayout) {
					VerticalLayout verticalLayout = (VerticalLayout) parent;
					int originalIndex = verticalLayout.getComponentIndex(sourceButton);
					verticalLayout.removeComponent(sourceButton);
					HorizontalLayout horizontalLayout = new HorizontalLayout();
					horizontalLayout.addComponents(sourceButton, newButton);
					verticalLayout.addComponent(horizontalLayout, originalIndex);
				} else if (parent instanceof HorizontalLayout) {
					((HorizontalLayout) parent).addComponent(newButton);
				} else {
					System.out.println("Parent not supported : " + parent.getClass());
				}
			} else {
                VerticalLayout ancestor = sourceButton.findAncestor(VerticalLayout.class);
                ancestor.addComponent(newButton);
            }
		}
	}
}
