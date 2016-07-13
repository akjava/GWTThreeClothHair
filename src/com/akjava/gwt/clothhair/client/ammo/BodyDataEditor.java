package com.akjava.gwt.clothhair.client.ammo;

import javax.annotation.Nullable;

import com.akjava.gwt.three.client.gwt.ui.LabeledInputRangeWidget2;
import com.akjava.gwt.threeammo.client.AmmoBodyPropertyData;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.EditorDelegate;
import com.google.gwt.editor.client.ValueAwareEditor;
import com.google.gwt.user.client.ui.DoubleBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class BodyDataEditor extends VerticalPanel implements Editor<AmmoBodyPropertyData>,ValueAwareEditor<AmmoBodyPropertyData>{
		private AmmoBodyPropertyData value;
		private LabeledInputRangeWidget2 frictionEditor;
		private LabeledInputRangeWidget2 restitutionEditor;
		private BodyDampingEditor dampingEditor;
		private VerticalPanel dampingPanel;

		public VerticalPanel getDampingPanel() {
			return dampingPanel;
		}

		public AmmoBodyPropertyData getValue() {
			return value;
		}
		
		public BodyDataEditor(){
			String labelWidth="180px";
			int fontSize=14;

						HorizontalPanel frictionPanel=new HorizontalPanel();
						frictionPanel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
						add(frictionPanel);
						
						frictionEditor=new LabeledInputRangeWidget2("Friction",0,10,0.1);
						frictionPanel.add(frictionEditor);
						frictionEditor.setButtonVisible(true);
						frictionEditor.getLabel().setWidth("90px");

						HorizontalPanel restitutionPanel=new HorizontalPanel();
						restitutionPanel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
						add(restitutionPanel);
						
						restitutionEditor=new LabeledInputRangeWidget2("Restitution",0,10,0.1);
						restitutionPanel.add(restitutionEditor);
						restitutionEditor.setButtonVisible(true);
						restitutionEditor.getLabel().setWidth("90px");

						
						dampingPanel = new VerticalPanel();
						//dampingPanel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
						add(dampingPanel);
						Label dampingLabel=new Label("Damping");
						dampingLabel.getElement().getStyle().setFontSize(fontSize, Unit.PX);
						dampingLabel.setWidth(labelWidth);
						dampingPanel.add(dampingLabel);
						dampingEditor=new BodyDampingEditor();
						dampingPanel.add(dampingEditor);


		}
@Override
			public void setDelegate(EditorDelegate<AmmoBodyPropertyData> delegate) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void flush() {
				value.setFriction(frictionEditor.getValue());
				value.setRestitution(restitutionEditor.getValue());
				dampingEditor.flush();

			}

			@Override
			public void onPropertyChange(String... paths) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void setValue(@Nullable AmmoBodyPropertyData value) {
				this.value=value;
				if(value==null){
					frictionEditor.setEnabled(false);
					restitutionEditor.setEnabled(false);
					dampingEditor.setEnabled(false);

					return;
				}else{
					frictionEditor.setEnabled(true);
					restitutionEditor.setEnabled(true);
					dampingEditor.setEnabled(true);

				}
				frictionEditor.setValue(value.getFriction());
				restitutionEditor.setValue(value.getRestitution());
				dampingEditor.setValue(value.getDamping());


			}
	}