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
package org.kabeja.svg.action;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;

import org.apache.batik.swing.gvt.Interactor;
import org.w3c.dom.svg.SVGDocument;


public class ToggleInteractorActionAdapter extends AbstractAction
    implements Interactor, ItemListener, SVGDocumentAction, GroupAction {
    protected Interactor interactor;
    protected boolean selected = false;
    protected int controlKey = 0;
    protected SVGDocument doc;

    public ToggleInteractorActionAdapter(Interactor interactor, int controlKey) {
        super();
        this.interactor = interactor;
        this.controlKey = controlKey;
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            this.selected = true;
        } else {
            this.selected = false;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        this.interactor.mouseDragged(e);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        this.interactor.mouseMoved(e);
    }

    @Override
    public boolean endInteraction() {
        return this.interactor.endInteraction();
    }

    @Override
    public boolean startInteraction(InputEvent ie) {
        int mods = ie.getModifiers();
        boolean b = ((ie.getID() == MouseEvent.MOUSE_PRESSED) &&
            ((mods & InputEvent.BUTTON1_MASK) != 0) &&
            ((mods & this.controlKey) != 0)) ||
            ((ie.getID() == MouseEvent.MOUSE_PRESSED) && this.selected &&
            ((mods & InputEvent.BUTTON1_MASK) != 0));

        return b;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        this.interactor.mouseClicked(e);
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        this.interactor.mouseEntered(e);
    }

    @Override
    public void mouseExited(MouseEvent e) {
        this.interactor.mouseExited(e);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        this.interactor.mousePressed(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        this.interactor.mouseReleased(e);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        this.interactor.keyPressed(e);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        this.interactor.keyReleased(e);
    }

    @Override
    public void keyTyped(KeyEvent e) {
        this.interactor.keyTyped(e);
    }

    @Override
    public void setDocument(SVGDocument doc) {
        this.doc = doc;
    }
}
