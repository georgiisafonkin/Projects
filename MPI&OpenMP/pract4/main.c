#include <stdio.h>
#include <stdlib.h>
#include <mpi/mpi.h>

void print_vector(double* v, size_t size);
void print_matrix(double* m, size_t n1, size_t n2);

int main(int argc, char* argv[]) {
    if (argc < 6) {
        printf("too few program arguments\n");
        printf("<P1> <P2> <N1> <N2> <N3>\n");
        return EXIT_FAILURE;
    }

    int P1, P2, N1, N2, N3;

    P1 = atoi(argv[1]);
    P2 = atoi(argv[2]);
    N1 = atoi(argv[3]);
    N2 = atoi(argv[4]);
    N3 = atoi(argv[5]);

    double* A = (double*)calloc(N1*N2, sizeof(double));
    double* A_local = (double*) calloc(N1*N2/P1, sizeof(double ));
    double* B = (double*)calloc(N2*N3, sizeof(double));
    double* B_local = (double*)calloc(N2 * N3/P2, sizeof(double));
    double* C = (double*)calloc(N1*N3, sizeof(double));
    double* C_local = (double*)calloc((N1*N3)/(P1*P2), sizeof(double));

    int rank, size;
    int coords[2];
    int dims[2] = {P1, P2};
    int periods[2] = {0,0};

    int row_size;
    int column_size;

    MPI_Init(&argc, &argv);
    double t1 = MPI_Wtime();

    //Creating 2D P1xP2 grid
    MPI_Comm grid_comm;
    MPI_Comm row_comm;
    MPI_Comm column_comm;
    int save_columns[2] = {0, 1};
    int save_rows[2] = {1, 0};
    MPI_Cart_create(MPI_COMM_WORLD, 2, dims, periods, 0, &grid_comm);
    MPI_Cart_sub(grid_comm, save_rows, &row_comm);
    MPI_Cart_sub(grid_comm, save_columns, &column_comm);

    MPI_Comm_rank(grid_comm, &rank);
    MPI_Cart_coords(grid_comm, rank, 2, coords);
    MPI_Comm_size(grid_comm, &size);
    int count = 0;
    if (coords[0] == 0 && coords[1] == 0) {
        //Matrix A initialization
        for (int i = 0; i < N1; ++i) {
            for (int j = 0; j < N2; ++j) {
                A[i*N2 + j] = ++count;//i+j;
            }
        }
//        print_matrix(A, N1, N2);
//        printf("\n");
        //Matrix B initialization
        count = 0;
        for (int i = 0; i < N2; ++i) {
            for (int j = 0; j < N3; ++j) {
                B[i*N3 + j] = ++count;;
            }
        }
//        print_matrix(B, N2, N3);
//        printf("\n");
    }
    if (coords[1] == 0)
        MPI_Scatter(A, N1*N2/P1, MPI_DOUBLE, A_local, N1*N2/P1, MPI_DOUBLE, 0, row_comm);

    MPI_Comm_size(column_comm, &column_size);

    //new_type that represents the vertical strip of matrix (a single or few columns)
    MPI_Datatype columns_type;
    MPI_Type_vector(N2, N3/P2, N3, MPI_DOUBLE, &columns_type);
    MPI_Type_commit(&columns_type);
    for (int i = 0; i < column_size; ++i) {
        if (rank == 0) {
            if (i != 0) {
                MPI_Send(B + N3/P2 *i, 1, columns_type, i, i, column_comm);
            }
            else {
                for (int j = 0; j < N2; ++j) {
                    for (int k = 0; k < N3/P2; ++k) {
                        B_local[j * N3/P2 + k] = B[j*N3 + k];
                    }
                }
            }
        }
        if (rank == i && rank != 0) {
            MPI_Recv(B_local, N2 * N3/P2, MPI_DOUBLE, 0, i, column_comm, MPI_STATUS_IGNORE);
        }
    }
    MPI_Type_free(&columns_type);

    MPI_Bcast(A_local, N1 * N2/P1, MPI_DOUBLE, 0, column_comm);

    MPI_Bcast(B_local, N2 * N3/P2, MPI_DOUBLE, 0, row_comm);

    //computing minors of C
    for (int i = 0; i < N1/P1; ++i) {
        for (int j = 0; j < N3/P2; ++j) {
            for(int k = 0; k < N2; ++k) {
                C_local[i * N3/P2 + j] += A_local[N2 * i + k] * B_local[k*N3/P2 + j];
            }
        }
    }

    MPI_Datatype minors_type;
    MPI_Type_vector(N1/P1, N3/P2, N3, MPI_DOUBLE, &minors_type);
    MPI_Type_commit(&minors_type);
    if (rank != 0) {
        MPI_Send(C_local, (N1*N3)/(P1*P2), MPI_DOUBLE, 0, 1, MPI_COMM_WORLD);
    }
    else {
        for (int i = 0; i < P1; ++i) {
            for(int j = 0; j < P2; ++j) {
                if (i != 0 || j != 0) {
                    MPI_Recv(C + (i * N3 *N1/P1 + j * N3/P2), 1, minors_type, i * P2 + j, 1, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
                }
            }
        }
        for (int i = 0; i < N1/P1; ++i) {
            for(int j = 0; j < N3/P2; ++j) {
                C[i*N3 + j] = C_local[i*N3/P2 +j];
            }
        }
    }

    MPI_Type_free(&minors_type);

    if (rank == 0) {
//        print_matrix(C, N1, N3);
//        printf("\n");
        printf("The job is done.\nTime: %lf\n", MPI_Wtime() - t1);
    }

    MPI_Finalize();
    return EXIT_SUCCESS;
}

void print_vector(double* v, size_t size) {
    int i = 0;
    while (i < size) {
        printf("%lf ", v[i]);
        ++i;
    }
    printf("\n");
    printf("\n");
    printf("\n");
}

void print_matrix(double* m, size_t n1, size_t n2) {
    for (int i = 0; i < n1; ++i) {
        for (int j = 0; j < n2; ++j) {
            printf("%lf ", m[i*n2 + j]);
        }
        printf("\n");
    }
}
