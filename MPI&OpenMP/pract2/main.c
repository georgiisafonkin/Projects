#include<stdio.h>
#include<stdlib.h>
#include<string.h>
#include<math.h>
#include<time.h>
#define MAX_ITERATIONS_NUMBER 50000
double get_norm(double* vector, int N);
double get_scalar_product_part(double* vector1, double* vector2, int
vector_part_size);
void mul_matrix_with_the_vector(double* matrix_piece, double* vector, double*
result_vector, int size);
void get_vectors_sum_or_sub(double* vector1, double* vector2, double*
result_vector, double k, int size);
void print_vector(double* vector, int size);
void print_matrix(double* matrix, int size);
int main(int argc, char* argv[]) {
//data initialization
    time_t t1 = time(NULL);
    int N = atoi(argv[1]);
    printf("N equals to %d\n", N);
    double epsilon = atof(argv[2]);
    printf("epsilon equals to %lf\n", epsilon);
    double* a_matrix = (double*)malloc(N * N * sizeof(double));
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
    double* x_vector = (double*)malloc(N * sizeof(double));
    for(int i = 0; i < N; ++i) {
        x_vector[i] = 1;
    }
    double* b_vector = (double*)malloc(N * sizeof(double));
    for(int i = 0; i < N; ++i) {
        b_vector[i] = N + 1;
    }
    double* r_vector = (double*)malloc(N * sizeof(double));
    double* tmp_vector = (double*)calloc(N, sizeof(double));
    mul_matrix_with_the_vector(a_matrix, x_vector, tmp_vector, N);
    get_vectors_sum_or_sub(b_vector, tmp_vector, r_vector, -1, N);
    double* z_vector = (double*)malloc(N* sizeof(double));
    memcpy(z_vector, r_vector, N * sizeof(double));
    double alpha;
    double beta;
    double* azn_vector = (double*)malloc(N * sizeof(double));
    double prev_rn_scalar = get_scalar_product_part(r_vector, r_vector, N);
    int iterations_counter = 0;
    double end_val = 1;
    int flag = 0;
//work
    while((iterations_counter < MAX_ITERATIONS_NUMBER) && (flag < 3)) {
//getting Azn vector
        mul_matrix_with_the_vector(a_matrix, z_vector, azn_vector, N);
//getting alpha^(n+1)
        alpha = prev_rn_scalar /
                get_scalar_product_part(azn_vector, z_vector, N);
//getting x^(n+1)
        get_vectors_sum_or_sub(x_vector, z_vector, x_vector, alpha, N);
//getting r^(n+1)
        get_vectors_sum_or_sub(r_vector, azn_vector, r_vector, -(alpha), N);
//getting beta^(n+1)
        beta = get_scalar_product_part(r_vector, r_vector, N) / prev_rn_scalar;
//getting z^(n+1)
        get_vectors_sum_or_sub(r_vector, z_vector, z_vector, beta, N);
//saving (r^n, r^n)
        prev_rn_scalar = get_scalar_product_part(r_vector, r_vector, N);
//loop service
        ++iterations_counter;
        end_val = (get_norm(r_vector, N) / get_norm(b_vector, N));
        if (end_val < epsilon)
            ++flag;
        else
            flag = 0;
    }
    time_t t2 = time(NULL);
    double time = t2 - t1;
    print_vector(x_vector, N);
    printf("The number of iterations is %d\nTime: %lf\n", iterations_counter,
           time);
//memory freeing
    free(a_matrix);
    free(x_vector);
    free(b_vector);
    free(r_vector);
    free(tmp_vector);
    free(z_vector);
    free(azn_vector);
    return 0;
}
double get_norm(double* vector, int size) {
    double norm = 0;
    for (int i = 0; i < size; ++i) {
        norm += pow(vector[i], 2);
    }
    return sqrt(norm);
}
double get_scalar_product_part(double* vector1, double* vector2, int size) {
    double scalar_production = 0;
    for(int i = 0; i < size; ++i) {
        scalar_production += vector1[i] * vector2[i];
    }
    return scalar_production;
}
void mul_matrix_with_the_vector(double* matrix, double* vector, double*
result_vector, int size) {
    for(int i = 0; i < size; ++i) {
        result_vector[i] = 0;
    }
    for (int i = 0; i < size; ++i) {
        for (int j = 0; j < size; ++j) {
            result_vector[i] += matrix[i*size + j] * vector[j];
        }
    }
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
void get_vectors_sum_or_sub(double* vector1, double* vector2, double*
result_vector, double k, int size) {
    for (int i = 0; i < size; ++i) {
        result_vector[i] = vector1[i] + k*vector2[i];
    }
}