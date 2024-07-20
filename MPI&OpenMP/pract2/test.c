#include<stdio.h>
#include<stdlib.h>
#include<mpi.h>

int main(int argc, char* argv[]) {
    int rank, size;
    MPI_Init(&argc, &argv);
    MPI_Comm_size(MPI_COMM_WORLD, &size);
    MPI_Comm_rank(MPI_COMM_WORLD, &rank);
    int a = 1337;
    int b = (rank+1)*10;
    printf("This is b: %d, from process #%d\n", b, rank);
    MPI_Allreduce(&b, &a, 1,
                  MPI_INTEGER, MPI_SUM, MPI_COMM_WORLD);
    printf("This is a: %d, from process #%d\n", a, rank);
    return 0;
}