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

import java.util.Map.Entry;

import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import eu.maxschuster.vaadin.localstorage.LocalStorage;
import eu.maxschuster.vaadin.localstorage.LocalStorage.ReadyEvent;
import eu.maxschuster.vaadin.localstorage.LocalStorage.ReadyListener;
import eu.maxschuster.vaadin.localstorage.LocalStorage.RefreshEvent;
import eu.maxschuster.vaadin.localstorage.LocalStorage.RefreshListener;
import eu.maxschuster.vaadin.localstorage.LocalStorage.UpdateEvent;
import eu.maxschuster.vaadin.localstorage.LocalStorage.UpdateListener;

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
		LocalStorage localStorage = new LocalStorage(this);
		localStorage.setLiveUpdate(false);
		localStorage.addReadyListener(new ReadyListener() {
			
			@Override
			public void onReady(ReadyEvent event) {
				StringBuilder a = new StringBuilder();
				for (Entry<String, String> e : event.getLocalStorage().getItems().entrySet()) {
					a.append(e.getKey()).append(" = ").append(e.getValue()).append('\n');
				}
				Notification.show(a.toString());
				event.getLocalStorage().removeItem("SESSIONID2");
				event.getLocalStorage().setItem("SESSIONID3", "123Schlachmichtod");
				event.getLocalStorage().refresh();
			}
		});
		localStorage.addRefreshListener(new RefreshListener() {
			
			@Override
			public void onRefresh(RefreshEvent event) {
				StringBuilder a = new StringBuilder();
				for (Entry<String, String> e : event.getLocalStorage().getItems().entrySet()) {
					a.append(e.getKey()).append(" = ").append(e.getValue()).append('\n');
				}
				Notification.show(a.toString());
			}
		});
		localStorage.addUpdateListener(new UpdateListener() {
			
			@Override
			public void onUpdate(UpdateEvent event) {
				System.out.println("update item " + event.getKey() + " " + event.getOldValue() + " " + event.getNewValue());
			}
		});
		
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