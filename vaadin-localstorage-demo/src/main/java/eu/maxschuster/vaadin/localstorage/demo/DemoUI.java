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

package eu.maxschuster.vaadin.localstorage.demo;

import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import eu.maxschuster.vaadin.localstorage.LocalStorage;
import eu.maxschuster.vaadin.localstorage.LocalStorage.ItemUpdateListener;
import eu.maxschuster.vaadin.localstorage.LocalStorageItem;
import eu.maxschuster.vaadin.localstorage.LocalStorageItemCallback;
import eu.maxschuster.vaadin.localstorage.LocalStorage.ItemUpdateEvent;

/**
 * Test {@link UI}
 * @author Max Schuster
 */
@SuppressWarnings("serial")
@Push
@Theme("runo")
public class DemoUI extends UI {
	
	private final TextArea log = new TextArea("Log:", "");

	@Override
	protected void init(VaadinRequest request) {
		
		final LocalStorageItemCallback callback = new LocalStorageItemCallback() {
			
			/*
			 * (non-Javadoc)
			 * @see eu.maxschuster.vaadin.localstorage.shared.LocalStorageItemCallback#onSussess(java.lang.String, java.lang.String, java.lang.String)
			 */
			@Override
			public void onSuccess(LocalStorageItem item) {
				appendToLog("Success:\n" + item);
			}
			
			/*
			 * (non-Javadoc)
			 * @see eu.maxschuster.vaadin.localstorage.shared.LocalStorageItemCallback#onError()
			 */
			@Override
			public void onError(String key) {
				appendToLog("ERROR for item \"" + key + "\" (simulateNotSupported=" +
						LocalStorage.getCurrent().isSimulateNotSupported() + 
					")"
				);
			}
			
		};
		
		final ItemUpdateListener itemUpdateListener = new ItemUpdateListener() {
			
			@Override
			public void onUpdate(ItemUpdateEvent event) {
				appendToLog("Update (type=" + event.getType() + "):\n" + event.getItem());
			}
			
		};
		
		
		
		final VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		layout.setSizeFull();
		setContent(layout);
		
		final HorizontalLayout topLayout = new HorizontalLayout();
		topLayout.setSpacing(true);
		layout.addComponent(topLayout);
		layout.setExpandRatio(topLayout, 0);

		Button button = new Button("Start", new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				
				appendToLog("Start localStorage test:");
				
				LocalStorage localStorage = LocalStorage.getCurrent();
				
				localStorage.setItem("remove", "REMOVE ME!");
				
				localStorage.setItem("test", "Lorem ipsum dolor sit amet, consectetur adipiscing elit.", callback);
				
				localStorage.removeItem("remove");
				
				localStorage.setItem("test", "Duis commodo.", callback);
				
				localStorage.getItem("test", callback);
				
				localStorage.getItem("remove", callback);
				
			}
		});
		topLayout.addComponent(button);
		
		Button clearButton = new Button("Clear localStorage", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				LocalStorage.getCurrent().clear(callback);
			}
			
		});
		
		CheckBox fireUpdateCheckBox = new CheckBox("Fire update events");
		fireUpdateCheckBox.addValueChangeListener(new ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {
				Boolean value = (Boolean) event.getProperty().getValue();
				if (value)
					LocalStorage.getCurrent().addItemUpdateListener(itemUpdateListener);
				else
					LocalStorage.getCurrent().removeItemUpdateListener(itemUpdateListener);
			}
			
		});
		
		CheckBox simulateNotSupportedCheckbox = new CheckBox("Simulate not supported");
		simulateNotSupportedCheckbox.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {
				LocalStorage.getCurrent().setSimulateNotSupported(
					(Boolean) event.getProperty().getValue());
			}
		});
		
		topLayout.addComponents(clearButton, fireUpdateCheckBox, simulateNotSupportedCheckbox);
		
		log.setSizeFull();
		log.setReadOnly(true);
		layout.addComponent(log);
		layout.setExpandRatio(log, 1);
		
	}
	
	private void appendToLog(final String string) {
		getUI().access(new Runnable() {
			@Override
			public void run() {
				log.setReadOnly(false);
				log.setValue(
					new StringBuilder(log.getValue())
						.append(string)
						.append("\n\n")
						.toString()
				);
				log.setCursorPosition(log.getValue().length()-1);
				log.setReadOnly(true);
				getUI().focus();
			}
		});
	}

}