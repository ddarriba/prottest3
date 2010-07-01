/*
 * OptionsView.java
 *
 * Created on 1 de octubre de 2009, 15:29
 */

package es.uvigo.darwin.xprottest.compute;

import es.uvigo.darwin.xprottest.*;
import es.uvigo.darwin.xprottest.util.OptimizationStrategyWrapper;
import es.uvigo.darwin.prottest.facade.util.ProtTestParameterVO;
import static es.uvigo.darwin.prottest.global.AminoAcidApplicationGlobals.*;
import es.uvigo.darwin.prottest.global.options.ApplicationOptions;
import es.uvigo.darwin.prottest.util.exception.AlignmentParseException;
import es.uvigo.darwin.prottest.util.exception.ProtTestInternalException;
import es.uvigo.darwin.prottest.util.exception.TreeFormatException;
import java.awt.Color;
import java.awt.FileDialog;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ResourceBundle;
import javax.swing.GroupLayout;
import javax.swing.JCheckBox;
import org.jdesktop.application.Action;
import pal.tree.Tree;
import pal.alignment.Alignment;
import pal.misc.Identifier;

/**
 * The execution settings. The options chosen in this view will lead the
 * execution of ProtTest-HPC likelihood computation.
 * 
 * @author  Diego Darriba
 */
public class OptionsView extends javax.swing.JFrame {
    
    private XProtTestView mainFrame;
    
    private ArrayList<JCheckBox> cbMatrices;
    
    private String alignmentFilePath;
    
    private String treeFilePath;
    
    private Alignment alignment;
    
    private Tree userTree;
    
    private boolean running;
    
    private ResourceBundle resource;
    
    /** Creates new form OptionsView */
    public OptionsView(XProtTestView mainFrame, 
            Alignment alignment, String alignmentFilePath) {

        resource = java.util.ResourceBundle.getBundle("es/uvigo/darwin/xprottest/compute/resources/OptionsView");
        this.alignment = alignment;
        
        this.mainFrame = mainFrame;
        this.alignmentFilePath = alignmentFilePath;
        this.running = false;
        initComponents();
        msgValidate.setVisible(false);
        
        GroupLayout jPanel1Layout = new GroupLayout(subMatricesPanel);
        GroupLayout.ParallelGroup pg1 = jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING);
        GroupLayout.ParallelGroup pg2 = jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING);
        GroupLayout.SequentialGroup horizontalGroup = jPanel1Layout.createSequentialGroup()
                .addContainerGap();

        GroupLayout.ParallelGroup verticalGroup = jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING);
        GroupLayout.SequentialGroup sg1 = jPanel1Layout.createSequentialGroup()
                .addContainerGap();
        GroupLayout.SequentialGroup sg2 = jPanel1Layout.createSequentialGroup()
                .addContainerGap();
        String[] matrices = ALL_MATRICES;
        cbMatrices = new ArrayList<JCheckBox>(matrices.length);
        int i = 0;
        for (String matrix : matrices) {
            JCheckBox cbMatrix = new JCheckBox();
            cbMatrix.setText(matrix);
            cbMatrix.setSelected(true);
            cbMatrix.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    onChangeOptions(evt);
                }
            });
            cbMatrices.add(cbMatrix);
        
            if (i < matrices.length/2) {
                pg1.addComponent(cbMatrix);
            sg1.addComponent(cbMatrix)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED);
        }
            else {
                pg2.addComponent(cbMatrix);
            sg2.addComponent(cbMatrix)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED);
            }
            i++;
        }
        
        horizontalGroup.addGroup(pg1);
        horizontalGroup.addGroup(pg2);
        verticalGroup.addGroup(sg1);
        verticalGroup.addGroup(sg2);
        subMatricesPanel.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(horizontalGroup)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(verticalGroup)
        );

        lblNumModels.setText(String.valueOf(calculateNumberOfModels()));
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        subMatPanel = new javax.swing.JPanel();
        subMatricesPanel = new javax.swing.JPanel();
        distributionsPanel = new javax.swing.JPanel();
        rateVarPanel = new javax.swing.JPanel();
        lblNCat = new javax.swing.JLabel();
        cbDistGroupCat = new javax.swing.JCheckBox();
        cbDistInvariant = new javax.swing.JCheckBox();
        txtNCat = new javax.swing.JTextField();
        cbDistInvGC = new javax.swing.JCheckBox();
        msgValidate = new javax.swing.JLabel();
        empiricalFPanel = new javax.swing.JPanel();
        cbEmpiricalF = new javax.swing.JCheckBox();
        lblNumModelsLabel = new javax.swing.JLabel();
        lblNumModels = new javax.swing.JLabel();
        btnCompute = new javax.swing.JButton();
        lblStrategyMode = new javax.swing.JLabel();
        cmbStrategyMode = new javax.swing.JComboBox();
        lblUserTree = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        sliderProcessors = new javax.swing.JSlider();
        lblNumProc = new javax.swing.JLabel();
        btnSetDefault = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(es.uvigo.darwin.xprottest.XProtTestApp.class).getContext().getResourceMap(OptionsView.class);
        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(resourceMap.getString("Form.title")); // NOI18N
        setName("Form"); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                onClose(evt);
            }
        });

        subMatPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, resourceMap.getString("subMatPanel.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, XProtTestApp.FONT_PANEL_TITLE, resourceMap.getColor("subMatPanel.border.titleColor"))); // NOI18N
        subMatPanel.setName("subMatPanel"); // NOI18N

        subMatricesPanel.setToolTipText(resourceMap.getString("subMatricesPanel.toolTipText")); // NOI18N
        subMatricesPanel.setName("subMatricesPanel"); // NOI18N

        javax.swing.GroupLayout subMatricesPanelLayout = new javax.swing.GroupLayout(subMatricesPanel);
        subMatricesPanel.setLayout(subMatricesPanelLayout);
        subMatricesPanelLayout.setHorizontalGroup(
            subMatricesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 172, Short.MAX_VALUE)
        );
        subMatricesPanelLayout.setVerticalGroup(
            subMatricesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 234, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout subMatPanelLayout = new javax.swing.GroupLayout(subMatPanel);
        subMatPanel.setLayout(subMatPanelLayout);
        subMatPanelLayout.setHorizontalGroup(
            subMatPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(subMatPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(subMatricesPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        subMatPanelLayout.setVerticalGroup(
            subMatPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(subMatPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(subMatricesPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        distributionsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, resourceMap.getString("distributionsPanel.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, XProtTestApp.FONT_LABEL, resourceMap.getColor("distributionsPanel.border.titleColor"))); // NOI18N
        distributionsPanel.setName("distributionsPanel"); // NOI18N

        rateVarPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, resourceMap.getString("rateVarPanel.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, XProtTestApp.FONT_PANEL_TITLE, resourceMap.getColor("rateVarPanel.border.titleColor"))); // NOI18N
        rateVarPanel.setName("rateVarPanel"); // NOI18N

        lblNCat.setFont(lblNCat.getFont());
        lblNCat.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblNCat.setText(resourceMap.getString("lblNCat.text")); // NOI18N
        lblNCat.setName("lblNCat"); // NOI18N

        cbDistGroupCat.setFont(cbDistGroupCat.getFont());
        cbDistGroupCat.setSelected(true);
        cbDistGroupCat.setText(resourceMap.getString("cbDistGroupCat.text")); // NOI18N
        cbDistGroupCat.setToolTipText(resourceMap.getString("cbDistGroupCat.toolTipText")); // NOI18N
        cbDistGroupCat.setName("cbDistGroupCat"); // NOI18N
        cbDistGroupCat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onChangeOptions(evt);
            }
        });

        cbDistInvariant.setFont(cbDistInvariant.getFont());
        cbDistInvariant.setSelected(true);
        cbDistInvariant.setText(resourceMap.getString("cbDistInvariant.text")); // NOI18N
        cbDistInvariant.setToolTipText(resourceMap.getString("cbDistInvariant.toolTipText")); // NOI18N
        cbDistInvariant.setName("cbDistInvariant"); // NOI18N
        cbDistInvariant.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onChangeOptions(evt);
            }
        });

        txtNCat.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtNCat.setText(resourceMap.getString("txtNCat.text")); // NOI18N
        txtNCat.setToolTipText("null");
        txtNCat.setName("txtNCat"); // NOI18N

        cbDistInvGC.setFont(cbDistInvGC.getFont());
        cbDistInvGC.setSelected(true);
        cbDistInvGC.setText(resourceMap.getString("cbDistInvGC.text")); // NOI18N
        cbDistInvGC.setName("cbDistInvGC"); // NOI18N
        cbDistInvGC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onChangeOptions(evt);
            }
        });

        msgValidate.setFont(XProtTestApp.FONT_OPTION_LABEL);
        msgValidate.setForeground(XProtTestView.CRITIC_COLOR);
        msgValidate.setText(resourceMap.getString("msgValidate.text")); // NOI18N
        msgValidate.setName("msgValidate"); // NOI18N

        javax.swing.GroupLayout rateVarPanelLayout = new javax.swing.GroupLayout(rateVarPanel);
        rateVarPanel.setLayout(rateVarPanelLayout);
        rateVarPanelLayout.setHorizontalGroup(
            rateVarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(rateVarPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cbDistInvariant)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbDistGroupCat)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbDistInvGC)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblNCat)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtNCat, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, rateVarPanelLayout.createSequentialGroup()
                .addContainerGap(70, Short.MAX_VALUE)
                .addComponent(msgValidate)
                .addContainerGap())
        );
        rateVarPanelLayout.setVerticalGroup(
            rateVarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(rateVarPanelLayout.createSequentialGroup()
                .addComponent(msgValidate)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(rateVarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbDistInvariant)
                    .addComponent(cbDistGroupCat)
                    .addComponent(cbDistInvGC)
                    .addComponent(txtNCat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblNCat))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        empiricalFPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, resourceMap.getString("empiricalFPanel.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, XProtTestApp.FONT_PANEL_TITLE, resourceMap.getColor("empiricalFPanel.border.titleColor"))); // NOI18N
        empiricalFPanel.setName("empiricalFPanel"); // NOI18N

        cbEmpiricalF.setFont(cbEmpiricalF.getFont());
        cbEmpiricalF.setSelected(true);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("es/uvigo/darwin/xprottest/compute/resources/OptionsView"); // NOI18N
        cbEmpiricalF.setText(bundle.getString("cbEmpiricalF.text")); // NOI18N
        cbEmpiricalF.setToolTipText(resourceMap.getString("cbEmpiricalF.toolTipText")); // NOI18N
        cbEmpiricalF.setName("cbEmpiricalF"); // NOI18N
        cbEmpiricalF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onChangeOptions(evt);
            }
        });

        javax.swing.GroupLayout empiricalFPanelLayout = new javax.swing.GroupLayout(empiricalFPanel);
        empiricalFPanel.setLayout(empiricalFPanelLayout);
        empiricalFPanelLayout.setHorizontalGroup(
            empiricalFPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(empiricalFPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cbEmpiricalF)
                .addContainerGap(187, Short.MAX_VALUE))
        );
        empiricalFPanelLayout.setVerticalGroup(
            empiricalFPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(empiricalFPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cbEmpiricalF)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout distributionsPanelLayout = new javax.swing.GroupLayout(distributionsPanel);
        distributionsPanel.setLayout(distributionsPanelLayout);
        distributionsPanelLayout.setHorizontalGroup(
            distributionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(distributionsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(distributionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(rateVarPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(empiricalFPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        distributionsPanelLayout.setVerticalGroup(
            distributionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(distributionsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(rateVarPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(empiricalFPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        lblNumModelsLabel.setFont(XProtTestApp.FONT_OPTION_LABEL);
        lblNumModelsLabel.setForeground(resourceMap.getColor("lblNumModelsLabel.foreground")); // NOI18N
        lblNumModelsLabel.setText(resourceMap.getString("lblNumModelsLabel.text")); // NOI18N
        lblNumModelsLabel.setName("lblNumModelsLabel"); // NOI18N

        lblNumModels.setFont(XProtTestApp.FONT_OPTION_LABEL);
        lblNumModels.setForeground(resourceMap.getColor("lblNumModelsLabel.foreground")); // NOI18N
        lblNumModels.setName("lblNumModels"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(es.uvigo.darwin.xprottest.XProtTestApp.class).getContext().getActionMap(OptionsView.class, this);
        btnCompute.setAction(actionMap.get("computeLikelihood")); // NOI18N
        btnCompute.setText(resourceMap.getString("btnCompute.text")); // NOI18N
        btnCompute.setName("btnCompute"); // NOI18N

        lblStrategyMode.setFont(lblStrategyMode.getFont());
        java.util.ResourceBundle bundle1 = java.util.ResourceBundle.getBundle("es/uvigo/darwin/xprottest/resources/OptionsView"); // NOI18N
        lblStrategyMode.setText(bundle1.getString("lbl-strategy-mode")); // NOI18N
        lblStrategyMode.setName("lblStrategyMode"); // NOI18N

        cmbStrategyMode.setAction(actionMap.get("setStrategyMode")); // NOI18N
        cmbStrategyMode.setName("cmbStrategyMode"); // NOI18N

        lblUserTree.setFont(XProtTestApp.FONT_OPTION_LABEL);
        lblUserTree.setForeground(resourceMap.getColor("lblUserTree.foreground")); // NOI18N
        lblUserTree.setText(bundle.getString("msg-no-user-tree-loaded")); // NOI18N
        lblUserTree.setName("lblUserTree"); // NOI18N

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, resourceMap.getString("jPanel1.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, XProtTestApp.FONT_PANEL_TITLE, resourceMap.getColor("jPanel1.border.titleColor"))); // NOI18N
        jPanel1.setName("jPanel1"); // NOI18N

        sliderProcessors.setMinimum(1);
        sliderProcessors.setMaximum(
            Runtime.getRuntime().availableProcessors()
        );
        sliderProcessors.setValue(
            mainFrame.getFacade().getNumberOfThreads()
        );
        sliderProcessors.setPaintLabels(true);
        sliderProcessors.setPaintTicks(true);
        sliderProcessors.setSnapToTicks(true);
        sliderProcessors.setToolTipText(resourceMap.getString("sliderProcessors.toolTipText")); // NOI18N
        sliderProcessors.setName("sliderProcessors"); // NOI18N
        sliderProcessors.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sliderProcessorsStateChanged(evt);
            }
        });

        lblNumProc.setFont(lblNumProc.getFont());
        lblNumProc.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblNumProc.setText("[" + String.valueOf(sliderProcessors.getValue()) + "]");
        lblNumProc.setName("lblNumProc"); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblNumProc)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sliderProcessors, javax.swing.GroupLayout.DEFAULT_SIZE, 410, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(sliderProcessors, javax.swing.GroupLayout.DEFAULT_SIZE, 47, Short.MAX_VALUE)
                    .addComponent(lblNumProc))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        btnSetDefault.setAction(actionMap.get("restoreDefault")); // NOI18N
        btnSetDefault.setText(resourceMap.getString("btnSetDefault.text")); // NOI18N
        btnSetDefault.setToolTipText(resourceMap.getString("btnSetDefault.toolTipText")); // NOI18N
        btnSetDefault.setName("btnSetDefault"); // NOI18N

        btnCancel.setAction(actionMap.get("close")); // NOI18N
        btnCancel.setText(resourceMap.getString("btnCancel.text")); // NOI18N
        btnCancel.setToolTipText(resourceMap.getString("btnCancel.toolTipText")); // NOI18N
        btnCancel.setName("btnCancel"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(subMatPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lblUserTree, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 316, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(lblNumModelsLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 241, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblNumModels, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(cmbStrategyMode, javax.swing.GroupLayout.Alignment.LEADING, 0, 316, Short.MAX_VALUE)
                            .addComponent(distributionsPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblStrategyMode, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 316, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btnCompute)
                                .addGap(18, 18, 18)
                                .addComponent(btnSetDefault)
                                .addGap(18, 18, 18)
                                .addComponent(btnCancel)))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(subMatPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(distributionsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblNumModelsLabel)
                            .addComponent(lblNumModels, javax.swing.GroupLayout.PREFERRED_SIZE, 13, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblStrategyMode)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmbStrategyMode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblUserTree)
                        .addGap(12, 12, 12)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnCancel)
                            .addComponent(btnSetDefault)
                            .addComponent(btnCompute))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        for (int optimizeValue : OPTIMIZE_VALUES) {
            OptimizationStrategyWrapper optimizeItem = new OptimizationStrategyWrapper(OPTIMIZE_NAMES[optimizeValue], optimizeValue);
            cmbStrategyMode.addItem(optimizeItem);
        }
        cmbStrategyMode.setSelectedIndex(DEFAULT_STRATEGY_MODE);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void onClose(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_onClose
        mainFrame.unloadOptionsView(running);
    }//GEN-LAST:event_onClose

    private void onChangeOptions(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onChangeOptions
        
        lblNumModels.setText(String.valueOf(calculateNumberOfModels()));
        txtNCat.setEnabled(cbDistGroupCat.isSelected() || cbDistInvGC.isSelected());
    }//GEN-LAST:event_onChangeOptions

    private void sliderProcessorsStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sliderProcessorsStateChanged
        lblNumProc.setText(String.valueOf(sliderProcessors.getValue()));
    }//GEN-LAST:event_sliderProcessorsStateChanged
    
    private int calculateNumberOfModels() {
        int numberOfModels = 0;
        for (JCheckBox cbMatrix : cbMatrices ) {
            if (cbMatrix.isSelected())
                numberOfModels++;
        }
        int numberOfMatrices = numberOfModels;
        if (cbDistInvariant.isSelected())
            numberOfModels += numberOfMatrices;
        if (cbDistGroupCat.isSelected())
            numberOfModels += numberOfMatrices;
        if (cbDistInvGC.isSelected())
            numberOfModels += numberOfMatrices;
        if (cbEmpiricalF.isSelected())
            numberOfModels *= 2;
        return numberOfModels;
    }

    @Action
    public void computeLikelihood() {
        boolean validate = true;
        msgValidate.setVisible(false);
        Collection<String> matrices = new ArrayList<String>();
        Collection<String> distributions = new ArrayList<String>();
        int ncat = 0;
        boolean plusF;
        int strategyMode;
        for (JCheckBox cbMatrix : cbMatrices)
            if (cbMatrix.isSelected()) {
                matrices.add(cbMatrix.getText());
            }
        distributions.add("Uniform");
        if (cbDistGroupCat.isSelected())
            distributions.add("+G");
        if (cbDistInvariant.isSelected())
            distributions.add("+I");
        if (cbDistInvGC.isSelected())
            distributions.add("+I+G");
        if (cbDistGroupCat.isSelected() || cbDistInvGC.isSelected()) {
            try {
                ncat = Integer.parseInt(txtNCat.getText());
                if (ncat <= 0) {
                    msgValidate.setVisible(true);
                    validate = false;
                }
            } catch (NumberFormatException e) {
                msgValidate.setVisible(true);
                validate = false;
            }
        }
        plusF = cbEmpiricalF.isSelected();
        strategyMode = cmbStrategyMode.getSelectedIndex();
        if (validate) {
            try {
                mainFrame.getFacade().setNumberOfThreads(sliderProcessors.getValue());
                ProtTestParameterVO parameters = new ProtTestParameterVO(
                        alignmentFilePath, alignment, treeFilePath, matrices, distributions, plusF, ncat,
                        strategyMode);
                ApplicationOptions options = mainFrame.getFacade().configure(parameters);
                mainFrame.computeLikelihood(calculateNumberOfModels(), options);
                running = true;
            } catch (IOException e) {
                mainFrame.getDisplayWriter().println(e.getMessage());
            } catch (AlignmentParseException e) {
                mainFrame.getDisplayWriter().println(e.getMessage());
            } catch (ProtTestInternalException e) {
                mainFrame.getDisplayWriter().println(e.getMessage());
            }
            this.dispose();
        }
    }

    @Action
    public void setStrategyMode() {
        org.jdesktop.application.ResourceMap resourceMap 
                    = org.jdesktop.application.Application.getInstance(es.uvigo.darwin.xprottest.XProtTestApp.class)
                    .getContext().getResourceMap(OptionsView.class);
        File f = null;
        if (((OptimizationStrategyWrapper)cmbStrategyMode.getSelectedItem()).getValue()
                == OPTIMIZE_USER) {
            
            FileDialog fc = new FileDialog(this, "Load DNA alignment", FileDialog.LOAD);
	fc.setDirectory(System.getProperty("user.dir"));
	fc.setVisible(true);
		
	String dataFileName = fc.getFile();
        
//            JFileChooser fc = XProtTestApp.createFileChooser(resourceMap.getString(
//            "loadTree.dialogTitle"));
//            int option = fc.showOpenDialog(this);
//
//            if (JFileChooser.APPROVE_OPTION == option) {
        if (dataFileName != null) {
                try {
                    f = new File(fc.getDirectory() + dataFileName);

                    Tree tree = mainFrame.getFacade().readTree(
                            mainFrame.getDisplayWriter(), f.getAbsolutePath(), true);
                    this.treeFilePath = f.getAbsolutePath();
                    
                    // check tree consistency
                    boolean consistent = true;
                    if (alignment.getIdCount() == tree.getIdCount()) {
                        for (int id = 0; id < alignment.getIdCount(); id++) {
                            Identifier identifier = alignment.getIdentifier(id);
                            if (tree.whichIdNumber(identifier.getName()) == -1) {
                                consistent = false;
                                break;
                            }
                        }
                    } else
                        consistent = false;
                    if (consistent)
                        setUserTree(tree);
                    else {
                        mainFrame.getDisplayWriter().println("User topology is not consistent with current alignment");
                        setUserTree(null);
                        cmbStrategyMode.setSelectedIndex(0);
                    }
                } catch (TreeFormatException ex) {
                    mainFrame.getDisplayWriter().println(ex.getMessage());
                    setUserTree(null);
                    cmbStrategyMode.setSelectedIndex(0);
                }
                mainFrame.getDisplayWriter().println("");
                mainFrame.getFrame().toFront();
                mainFrame.getFrame().transferFocus();
            }
            
        } else {
            
            if (userTree != null)
                setUserTree(null);
            
        }
    }
    
    private void setUserTree(Tree tree) {
        this.userTree = tree;
        org.jdesktop.application.ResourceMap resourceMap 
                    = org.jdesktop.application.Application.getInstance(es.uvigo.darwin.xprottest.XProtTestApp.class)
                    .getContext().getResourceMap(OptionsView.class);
        if (tree == null) {
            lblUserTree.setText(resourceMap.getString("msg-no-user-tree-loaded"));
            lblUserTree.setForeground(Color.GRAY);
            this.treeFilePath = null;
        }
        else {
            lblUserTree.setText(resourceMap.getString("msg-user-tree-loaded"));
            lblUserTree.setForeground(XProtTestView.DONE_COLOR);
        }
    }

    @Action
    public void restoreDefault() {
        for (JCheckBox matrix : cbMatrices) {
            matrix.setSelected(true);
        }
        cbDistGroupCat.setSelected(true);
        cbDistInvGC.setSelected(true);
        cbDistInvariant.setSelected(true);
        cbEmpiricalF.setSelected(true);
        txtNCat.setText(String.valueOf(DEFAULT_NCAT));
        sliderProcessors.setValue(sliderProcessors.getMaximum());
        cmbStrategyMode.setSelectedIndex(DEFAULT_STRATEGY_MODE);
        onChangeOptions(null);
        sliderProcessorsStateChanged(null);
        setUserTree(null);
    }

    @Action
    public void close() {
        this.dispose();
    }
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnCompute;
    private javax.swing.JButton btnSetDefault;
    private javax.swing.JCheckBox cbDistGroupCat;
    private javax.swing.JCheckBox cbDistInvGC;
    private javax.swing.JCheckBox cbDistInvariant;
    private javax.swing.JCheckBox cbEmpiricalF;
    private javax.swing.JComboBox cmbStrategyMode;
    private javax.swing.JPanel distributionsPanel;
    private javax.swing.JPanel empiricalFPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lblNCat;
    private javax.swing.JLabel lblNumModels;
    private javax.swing.JLabel lblNumModelsLabel;
    private javax.swing.JLabel lblNumProc;
    private javax.swing.JLabel lblStrategyMode;
    private javax.swing.JLabel lblUserTree;
    private javax.swing.JLabel msgValidate;
    private javax.swing.JPanel rateVarPanel;
    private javax.swing.JSlider sliderProcessors;
    private javax.swing.JPanel subMatPanel;
    private javax.swing.JPanel subMatricesPanel;
    private javax.swing.JTextField txtNCat;
    // End of variables declaration//GEN-END:variables
    
}
