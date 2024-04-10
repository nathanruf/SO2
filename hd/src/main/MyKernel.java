package main;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

import hardware.HD;
import operatingSystem.Kernel;

public class MyKernel implements Kernel {

	private String result;
	private HD hd = new HD();

	private int navegar(String[] caminho) {
		int index = 0;
		String atual = "";
		int destino = hd.getPosAtual();

		if (caminho.length != 0)
			atual = caminho[0];

		if (caminho.length == 0 || atual.equals("")) {
			while (!(hd.pai(destino) == destino))
				destino = hd.pai(destino);
			index++;
		}

		while (index < caminho.length) {
			atual = caminho[index];
			if (!atual.equals(".")) {
				if (atual.equals("..")) {
					destino = hd.pai(destino);
				} else if (hd.containsFilho(destino, atual)) {
					destino = hd.getFilho(destino, atual);
				} else {
					result = "Caminho informado não existe";
					destino = -1;
					index = caminho.length;
				}
			}
			index++;
		}

		return destino;

	}

	public String ls(String parameters) {

		result = "";
		int alvo;

		if (parameters.isEmpty()) {
			result += ".\n";
			result += "..\n";
			hd.filhos(hd.getPosAtual()).forEach((key) -> result += hd.getNome(key) + "\n");
			hd.arquivos(hd.getPosAtual()).forEach((key) -> result += hd.getNome(key) + "\n");
		} else if (parameters.split(" ")[0].equals("-l")) {

			if (parameters.length() > 3) {
				alvo = navegar(parameters.substring(3).split("/"));
				if (alvo != -1) {
					result += convertePermissao(hd.getPerm(alvo)) + " " + hd.getData(alvo) + " .\n";
					result += convertePermissao(hd.getPerm(hd.pai(alvo))) + " " + hd.getData(hd.pai(alvo)) + " ..\n";
					hd.filhos(alvo).forEach((key) -> result += convertePermissao(hd.getPerm(key)) + " "
							+ hd.getData(key) + " " + hd.getNome(key) + "\n");
					hd.arquivos(alvo).forEach((key) -> result += convertePermissao(hd.getPerm(key)) + " "
							+ hd.getData(key) + " " + hd.getNome(key) + "\n");
				} else
					return result;
			} else {
				result += convertePermissao(hd.getPerm(hd.getPosAtual())) + " " + hd.getData(hd.getPosAtual()) + " .\n";
				result += convertePermissao(hd.getPerm(hd.pai(hd.getPosAtual()))) + " "
						+ hd.getData(hd.pai(hd.getPosAtual())) + " ..\n";
				hd.filhos(hd.getPosAtual()).forEach((key) -> result += convertePermissao(hd.getPerm(key)) + " "
						+ hd.getData(key) + " " + hd.getNome(key) + "\n");
				hd.arquivos(hd.getPosAtual()).forEach((key) -> result += convertePermissao(hd.getPerm(key)) + " "
						+ hd.getData(key) + " " + hd.getNome(key) + "\n");
			}
		} else {
			result += ".\n";
			result += "..\n";
			alvo = navegar(parameters.split("/"));
			if (alvo != -1) {
				hd.filhos(alvo).forEach((key) -> result += hd.getNome(key) + "\n");
				hd.arquivos(alvo).forEach((key) -> result += hd.getNome(key) + "\n");
			} else
				return result;
		}

		return result.trim();
	}

	private String convertePermissao(String numPermissao) {
		String permissao = "-";

		for (char num : numPermissao.toCharArray()) {
			switch (num) {
			case '0':
				permissao += "---";
				break;
			case '1':
				permissao += "--x";
				break;
			case '2':
				permissao += "-w-";
				break;
			case '3':
				permissao += "-wx";
				break;
			case '4':
				permissao += "r--";
				break;
			case '5':
				permissao += "r-x";
				break;
			case '6':
				permissao += "rw-";
				break;
			case '7':
				permissao += "rwx";
				break;
			}
		}

		return permissao;
	}

	public String mkdir(String parameters) {

		result = "";
		if (hd.hdLotado())
			result = "O HD está cheio, não é possível criar mais diretórios";
		else if (parameters.isEmpty())
			result = "O comando 'mkdir' não tem efeito sem parâmetros";
		else if (parameters.equals("/"))
			result = "É necessário fornecer um nome para o diretório";
		else {
			int index = parameters.lastIndexOf('/') + 1;
			String nome = parameters;
			int pai = hd.getPosAtual();
			String[] diretorios = null;

			if (index != 0) {
				diretorios = parameters.substring(0, index).split("/");
				nome = parameters.substring(index);
			}

			if (nome.length() > 85)
				result = "O nome pode conter no máximo 85 caracteres";
			else if (nome.isEmpty())
				result = "O nome não pode começar com '/'";
			else if (nome.charAt(0) == '-' || nome.charAt(0) == '.' || nome.contains(" ") || nome.contains(".txt"))
				result = "O nome do diretório não pode conter espaços,\na string '.txt' e nem começar com: '-', '.', '/'";
			else {
				if (index != 0)
					pai = navegar(diretorios);

				if (pai != -1) {
					if (hd.limiteDeFilhos(pai))
						result = "O diretório onde foi solicitada a criação já atingiu\no limite de diretórios filhos";
					else if (hd.containsFilho(pai, nome))
						result = "Diretório já existente";
					else
						hd.addFilho(nome, pai);
				}
			}

		}

		return result;
	}

	public String cd(String parameters) {

		result = "";
		if (parameters.isEmpty())
			result = "O comando 'cd' não tem efeito sem parâmetros";
		else {
			String[] diretorios = parameters.split("/");
			int destino = navegar(diretorios);

			if (destino != -1) {
				hd.setPosAtual(destino);
				String atual = operatingSystem.fileSystem.FileSytemSimulator.currentDir;
				String caminho = "";
				String aux = "";
				int i = 0;

				if (diretorios.length > 0)
					aux = diretorios[0];

				if (diretorios.length == 0 || aux.equals("")) {
					atual = "/";
					i++;
				}

				for (; i < diretorios.length; i++) {
					if (diretorios[i].equals(".."))
						if (caminho.isEmpty()) {
							atual = atual.substring(0, atual.lastIndexOf('/'));
							if (atual.isEmpty())
								atual = "/";
						} else {
							caminho = caminho.substring(0, caminho.length() - 1);
							caminho = caminho.substring(0, caminho.lastIndexOf('/') + 1);
						}
					else if (!diretorios[i].equals("."))
						caminho += diretorios[i] + "/";
				}

				if (!caminho.isEmpty()) {
					caminho = caminho.substring(0, caminho.length() - 1);
					caminho = "/" + caminho;
					if (atual.equals("/"))
						atual = "";
				}

				operatingSystem.fileSystem.FileSytemSimulator.currentDir = atual + caminho;
			}
		}

		return result;
	}

	public String rmdir(String parameters) {

		result = "";
		if (parameters.isEmpty())
			result = "O comando 'rmdir' precisa de ao menos 1 parâmetro";
		else if (parameters.equals("/"))
			result = "Não é possível remover a raiz";
		else {
			int atual = navegar(parameters.split("/"));

			if (atual != -1) {
				if (hd.filhos(atual).isEmpty() && hd.arquivos(atual).isEmpty()) {
					hd.remove(atual, true);

					if (atual == hd.getPosAtual())
						cd("..");
				} else
					result = "Diretório necessita estar vazio para ser removido";
			} else
				result += " (nada foi removido)";
		}

		return result;
	}

	public String cp(String parameters) {

		result = "";
		String[] param = parameters.split(" ");

		if (hd.hdLotado())
			result = "O HD está cheio, não é possível realizar cópias";
		else if (param.length < 2)
			result = "O comando 'cp' precisa de pelo menos 2 parâmetros";
		else {
			if (param.length == 3) {
				if (!param[0].equals("-R"))
					result = "No caso de 3 parâmetros o 1° deve ser '-R'";
				else if (param[1].equals("/"))
					result = "Não é possível copiar a raiz";
				else {
					int origem = navegar(param[1].split("/"));
					if (origem != -1) {
						int destino = navegar(param[2].split("/"));
						if (destino != -1) {
							if (hd.limiteDeFilhos(destino))
								result = "O diretório de destino já atingiu o limite de\n100 diretórios filhos";
							else if (hd.containsFilho(destino, hd.getNome(origem)))
								result = "Diretório de mesmo nome já presente,\nnão é possível copiar";
							else if (!(destino == origem) && !origemPaiDestino(origem, destino))
								copiarDiretorio(origem, hd.getNome(origem), destino);
							else
								result = "Não é possível copiar um diretório para si mesmo\nou para um de seus descendentes";
						}
					}
				}
			} else {
				int indexOrigem = param[0].lastIndexOf('/') + 1;
				int indexDestino = param[1].lastIndexOf('/') + 1;
				String arquivoOrigem, arquivoDestino = "";
				int origem, destino = hd.getPosAtual();

				if (indexOrigem == 0) {
					arquivoOrigem = param[0];
					origem = hd.getPosAtual();
					if (!arquivoOrigem.contains(".txt"))
						result = "Arquivo desejado para cópia não existe";
				} else {
					String[] caminhoOrigem = param[0].substring(0, indexOrigem - 1).split("/");
					arquivoOrigem = param[0].substring(indexOrigem);
					origem = navegar(caminhoOrigem);
					if (hd.containsArquivo(origem, arquivoOrigem))
						result = "Arquivo desejado para cópia não existe";
				}

				if (result.isEmpty()) {

					if (param[1].contains(".txt")) {
						if (indexDestino == 0) {
							arquivoDestino = param[1];
						} else {
							arquivoDestino = param[1].substring(indexDestino);
							destino = navegar(param[1].substring(0, indexDestino - 1).split("/"));
						}
					} else
						destino = navegar(param[1].split("/"));

					arquivoDestino = arquivoDestino.isEmpty() ? arquivoOrigem : arquivoDestino;

					if (result.isEmpty()) {
						if (hd.limiteDeArquivos(destino))
							result = "O diretório de destino já atingiu o limite de\n100 arquivos";
						else if (arquivoDestino.length() > 85)
							result = "O nome do arquivo pode conter no máximo 85 caracteres";
						else if (hd.containsArquivo(destino, arquivoDestino))
							result = "Arquivo de mesmo nome já presente,\nnão é possível copiar";
						else {
							int antigo = hd.getArquivo(origem, arquivoOrigem);
							hd.addArquivo(arquivoDestino, hd.getConteudo(origem, arquivoOrigem), destino);
							int novo = hd.getArquivo(destino, arquivoDestino);
							hd.setData(novo, hd.getData(antigo));
							hd.setPerm(novo, hd.getPerm(antigo));
						}
					}
				}
			}
		}

		return result;
	}

	private void copiarDiretorio(int origem, String nome, int destino) {
		hd.addFilho(nome, destino);
		int pos = hd.getFilho(destino, nome);
		hd.setPerm(pos, hd.getPerm(origem));
		hd.setData(pos, hd.getData(origem));

		hd.arquivos(origem).forEach((key) -> {
			String name = hd.getNome(key);
			hd.addArquivo(name, hd.getConteudo(origem, name), pos);
			int endereco = hd.getArquivo(pos, name);
			hd.setData(endereco, hd.getData(key));
			hd.setPerm(endereco, hd.getPerm(key));
		});

		hd.filhos(origem).forEach((key) -> copiarDiretorio(key, hd.getNome(key), pos));

		return;
	}

	public String mv(String parameters) {

		result = "";
		String[] param = parameters.split(" ");

		if (param.length < 2)
			result = "O comando 'mv' necessita de ao menos 2 parâmetros";
		else if (param[0].equals("/"))
			result = "Não é possível mover a raiz";
		else {
			int indexOrigem = param[0].lastIndexOf('/');
			int indexDestino = param[1].lastIndexOf('/');
			String nomeOrigem, nomeDestino;
			int origem, destino;
			boolean arquivoOrigem = false, arquivoDestino = false, renomear = false;

			if (indexOrigem == -1) {
				nomeOrigem = param[0];
				if (nomeOrigem.contains(".txt")) {
					origem = hd.getPosAtual();
					arquivoOrigem = true;
				} else {
					String[] caminho = { param[0] };
					origem = navegar(caminho);
				}
			} else {
				String caminho;
				nomeOrigem = param[0].substring(indexOrigem + 1);

				if (nomeOrigem.contains(".txt")) {
					arquivoOrigem = true;
					caminho = param[0].substring(0, indexOrigem);
				} else
					caminho = param[0];

				origem = navegar(caminho.split("/"));
			}

			if (arquivoOrigem && !hd.containsArquivo(origem, nomeOrigem))
				return "Arquivo especificado não existe";

			if (indexDestino == -1) {
				nomeDestino = param[1];
				if (nomeDestino.contains(".txt")) {
					destino = hd.getPosAtual();
					arquivoDestino = true;
				} else {
					String[] caminho = { param[1] };
					destino = navegar(caminho);
				}
			} else {
				String caminho;
				nomeDestino = param[1].substring(indexDestino + 1);

				if (nomeDestino.contains(".txt")) {
					arquivoDestino = true;
					caminho = param[1].substring(0, indexDestino);
				} else
					caminho = param[1];

				destino = navegar(caminho.split("/"));
			}
			renomear = destino == -1 && !arquivoOrigem && indexDestino == -1 ? true : false;

			if (result.isEmpty() || renomear) {

				if (renomear) {
					if(nomeDestino.charAt(0) == '-')
						result = "O nome não pode conter '-' na primeira posição";
					else
						result = hd.setNome(origem, nomeDestino) ? "" : "O nome pode conter no máximo 85 caracteres";
				}

				else if (arquivoOrigem) {

					if (hd.containsArquivo(destino, nomeDestino))
						result = "Nome já pertencente a outro arquivo na pasta,\nportanto não é possível renomear";
					else {

						if (hd.limiteDeArquivos(destino))
							result = "O diretório de destino já atingiu o limite de\n100 arquivos";
						else if (hd.containsArquivo(destino, arquivoDestino ? nomeDestino : nomeOrigem))
							result = "Nome de arquivo já existente na pasta de destino,\nportanto não é possível mover";
						else {

							int endereco = hd.getArquivo(origem, nomeOrigem);

							if (arquivoDestino)
								result = hd.setNome(hd.getArquivo(origem, nomeOrigem), nomeDestino) ? ""
										: "O nome do arquivo pode conter no máximo\n85 caracteres";

							hd.trocaPaiArquivo(endereco, origem, destino);

						}
					}

				} else if (arquivoDestino) {

					result = "Não é possível mover um diretório para um arquivo";

				} else {

					if (hd.limiteDeFilhos(destino))
						result = "O diretório de destino já atingiu o limite de\n100 diretórios filhos";
					else if (origem == destino)
						result = "Não é possível mover um diretório para si mesmo";
					else if (hd.containsFilho(destino, nomeOrigem))
						result = "Nome já existente na pasta de destino,\nnão foi possível mover";
					else if (origemPaiDestino(origem, destino))
						result = "Não é possível mover um diretório para um de\nseus descendentes";
					else {
						hd.trocaPai(origem, destino);
						if (origem == hd.getPosAtual())
							cd("/" + hd.getNome(destino) + "/" + hd.getNome(origem));
					}

				}

			}
		}

		return result;
	}

	private boolean origemPaiDestino(int origem, int destino) {
		boolean pai = false;
		int atual = destino;
		int paiAtual = hd.pai(atual);

		while (!(atual == paiAtual) && !pai) {
			if (paiAtual == origem)
				pai = true;
			atual = paiAtual;
			paiAtual = hd.pai(atual);
		}

		return pai;
	}

	public String rm(String parameters) {

		result = "";
		if (parameters.isEmpty())
			result = "O comando 'rm' precisa de ao menos 1 parâmetro";
		else {
			String param[] = parameters.split(" ");
			int argc = param.length - 1;
			if (param[argc].equals("/"))
				result = "Não é possível remover a raiz";
			else {
				boolean menosR = param[0].equals("-R") ? true : false;
				String caminho = param[argc];
				String arquivo = null;
				int atual;

				if (!menosR) {

					int index = param[0].lastIndexOf('/');

					if (index == -1) {
						caminho = ".";
						arquivo = param[0];
					} else {
						caminho = param[0].substring(0, index);
						arquivo = param[0].substring(index + 1);
					}

				}

				atual = navegar(caminho.split("/"));

				if (atual != -1) {
					if (!menosR) {
						if (hd.containsArquivo(atual, arquivo))
							hd.removeArquivo(hd.getArquivo(atual, arquivo), atual, true);
						else
							result = "O arquivo especificado não existe";
					} else if (hd.pai(atual) == atual)
						result = "Não é possível remover a raiz";
					else {

						removeFilhos(atual);
						Object aux = atual;
						hd.posLivres.remove(aux);
						hd.remove(atual, true);
						if (atual == hd.getPosAtual())
							cd("..");
					}
				} else
					result += " (nada foi removido)";
			}
		}

		return result;
	}

	private void removeFilhos(int atual) {
		hd.arquivos(atual).forEach((pos) -> hd.posLivres.add(pos));
		hd.filhos(atual).forEach((pos) -> removeFilhos(pos));
		hd.posLivres.add(atual);
	}

	public String chmod(String parameters) {

		result = "";
		String[] param = parameters.split(" ");

		if (param.length < 2)
			result = "O comando 'chmod' precisa de pelo menos 2 parâmetros";
		else {

			boolean menosR = param[0].equals("-R") ? true : false;
			if (menosR && param.length < 3)
				result = "O comando 'chmod' com o parâmetro '-R'\nexige 3 parâmetros";
			else {
				boolean arquivo = false;
				String permissao;
				String[] caminho;
				int indexParam = 1;
				String nomeArquivo = "";
				if (menosR)
					indexParam++;

				permissao = param[indexParam - 1];

				if (param[indexParam].contains(".txt")) {
					if (menosR)
						return "O parâmetro '-R' não deve ser utilizado em arquivos";
					else {
						int indexBarra = param[indexParam].lastIndexOf('/');
						arquivo = true;

						if (indexBarra == -1) {
							nomeArquivo = param[indexParam];
							param[indexParam] = ".";
						} else {
							nomeArquivo = param[indexParam].substring(indexBarra + 1);
							param[indexParam] = param[indexParam].substring(0, indexBarra);
						}
					}
				}
				caminho = param[indexParam].split("/");
				int tam = permissao.length();

				if (tam != 3)
					result = "A permissão deve conter 3 números";
				else {
					int i = 0;
					while (i < tam && result.isEmpty()) {
						int num = permissao.charAt(i++) - '0';
						if (num > 7 || num < 0)
							result = "A permissão deve conter apenas\nnúmeros naturais abaixo de 8";
					}

					if (result.isEmpty()) {
						int alvo = navegar(caminho);
						if (alvo != -1) {
							if (arquivo) {
								if (hd.containsArquivo(alvo, nomeArquivo))
									hd.setPerm(hd.getArquivo(alvo, nomeArquivo), permissao);
								else
									result = "Arquivo especificado não existe";
							} else {
								if (menosR) {
									alteraPermissao(alvo, permissao);
								} else {
									hd.setPerm(alvo, permissao);
								}
							}
						}
					}

				}
			}
		}

		return result;
	}

	public void alteraPermissao(int no, String permissao) {
		hd.filhos(no).forEach((key) -> alteraPermissao(key, permissao));
		hd.arquivos(no).forEach((key) -> hd.setPerm(key, permissao));
		hd.setPerm(no, permissao);

		return;
	}

	public String createfile(String parameters) {

		result = "";
		String[] param = parameters.split(" ");

		if (hd.hdLotado())
			result = "O HD está cheio, não é possível criar mais arquivos";
		else if (param.length < 2)
			result = "O comando 'createfile' precisa\nde pelo menos 2 parâmetros";
		else {
			int tam = param[0].length();

			if (tam < 4)
				result = "O nome do arquivo deve terminar com '.txt'";
			else if (tam > 85)
				result = "O nome do arquivo pode conter no máximo\n85 caracteres contando com a extensão";
			else {
				if (!param[0].substring(tam - 4).equals(".txt"))
					result = "O nome do arquivo deve terminar com '.txt'";
				else {
					int index = param[0].lastIndexOf('/');
					int alvo;
					String nome;
					if (index == -1) {
						alvo = hd.getPosAtual();
						nome = param[0];
					} else {
						String[] caminho = param[0].substring(0, index).split("/");
						nome = param[0].substring(index + 1);
						alvo = navegar(caminho);
					}
					if (result.isEmpty()) {

						if (hd.limiteDeArquivos(alvo))
							result = "O diretório onde foi solicitada a criação\njá atingiu o limite de 100 arquivos";
						else if (hd.containsArquivo(alvo, nome))
							result = "Arquivo já existente";
						else {
							String conteudo = "";

							for (int i = 1; i < param.length; i++) {
								conteudo += param[i] + " ";
							}
							conteudo = conteudo.trim();
							if (conteudo.length() > 406)
								result = "O conteúdo do arquivo pode conter no máximo\n406 caracteres";
							else
								hd.addArquivo(nome, conteudo, alvo);
						}
					}
				}
			}
		}

		return result;
	}

	public String cat(String parameters) {

		result = "";
		if (parameters.isEmpty())
			result = "O comando 'cat necessita de ao menos 1 parâmetro'";
		else {
			int index = parameters.lastIndexOf('/');
			String arquivo = parameters;
			int alvo = hd.getPosAtual();
			String caminho = ".";

			if (index != -1) {
				arquivo = parameters.substring(index + 1);
				caminho = parameters.substring(0, index);
				alvo = navegar(caminho.split("/"));
			}

			if (alvo != -1) {
				cd(caminho);
				if (hd.containsArquivo(alvo, arquivo)) {
					String conteudo = hd.getConteudo(alvo, arquivo);
					if (conteudo.contains("\\n")) {
						String[] aux = conteudo.split("\\\\n");
						conteudo = "";
						for (String string : aux)
							conteudo += string + "\n";
						conteudo = conteudo.substring(0, conteudo.length() - 1);
					}
					result = conteudo;
				} else
					result = "Arquivo informado não existe";
			}
		}

		return result;
	}

	public String batch(String parameters) {

		result = "";

		if (parameters.contains(" "))
			result = "O caminho não pode conter espaços";
		else {
			Path path = Paths.get(parameters);
			try {
				Scanner scan = new Scanner(path);
				String comando, param = "";
				int index;
				while (scan.hasNextLine()) {

					param = scan.nextLine();
					index = param.indexOf(' ');

					if (index == -1)
						comando = param;
					else {
						comando = param.substring(0, index);
						param = param.substring(index + 1);
					}

					switch (comando) {
					case "mkdir":
						this.mkdir(param);
						break;
					case "cd":
						this.cd(param);
						break;
					case "chmod":
						this.chmod(param);
						break;
					case "createfile":
						this.createfile(param);
						break;
					case "rm":
						this.rm(param);
						break;
					case "rmdir":
						this.rmdir(param);
						break;
					case "cp":
						this.cp(param);
						break;
					case "cat":
						this.cat(param);
						break;
					case "info":
						this.info();
						break;
					case "ls":
						this.ls(param);
						break;
					case "mv":
						this.mv(param);
						break;
					case "dump":
						this.dump(param);
						break;
					}
				}

				result = "Comandos executados";
				scan.close();

			} catch (IOException e) {
				result = "O arquivo especificado não existe";
			}
		}

		return result;
	}

	public String dump(String parameters) {

		result = "";

		try {

			FileWriter dump = new FileWriter(parameters);
			int raiz = navegar(new String[0]);

			hd.arquivos(raiz).forEach((key) -> {
				try {
					String nome = hd.getNome(key);
					dump.write("createfile " + nome + " " + hd.getConteudo(raiz, nome) + "\n");
					String permArquivo = hd.getPerm(key);
					if (!permArquivo.equals("777"))
						dump.write("chmod " + permArquivo + " " + nome + "\n");
				} catch (IOException e) {
					e.printStackTrace();
				}
			});

			String permissao = hd.getPerm(raiz);
			if (!permissao.equals("777")) {
				dump.write("chmod " + permissao + " ." + "\n");
			}

			criarDump(dump, raiz);
			result = "Dump criado com sucesso";
			dump.close();

		} catch (IOException e) {
			result = "Erro ao criar o dump";
		}

		return result;
	}

	private void criarDump(FileWriter dump, int atual) {

		hd.filhos(atual).forEach((key) -> {
			try {
				String nome = hd.getNome(key);
				String permissao = hd.getPerm(key);
				dump.write("mkdir " + nome + "\n");

				if (!permissao.equals("777"))
					dump.write("chmod " + permissao + " " + nome + "\n");

				hd.arquivos(key).forEach((posArquivo) -> {
					String arquivo = hd.getNome(posArquivo);
					String permArquivo = hd.getPerm(posArquivo);
					try {
						dump.write("createfile " + nome + "/" + arquivo + " " + hd.getConteudo(key, arquivo) + "\n");

						if (!permArquivo.equals("777"))
							dump.write("chmod " + permArquivo + " " + nome + "/" + arquivo + "\n");

					} catch (IOException e) {
						e.printStackTrace();
					}
				});

				dump.write("cd " + nome + "\n");
				criarDump(dump, key);
				dump.write("cd .." + "\n");

			} catch (IOException e) {
				e.printStackTrace();
			}
		});

	}

	public String info() {

		result = "";

		String name = "Nathan Augusto Rufino";

		String registration = "202011020015";

		String version = "0.1";

		result += "Nome do Aluno:        " + name;
		result += "\nMatricula do Aluno:   " + registration;
		result += "\nVersao do Kernel:     " + version;

		return result;
	}
}
