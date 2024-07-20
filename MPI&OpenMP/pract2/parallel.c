#include<stdio.h>
#include<stdlib.h>
#include<string.h>
#include<math.h>
#include<mpi.h>
#define MAX_ITERATIONS_NUMBER 50000
void print_vector(double* vector, int size);
void print_matrix(double* matrix, int size);
double get_norm(double* vector, int N);
double get_scalar_product_part(double* vector1, double* vector2, int
vector_part_size);
void mul_matrix_part_with_the_vector(double* matrix_part, double* vector, double*
result_vector_part, int matrix_part_size, int N);
void sum_or_sub_vectors_parts(double* part1, double* part2, double*
result_vector_part, double k, int vector_part_size);
int main(int argc, char* argv[]) {
    int processes_number, rank;
    MPI_Init(&argc, &argv);
    double t1 = MPI_Wtime();
    MPI_Comm_size(MPI_COMM_WORLD, &processes_number);
    MPI_Comm_rank(MPI_COMM_WORLD, &rank);
    int N = atoi(argv[1]);
    double epsilon = atof(argv[2]);
    if (rank == 0) {
        printf("epsilon equals to %lf\n", epsilon);
        printf("N equals to %d\n", N);
    }
    epsilon *= epsilon;
    int matrix_part_size = N * N / processes_number;
    int vector_part_size = N / processes_number;
//important data
    double* a_matrix = (double*)malloc(N * N * sizeof(double));
    double* a_part = (double*)malloc(matrix_part_size * sizeof(double));
    double* b_vector = (double*)malloc(N * sizeof(double));
    double* b_part = (double*)malloc(vector_part_size * sizeof(double));
    double* x_vector = (double*)malloc(N * sizeof(double));
    double* x_part = (double*)malloc(vector_part_size * sizeof(double));
    double* r_vector = (double*)malloc(N * sizeof(double));
    double* r_part = (double*)malloc(vector_part_size * sizeof(double));
    double* z_vector = (double*)malloc(N * sizeof(double));
    double* z_part = (double*)malloc(vector_part_size * sizeof(double));
    double* azn_part = (double*)malloc(vector_part_size * sizeof(double));
    double alpha, beta, alpha_part, beta_part;
//A matrix, b vector and x vector initialization
    if (rank == 0) {
//a_matrix initialization
        for (int i = 0; i < N; ++i) {
            for (int j = 0; j < N; ++j) {
                if (i == j) {
                    a_matrix[i*N + j] = (double)N;
                }
                else {
                    a_matrix[i*N + j] = i * j;
                }
            }
        }
//b_vector initialization
        for(int i = 0; i < N; ++i) {
            b_vector[i] = N + 1;
        }
//x_vector initialization
        for(int i = 0; i < N; ++i) {
            x_vector[i] = 1;
        }
    }
    MPI_Bcast(b_vector, N, MPI_DOUBLE,
              0, MPI_COMM_WORLD);
    MPI_Bcast(x_vector, N, MPI_DOUBLE,
              0, MPI_COMM_WORLD);
    MPI_Scatter(x_vector, vector_part_size, MPI_DOUBLE,
                x_part, vector_part_size, MPI_DOUBLE,
                0, MPI_COMM_WORLD);
    MPI_Scatter(b_vector, vector_part_size, MPI_DOUBLE,
                b_part, vector_part_size, MPI_DOUBLE,
                0, MPI_COMM_WORLD);
    MPI_Scatter(a_matrix, matrix_part_size, MPI_DOUBLE,
                a_part, matrix_part_size, MPI_DOUBLE,
                0, MPI_COMM_WORLD);
//initial data getting
//r0 initialization
    double* tmp_part = (double*)malloc(vector_part_size * sizeof(double));
    mul_matrix_part_with_the_vector(a_part, x_vector, tmp_part, matrix_part_size,
                                    N);
    sum_or_sub_vectors_parts(b_part, tmp_part, r_part, -1, vector_part_size);
    free(tmp_part);
//z0 vector initialization
    memcpy(z_part, r_part, vector_part_size * sizeof(double));
    MPI_Allgather(z_part, vector_part_size, MPI_DOUBLE,
                  z_vector, vector_part_size, MPI_DOUBLE,
                  MPI_COMM_WORLD);
//Az0 part initialization
    mul_matrix_part_with_the_vector(a_part, z_vector, azn_part, matrix_part_size,
                                    N);
//getting scalar productions
    double scalar_production_part = 0;
    double azn_zn_scalar_production = 0;
    double rn_scalar_production = 0;
//getting (Az0, Z0)
    scalar_production_part = get_scalar_product_part(azn_part, z_part,
                                                     vector_part_size);
    MPI_Allreduce(&scalar_production_part, &azn_zn_scalar_production, 1,
                  MPI_DOUBLE,
                  MPI_SUM, MPI_COMM_WORLD);
//getting (r0, r0)
    scalar_production_part = get_scalar_product_part(r_part, r_part,
                                                     vector_part_size);
    MPI_Allreduce(&scalar_production_part, &rn_scalar_production, 1, MPI_DOUBLE,
                  MPI_SUM, MPI_COMM_WORLD);
//getting b norm
    double b_norm = get_norm(b_vector, N);
//loop preparation
    int iterations_counter = 0;
    double end_val = 0;
    double flag = 0;
    double norm_part = 0;
    double r_norm;
//iterations loop
    while((iterations_counter < MAX_ITERATIONS_NUMBER) && (flag < 3)) {
//getting alpha^n+1
        alpha = rn_scalar_production / azn_zn_scalar_production;
//getting x^n+1 part
        sum_or_sub_vectors_parts(x_part, z_part, x_part, alpha,
                                 vector_part_size);
//getting r^n+1 part
        sum_or_sub_vectors_parts(r_part, azn_part, r_part, -alpha,
                                 vector_part_size);
//getting beta
        scalar_production_part =
                get_scalar_product_part(r_part,r_part,vector_part_size);
        beta_part = scalar_production_part/rn_scalar_production;
        MPI_Allreduce(&beta_part, &beta, 1, MPI_DOUBLE, MPI_SUM, MPI_COMM_WORLD);
//getting z^n+1 part
        sum_or_sub_vectors_parts(r_part, z_part, z_part, beta, vector_part_size);
        MPI_Allgather(z_part, vector_part_size, MPI_DOUBLE,
                      z_vector, vector_part_size, MPI_DOUBLE,
                      MPI_COMM_WORLD);
//getting Azn+1 part
        mul_matrix_part_with_the_vector(a_part, z_vector, azn_part,
                                        matrix_part_size, N);
//getting (r^n+1, r^n+1)
        MPI_Allreduce(&scalar_production_part, &rn_scalar_production, 1,
                      MPI_DOUBLE, MPI_SUM, MPI_COMM_WORLD);
//getting (Az^n+1, z^n+1)
        scalar_production_part = get_scalar_product_part(azn_part, z_part,
                                                         vector_part_size);
        MPI_Allreduce(&scalar_production_part, &azn_zn_scalar_production, 1,
                      MPI_DOUBLE, MPI_SUM, MPI_COMM_WORLD);
//loop service
        ++iterations_counter;
        norm_part = get_norm(r_part, vector_part_size);
        MPI_Allreduce(&norm_part, &r_norm, 1,
                      MPI_DOUBLE, MPI_SUM, MPI_COMM_WORLD);
        end_val = (r_norm / b_norm);
        if (end_val < epsilon) {
            ++flag;
        }
        else {
            flag = 0;
        }
    }
    MPI_Gather(x_part, vector_part_size, MPI_DOUBLE,
               x_vector, vector_part_size, MPI_DOUBLE,
               0, MPI_COMM_WORLD);
    double t2 = MPI_Wtime();
    double time = t2 - t1;
    if (rank == 0) {
        print_vector(x_vector, N);
        printf("The number of iterations is %d\nTime: %lf\n",
               iterations_counter, time);
    }
//memory freeing
    free(a_matrix);
    free(a_part);
    free(x_vector);
    free(b_vector);
    free(r_part);
    free(z_part);
    free(azn_part);
    MPI_Finalize();
    return 0;
}
void print_vector(double* vector, int size) {
    for (int i = 0; i < size; ++i) {
        printf("%lf\n", vector[i]);
    }
}
void print_matrix(double* matrix, int size) {
    for (int i = 0; i < size; ++i) {
        for (int j = 0; j < size; ++j) {
            printf("%lf ", matrix[i*size + j]);
        }
        printf("\n");
    }
}
double get_norm(double* vector, int N) {
    double norm = 0;
    for (int i = 0; i < N; ++i) {
        norm += pow(vector[i], 2);
    }
    return norm;
}
double get_scalar_product_part(double* vector1, double* vector2, int
vector_part_size) {
    double scalar_production = 0;
    for(int i = 0; i < vector_part_size; ++i) {
        scalar_production += vector1[i] * vector2[i];
    }
    return scalar_production;
}
void mul_matrix_part_with_the_vector(double* matrix_part, double* vector, double*
result_vector_part, int matrix_part_size, int N) {
    for (int k = 0; k < matrix_part_size / N; ++k) {
        result_vector_part[k] = 0;
    }
    for (int i = 0; i < matrix_part_size / N; ++i) {
        for (int j = 0; j < N; ++j) {
            result_vector_part[i] += matrix_part[i*N + j] * vector[j];
        }
    }
}
void mul_vector_piece_with_the_number(double* vector_piece, double number, int
piece_size) {
    for (int i = 0; i < piece_size; ++i) {
        vector_piece[i] *= number;
    }
}
void sum_or_sub_vectors_parts(double* part1, double* part2, double*
result_vector_part, double k, int vector_part_size) {
    for (int i = 0; i < vector_part_size; ++i) {
        result_vector_part[i] = part1[i] + k*part2[i];
    }
}