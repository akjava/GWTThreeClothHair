package com.akjava.gwt.clothhair.client.ammo;

import javax.annotation.Nullable;

import com.akjava.gwt.threeammo.client.AmmoConstraintPropertyData;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.EditorDelegate;
import com.google.gwt.editor.client.ValueAwareEditor;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DoubleBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ConstraintDataEditor extends VerticalPanel implements Editor<AmmoConstraintPropertyData>,ValueAwareEditor<AmmoConstraintPropertyData>{
		private AmmoConstraintPropertyData value;
		
		private CheckBox useLinearReferenceFrameAEditor;
		private CheckBox disableCollisionsBetweenLinkedBodiesEditor;
		private DoubleBox frameInARelativePosRatioEditor;
		private DoubleBox frameInBRelativePosRatioEditor;
		private DampingsEditor dumpingsEditor;
		private EnableSpringsEditor enableSpringsEditor;
		private StiffnessesEditor stiffnessesEditor;
		private AngularLimitEditor angularLowerLimitEditor;
		private AngularLimitEditor angularUpperLimiEditor;
		private LinearLimitEditor linearLowerLimitEditor;
		private LinearLimitEditor linearUpperLimitEditor;

		
		public AmmoConstraintPropertyData getValue() {
			return value;
		}
		
		public ConstraintDataEditor(){
			String labelWidth="260px";
			int fontSize=14;

						HorizontalPanel useLinearReferenceFrameAPanel=new HorizontalPanel();
						useLinearReferenceFrameAPanel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
						add(useLinearReferenceFrameAPanel);
						Label useLinearReferenceFrameALabel=new Label("useLinearReferenceFrameA");
						useLinearReferenceFrameALabel.getElement().getStyle().setFontSize(fontSize, Unit.PX);
						useLinearReferenceFrameALabel.setWidth(labelWidth);
						useLinearReferenceFrameAPanel.add(useLinearReferenceFrameALabel);
						useLinearReferenceFrameAEditor=new CheckBox();
						useLinearReferenceFrameAPanel.add(useLinearReferenceFrameAEditor);


						HorizontalPanel disableCollisionsBetweenLinkedBodiesPanel=new HorizontalPanel();
						disableCollisionsBetweenLinkedBodiesPanel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
						add(disableCollisionsBetweenLinkedBodiesPanel);
						Label disableCollisionsBetweenLinkedBodiesLabel=new Label("disableCollisionsBetweenLinkedBodies");
						disableCollisionsBetweenLinkedBodiesLabel.getElement().getStyle().setFontSize(fontSize, Unit.PX);
						disableCollisionsBetweenLinkedBodiesLabel.setWidth(labelWidth);
						disableCollisionsBetweenLinkedBodiesPanel.add(disableCollisionsBetweenLinkedBodiesLabel);
						disableCollisionsBetweenLinkedBodiesEditor=new CheckBox();
						disableCollisionsBetweenLinkedBodiesPanel.add(disableCollisionsBetweenLinkedBodiesEditor);


						HorizontalPanel frameInARelativePosRatioPanel=new HorizontalPanel();
						frameInARelativePosRatioPanel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
						add(frameInARelativePosRatioPanel);
						Label frameInARelativePosRatioLabel=new Label("frameInARelativePosRatio");
						frameInARelativePosRatioLabel.getElement().getStyle().setFontSize(fontSize, Unit.PX);
						frameInARelativePosRatioLabel.setWidth(labelWidth);
						frameInARelativePosRatioPanel.add(frameInARelativePosRatioLabel);
						frameInARelativePosRatioEditor=new DoubleBox();
						frameInARelativePosRatioPanel.add(frameInARelativePosRatioEditor);


						HorizontalPanel frameInBRelativePosRatioPanel=new HorizontalPanel();
						frameInBRelativePosRatioPanel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
						add(frameInBRelativePosRatioPanel);
						Label frameInBRelativePosRatioLabel=new Label("frameInBRelativePosRatio");
						frameInBRelativePosRatioLabel.getElement().getStyle().setFontSize(fontSize, Unit.PX);
						frameInBRelativePosRatioLabel.setWidth(labelWidth);
						frameInBRelativePosRatioPanel.add(frameInBRelativePosRatioLabel);
						frameInBRelativePosRatioEditor=new DoubleBox();
						frameInBRelativePosRatioPanel.add(frameInBRelativePosRatioEditor);

						
						HorizontalPanel enableSpringsPanel=new HorizontalPanel();
						enableSpringsPanel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
						add(enableSpringsPanel);
						Label enableSpringsLabel=new Label("EnableSprings");
						enableSpringsLabel.getElement().getStyle().setFontSize(fontSize, Unit.PX);
						enableSpringsLabel.setWidth(labelWidth);
						enableSpringsPanel.add(enableSpringsLabel);
						
						enableSpringsEditor=new EnableSpringsEditor();
						add(enableSpringsEditor);
						

						HorizontalPanel dumpingsPanel=new HorizontalPanel();
						dumpingsPanel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
						add(dumpingsPanel);
						Label dumpingsLabel=new Label("Dampings");
						dumpingsLabel.getElement().getStyle().setFontSize(fontSize, Unit.PX);
						dumpingsLabel.setWidth(labelWidth);
						dumpingsPanel.add(dumpingsLabel);
						dumpingsEditor=new DampingsEditor();
						add(dumpingsEditor);


						


						HorizontalPanel stiffnessesPanel=new HorizontalPanel();
						stiffnessesPanel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
						add(stiffnessesPanel);
						Label stiffnessesLabel=new Label("Stiffnesses");
						stiffnessesLabel.getElement().getStyle().setFontSize(fontSize, Unit.PX);
						stiffnessesLabel.setWidth(labelWidth);
						stiffnessesPanel.add(stiffnessesLabel);
						stiffnessesEditor=new StiffnessesEditor();
						add(stiffnessesEditor);


						HorizontalPanel angularLowerLimitPanel=new HorizontalPanel();
						angularLowerLimitPanel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
						add(angularLowerLimitPanel);
						Label angularLowerLimitLabel=new Label("angularLowerLimit");
						angularLowerLimitLabel.getElement().getStyle().setFontSize(fontSize, Unit.PX);
						angularLowerLimitLabel.setWidth(labelWidth);
						angularLowerLimitPanel.add(angularLowerLimitLabel);
						angularLowerLimitEditor=new AngularLimitEditor();
						add(angularLowerLimitEditor);


						HorizontalPanel angularUpperLimiPanel=new HorizontalPanel();
						angularUpperLimiPanel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
						add(angularUpperLimiPanel);
						Label angularUpperLimiLabel=new Label("angularUpperLimi");
						angularUpperLimiLabel.getElement().getStyle().setFontSize(fontSize, Unit.PX);
						angularUpperLimiLabel.setWidth(labelWidth);
						angularUpperLimiPanel.add(angularUpperLimiLabel);
						angularUpperLimiEditor=new AngularLimitEditor();
						add(angularUpperLimiEditor);


						HorizontalPanel linearLowerLimitPanel=new HorizontalPanel();
						linearLowerLimitPanel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
						add(linearLowerLimitPanel);
						Label linearLowerLimitLabel=new Label("linearLowerLimit");
						linearLowerLimitLabel.getElement().getStyle().setFontSize(fontSize, Unit.PX);
						linearLowerLimitLabel.setWidth(labelWidth);
						linearLowerLimitPanel.add(linearLowerLimitLabel);
						linearLowerLimitEditor=new LinearLimitEditor();
						add(linearLowerLimitEditor);


						HorizontalPanel linearUpperLimitPanel=new HorizontalPanel();
						linearUpperLimitPanel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
						add(linearUpperLimitPanel);
						Label linearUpperLimitLabel=new Label("linearUpperLimit");
						linearUpperLimitLabel.getElement().getStyle().setFontSize(fontSize, Unit.PX);
						linearUpperLimitLabel.setWidth(labelWidth);
						linearUpperLimitPanel.add(linearUpperLimitLabel);
						linearUpperLimitEditor=new LinearLimitEditor();
						add(linearUpperLimitEditor);


		}
		
			@Override
			public void setDelegate(EditorDelegate<AmmoConstraintPropertyData> delegate) {
				
				
				
				
				
			}

			@Override
			public void flush() {
				value.setUseLinearReferenceFrameA(useLinearReferenceFrameAEditor.getValue());
				value.setDisableCollisionsBetweenLinkedBodies(disableCollisionsBetweenLinkedBodiesEditor.getValue());
				value.setFrameInARelativePosRatio(frameInARelativePosRatioEditor.getValue());
				value.setFrameInBRelativePosRatio(frameInBRelativePosRatioEditor.getValue());
				
				enableSpringsEditor.flush();
				value.setEnableSprings(enableSpringsEditor.getValue());
				
				dumpingsEditor.flush();
				stiffnessesEditor.flush();
				
				angularLowerLimitEditor.flush();
				angularUpperLimiEditor.flush();
				linearLowerLimitEditor.flush();
				linearUpperLimitEditor.flush();
				
				/*
				value.setDumpings(dumpingsEditor.getValue());
				value.setStiffnesses(stiffnessesEditor.getValue());
				value.setAngularLowerLimit(angularLowerLimitEditor.getValue());
				value.setAngularUpperLimi(angularUpperLimiEditor.getValue());
				value.setLinearLowerLimit(linearLowerLimitEditor.getValue());
				value.setLinearUpperLimit(linearUpperLimitEditor.getValue());
				*/

			}

			@Override
			public void onPropertyChange(String... paths) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void setValue(@Nullable AmmoConstraintPropertyData value) {
				this.value=value;
				if(value==null){
					useLinearReferenceFrameAEditor.setEnabled(false);
					disableCollisionsBetweenLinkedBodiesEditor.setEnabled(false);
					frameInARelativePosRatioEditor.setEnabled(false);
					frameInBRelativePosRatioEditor.setEnabled(false);
					dumpingsEditor.setEnabled(false);
					enableSpringsEditor.setEnabled(false);
					stiffnessesEditor.setEnabled(false);
					angularLowerLimitEditor.setEnabled(false);
					angularUpperLimiEditor.setEnabled(false);
					linearLowerLimitEditor.setEnabled(false);
					linearUpperLimitEditor.setEnabled(false);

				}else{
					useLinearReferenceFrameAEditor.setEnabled(true);
					disableCollisionsBetweenLinkedBodiesEditor.setEnabled(true);
					frameInARelativePosRatioEditor.setEnabled(true);
					frameInBRelativePosRatioEditor.setEnabled(true);
					dumpingsEditor.setEnabled(true);
					enableSpringsEditor.setEnabled(true);
					stiffnessesEditor.setEnabled(true);
					angularLowerLimitEditor.setEnabled(true);
					angularUpperLimiEditor.setEnabled(true);
					linearLowerLimitEditor.setEnabled(true);
					linearUpperLimitEditor.setEnabled(true);
				}
				
				useLinearReferenceFrameAEditor.setValue(value.isUseLinearReferenceFrameA());
				disableCollisionsBetweenLinkedBodiesEditor.setValue(value.isDisableCollisionsBetweenLinkedBodies());
				frameInARelativePosRatioEditor.setValue(value.getFrameInARelativePosRatio());
				frameInBRelativePosRatioEditor.setValue(value.getFrameInBRelativePosRatio());
				
				enableSpringsEditor.setValue(value.getEnableSprings());
				dumpingsEditor.setValue(value.getDampings());
				stiffnessesEditor.setValue(value.getStiffnesses());
				
				angularLowerLimitEditor.setValue(value.getAngularLowerLimit());
				angularUpperLimiEditor.setValue(value.getAngularUpperLimit());
				
				linearLowerLimitEditor.setValue(value.getLinearLowerLimit());
				linearUpperLimitEditor.setValue(value.getLinearUpperLimit());
				
				
			}
	}

