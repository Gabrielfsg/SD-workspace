import grpc
from concurrent import futures


def iniciar():
    server = grpc.server(futures.ThreadPoolExecutor(max_workers=10))


if __name__ == '__main__':
    iniciar()