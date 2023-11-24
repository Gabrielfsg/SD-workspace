@echo off

:: Limpar compilações anteriores
call mvn clean

:: Empacotar o projeto em um arquivo JAR
call mvn package

:: Caminho para o diretório onde o JAR é gerado (por padrão, é a pasta 'target')
set caminho=target

:: Verificar se o arquivo JAR existe no diretório especificado
if not exist "%caminho%\cliente.jar" (
    echo Erro: O arquivo cliente.jar não foi encontrado em %caminho%.
    exit /b 1
)

if not exist "%caminho%\server.jar" (
    echo Erro: O arquivo server.jar não foi encontrado em %caminho%.
    exit /b 1
)

:: Executar o primeiro cliente em uma nova janela
start java -jar "%caminho%\server.jar"

:: Aguardar dois segundos
timeout /t 5 /nobreak > nul

:: Executar o segundo cliente em uma nova janela
start java -jar "%caminho%\cliente.jar"
start java -jar "%caminho%\cliente.jar"