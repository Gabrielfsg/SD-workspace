import grpc

def startClient():
    startMenu()

def startMenu():
    while (True):
        print("### Bem Vindo ### \n"
              "1.Criar Conta \n"
              "2.Login. \n"
              "3.Sair")
        opc = input("Escolha uma opção: ")
        if (opc == '1'):
            createAccount()
        elif (opc == '2'):
            loginAccount()
        elif (opc == '3'):
            breakConnection(False)
        else:
            print("Digite uma opção Válida!")


def breakConnection(isLogged):
    print("Encerrar Conexão")
    exit()

def createAccount():
    print("Criação de Conta")

def loginAccount():
    print("Login")

def checkBalance():
    print("Consultar saldo usuário")

def makeTransfer():
    print("Fazer Transferência")

def changeUserData():
    print("Fazer Transferência")

def menuLoginSucess():
    while (True):
        print("### Bem Vindo ### \n"
              "1.Consultar Saldo \n"
              "2.Fazer Transferência. \n"
              "3.Alterar Dados. \n"
              "4.Sair")
        opc = input("Escolha uma opção: ")
        if (opc == '1'):
            checkBalance()
        elif (opc == '2'):
            makeTransfer()
        elif (opc == '3'):
            changeUserData()
        elif (opc == '4'):
            breakConnection(True)
        else:
            print("Digite uma opção Válida!")

if __name__ == '__main__':
    startClient()