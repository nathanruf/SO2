package main;

public class Processo {

	private int chegada;
	private int duracao;
	private int prioridade;
	private int id;
	private int ultimaExecucao;

	public Processo(int chegada, int duracao, int prioridade, int id) {
		this.chegada = chegada;
		this.duracao = duracao;
		this.prioridade = prioridade;
		this.id = id;
		this.ultimaExecucao = chegada;
	}

	public int getId() {
		return id;
	}

	public int getChegada() {
		return chegada;
	}

	public void setChegada(int chegada) {
		this.chegada = chegada;
	}

	public int getDuracao() {
		return duracao;
	}

	public void setDuracao(int duracao) {
		this.duracao = duracao;
	}

	public int getPrioridade() {
		return prioridade;
	}

	public void setPrioridade(int prioridade) {
		this.prioridade = prioridade;
	}

	public int getUltimaExecucao() {
		return ultimaExecucao;
	}

	public void setUltimaExecucao(int ultimaExecucao) {
		this.ultimaExecucao = ultimaExecucao;
	}

}
