/*
 * eu.maxschuster.vaadin.localstorage.test.LocalStorageUI.java
 * 
 * Copyright 2013 Max Schuster
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.maxschuster.vaadin.localstorage.test;

import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import eu.maxschuster.vaadin.localstorage.LocalStorage;
import eu.maxschuster.vaadin.localstorage.shared.LocalStorageItem;
import eu.maxschuster.vaadin.localstorage.shared.LocalStorageItemCallback;

/**
 * Test {@link UI}
 * @author Max Schuster <dev@maxschutser.eu>
 */
@SuppressWarnings("serial")
@Theme("localstorage")
@Push
public class LocalStorageUI extends UI {

	@Override
	protected void init(VaadinRequest request) {
		final LocalStorage localStorage = LocalStorage.getCurrent();
		
		
		
		LocalStorageItemCallback cb = new LocalStorageItemCallback() {
			
			/*
			 * (non-Javadoc)
			 * @see eu.maxschuster.vaadin.localstorage.shared.LocalStorageItemCallback#onSussess(java.lang.String, java.lang.String, java.lang.String)
			 */
			@Override
			public void onSussess(LocalStorageItem item) {
				System.out.println(item);
			}
			
			/*
			 * (non-Javadoc)
			 * @see eu.maxschuster.vaadin.localstorage.shared.LocalStorageItemCallback#onError()
			 */
			@Override
			public void onError() {
				System.out.println("ERROR");
			}
			
		};
		
		localStorage.setItem("test", "Lorem ipsum dolor sit amet, consectetur adipiscing elit.", cb);
		
		localStorage.setItem("test", "Duis commodo.", cb);
		
		localStorage.getItem("test", cb);
		
		localStorage.clear();
		
		final VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		setContent(layout);

		Button button = new Button("Click Me");
		button.addClickListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				layout.addComponent(new Label("Thank you for clicking"));
			}
		});
		layout.addComponent(button);
		
		
	}

}