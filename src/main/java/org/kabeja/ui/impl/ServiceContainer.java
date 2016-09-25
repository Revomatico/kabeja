/*
   Copyright 2008 Simon Mieth

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
package org.kabeja.ui.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.kabeja.processing.ProcessingManager;
import org.kabeja.ui.Application;
import org.kabeja.ui.ApplicationMenuBar;
import org.kabeja.ui.Component;
import org.kabeja.ui.ProcessingUIComponent;
import org.kabeja.ui.ServiceManager;
import org.kabeja.ui.Serviceable;
import org.kabeja.ui.Startable;


public class ServiceContainer implements ServiceManager, Application {
    protected List<Component> components = new ArrayList<Component>();
    protected ProcessingManager manager;

    public ServiceContainer() {
        this.addComponent(this);
    }

    public void addComponent(Component c) {
        this.components.add(c);
    }

    @Override
    public Component[] getServiceComponents(String service) {
        List<Component> l = this.getServiceComponentsByServiceField(service);
        return l.toArray(new Component[l.size()]);
    }

    protected List<Component> getServiceComponentsByServiceField(String service) {
        List<Component> list = new ArrayList<Component>();
        Iterator<Component> i = this.components.iterator();

        try {
            Class<?> serviceClass = this.getClass().getClassLoader()
                                     .loadClass(service);

            while (i.hasNext()) {
                Component obj = i.next();

                if (serviceClass.isInstance(obj)) {
                    list.add(obj);
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return list;
    }

    public void setupComponents() {
        Iterator<Component> i = this.components.iterator();

        while (i.hasNext()) {
            Object obj = i.next();

            if (obj instanceof Serviceable) {
                ((Serviceable) obj).setServiceManager(this);
            }
        }

        ApplicationMenuBar mbar = ((ApplicationMenuBar) this.getServiceComponents(ApplicationMenuBar.SERVICE)[0]);
        mbar.setAction(ApplicationMenuBar.MENU_ID_FILE,
            new OpenProcessingAction(this));
    }

    @Override
    public void start() {
        this.setupComponents();

        Iterator<Component> i = this.components.iterator();

        while (i.hasNext()) {
            Object obj = i.next();

            if (obj instanceof Startable) {
                ((Startable) obj).start();
            }
        }
    }

    @Override
    public void stop() {
        Iterator<Component> i = this.components.iterator();

        while (i.hasNext()) {
            Object obj = i.next();

            if (obj instanceof Startable) {
                ((Startable) obj).stop();
            }
        }

        System.exit(0);
    }

    public void setProcessingManager(ProcessingManager manager) {
        this.manager = manager;

        Iterator<Component> i = this.components.iterator();

        while (i.hasNext()) {
            Object obj = i.next();

            if (obj instanceof ProcessingUIComponent) {
                ((ProcessingUIComponent) obj).setProcessingManager(manager);
            }
        }
    }
}
