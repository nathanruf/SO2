# Simulador de Sistema de Arquivos

Esse projeto oferece uma interface gráfica para interação e simula as principais operações de um sistema de arquivos. As informações das pastas e arquivos criados pelo usuário são armazenadas em um HD simulado.

### Funções Implementadas no simulador

- **batch:** Executa comandos de um arquivo de texto no sistema de arquivos simulado.
  - Uso: `batch caminho_do_arquivo.txt`

- **dump:** Gera um script capaz de recriar a estrutura atual do sistema de arquivos.
  - Uso: `dump nome_do_arquivo.txt`

- **cat:** Lista o conteúdo de um arquivo no sistema de arquivos simulado.
  - Uso: `cat caminho_do_arquivo`

- **cd:** Navega na estrutura de diretórios do sistema de arquivos simulado.
  - Uso: `cd caminho_do_diretorio`

- **chmod:** Define permissões de arquivos ou diretórios.
  - Uso: `chmod permissões caminho`

- **cp:** Copia arquivos ou diretórios para outro local.
  - Uso: `cp [-R] origem destino` (o [-R] indica a opção de cópia recursiva, copia também os diretórios e seu conteúdo)

- **createfile:** Cria um novo arquivo de texto com o conteúdo especificado.
  - Uso: `createfile caminho conteúdo`

- **info:** Imprime informações do trabalho no shell.
  - Uso: `info`

- **ls:** Lista o conteúdo de um diretório.
  - Uso: `ls [opções] [caminho]` (os [opções] e [caminho] são argumentos opcionais, por exemplo, `-l` para exibir em formato detalhado)

- **mkdir:** Cria um novo diretório.
  - Uso: `mkdir caminho`

- **mv:** Move ou renomeia arquivos ou diretórios.
  - Uso: `mv origem destino`

- **rm:** Remove arquivos ou diretórios.
  - Uso: `rm [-R] caminho` (o [-R] indica a opção de remoção recursiva, remove também os diretórios e seu conteúdo)

- **rmdir:** Remove diretórios vazios.
  - Uso: `rmdir caminho`
 
- **clear:** Limpa a tela do terminal.
  - Uso: `clear`

### HD Simulado

O HD simulado é um vetor do tipo booleano, onde as informações das pastas e arquivos são convertidas em binário e armazenadas. Segue a padronização do tamanho do registro/bloco para salvamento das informações:

- **Tamanho do Bloco = Diretório = Arquivo**: 512 bytes

#### Registro do Diretório:
- Nome: 81 bytes
- Permissão: 9 bytes
- Data da Criação: 20 bytes
- Endereço do Pai: 2 bytes
- Diretórios Filhos: 100 x 2 bytes = 200 bytes
- Arquivos: 100 x 2 bytes = 200 bytes

#### Registro do Arquivo:
- Nome + Extensão: 81 bytes
- Permissão: 9 bytes
- Data da Criação: 20 bytes
- Conteúdo: 402 bytes

Considerando essa padronização, a quantidade de endereços (2 bytes) é equivalente a 2^16. Cada endereço tem 512 bytes (2^9). Portanto, o tamanho máximo do HD simulado é 2^25 bytes, equivalente a 32 MB.
