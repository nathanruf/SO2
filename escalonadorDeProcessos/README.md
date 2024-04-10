# Simulador de Escalonamento de Processos

Este é um projeto de simulador desenvolvido para avaliar as principais técnicas de Escalonamento de Processos. Ele foi criado com o objetivo de implementar e visualizar diferentes algoritmos de escalonamento de processos, fornecendo métricas para avaliação de desempenho.

## Funcionalidades Implementadas

### Técnicas de Escalonamento:

1. **Escalonamento em Lote**:
    - First come, First served (FCFS)
    - Job mais curto primeiro
    - Próximo de menor tempo restante

2. **Escalonamento Interativo**:
    - Escalonamento Circular (Round Robin)
    - Escalonamento por prioridades (com 4 níveis de prioridade)
    - Escalonamento por loteria (com 4 níveis de prioridade)

### Métricas Implementadas:

- Tempo total para simulação de todos os processos
- Tempo máximo de espera de um processo na fila para execução
- Tempo médio de espera de um processo na fila para execução
- Número total de trocas de contexto
- Tempo médio de ociosidade registrado pela CPU
- Tempo máximo de ociosidade registrado pela CPU

### Modos de Execução:

1. **Passo-a-passo**: Simulação onde cada clique do mouse representa a execução de um ciclo/unidade de tempo do processador.
2. **Tempo Real**: Simulação onde cada ciclo/unidade de tempo do processador é equivalente a 1 segundo.
3. **Completa**: Simulação que executa toda a simulação em background e apresenta apenas o resultado final na interface.

## Formato de Entrada

O simulador utiliza o seguinte formato como padrão de entrada para simulação:

tempo_de_chegada, tempo_de_serviço, prioridade
