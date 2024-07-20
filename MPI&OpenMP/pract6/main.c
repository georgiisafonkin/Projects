#include<math.h>
#include<mpi/mpi.h>
#include<pthread.h>
#include<stdio.h>
#include<stdlib.h>

#define TASK_LIST_SIZE 1024
#define L 3072
#define ITERATIONS 8
#define REQUEST_TAG 11
#define ANSWER_TAG 12
#define RECEIVE_TAG 13
#define STOP_SENDER -10

typedef struct {
    int size;
    int rank;
    int finished_task_counter;
    int unfinished_task_number;
    double global_res;
    pthread_mutex_t mutex;
    int tasks[TASK_LIST_SIZE];
} shared_data_st;

void generate_task_list(shared_data_st *shared_data, int iter_numb); //generate new task list and refresh finished and unfinished task counters
void execute_tasks(shared_data_st *shared_data);
void start_worker_routine(shared_data_st* shared_data);
void* start_sender_routine(void* shared_data);

int main(int argc, char* argv[]) {
    int provided_flag;
    MPI_Init_thread(&argc, &argv, MPI_THREAD_MULTIPLE, &provided_flag);
    if(provided_flag != MPI_THREAD_MULTIPLE){
        fprintf(stderr, "initialized without MPI_THREAD_MULTIPLE\n");
        MPI_Finalize();
        return EXIT_FAILURE;
    }

    shared_data_st* shared_data = (shared_data_st*)calloc(1, sizeof(shared_data_st));

    MPI_Comm_rank(MPI_COMM_WORLD, &shared_data->rank);
    MPI_Comm_size(MPI_COMM_WORLD, &shared_data->size);

    pthread_t sender_thread;
    pthread_mutex_init(&shared_data->mutex, NULL);
    pthread_attr_t attrs;
    pthread_attr_init(&attrs);
    pthread_attr_setdetachstate(&attrs, PTHREAD_CREATE_JOINABLE);
    pthread_create(&sender_thread, &attrs, start_sender_routine, shared_data);

    double start, finish;
    if (shared_data->rank == 0) {
        start = MPI_Wtime();
    }
    start_worker_routine(shared_data);
    pthread_join(sender_thread, NULL);

    if (shared_data->rank == 0) {
        finish = MPI_Wtime();
        double timeTaken = finish - start;
        printf("Global time: %f\n", timeTaken);
    }

    pthread_attr_destroy(&attrs);
    pthread_mutex_destroy(&shared_data->mutex);
    free(shared_data);
    MPI_Finalize();

    return EXIT_SUCCESS;
}

void generate_task_list(shared_data_st *shared_data, int iter_numb) {
    for(int i = 0; i < TASK_LIST_SIZE; ++i) {
        shared_data->tasks[i] = abs(50 - i % 100) * abs(shared_data->rank - (iter_numb % shared_data->size)) * L;
    }
    shared_data->finished_task_counter = 0;
    shared_data->unfinished_task_number = TASK_LIST_SIZE;
}

void execute_tasks(shared_data_st *shared_data) {
    int weight;
    for(int i = 0; i < shared_data->unfinished_task_number; ++i) {
        pthread_mutex_lock(&shared_data->mutex); //TODO REMOVE MAYBE
        weight = shared_data->tasks[i];
        pthread_mutex_unlock(&shared_data->mutex);
        for (int j = 0; j < weight; ++j) {
            shared_data->global_res += sin(j);
        }
        pthread_mutex_lock(&shared_data->mutex);
        shared_data->finished_task_counter++;
        pthread_mutex_unlock(&shared_data->mutex);
    }
    shared_data->unfinished_task_number = 0;
}

void start_worker_routine(shared_data_st* shared_data) {
    double start;
    double finish;
    double min_time, max_time;
    double imbalance, imbalance_proportion;
    for (int i = 0; i < ITERATIONS; ++i) {
        pthread_mutex_lock(&shared_data->mutex);
        generate_task_list(shared_data, i);
        pthread_mutex_unlock(&shared_data->mutex);

        start = MPI_Wtime();
        execute_tasks(shared_data);

        int received_tasks_number = 0;
        int receiving_flag = 1;
        while(receiving_flag) {
            int requests_counter = 0;
            for (int responder = 0; responder < shared_data->size; ++responder) {
                if (shared_data->rank == responder) {
                    continue;
                }

                MPI_Send(&shared_data->rank, 1, MPI_INT, responder, REQUEST_TAG, MPI_COMM_WORLD);
                MPI_Recv(&received_tasks_number, 1, MPI_INT, responder, ANSWER_TAG, MPI_COMM_WORLD, MPI_STATUS_IGNORE);

                if (received_tasks_number == 0) {
                    ++requests_counter;
                }

                if (requests_counter >= shared_data->size - 1) {
                    receiving_flag = 0;
                    break;
                }

                if (received_tasks_number == 0) {
                    continue;
                }

                MPI_Recv(shared_data->tasks, received_tasks_number, MPI_INT, responder, RECEIVE_TAG, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
                pthread_mutex_lock(&shared_data->mutex);
                shared_data->unfinished_task_number = received_tasks_number;
                pthread_mutex_unlock(&shared_data->mutex);
                execute_tasks(shared_data);
            }
        }

        finish = MPI_Wtime();
        double time = finish - start;

        MPI_Allreduce(&time, &min_time, 1, MPI_DOUBLE, MPI_MIN, MPI_COMM_WORLD);
        MPI_Allreduce(&time, &max_time, 1, MPI_DOUBLE, MPI_MAX, MPI_COMM_WORLD);

        MPI_Barrier(MPI_COMM_WORLD);
        printf("PROCESS#%d:\n ITERATION#%d: time = %lf, finished_tasks_counter = %d, global_res = %lf\n", shared_data->rank, i, time, shared_data->finished_task_counter, shared_data->global_res);

        if(shared_data->rank==0) {
            imbalance = max_time - min_time;
            imbalance_proportion = (imbalance/max_time) * 100;
            printf("ITERATION#%d:\nimbalance: %lf imbalance_proportion: %lf\n", i, imbalance, imbalance_proportion);
        }
    }
    int stop_flag = STOP_SENDER;
    MPI_Send(&stop_flag, 1, MPI_INT, shared_data->rank, REQUEST_TAG, MPI_COMM_WORLD);
}

void* start_sender_routine(void* data) {
    shared_data_st* shared_data = (shared_data_st*)data;
    int worker;
    while(1){
        MPI_Recv(&worker, 1, MPI_INT, MPI_ANY_SOURCE, REQUEST_TAG, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
        if (worker == STOP_SENDER) {
            break;
        }
        pthread_mutex_lock(&shared_data->mutex);
        int tasks_remained = shared_data->unfinished_task_number - shared_data->finished_task_counter;
        pthread_mutex_unlock(&shared_data->mutex);
        int tasks_to_send = tasks_remained / (shared_data->size);
        if(tasks_remained >= 1 && tasks_to_send >= 1) {
            pthread_mutex_lock(&shared_data->mutex);
            shared_data->unfinished_task_number -= tasks_to_send;
            pthread_mutex_unlock(&shared_data->mutex);
            MPI_Send(&tasks_to_send, 1, MPI_INT, worker, ANSWER_TAG, MPI_COMM_WORLD);
            MPI_Send(&shared_data->tasks[shared_data->unfinished_task_number - tasks_to_send], tasks_to_send, MPI_INT, worker, RECEIVE_TAG, MPI_COMM_WORLD);
        } else {
            tasks_to_send = 0;
            MPI_Send(&tasks_to_send, 1, MPI_INT, worker, ANSWER_TAG, MPI_COMM_WORLD);
        }
    }
}