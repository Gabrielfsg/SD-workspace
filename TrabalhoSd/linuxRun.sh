#!/bin/bash

javac BancoAPI.java
javac frotend.Cliente.java
javac Servidor.java


killall -9 rmiregistry
rmiregistry 1099 &

xterm -hold -e 'java Servidor' &
## OU
## execute o servidor da aplicação, que irá consultar o (servidor de) Registro RMI no IP informado
# xterm -hold -e 'java -Djava.rmi.server.hostname=172.22.70.30 CalculadoraServer' &

sleep 2

xterm -hold -e 'java frotend.Cliente' &
