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

    double t1 = MPI_Wtime();
    int* b = new int[N];
    int* a_piece = new int[offset];

    if (rank == 0) {
        int *a = new int[N];
        for (int i = 0; i < N; ++i) {
            a[i] = i;
        }

        for (int i = 0; i < N; ++i) {
            b[i] = i;
        }

        MPI_Scatter(a, offset, MPI_INT, a_piece, offset, MPI_INT, 0, MPI_COMM_WORLD);

        delete[] a;
    }
    else {
        MPI_Scatter(NULL, offset, MPI_INT, a_piece, offset, MPI_INT, 0, MPI_COMM_WORLD);
    }

    MPI_Bcast(b, N, MPI_INT, 0, MPI_COMM_WORLD);

    for (int i = 0; i < offset; ++i) {
        for (int j = 0; j < N; ++j) {
            recv_send_s += a_piece[i] * b[j];
        }
    }

    MPI_Reduce(&recv_send_s, &s, 1, MPI_INT, MPI_SUM, 0, MPI_COMM_WORLD);

    if (rank == 0) {
        std::cout << "S is: " << s << std::endl;
        double t2 = MPI_Wtime();
        std::cout << "Wtime: " << t2 - t1 << " seconds.\n";
    }

    delete[] b;
    delete[] a_piece;

    MPI_Finalize();

    return 0;
}