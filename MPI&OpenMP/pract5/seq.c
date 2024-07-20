#include<stdio.h>
#include<stdlib.h>
#include<stdbool.h>
#include<string.h>
#include<mpi/mpi.h>

#define TAG 100
#define N 17000

void print_vector(bool* vector, int size) {
    for (int i = 0; i < size; ++i) {
        printf("%d ", vector[i]);
    }
    printf("\n");
}

bool is_two_replayed(bool* field1, bool* field2, int size);
void compare_fields(bool* vector, bool* cur_field, bool** fields, int size, int iterations_n, int N2);
bool dead_or_alive(bool* current_field, int x, int y, int N2);
void compute_inner_new(bool* current_field, bool* next_field, int lines_N, int N2);

int main(int argc, char* argv[]) {
    if (argc < 3) {
        return EXIT_FAILURE;
    }

    int N1 = atoi(argv[1]);
    int N2 = atoi(argv[2]);


    int size, rank;
    int subfield_size;
    MPI_Init(&argc, &argv);

    bool is_replayed[N] = {false};
    bool checked_replay[N] = {false};
    bool* past_fields[N];

    MPI_Comm_size(MPI_COMM_WORLD, &size);
    MPI_Comm_rank(MPI_COMM_WORLD, &rank);

    int next_proc = (rank - 1 + size) % size;
    int previous_proc = (rank + 1) % size;

    subfield_size = ((N1 / size) + 2) * N2;

    bool* current_field = calloc(subfield_size, sizeof(bool));

    double start;
    if (rank == 0) {
        //initialization
        current_field[1*N2 + 1] = true;
        current_field[2*N2 + 2] = true;
        current_field[3*N2 + 0] = true;
        current_field[3*N2 + 1] = true;
        current_field[3*N2 + 2] = true;

        //start
        start = MPI_Wtime();
    }

    //memcpy(current_field + N2, field, N2 * N1/size);


    int iter = 0;
    bool stop_flag = false;
    while(stop_flag == false) {
//        if (rank == 0) {
//            for (int i = 0; i < N1/size; ++i) {
//                print_vector(current_field + N2 + i*N2, N2);
//            }
//            printf("\n\n");
//        }
        bool* next_field = (bool*) calloc(subfield_size, sizeof(bool));

        memcpy(current_field, current_field + (N1 / size) * N2, N2);
        memcpy(current_field + (N1 / size + 1) * N2, current_field + N2, N2);

        compare_fields(is_replayed, current_field + N2, past_fields, N2 * (N1 / size), iter, N2);

        //computing inner lines of new field piece
        compute_inner_new(current_field + 2*N2, next_field + 2*N2, N1 / size - 2, N2);

        //computing upper line of new field piece
        compute_inner_new(current_field + N2, next_field + N2, 1, N2);

        //computing bottom line of new field piece
        compute_inner_new(current_field + N2 * (N1 / size), next_field + N2 * (N1 / size), 1, N2);

        //check is it time to stop
        if (iter > 0) {
            for (int j = 0; j < iter; ++j) {
                if (is_replayed[j] == true) {
                    stop_flag = true;
                    break;
                }
            }
        }

        past_fields[iter] = current_field;
        current_field = next_field;
        ++iter;
    }

    if (rank == 0) {
        printf("Job is done.\nTime: %lf\nIterations: %d\n", MPI_Wtime() - start, iter);
    }

    //freeing resources
    for(int i = 0; i < iter; ++i) {
        free(past_fields[i]);
    }
    free(current_field);

    MPI_Finalize();

    return 0;
}

bool is_two_replayed(bool* field1, bool* field2, int size) {
    for(int i = 0; i < size; ++i) {
        if (field1[i] != field2[i]) {
            return false;
        }
    }
    return true;
}

void compare_fields(bool* vector, bool* cur_field, bool** fields, int size, int iterations_n, int N2) {
    for (int i = 0; i < iterations_n; ++i) {
        vector[i] = is_two_replayed(cur_field, fields[i] + N2, size);
    }
}

bool dead_or_alive(bool* current_field, int x, int y, int N2) {
    //count socials
    int socials = 0;
    for(int i = -1; i <= 1; ++i) {
        for (int j = -1; j <= 1; ++j) {
            if (i == 0 && j == 0) {
                continue;
            }
            int social_x = x + j;
            int social_y = y + i;
            if (social_x < 0) {
                social_x = N2 - 1;
            }
            else if (social_x >= N2) {
                social_x = 0;
            }
            socials += current_field[N2 * social_y + social_x];
        }
    }
    //check is cell dead or alive
    int pos = N2 * y + x;
    switch (current_field[pos]) {
        case false:
            if (socials == 3) {
                return true;
            }
            else {
                return false;
            }
            break;
        case true:
            if (socials < 2 || socials > 3)
                return false;
            else
                return true;
            break;
    }
}

void compute_inner_new(bool* current_field, bool* next_field, int lines_N, int N2) {
    for (int y = 0; y < lines_N; ++y) {
        for (int x = 0; x < N2; ++x) {
            next_field[N2 * y + x] = dead_or_alive(current_field, x, y, N2);
        }
    }
}