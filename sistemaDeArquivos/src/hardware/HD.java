package hardware;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import binary.Binario;

public class HD {

	private final int TAM_BLOCO = 4096; // 512B
	private final int TAM_HD = 268435456; // 32MB
	private final int TAM_NOME = 680; // 85B
	private final int TAM_PERM = 24; // 3B
	private final int TAM_DATA = 144; // 18B
	private final int TAM_ENDERECO = 16; // 2B
	private final int TAM_CONTEUDO = 3248; // 406B
	private final int POS_PERM = TAM_NOME;
	private final int POS_DATA = POS_PERM + TAM_PERM;
	private final int POS_PAI = POS_DATA + TAM_DATA;
	private final int POS_QUANT_FILHOS = POS_PAI + TAM_ENDERECO; // 2B para quantidade de filhos
	private final int POS_FILHOS = POS_QUANT_FILHOS + 16;
	private final int POS_QUANT_ARQUIVOS = POS_FILHOS + (TAM_ENDERECO * 100); // 2B para quantidade de arquivos
	private final int POS_ARQUIVOS = POS_QUANT_ARQUIVOS + 16;
	private final int POS_CONTEUDO = POS_PAI;
	private final int MAX_ENDERECOS = 100;

	private int posAtual = 0;
	public ArrayList<Integer> posLivres = new ArrayList<Integer>();
	private boolean[] posicoes = new boolean[TAM_HD];

	public HD() {
		escrever(getData(), POS_DATA);
		escrever(getPermPadrao(), POS_PERM);
		for (int i = 1; i < 65536; i++)
			posLivres.add(i);
	}

	private void escrever(String bits, int pos) {
		char[] bin = bits.toCharArray();
		int tam = pos + bin.length;
		for (int i = pos; i < tam; i++) {
			posicoes[i] = bin[i - pos] == '0' ? false : true;
		}
	}

	private char[] ler(int pos) {
		pos *= TAM_BLOCO;
		char[] res = new char[TAM_BLOCO];
		int tam = pos + TAM_BLOCO;
		for (int i = pos; i < tam; i++)
			res[i - pos] = posicoes[i] == true ? '1' : '0';
		return res;
	}

	private int quantFilhos(char[] bloco) {
		return Binario.binaryStringToInt(new String(podaChar(bloco, POS_QUANT_FILHOS, POS_FILHOS)));
	}

	private int quantArquivos(char[] bloco) {
		return Binario.binaryStringToInt(new String(podaChar(bloco, POS_QUANT_ARQUIVOS, POS_ARQUIVOS)));
	}

	private String getPermPadrao() {
		return Binario.stringToBinaryString("777");
	}

	private String getZeros(int quant) {
		char[] zeros = new char[quant];
		for (int i = 0; i < quant; i++)
			zeros[i] = '0';
		return new String(zeros);
	}

	private String getData() {
		SimpleDateFormat formato = new SimpleDateFormat("MMM dd yyyy HH:mm");
		String data = formato.format(new Date());
		String bin = Binario.stringToBinaryString(data);
		return data.length() == TAM_DATA / 8 ? bin : getZeros(TAM_DATA - (data.length() * 8)) + bin;
	}

	private String removeZeroEsquerda(char[] bin) {
		int i = 0;
		while (bin[i] == '0')
			i++;
		String res = new String(podaChar(bin, i, bin.length));

		if (res.length() % 8 != 0)
			res = (res.length() + 1) % 8 != 0 ? "00" + res : "0" + res;
		return res;
	}

	// posição do endereco no diretório pai
	private int getPosEndereco(char[] bloco, int pos) {
		boolean encontrado = false;
		int i = 0;
		while (!encontrado) {
			if (Binario.binaryStringToInt(new String(podaChar(bloco, i * TAM_ENDERECO, (i + 1) * TAM_ENDERECO))) == pos)
				encontrado = true;
			else
				i++;
		}

		return i;
	}

	private int proxPosLivrePai(char[] bloco, int quant) {
		boolean encontrado = false;
		int i = 0;
		while (i < quant && !encontrado) {
			int endereco = Binario
					.binaryStringToInt(new String(podaChar(bloco, i * TAM_ENDERECO, (i + 1) * TAM_ENDERECO)));
			if (endereco == 0)
				encontrado = true;
			else
				i++;
		}

		return i;
	}

	private char[] podaChar(char[] c, int inicio, int fim) {
		char[] res = new char[fim - inicio];
		for (int i = inicio; i < fim; i++)
			res[i - inicio] = c[i];
		return res;
	}

	public boolean hdLotado() {
		if (posLivres.isEmpty())
			return true;
		else
			return false;
	}

	public boolean limiteDeArquivos(int pos) {
		return quantArquivos(ler(pos)) == MAX_ENDERECOS ? true : false;
	}

	public boolean limiteDeFilhos(int pos) {
		return quantFilhos(ler(pos)) == MAX_ENDERECOS ? true : false;
	}

	public String getData(int pos) {
		char[] bloco = ler(pos);
		return Binario.binaryStringtoString(new String(removeZeroEsquerda(podaChar(bloco, POS_DATA, POS_PAI))));
	}

	public void setData(int pos, String data) {
		escrever(Binario.stringToBinaryString(data), (pos * TAM_BLOCO) + POS_DATA);
	}

	public int getPosAtual() {
		return posAtual;
	}

	public void setPosAtual(int posAtual) {
		this.posAtual = posAtual;
	}

	public String getPerm(int pos) {
		return Binario.binaryStringtoString(new String(podaChar(ler(pos), POS_PERM, POS_DATA)));
	}

	public void setPerm(int pos, String perm) {
		escrever(Binario.stringToBinaryString(perm), (pos * TAM_BLOCO) + POS_PERM);
	}

	public String getNome(int pos) {
		return Binario.binaryStringtoString(removeZeroEsquerda(podaChar(ler(pos), 0, TAM_NOME)));
	}

	public boolean setNome(int pos, String nome) {
		if (nome.length() > TAM_NOME / 8)
			return false;
		escrever(getZeros(TAM_NOME - (nome.length() * 8)) + Binario.stringToBinaryString(nome), pos * TAM_BLOCO);
		return true;
	}

	public void addFilho(String nome, int pai) {
		char[] bloco = ler(pai);
		int quantFilhos = quantFilhos(bloco);
		char[] filhos = podaChar(bloco, POS_FILHOS, POS_QUANT_ARQUIVOS);
		int destino = posLivres.remove(0);
		escrever(Binario.intToBinaryString(destino, 16),
				(pai * TAM_BLOCO) + POS_FILHOS + (proxPosLivrePai(filhos, quantFilhos) * TAM_ENDERECO));
		escrever(Binario.intToBinaryString(++quantFilhos, 16), (pai * TAM_BLOCO) + POS_QUANT_FILHOS);

		String conteudo = getZeros(TAM_NOME - (nome.length() * 8)) + Binario.stringToBinaryString(nome)
				+ getPermPadrao() + getData() + Binario.intToBinaryString(pai, 16)
				+ getZeros(TAM_BLOCO - TAM_NOME - TAM_PERM - TAM_DATA - TAM_ENDERECO);
		escrever(conteudo, destino * TAM_BLOCO);
	}

	public void addArquivo(String nome, String conteudo, int pai) {
		char[] bloco = ler(pai);
		int quantArquivo = quantArquivos(bloco);
		char[] arquivos = podaChar(bloco, POS_ARQUIVOS, bloco.length);
		int destino = posLivres.remove(0);
		escrever(Binario.intToBinaryString(destino, 16),
				(pai * TAM_BLOCO) + POS_ARQUIVOS + (proxPosLivrePai(arquivos, quantArquivo) * TAM_ENDERECO));
		escrever(Binario.intToBinaryString(++quantArquivo, 16), (pai * TAM_BLOCO) + POS_QUANT_ARQUIVOS);

		String write = getZeros(TAM_NOME - (nome.length() * 8)) + Binario.stringToBinaryString(nome) + getPermPadrao()
				+ getData() + getZeros(TAM_CONTEUDO - (conteudo.length() * 8)) + Binario.stringToBinaryString(conteudo);
		escrever(write, destino * TAM_BLOCO);
	}

	public boolean containsFilho(int pai, String nome) {
		char[] bloco = ler(pai);
		int i = 0, quantFilhos = quantFilhos(bloco);
		char[] filhos = podaChar(bloco, POS_FILHOS, POS_QUANT_ARQUIVOS);
		boolean contains = false;

		while (i < quantFilhos && !contains) {
			int endereco = Binario
					.binaryStringToInt(new String(podaChar(filhos, i * TAM_ENDERECO, (i + 1) * TAM_ENDERECO)));
			if (endereco != 0) {
				if (nome.equals(getNome(endereco)))
					contains = true;
			} else
				quantFilhos++;

			i++;
		}
		return contains;

	}

	public boolean containsArquivo(int pai, String nome) {
		char[] bloco = ler(pai);
		int i = 0, quantArquivos = quantArquivos(bloco);
		char[] arquivos = podaChar(bloco, POS_ARQUIVOS, bloco.length);
		boolean contains = false;

		while (i < quantArquivos && !contains) {
			int endereco = Binario
					.binaryStringToInt(new String(podaChar(arquivos, i * TAM_ENDERECO, (i + 1) * TAM_ENDERECO)));
			if (endereco != 0) {
				if (nome.equals(getNome(endereco)))
					contains = true;
			} else
				quantArquivos++;

			i++;
		}

		return contains;
	}

	public int pai(int pos) {
		return Binario.binaryStringToInt(new String(podaChar(ler(pos), POS_PAI, POS_QUANT_FILHOS)));
	}

	public int getFilho(int pai, String nome) {
		char[] bloco = ler(pai);
		int res = -1, i = 0, quantFilhos = quantFilhos(bloco);
		char[] filhos = podaChar(bloco, POS_FILHOS, POS_QUANT_ARQUIVOS);
		boolean encontrado = false;

		while (i < quantFilhos && !encontrado) {
			int pos = Binario.binaryStringToInt(new String(podaChar(filhos, i * TAM_ENDERECO, (i + 1) * TAM_ENDERECO)));
			if (pos == 0)
				quantFilhos++;
			else {
				if (nome.equals(getNome(pos))) {
					res = pos;
					encontrado = true;
				}
			}

			i++;
		}

		return res;
	}

	public int getArquivo(int pai, String nome) {
		char[] bloco = ler(pai);
		int res = -1, i = 0, quantArquivos = quantArquivos(bloco);
		char[] arquivos = podaChar(bloco, POS_ARQUIVOS, bloco.length);
		boolean encontrado = false;

		while (i < quantArquivos && !encontrado) {
			int pos = Binario
					.binaryStringToInt(new String(podaChar(arquivos, i * TAM_ENDERECO, (i + 1) * TAM_ENDERECO)));
			if (pos == 0)
				quantArquivos++;
			else {
				if (nome.equals(getNome(pos))) {
					res = pos;
					encontrado = true;
				}
			}

			i++;
		}

		return res;
	}

	public String getConteudo(int pai, String arquivo) {
		int pos = getArquivo(pai, arquivo);
		char[] bloco = podaChar(ler(pos), POS_CONTEUDO, POS_CONTEUDO + TAM_CONTEUDO);
		return Binario.binaryStringtoString(removeZeroEsquerda(bloco));
	}

	public ArrayList<Integer> filhos(int pos) {
		char[] bloco = ler(pos);
		ArrayList<Integer> filhos = new ArrayList<Integer>();
		int quantFilhos = quantFilhos(bloco);
		char[] posFilhos = podaChar(bloco, POS_FILHOS, POS_QUANT_ARQUIVOS);

		for (int i = 0; i < quantFilhos; i++) {
			int endereco = Binario
					.binaryStringToInt(new String(podaChar(posFilhos, i * TAM_ENDERECO, (i + 1) * TAM_ENDERECO)));
			if (endereco != 0)
				filhos.add(endereco);
			else
				quantFilhos++;
		}

		return filhos;
	}

	public ArrayList<Integer> arquivos(int pos) {
		char[] bloco = ler(pos);
		ArrayList<Integer> arquivos = new ArrayList<Integer>();
		int quantArquivos = quantArquivos(bloco);
		char[] posArquivos = podaChar(bloco, POS_ARQUIVOS, bloco.length);

		for (int i = 0; i < quantArquivos; i++) {
			int endereco = Binario
					.binaryStringToInt(new String(podaChar(posArquivos, i * TAM_ENDERECO, (i + 1) * TAM_ENDERECO)));
			if (endereco != 0)
				arquivos.add(endereco);
			else
				quantArquivos++;
		}

		return arquivos;
	}

	public void remove(int pos, boolean liberarPos) {
		int pai = pai(pos);
		char[] bloco = ler(pai);
		int quantFilhos = quantFilhos(bloco);
		char[] filhos = podaChar(bloco, POS_FILHOS, POS_QUANT_ARQUIVOS);
		escrever(getZeros(TAM_ENDERECO), (pai * TAM_BLOCO) + POS_FILHOS + (getPosEndereco(filhos, pos) * TAM_ENDERECO));
		escrever(Binario.intToBinaryString(--quantFilhos, 16), (pai * TAM_BLOCO) + POS_QUANT_FILHOS);
		if (liberarPos) {
			posLivres.add(pos);
		}
	}

	public void removeArquivo(int pos, int pai, boolean liberarPos) {
		char[] bloco = ler(pai);
		int quantArquivos = quantArquivos(bloco);
		char[] arquivos = podaChar(bloco, POS_ARQUIVOS, bloco.length);
		escrever(getZeros(TAM_ENDERECO),
				(pai * TAM_BLOCO) + POS_ARQUIVOS + (getPosEndereco(arquivos, pos) * TAM_ENDERECO));
		escrever(Binario.intToBinaryString(--quantArquivos, 16), (pai * TAM_BLOCO) + POS_QUANT_ARQUIVOS);
		if (liberarPos)
			posLivres.add(pos);
	}

	public void trocaPai(int no, int novo) {
		remove(no, false);
		char[] bloco = ler(novo);
		char[] filhos = podaChar(bloco, POS_FILHOS, POS_QUANT_ARQUIVOS);
		int quantFilhos = quantFilhos(bloco);
		escrever(Binario.intToBinaryString(novo, 16), (no * TAM_BLOCO) + POS_PAI);
		escrever(Binario.intToBinaryString(no, 16),
				(novo * TAM_BLOCO) + POS_FILHOS + (proxPosLivrePai(filhos, quantFilhos) * TAM_ENDERECO));
		escrever(Binario.intToBinaryString(++quantFilhos, 16), (novo * TAM_BLOCO) + POS_QUANT_FILHOS);
	}

	public void trocaPaiArquivo(int arquivo, int antigo, int novo) {
		removeArquivo(arquivo, antigo, false);
		char[] bloco = ler(novo);
		char[] arquivos = podaChar(bloco, POS_ARQUIVOS, bloco.length);
		int quantArquivo = quantArquivos(bloco);
		escrever(Binario.intToBinaryString(arquivo, 16),
				(novo * TAM_BLOCO) + POS_ARQUIVOS + (proxPosLivrePai(arquivos, quantArquivo) * TAM_ENDERECO));
		escrever(Binario.intToBinaryString(++quantArquivo, 16), (novo * TAM_BLOCO) + POS_QUANT_ARQUIVOS);
	}
}
