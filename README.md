## Sistemas Sistribuidos 1 - Trabalho Prático
Esse projeto é um tabalho prático feito com arquitetura de sistemas distribuidos.
O projeto tem como objetivo aplicar os conceitos demostrados nas aulas teoricas e práticas do
Prof. Evertho do IFM - Campos Formiga.

Alunos:

1.Gabriel Fernandes Silva Gondim.
2. Daniel Antônio.
3. Luiz Otávio.

## Ferramentas
O sistema utiliza a aplicacao de comunicação RMI, Multicast e JGroups.
Cada um tem uma responsabilidade especifica:
Para comunicação entre o cliente e servidores, utilizamos o RMI para comunicar o Cliente com o Servidor(s),
para a comunicação e distrubuicao entre os servidores, utilizamos o JGroups. Para descoberta do coordenados a qual é nossa
ponte entre o cliente e o servidor utilizamos a cominicação multicast.

## Compilando o projeto

Caso esteja em uma idea, execute as classes: Cliente e Servidor.

Se quiser via terminal:

Abra um terminal e execute (para plataforma windows):

    windowsRun.bat


 Abra um terminal e execute (para plataforma Linux):
    
    ./linuxRun

## Requisitos Funcionais

- Login
- Cadastro
- Alterar Senha
- Saldo
- Transferência de Saldo (Saldo não pode chegar a 0)
- Ver Extrato

## Requisitos Básicos

• Prover alguma interface com o usuário (CLI ou GUI) para acesso rápido às funcionalidades do
serviço;
• Definição de um identificador único por conta no sistema, de maneira que um mesmo cliente seja
corretamente identificado ao sair e retornar;
• Cadastro de nova conta no sistema bancário sem duplicidade, conforme acordo dos membros do
cluster (todos ou maioria);
• Operação de transferência de valor entre contas bancárias (atente para a necessidade de acesso
mutuamente exclusivo às duas contas envolvidas);
• Operação de consulta ao saldo da conta, conforme acordo dos membros do cluster (primeira resposta
ou maioria);
• Operação de consulta ao extrato da conta, conforme acordo dos membros do cluster (primeira
resposta ou maioria);
• O valor do montante de dinheiro do banco (somatório do saldo de todas as contas) deve ser visível e
consistente em todos os membros do cluster JGroups, para fins de auditoria;

## Requisitos Intermediários

• O sistema distribuído deve utilizar mais de um canal (JChannel) ou preferencialmente subcanais
(ForkChannel) de comunicação para devidamente implementar a distribuição vertical (ex.: clusters
“modelo”, “controle”, “visão”), segmentando as mensagens trocadas conforme a função de cada
componente;
• O sistema distribuído deve providenciar armazenamento em disco do estado do sistema (cadastros,
movimentações, etc.) em memória secundária (persistente);
• No reingresso de um membro ao cluster, deverá ser obtido o estado do sistema, ou seja, atualizações
que ele possa não ter recebido enquanto estava desconectado;
• O sistema deverá prover mecanismos de segurança, como criptografia das mensagens trocadas,
autenticação dos usuários e autenticidade das solicitações.
• No reingresso de um membro ao cluster, deverá ser obtido o estado do sistema, ou seja, atualizações
que ele possa não ter recebido enquanto estava desconectado;
• O sistema deverá prover mecanismos de segurança, como criptografia das mensagens trocadas,
autenticação dos usuários e autenticidade das solicitações.

