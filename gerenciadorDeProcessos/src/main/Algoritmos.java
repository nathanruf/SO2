package main;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

public class Algoritmos {

	private TelaExecucao tela;
	private RandomAccessFile arquivo;
	private boolean tempoReal = false;
	private boolean passoPasso = false;
	private boolean exit = false;
	private int quantum;
	private int tempoIncremento;
	private int esperaMaxima = 0, trocaContexto = 0, maximoOcioso = 0, tempoAtual = 0, countOciosidade = 0,
			countEspera = 0,idCount = 0;
	private long esperaTotal = 0, ociosoTotal = 0;
	private Queue<Processo> fila = null;
	private List<Processo> l1 = null, l2 = null, l3 = null, l4 = null;
	private String ultimo;
	private int idUltimo = 1;
	private boolean finalizado = false;

	public Algoritmos(TelaExecucao tela, String caminho, int quantum, int tempoIncremento) {
		this.tela = tela;
		this.quantum = quantum;
		this.tempoIncremento = tempoIncremento;
		try {
			this.arquivo = new RandomAccessFile(caminho, "r");
		} catch (FileNotFoundException e) {
			System.out.println("Arquivo não encontrado");
		}
	}

	public boolean hasNext() {
		try {
			return !(arquivo.length() == arquivo.getFilePointer());
		} catch (IOException e) {
			return false;
		}
	}

	public boolean fimPassoPasso() {
		try {
			return arquivo.length() == arquivo.getFilePointer() && (fila == null || fila.isEmpty())
					&& (l1 == null || l1.isEmpty()) && (l2 == null || l2.isEmpty()) && (l3 == null || l3.isEmpty())
					&& (l4 == null || l4.isEmpty());
		} catch (IOException e) {
			return false;
		}
	}

	public void exibirMetricas() {
		if (countOciosidade == 0)
			countOciosidade = 1;
		if (countEspera == 0)
			countEspera = 1;
		double mediaEspera = ((double) esperaTotal) / countEspera;
		double mediaOciosidade = ((double) ociosoTotal) / countOciosidade;

		String resultado = "Tempo total: " + tempoAtual + "\nTempo máximo de espera: " + esperaMaxima
				+ "\nTempo médio de espera: " + formatarDouble(mediaEspera) + "\nTempo máximo de ociosidade: "
				+ maximoOcioso + "\nTempo médio de ociosidade: " + formatarDouble(mediaOciosidade)
				+ "\nNúmero de trocas de contexto: " + trocaContexto;

		tela.getPainel().append(resultado);
	}

	public void executar(EnumAlgoritmos algoritmo, Botoes b) {

		if (b == Botoes.PP)
			passoPasso = true;
		else if (b == Botoes.TR)
			tempoReal = true;

		switch (algoritmo) {
		case FCFS:
			fcfs();
			break;
		case JOB_MAIS_CURTO:
			if (fila == null)
				fila = new PriorityQueue<Processo>(new PriorityComparator());
			jobMaisCurto();
			break;
		case MENOR_TEMPO_RESTANTE:
			if (fila == null)
				fila = new PriorityQueue<Processo>(new PriorityComparator());
			menorTempoRestante();
			break;
		case ROUND_ROBIN:
			if (l1 == null)
				l1 = new ArrayList<Processo>();
			roundRobin();
			break;
		case PRIORIDADE:
			if (l1 == null) {
				l1 = new ArrayList<Processo>();
				l2 = new ArrayList<Processo>();
				l3 = new ArrayList<Processo>();
				l4 = new ArrayList<Processo>();
			}
			prioridade();
			break;
		case LOTERIA:
			if (l1 == null)
				l1 = new ArrayList<Processo>();
			loteria();
			break;
		}
	}

	class PriorityComparator implements Comparator<Processo> {

		public int compare(Processo p1, Processo p2) {

			int duracaoP1 = p1.getDuracao();
			int duracaoP2 = p2.getDuracao();

			if (duracaoP1 > duracaoP2)
				return 1;
			else if (duracaoP1 < duracaoP2)
				return -1;
			else
				return 0;
		}

	}

	private String formatarDouble(double num) {
		String aux = Double.toString(num);
		int tam = aux.indexOf('.') + 2;

		if (tam != aux.length())
			tam++;

		char[] formatado = new char[tam];

		for (int i = 0; i < tam; i++)
			formatado[i] = aux.charAt(i);

		return new String(formatado);
	}

	private void atualizarMetricas(Processo processo, int duracao) {
		int espera = tempoAtual - processo.getUltimaExecucao();

		if (espera > 0) {
			countEspera++;
			esperaTotal += espera;
			if (espera > esperaMaxima)
				esperaMaxima = espera;
		} else if (espera < 0) {
			countOciosidade++;
			espera *= -1;
			tempoAtual += espera;
			ociosoTotal += espera;
			if (espera > maximoOcioso)
				maximoOcioso = espera;
		}

		tempoAtual += duracao;
	}

	private void retornaUmaLinha(Processo processo) {

		if (processo == null)
			return;
		// 9 = prioridade, 2 virgulas, 2 espaços, 2 \n e 2\r
		idCount--;
		String aux = Integer.toString(processo.getChegada());
		aux += Integer.toString(processo.getDuracao());
		int casas = hasNext() ? aux.length() + 9 : aux.length() + 7;
		try {
			long posDesejada = arquivo.getFilePointer() - casas;
			arquivo.seek(posDesejada);
			int espacosSobrando = 0;
			while (arquivo.readLine().length() != 0)
				arquivo.seek(posDesejada - (++espacosSobrando));
		} catch (IOException e) {
			System.out.println("Ocorreu um erro");
		}

	}

	private void imprimirPreemptivo(int tempoAtualAnterior, int tempoExecutado, Processo processo,
			EnumAlgoritmos algoritmo) {
		int chegada = processo.getChegada();
		int duracao = processo.getDuracao();
		int id = processo.getId();
		

		if (idUltimo != id) {
			String execucao;
			if (finalizado)
				execucao = "\nFim da execução: ";
			else {
				execucao = "\nExecutado até: ";
				trocaContexto++;
			}
			if (tempoReal)
				tempoReal(ultimo + execucao + tempoAtualAnterior + "\n\n");
			else
				tela.getPainel().append(ultimo + execucao + tempoAtualAnterior + "\n\n");
			ultimo = "Processo: " + id + "\nChegada: " + chegada + "\nInício da execução: "
					+ (tempoAtual - tempoExecutado);
		}

		if (ultimo == null)
			ultimo = "Processo: " + id + "\nChegada: " + chegada + "\nInício da execução: "
					+ (tempoAtual - tempoExecutado);
		finalizado = duracao == 0;
		int idUltimoAntigo = idUltimo;
		idUltimo = id;

		if (passoPasso) {
			if (fimPassoPasso())
				tela.getPainel().append(ultimo + "\nFim da execução: " + tempoAtual + "\n\n");
			else {
				if (idUltimoAntigo == id) {
					switch (algoritmo) {
					case ROUND_ROBIN:
						this.roundRobin();
						break;
					case LOTERIA:
						this.loteria();
						break;
					case PRIORIDADE:
						this.prioridade();
						break;
					case MENOR_TEMPO_RESTANTE:
						this.menorTempoRestante();
						break;
					default:
						break;
					}
				}
			}
		}
	}

	private Processo realizaSorteio() {
		int num = 0, size = l1.size();

		for (int i = 0; i < size; i++) {
			num += l1.get(i).getPrioridade();
		}

		int numSorteio = (int) (Math.random() * num);
		int sorteado = -1;

		while (numSorteio >= 0) {
			numSorteio -= l1.get(++sorteado).getPrioridade();
		}

		return l1.remove(sorteado);
	}

	private void loteria() {
		Processo processo = getProcesso();
		int limite;
		if (l1.isEmpty()) {
			int novoTempo = (int) (Math.ceil((double) processo.getChegada() / quantum) * quantum);
			tempoAtual = novoTempo;
			limite = novoTempo;
		} else
			limite = tempoAtual;
		while (processo != null && !exit && limite >= processo.getChegada()) {
			l1.add(processo);
			processo = getProcesso();
		}

		if (passoPasso) {
			Processo sorteado = realizaSorteio();

			int duracao = sorteado.getDuracao();
			int tempo = quantum;
			int tempoAtualAnterior = tempoAtual;

			atualizarMetricas(sorteado, tempo);
			sorteado.setUltimaExecucao(tempoAtual);

			if (tempo < duracao) {
				sorteado.setDuracao(duracao - tempo);
				l1.add(sorteado);
			} else {
				sorteado.setDuracao(0);
			}

			retornaUmaLinha(processo);
			imprimirPreemptivo(tempoAtualAnterior, tempo, sorteado, EnumAlgoritmos.LOTERIA);

		} else {
			while ((!l1.isEmpty() || processo != null) && !exit) {
				Processo sorteado = realizaSorteio();

				int duracao = sorteado.getDuracao();
				int tempo = quantum;
				int tempoAtualAnterior = tempoAtual;

				atualizarMetricas(sorteado, tempo);
				sorteado.setUltimaExecucao(tempoAtual);

				if (tempo < duracao) {
					sorteado.setDuracao(duracao - tempo);
					l1.add(sorteado);
				} else {
					sorteado.setDuracao(0);
				}

				imprimirPreemptivo(tempoAtualAnterior, tempo, sorteado, EnumAlgoritmos.LOTERIA);

				if (l1.isEmpty() && processo != null) {
					int novoTempo = (int) (Math.ceil((double) processo.getChegada() / quantum) * quantum);
					tempoAtual = novoTempo;
					limite = novoTempo;
				} else
					limite = tempoAtual;
				while (processo != null && !exit && limite >= processo.getChegada()) {
					l1.add(processo);
					processo = getProcesso();
				}
			}

			tempoReal(ultimo + "\nFim da execução: " + tempoAtual + "\n\n");
			if (!tempoReal)
				exibirMetricas();
		}
	}

	private void escolherFila(Processo processo) {
		int prioridade = processo.getPrioridade();

		switch (prioridade) {
		case 1:
			l1.add(processo);
			break;
		case 2:
			l2.add(processo);
			break;
		case 3:
			l3.add(processo);
			break;
		case 4:
			l4.add(processo);
			break;
		}
	}

	private Processo proximoProcesso() {
		Processo escolhido = null;

		if (!l4.isEmpty())
			escolhido = l4.remove(0);
		else if (!l3.isEmpty())
			escolhido = l3.remove(0);
		else if (!l2.isEmpty())
			escolhido = l2.remove(0);
		else if (!l1.isEmpty())
			escolhido = l1.remove(0);

		return escolhido;
	}

	private void aumentaPrioridade() {
		int size = l3.size();
		for (int i = 0; i < size; i++) {
			Processo processo = l3.get(i);
			if (tempoAtual - processo.getUltimaExecucao() >= tempoIncremento) {
				int prioridade = processo.getPrioridade();
				processo.setPrioridade(++prioridade);
				l3.remove(i--);
				escolherFila(processo);
				size--;
			}
		}
		size = l2.size();
		for (int i = 0; i < size; i++) {
			Processo processo = l2.get(i);
			if (tempoAtual - processo.getUltimaExecucao() >= tempoIncremento) {
				int prioridade = processo.getPrioridade();
				processo.setPrioridade(++prioridade);
				l2.remove(i--);
				escolherFila(processo);
				size--;
			}
		}
		size = l1.size();
		for (int i = 0; i < size; i++) {
			Processo processo = l1.get(i);
			if (tempoAtual - processo.getUltimaExecucao() >= tempoIncremento) {
				int prioridade = processo.getPrioridade();
				processo.setPrioridade(++prioridade);
				l1.remove(i--);
				escolherFila(processo);
				size--;
			}
		}
	}

	private boolean listasVazias() {
		return l1.isEmpty() && l2.isEmpty() && l3.isEmpty() && l4.isEmpty();
	}

	private void prioridade() {
		Processo processo = getProcesso();
		int limite;
		if (listasVazias()) {
			int novoTempo = (int) (Math.ceil((double) processo.getChegada() / quantum) * quantum);
			tempoAtual = novoTempo;
			limite = novoTempo;
		} else
			limite = tempoAtual;
		while (processo != null && !exit && limite >= processo.getChegada()) {
			escolherFila(processo);
			processo = getProcesso();
		}

		if (passoPasso) {
			Processo atual = proximoProcesso();

			int duracao = atual.getDuracao();
			int tempo = quantum;
			int tempoAtualAnterior = tempoAtual;

			atualizarMetricas(atual, tempo);
			atual.setUltimaExecucao(tempoAtual);
			aumentaPrioridade();

			if (tempo < duracao) {
				atual.setDuracao(duracao - tempo);
				escolherFila(atual);
			} else {
				atual.setDuracao(0);
			}

			retornaUmaLinha(processo);
			imprimirPreemptivo(tempoAtualAnterior, tempo, atual, EnumAlgoritmos.PRIORIDADE);

		} else {
			while ((!listasVazias() || processo != null) && !exit) {
				Processo atual = proximoProcesso();

				int duracao = atual.getDuracao();
				int tempo = quantum;
				int tempoAtualAnterior = tempoAtual;

				atualizarMetricas(atual, tempo);
				atual.setUltimaExecucao(tempoAtual);
				aumentaPrioridade();

				if (tempo < duracao) {
					atual.setDuracao(duracao - tempo);
					escolherFila(atual);
				} else {
					atual.setDuracao(0);
				}

				imprimirPreemptivo(tempoAtualAnterior, tempo, atual, EnumAlgoritmos.PRIORIDADE);

				if (listasVazias() && processo != null) {
					int novoTempo = (int) (Math.ceil((double) processo.getChegada() / quantum) * quantum);
					tempoAtual = novoTempo;
					limite = novoTempo;
				} else
					limite = tempoAtual;
				while (processo != null && !exit && limite >= processo.getChegada()) {
					escolherFila(processo);
					processo = getProcesso();
				}
			}

			tempoReal(ultimo + "\nFim da execução: " + tempoAtual + "\n\n");
			if (!tempoReal)
				exibirMetricas();
		}
	}

	private void roundRobin() {
		Processo processo;
		if (l1.isEmpty()) {
			processo = getProcesso();
			int novoTempo = (int) (Math.ceil((double) processo.getChegada() / quantum) * quantum);
			tempoAtual = novoTempo;
		} else
			processo = l1.remove(0);

		if (passoPasso) {
			int duracao = processo.getDuracao();
			int tempo = quantum;
			int tempoAtualAnterior = tempoAtual;

			atualizarMetricas(processo, tempo);
			processo.setUltimaExecucao(tempoAtual);

			Processo prox = getProcesso();
			while (prox != null && !exit && prox.getChegada() <= tempoAtual) {
				l1.add(prox);
				prox = getProcesso();
			}

			if (tempo < duracao) {
				processo.setDuracao(duracao - tempo);
				l1.add(processo);
			} else
				processo.setDuracao(0);

			retornaUmaLinha(prox);
			imprimirPreemptivo(tempoAtualAnterior, tempo, processo, EnumAlgoritmos.ROUND_ROBIN);

		} else {
			Processo prox = null;
			while (processo != null && !exit) {
				int duracao = processo.getDuracao();
				int tempo = quantum;
				int tempoAtualAnterior = tempoAtual;

				atualizarMetricas(processo, tempo);
				processo.setUltimaExecucao(tempoAtual);

				if (prox == null)
					prox = getProcesso();

				while (prox != null && !exit && prox.getChegada() <= tempoAtual) {
					l1.add(prox);
					prox = getProcesso();
				}

				if (tempo < duracao) {
					processo.setDuracao(duracao - tempo);
					l1.add(processo);
				} else
					processo.setDuracao(0);

				imprimirPreemptivo(tempoAtualAnterior, tempo, processo, EnumAlgoritmos.ROUND_ROBIN);

				if (l1.isEmpty()) {
					processo = prox;
					prox = null;
					if (processo != null) {
						int novoTempo = (int) (Math.ceil((double) processo.getChegada() / quantum) * quantum);
						tempoAtual = novoTempo;
					}
				} else
					processo = l1.remove(0);
			}

			tempoReal(ultimo + "\nFim da execução: " + tempoAtual + "\n\n");
			if (!tempoReal)
				exibirMetricas();
		}

	}

	private void menorTempoRestante() {
		Processo processo = getProcesso();
		Processo atual;
		int limite, fim = 0, inicio;

		if (fila.isEmpty()) {
			atual = processo;
			processo = getProcesso();
		} else
			atual = fila.remove();

		if (processo != null) {

			inicio = atual.getChegada() > tempoAtual ? atual.getChegada() : tempoAtual;

			fim = inicio + atual.getDuracao();

			limite = fim > processo.getChegada() ? processo.getChegada() : fim;
			while (processo != null && !exit && processo.getChegada() <= limite) {
				fila.add(processo);
				processo = getProcesso();
			}
		} else {
			limite = atual.getChegada() + atual.getDuracao();
			inicio = atual.getChegada();
		}

		if (passoPasso) {
			if (limite <= fim)
				retornaUmaLinha(processo);
			int duracao = atual.getDuracao();
			int tempo = limite - inicio;
			if (tempo >= duracao) {
				tempo = duracao;
				atual.setDuracao(0);
			} else {
				atual.setDuracao(atual.getDuracao() - tempo);
				fila.add(atual);
			}

			int tempoAtualAnterior = tempoAtual;

			atualizarMetricas(atual, tempo);

			atual.setUltimaExecucao(tempoAtual);

			imprimirPreemptivo(tempoAtualAnterior, tempo, atual, EnumAlgoritmos.MENOR_TEMPO_RESTANTE);

		} else {
			boolean usado = false;
			while ((atual != null || !fila.isEmpty()) && !exit) {
				if (limite > fim)
					usado = true;
				else
					usado = false;
				int duracao = atual.getDuracao();
				int tempo = limite - inicio;
				if (tempo >= duracao) {
					tempo = duracao;
					atual.setDuracao(0);
				} else {
					atual.setDuracao(atual.getDuracao() - tempo);
					fila.add(atual);
				}

				int tempoAtualAnterior = tempoAtual;
				atualizarMetricas(atual, tempo);
				atual.setUltimaExecucao(tempoAtual);
				imprimirPreemptivo(tempoAtualAnterior, tempo, atual, EnumAlgoritmos.MENOR_TEMPO_RESTANTE);

				if (usado)
					processo = getProcesso();

				if (fila.isEmpty()) {
					atual = processo;
					processo = getProcesso();
				} else
					atual = fila.remove();

				if (processo != null) {

					inicio = atual.getChegada() > tempoAtual ? atual.getChegada() : tempoAtual;

					fim = inicio + atual.getDuracao();

					limite = fim > processo.getChegada() ? processo.getChegada() : fim;
					while (processo != null && !exit && processo.getChegada() <= limite) {
						fila.add(processo);
						processo = getProcesso();
					}
				} else if (atual != null) {
					limite = atual.getChegada() + atual.getDuracao();
					inicio = atual.getChegada();
					fim = limite + 1;
				}
			}

			tempoReal(ultimo + "\nFim da execução: " + tempoAtual + "\n\n");
			if (!tempoReal)
				exibirMetricas();
		}
	}

	private void jobMaisCurto() {
		Processo processo = getProcesso();

		if (processo != null) {
			if (fila.isEmpty() || processo.getChegada() <= tempoAtual) {
				fila.add(processo);
				processo = getProcesso();
				while (processo != null && !exit && processo.getChegada() <= tempoAtual) {
					fila.add(processo);
					processo = getProcesso();
				}
			}
		}

		if (passoPasso) {
			retornaUmaLinha(processo);
			processo = fila.remove();
			int chegada = processo.getChegada();
			int duracao = processo.getDuracao();

			atualizarMetricas(processo, duracao);

			tela.getPainel().append("Processo: " + processo.getId() + "\nChegada: " + chegada + "\nInício da execução: "
					+ (tempoAtual - duracao) + "\nFim da execução: " + tempoAtual + "\n\n");
		} else {
			while (!fila.isEmpty() && !exit) {
				Processo prox = processo;
				processo = fila.remove();
				int chegada = processo.getChegada();
				int duracao = processo.getDuracao();

				atualizarMetricas(processo, duracao);

				if (tempoReal)
					tempoReal("Processo: " + processo.getId() + "\nChegada: " + chegada + "\nInício da execução: "
							+ (tempoAtual - duracao) + "\nFim da execução: " + tempoAtual + "\n\n");
				else
					tela.getPainel()
							.append("Processo: " + processo.getId() + "\nChegada: " + chegada + "\nInício da execução: "
									+ (tempoAtual - duracao) + "\nFim da execução: " + tempoAtual + "\n\n");

				if (prox != null) {
					processo = prox;

					if (fila.isEmpty() || processo.getChegada() <= tempoAtual) {
						fila.add(processo);
						processo = getProcesso();
						while (processo != null && !exit && processo.getChegada() <= tempoAtual) {
							fila.add(processo);
							processo = getProcesso();
						}
					}
				} else
					processo = null;

			}

			if (!tempoReal)
				exibirMetricas();
		}
	}

	private void fcfs() {
		Processo processo = getProcesso();

		if (passoPasso) {
			int chegada = processo.getChegada();
			int duracao = processo.getDuracao();

			atualizarMetricas(processo, duracao);

			tela.getPainel().append("Processo: " + processo.getId() + "\nChegada: " + chegada + "\nInício da execução: "
					+ (tempoAtual - duracao) + "\nFim da execução: " + tempoAtual + "\n\n");
		} else {
			while (processo != null && !exit) {
				int chegada = processo.getChegada();
				int duracao = processo.getDuracao();

				atualizarMetricas(processo, duracao);

				if (tempoReal)
					tempoReal("Processo: " + processo.getId() + "\nChegada: " + chegada + "\nInício da execução: "
							+ (tempoAtual - duracao) + "\nFim da execução: " + tempoAtual + "\n\n");
				else
					tela.getPainel()
							.append("Processo: " + processo.getId() + "\nChegada: " + chegada + "\nInício da execução: "
									+ (tempoAtual - duracao) + "\nFim da execução: " + tempoAtual + "\n\n");
				processo = getProcesso();

			}

			if (!tempoReal)
				exibirMetricas();
		}

	}

	private Processo getProcesso() {
		if (hasNext()) {
			String[] aux;
			try {
				aux = arquivo.readLine().split(",");
				Processo processo = new Processo(Integer.parseInt(aux[0]), Integer.parseInt(aux[1].trim()),
						Integer.parseInt(aux[2].trim()), ++idCount);
				return processo;
			} catch (IOException e) {
				return null;
			}
		} else
			return null;
	}

	private void tempoReal(String conteudo) {
		tela.getPainel().append(conteudo);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			System.out.println("Ocorreu um erro");
		}
	}

	public void encerrar() {
		exit = true;
	}
}
