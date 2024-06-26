/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.io.File;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author natha
 */
public class TelaInicial extends javax.swing.JFrame {

	/**
	 * Creates new form main
	 */
	public TelaInicial() {
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

		buttonGroup1 = new javax.swing.ButtonGroup();
		jPanel1 = new javax.swing.JPanel();
		jLabel2 = new javax.swing.JLabel();
		jLabel1 = new javax.swing.JLabel();
		jLabel3 = new javax.swing.JLabel();
		restante = new javax.swing.JRadioButton();
		fcfs = new javax.swing.JRadioButton();
		job = new javax.swing.JRadioButton();
		loteria = new javax.swing.JRadioButton();
		roundRobin = new javax.swing.JRadioButton();
		prioridade = new javax.swing.JRadioButton();
		prox = new javax.swing.JButton();

		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		setTitle("Gerenciador de Processos");
		setMaximumSize(new java.awt.Dimension(804, 482));
		setMinimumSize(new java.awt.Dimension(804, 482));
		setResizable(false);

		jPanel1.setBackground(new java.awt.Color(204, 204, 204));

		jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/cpu.png"))); // NOI18N

		jLabel1.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
		jLabel1.setText("Lote");

		jLabel3.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
		jLabel3.setText("Interativo");

		restante.setBackground(new java.awt.Color(204, 204, 204));
		buttonGroup1.add(restante);
		restante.setText("Menor tempo restante");

		fcfs.setBackground(new java.awt.Color(204, 204, 204));
		buttonGroup1.add(fcfs);
		fcfs.setText("FCFS");

		job.setBackground(new java.awt.Color(204, 204, 204));
		buttonGroup1.add(job);
		job.setText("Job mais curto");

		loteria.setBackground(new java.awt.Color(204, 204, 204));
		buttonGroup1.add(loteria);
		loteria.setText("Loteria");

		roundRobin.setBackground(new java.awt.Color(204, 204, 204));
		buttonGroup1.add(roundRobin);
		roundRobin.setText("Round Robin");

		prioridade.setBackground(new java.awt.Color(204, 204, 204));
		buttonGroup1.add(prioridade);
		prioridade.setText("Prioridade");

		prox.setText("Pr�ximo");
		prox.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				proxActionPerformed(evt);
			}
		});

		javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
		jPanel1.setLayout(jPanel1Layout);
		jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
						.addGap(116, 116, 116)
						.addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
								.addComponent(fcfs).addComponent(job).addComponent(restante).addComponent(jLabel1))
						.addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addGroup(jPanel1Layout.createSequentialGroup().addGap(93, 93, 93)
										.addGroup(jPanel1Layout
												.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
												.addComponent(prox).addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
														jPanel1Layout.createSequentialGroup().addComponent(jLabel2)
																.addGap(8, 8, 8)))
										.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
								.addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
										jPanel1Layout.createSequentialGroup()
												.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED,
														244, Short.MAX_VALUE)
												.addGroup(jPanel1Layout
														.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(jLabel3).addComponent(loteria)
														.addComponent(prioridade).addComponent(roundRobin))
												.addGap(182, 182, 182)))));
		jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(jPanel1Layout.createSequentialGroup().addGroup(jPanel1Layout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(jPanel1Layout.createSequentialGroup().addGap(24, 24, 24).addComponent(jLabel2))
						.addGroup(jPanel1Layout.createSequentialGroup().addGap(109, 109, 109).addGroup(jPanel1Layout
								.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addGroup(jPanel1Layout.createSequentialGroup().addComponent(jLabel3)
										.addGroup(jPanel1Layout
												.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
												.addGroup(jPanel1Layout.createSequentialGroup().addGap(45, 45, 45)
														.addGroup(jPanel1Layout
																.createParallelGroup(
																		javax.swing.GroupLayout.Alignment.BASELINE)
																.addComponent(loteria).addComponent(fcfs)))
												.addGroup(jPanel1Layout.createSequentialGroup().addGap(103, 103, 103)
														.addComponent(prioridade).addGap(42, 42, 42)
														.addComponent(roundRobin))))
								.addGroup(jPanel1Layout.createSequentialGroup().addComponent(jLabel1)
										.addGap(103, 103, 103).addComponent(job).addGap(42, 42, 42)
										.addComponent(restante)))))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 88, Short.MAX_VALUE)
						.addComponent(prox).addGap(24, 24, 24)));

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(
				jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
		layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(
				jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));

		pack();
		setLocationRelativeTo(null);
	}// </editor-fold>

	private int VerificarInput(String str) {
		
		if(str == null)
			return -1;
		
		if(str.isBlank() || !str.matches("[0-9]*")) {
			JOptionPane.showMessageDialog(null, "Digite um n�mero natural maior que zero", "ERROR !", JOptionPane.ERROR_MESSAGE);
			return -1;
		}
		
		int quantum = Integer.parseInt(str);
		
		if(quantum == 0) {
			JOptionPane.showMessageDialog(null, "Digite um n�mero maior que zero", "ERROR !", JOptionPane.ERROR_MESSAGE);
			return -1;
		}
		
		return quantum;
	}
	
	private void proxActionPerformed(java.awt.event.ActionEvent evt) {
		EnumAlgoritmos e = null;
		int quantum = -1, tempoIncremento = -1;

		if (fcfs.isSelected()) {
			e = EnumAlgoritmos.FCFS;
		} else if (job.isSelected()) {
			e = EnumAlgoritmos.JOB_MAIS_CURTO;
		} else if (restante.isSelected()) {
			e = EnumAlgoritmos.MENOR_TEMPO_RESTANTE;
		} else if (prioridade.isSelected()) {
			e = EnumAlgoritmos.PRIORIDADE;
			
			quantum = VerificarInput(JOptionPane.showInputDialog("Digite o quantum, caro usu�rio."));
			if(quantum == -1)
				return;
			
			tempoIncremento = VerificarInput(JOptionPane.showInputDialog("Digite o tempo de incremento da prioridade, nobre usu�rio."));
			if(tempoIncremento == -1)
				return;
		} else if (loteria.isSelected()) {
			e = EnumAlgoritmos.LOTERIA;
			quantum = VerificarInput(JOptionPane.showInputDialog("Digite o quantum, caro usu�rio."));
			if(quantum == -1)
				return;
		} else if (roundRobin.isSelected()) {
			e = EnumAlgoritmos.ROUND_ROBIN;
			quantum = VerificarInput(JOptionPane.showInputDialog("Digite o quantum, caro usu�rio."));
			if(quantum == -1)
				return;
		}

		if (e != null) {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setDialogTitle("Escolher arquivo");
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			fileChooser.setCurrentDirectory(new File("."));

			FileNameExtensionFilter filtro = new FileNameExtensionFilter("texto", "txt");

			fileChooser.setFileFilter(filtro);

			boolean ok = fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION;

			if (ok) {
				String caminho = fileChooser.getSelectedFile().getPath();
				new TelaExecucao(caminho, e, quantum, tempoIncremento).setVisible(true);
				;
			}

		}
	}

	/**
	 * @param args the command line arguments
	 */
	public static void main(String args[]) {
		/* Set the Nimbus look and feel */
		// <editor-fold defaultstate="collapsed" desc=" Look and feel setting code
		// (optional) ">
		/*
		 * If Nimbus (introduced in Java SE 6) is not available, stay with the default
		 * look and feel. For details see
		 * http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
		 */
		try {
			for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					javax.swing.UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (ClassNotFoundException ex) {
			java.util.logging.Logger.getLogger(TelaInicial.class.getName()).log(java.util.logging.Level.SEVERE, null,
					ex);
		} catch (InstantiationException ex) {
			java.util.logging.Logger.getLogger(TelaInicial.class.getName()).log(java.util.logging.Level.SEVERE, null,
					ex);
		} catch (IllegalAccessException ex) {
			java.util.logging.Logger.getLogger(TelaInicial.class.getName()).log(java.util.logging.Level.SEVERE, null,
					ex);
		} catch (javax.swing.UnsupportedLookAndFeelException ex) {
			java.util.logging.Logger.getLogger(TelaInicial.class.getName()).log(java.util.logging.Level.SEVERE, null,
					ex);
		}
		// </editor-fold>

		/* Create and display the form */
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				new TelaInicial().setVisible(true);
			}
		});
	}

	// Variables declaration - do not modify
	private javax.swing.ButtonGroup buttonGroup1;
	private javax.swing.JRadioButton fcfs;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JLabel jLabel2;
	private javax.swing.JLabel jLabel3;
	private javax.swing.JPanel jPanel1;
	private javax.swing.JRadioButton job;
	private javax.swing.JRadioButton loteria;
	private javax.swing.JRadioButton prioridade;
	private javax.swing.JButton prox;
	private javax.swing.JRadioButton restante;
	private javax.swing.JRadioButton roundRobin;
	// End of variables declaration
}
