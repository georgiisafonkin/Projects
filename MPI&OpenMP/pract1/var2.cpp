#include <mpi.h>
#include <iostream>
#include <cstdlib>

int main(int argc, char** argv) {

    MPI_Init(&argc, &argv);

    int rank, size;
    MPI_Comm_size(MPI_COMM_WORLD, &size);
    MPI_Comm_rank(MPI_COMM_WORLD, &rank);

    int N = std::atoi(argv[1]);
    int s = 0;
    int recv_send_s = 0;
    int offset = N / size;

    if (rank == 0) {
        double t1 = MPI_Wtime();
        int* a = new int[N];
        for (int i = 0; i < N; ++i) {
            a[i] = i;
        }

        int* b = new int[N];
        for (int i = 0; i < N; ++i) {
            b[i] = i;
        }

        for (int i = 1; i < size; ++i) {
            MPI_Send(a + i*offset, offset, MPI_INT, i, 123123, MPI_COMM_WORLD);
            MPI_Send(b, N, MPI_INT, i, 123123, MPI_COMM_WORLD);
        }

        for (int i = 0; i < offset; ++i) {
            for (int j = 0; j < N; ++j) {
                s += a[i] * b[j];
            }
        }

        for (int i = 1; i < size; ++i) {
            MPI_Recv(&recv_send_s, 1, MPI_INT,
                     i, 123123, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
            s += recv_send_s;
        }

        std::cout << "S is: " << s << std::endl;
        double t2 = MPI_Wtime();
        std::cout << "Wtime: " << t2 -t1 << "seconds.\n";

        delete[] a;
        delete[] b;
    }
    else {
        int* a = new int[offset];

        int* b = new int[N];

        MPI_Recv(a, offset, MPI_INT,
                 0, 123123, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
        MPI_Recv(b, N, MPI_INT,
                 0, 123123, MPI_COMM_WORLD, MPI_STATUS_IGNORE);

        for (int i = 0; i < offset; ++i) {
            for (int j = 0; j < N; ++j) {
                recv_send_s += a[i] * b[j];
            }
        }
        MPI_Send(&recv_send_s, 1, MPI_INT, 0, 123123, MPI_COMM_WORLD);

        delete[] a;
        delete[] b;
    }

    MPI_Finalize();

    return 0;
}