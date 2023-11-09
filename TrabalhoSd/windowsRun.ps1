# ./windowsRun
# Compila o projeto com o Maven
mvn clean install

# Inicia o servidorRMI em outro terminal
Start-Process -FilePath "cmd.exe" -ArgumentList "/k java -jar target/servidorRMI.jar"

# Espera alguns segundos para que o servidorRMI inicie antes de iniciar o cliente
Start-Sleep -Seconds 2

# Inicia o cliente em outro terminal
Start-Process -FilePath "cmd.exe" -ArgumentList "/k java -jar target/cliente.jar"
