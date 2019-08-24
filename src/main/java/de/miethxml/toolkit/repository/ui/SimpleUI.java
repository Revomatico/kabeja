/*
Copyright 2005 Simon Mieth

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
package de.miethxml.toolkit.repository.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTree;



import de.miethxml.toolkit.io.DefaultFileModelHandler;
import de.miethxml.toolkit.io.FileModelException;
import de.miethxml.toolkit.io.FileModelHandler;
import de.miethxml.toolkit.io.FileModelManager;
import de.miethxml.toolkit.io.VFSFileModelHandler;
import de.miethxml.toolkit.repository.RepositoryModelImpl;
import de.miethxml.toolkit.ui.PanelFactory;


/**
 * This is a simple FileManagerUI-test. Do not use this.
 * 
 *@author <a href="mailto:simon.mieth@gmx.de">Simon Mieth </a>
 *
 */
public class SimpleUI {

	private JFrame frame;
	private JTextField left;
	private JTextField right;
	RepositoryModelImpl leftModel;
	RepositoryModelImpl rightModel;
	
	
	
	public void init(){
		frame = new JFrame("FileManager");
		
		//create the left RepositoryModel
		FileModelManager manager = new FileModelManager();
	
		manager.addFileModelHandler(new VFSFileModelHandler());
		manager.addFileModelHandler(new DefaultFileModelHandler());
		
		
		leftModel= new RepositoryModelImpl(manager);
		
		RepositoryViewBuilder builder = new RepositoryViewBuilder(leftModel,new JPopupMenu());
	    JTree tree = builder.getRepositoryTree(true);	
	    
	    JSplitPane sp = PanelFactory.createDefaultSplitPane();
	   
	    JPanel panel = new JPanel(new BorderLayout());
	    panel.add(new JScrollPane(tree),BorderLayout.CENTER);
	    left= new JTextField(30);
	    left.addActionListener(new ActionListener(){
	    	public void actionPerformed(ActionEvent event){
	    		  try {
	    			
	    			leftModel.setBase(left.getText());
	    		} catch (FileModelException e) {
	    			// TODO Auto-generated catch block
	    			e.printStackTrace();
	    		}
	    	}
	    });
	  
	    panel.add(left,BorderLayout.NORTH);
	    
	    
	    
	    sp.setLeftComponent(panel);
	    
	    
		//the right side
		rightModel= new RepositoryModelImpl(manager);
		builder = new RepositoryViewBuilder(rightModel,new JPopupMenu());
	    tree = builder.getRepositoryTree(true);	
	    
	    panel = new JPanel(new BorderLayout());
	    panel.add(new JScrollPane(tree),BorderLayout.CENTER);
	    right= new JTextField(30);
	    right.addActionListener(new ActionListener(){
	    	public void actionPerformed(ActionEvent event){
	    		  try {
	    			
	    			rightModel.setBase(right.getText());
	    		} catch (FileModelException e) {
	    			// TODO Auto-generated catch block
	    			e.printStackTrace();
	    		}
	    	}
	    });
	
	    panel.add(right,BorderLayout.NORTH);
	    sp.setRightComponent(panel);
	  
		
		frame.getContentPane().add(sp,BorderLayout.CENTER);
		frame.setSize(800,600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		
	}
	
	
	public static void main(String[] args){
		SimpleUI ui = new SimpleUI();
		ui.init();
	}
	
	
}
