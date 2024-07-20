#include<stdio.h>
#include<stdlib.h>
#include<string.h>
#include<omp.h>

#define ITERATIONS_MAX 50000
#define CHUNK 2

void print_vector(double* vector, int N) {
    for (int i = 0; i < N; ++i) {
        printf("%lf\n", vector[i]);
    }
}

void print_matrix(double* matrix, int N) {
    for (int i = 0; i < N; ++i) {
        for (int j = 0; j < N; ++j) {
            printf("%lf ", matrix[i * N + j]);
        }
        printf("\n");
    }
}

void put_scalar_prod_to(double* prod, const double* vector1, const double* vector2, int N);
void put_norm_to(double* norm, const double* vector, int N);
double rand_from(double min, double max);
void matrix_mul_vector(const double* matrix, const double* vector, double* result, int N);

int main(int argc, char* argv[]) {

    if(argc < 3) {
        printf("too few arguments\n");
        return EXIT_FAILURE;
    }

    double start = omp_get_wtime();

    int N = atoi(argv[1]);
    double epsilon = atof(argv[2]);

    double* A = (double*)calloc(N*N, sizeof(double));
    double* b = (double*)calloc(N, sizeof(double));
    double* x = (double*)calloc(N, sizeof(double));
    double* r = (double*)calloc(N, sizeof(double));
    double* z = (double*)calloc(N, sizeof(double));
    double* az = (double*)calloc(N, sizeof(double));
    double* ax0 = (double*)calloc(N, sizeof(double));

    double b_norm;
    double r_norm;
    double cur_r_prod;
    double prev_r_prod;
    double az_z_prod;
    double alpha;
    double betta;

    int flag = 0;
    int iters_counter = 0;

    //A initialization
    for (int i = 0; i < N; ++i) {
        for (int j = 0; j <= i; ++j) {
            A[i * N + j] = rand_from(-1, 1);
            A[j * N + i] = A[i * N + j];
        }
        A[i * N + i] += 0;
    }

    //b initialization
    for (int i = 0; i < N; ++i) {
        b[i] = rand_from(-1, 1);
    }

    //x initialization
    for (int i = 0; i < N; ++i) {
        x[i] = rand_from(-1,1);
    }

    #pragma omp parallel
    {
        //r initialization
        matrix_mul_vector(A, x, ax0, N);
        #pragma omp for schedule(auto)
        for (int i = 0; i < N; ++i) {
            r[i] = b[i] - ax0[i];
        }

        //z initialization
        #pragma omp single
        {
            memcpy(z, r, sizeof(double) * N);
        }

        put_norm_to(&b_norm, b, N);
        #pragma omp critical
        {
            put_scalar_prod_to(&prev_r_prod, r, r, N);
        }
#pragma omp single
        {
            printf("------------------------------------\n\n\n");
        }

        #pragma omp single
        {
            epsilon *= epsilon;
            epsilon *= b_norm;
        }

        while (iters_counter < ITERATIONS_MAX && flag < 3) {
            //computing new az and az_z_prod
            matrix_mul_vector(A, z, az, N);
            #pragma omp critical
            {
                put_scalar_prod_to(&az_z_prod, az, z, N);
            }
            //

            alpha = prev_r_prod / az_z_prod;
            #pragma omp for schedule(auto)
            for (int i = 0; i < N; ++i) {
                x[i] = x[i] + alpha*z[i];
            }
            #pragma omp for schedule(auto)
            for (int i = 0; i < N; ++i) {
                r[i] = r[i] - alpha*az[i];
            }
            #pragma omp critical
            {
                put_scalar_prod_to(&cur_r_prod, r, r, N);
            }
            #pragma omp single
            {
                betta = cur_r_prod / prev_r_prod;
                prev_r_prod = cur_r_prod;
            }
            #pragma omp for schedule(auto)
            for (int i = 0; i < N; ++i) {
                z[i] = r[i] + betta*z[i];
            }
            put_norm_to(&r_norm, r, N);
            #pragma omp single
            {
                if (r_norm < epsilon) {
                    ++flag;
                }
                else {
                    flag = 0;
                }
                ++iters_counter;
            }
        }
    }

    printf("x:\n");
    print_vector(x, N);
    printf("Time: %lf seconds\n", omp_get_wtime() - start);
    printf("%d\n", iters_counter);

    free(A);
    free(b);
    free(x);
    free(r);
    free(z);
    free(az);
    free(ax0);

    return EXIT_SUCCESS;
}

void put_scalar_prod_to(double* prod, const double* vector1, const double* vector2, int N) {
    printf("thread #%d inside the function\n", omp_get_thread_num());
    double tmp = 0.0;
    #pragma omp for schedule(auto)
    for (int i = 0; i < N; ++i) {
        tmp += vector1[i] * vector2[i];
        printf("thread #%d---\n", omp_get_thread_num());
    }
    *prod = tmp;
    printf("thread #%d quit from the function\n", omp_get_thread_num());
}

void put_norm_to(double* norm, const double* vector, int N) {
    double tmp = 0.0;
    #pragma omp parallel for schedule(auto) reduction (+:tmp)
    for (int i = 0; i < N; ++i)
        tmp += vector[i] * vector[i];
    *norm = tmp;
}

double rand_from(double min, double max) {
    double range = (max - min);
    double div = RAND_MAX / range;
    return min + (rand() / div);
}

void matrix_mul_vector(const double* matrix, const double* vector, double* result, int N) {
    double element;
    #pragma omp for schedule(auto)
    for (int i = 0; i < N; ++i) {
        element = 0;
        for (int j = 0; j < N; ++j) {
            element += matrix[i*N + j] * vector[j];
        }
        result[i] = element;
    }
}