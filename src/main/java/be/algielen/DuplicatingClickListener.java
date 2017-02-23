package be.algielen;

import com.vaadin.ui.Button;
import com.vaadin.ui.HasComponents;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;

class DuplicatingClickListener implements Button.ClickListener {

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
