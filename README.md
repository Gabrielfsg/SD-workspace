## Banco Sistemas Sistribuidos
Esse projeto é um tabalho prático feito com arquitetura de sistemas distribuidos.
O projeto tem como objetivo aplicar os conceitos demostrados nas aulas teoricas e práticas do
Prof. Evertho do IFM - Campos Formiga.

## Ferramentas
O sistema utiliza a aplicacao de comunicação RMI, Multicast e JGroups.
Cada um tem uma responsabilidade especifica:
Para comunicação entre o cliente e servidores, utilizamos o RMI para comunicar o Cliente com o Servidor(s),
para a comunicação e distrubuicao entre os servidores, utilizamos o JGroups. Para descoberta do coordenados a qual é nossa
ponte entre o cliente e o servidor utilizamos a cominicação multicast.

## Compilando o projeto

Pra compilar o projeto utilize:

    mvn clean install

Navegue dentro da pasta target/classes e você encontrará os respectivos arquivos compilados.

A classe cliente e/ou servidor podem ser executadas pelo jar gerados dentro da pasta target com o seguinte comando (você deve estar dentro da pasta no seu terminal):

    java -jar cliente.jar


# Agora... caso queira uma forma mais rápida de executar o programa:

Abra um terminal e execute (para plataforma windows):

    ./windowsRun


 Abra um terminal e execute (para plataforma Linux):
    
    ./linuxRun

## Requisitos Funcionais

- Login
- Cadastro
- Visualização de Saldo
- Transferência de Dinheiro (saldo não pode ficar negativo)
- Ver Extrato

## Requisitos Não Funcionais Básicos

- Definição da pilha de protocolos JGROUPS (.XML)
- Um relatório apresentando a arquitetura do sistema, explicando e  justificando as principais decisões do projeto.
Justificando a escolha dos protocolos, análise de desempenho da pilha de protocolos (tempo gasto, msgs por segundo e vaãzão do text com o MPerf configurado para 100k msgs).
Apontando os pontos fortes e fracos da solução.
- Distribuição vertical (MVC)
- Distribuição Horizontal (replicação de componentes em cada camada)
O sistema deverá ser tolerante a falhas, permanecendo operante mesmo se membros do cluster falharem.
- Novos membros do cluster devem receber uma transferencia de estado. Multicast confiável e com ordenação das mensagens.

## Requisitos Não Funcionais Intermediários
- O valor do montante de dinheiro do banco deve ser visível e consistente em todos os membros do cluster.
- O sistema distribuído deve utilizar mais de um canal (JChannel) ou preferencialmente subcanais
(ForkChannel) de comunicação para devidamente implementar a distribuição vertical (ex.: clusters
“modelo”, “controle”, “visão”), segmentando as mensagens trocadas conforme a função de cada
componente;
- O sistema distribuído deve providenciar armazenamento em disco do estado do sistema (cadastros,
movimentações, etc.) em memória secundária (persistente);
- No reingresso de um membro ao cluster, deverá ser obtido o estado do sistema, ou seja, atualizações
que ele possa não ter recebido enquanto estava desconectado;
- O sistema deverá prover mecanismos de segurança, como criptografia das mensagens trocadas,
autenticação dos usuários e autenticidade das solicitações.

