#!/bin/bash

# Limpar compilações anteriores
mvn clean

# Empacotar o projeto em um arquivo JAR
mvn package

# Caminho para o diretório onde o JAR é gerado (por padrão, é a pasta 'target')
caminho=target

# Verificar se o arquivo JAR existe no diretório especificado
if [ ! -e "$caminho/cliente.jar" ]; then
    echo "Erro: O arquivo cliente.jar não foi encontrado em $caminho."
    exit 1
fi

if [ ! -e "$caminho/server.jar" ]; then
    echo "Erro: O arquivo server.jar não foi encontrado em $caminho."
    exit 1
fi

# Executar o primeiro cliente em uma nova janela
java -jar "$caminho/server.jar" &

# Aguardar dois segundos
sleep 5

# Executar o segundo cliente em uma nova janela
java -jar "$caminho/cliente.jar" &
java -jar "$caminho/cliente.jar" &
