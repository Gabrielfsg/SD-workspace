import grpc


def iniciar():
    with grpc.insecure_channel('localhost:5001') as channel:
        print()


if __name__ == '__main__':
    iniciar()
