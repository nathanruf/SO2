/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.net.URL;
import javax.swing.ImageIcon;

/**
 *
 * @author natha
 */
public class TelaExecucao extends javax.swing.JFrame {

	/**
	 * Creates new form TelaExecucao
	 */
	String caminho;
	EnumAlgoritmos algoritmo;
	Algoritmos tempoReal, passoPasso;
	int quantum, tempoIncremento;

	public TelaExecucao(String caminho, EnumAlgoritmos e, int quantum, int tempoIncremento) {
		this.caminho = caminho;
		this.quantum = quantum;
		this.tempoIncremento = tempoIncremento;
		algoritmo = e;
		initComponents();
		URL iconURL = getClass().getResource("/img/logoIF.png");
		ImageIcon icon = new ImageIcon(iconURL);
		this.setIconImage(icon.getImage());
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
	// <editor-fold defaultstate="collapsed" desc="Generated Code">
	private void initComponents() {

		jPanel1 = new javax.swing.JPanel();
		jLabel2 = new javax.swing.JLabel();
		tr = new javax.swing.JButton();
		pp = new javax.swing.JButton();
		sc = new javax.swing.JButton();
		jScrollPane1 = new javax.swing.JScrollPane();
		painel = new javax.swing.JTextArea();

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setTitle("Gerenciador de Processsos");
		setMaximumSize(new java.awt.Dimension(800, 500));
		setMinimumSize(new java.awt.Dimension(800, 500));
		setPreferredSize(new java.awt.Dimension(800, 500));
		setResizable(false);

		jPanel1.setBackground(new java.awt.Color(204, 204, 204));

		jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/cpu.png"))); // NOI18N

		tr.setText("Tempo Real");
		tr.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				trActionPerformed(evt);
			}
		});

		pp.setText("Passo a Passo");
		pp.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				ppActionPerformed(evt);
			}
		});

		sc.setText("Simula��o Completa");
		sc.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				scActionPerformed(evt);
			}
		});

		painel.setBackground(new java.awt.Color(240, 240, 240));
		painel.setColumns(20);
		painel.setRows(5);
		jScrollPane1.setViewportView(painel);

		javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
		jPanel1.setLayout(jPanel1Layout);
		jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(jPanel1Layout.createSequentialGroup().addContainerGap().addComponent(jScrollPane1)
						.addContainerGap())
				.addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
						jPanel1Layout.createSequentialGroup().addContainerGap(122, Short.MAX_VALUE).addComponent(tr)
								.addGap(108, 108, 108).addComponent(pp).addGap(117, 117, 117).addComponent(sc)
								.addGap(90, 90, 90))
				.addGroup(jPanel1Layout.createSequentialGroup().addGap(346, 346, 346).addComponent(jLabel2)
						.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
		jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(jPanel1Layout.createSequentialGroup().addGap(19, 19, 19).addComponent(jLabel2)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 326,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
						.addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(pp).addComponent(tr).addComponent(sc))
						.addContainerGap(27, Short.MAX_VALUE)));

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(
				jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
		layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(
				jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));

		pack();
		setLocationRelativeTo(null);
	}// </editor-fold>

	class TempoReal implements Runnable {
		public void run() {
			tempoReal.executar(algoritmo, Botoes.TR);
			if (!tempoReal.hasNext())
				tempoReal.exibirMetricas();
			tr.setEnabled(true);
		}
	}

	class SimulacaoCompleta implements Runnable {
		
		private TelaExecucao tela;

		public SimulacaoCompleta(TelaExecucao tela) {
			this.tela = tela;
		}

		public void run() {
			new Algoritmos(tela, caminho, quantum, tempoIncremento).executar(algoritmo, Botoes.SC);
			tr.setEnabled(true);
			sc.setEnabled(true);
			pp.setEnabled(true);
		}
		
	}

	private void trActionPerformed(java.awt.event.ActionEvent evt) {
		passoPasso = null;

		if (tempoReal != null)
			tempoReal.encerrar();

		tempoReal = new Algoritmos(this, caminho, quantum, tempoIncremento);
		this.painel.setText(null);
		tr.setEnabled(false);
		new Thread(new TempoReal()).start();
	}

	private void ppActionPerformed(java.awt.event.ActionEvent evt) {
		if (tempoReal != null)
			tempoReal.encerrar();

		if (passoPasso == null) {
			this.painel.setText(null);
			passoPasso = new Algoritmos(this, caminho, quantum, tempoIncremento);
		}

		passoPasso.executar(algoritmo, Botoes.PP);

		if (passoPasso.fimPassoPasso()) {
			passoPasso.exibirMetricas();
			passoPasso = null;
		}
	}

	private void scActionPerformed(java.awt.event.ActionEvent evt) {
		passoPasso = null;
		
		if(tempoReal != null)
			tempoReal.encerrar();
		
		this.painel.setText(null);
		tr.setEnabled(false);
		sc.setEnabled(false);
		pp.setEnabled(false);
		new Thread(new SimulacaoCompleta(this)).start();
	}

	public javax.swing.JTextArea getPainel() {
		return this.painel;
	}

	// Variables declaration - do not modify
	private javax.swing.JLabel jLabel2;
	private javax.swing.JPanel jPanel1;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JTextArea painel;
	private javax.swing.JButton pp;
	private javax.swing.JButton sc;
	private javax.swing.JButton tr;
	// End of variables declaration
}
