# Parallel-and-Distributed-Problems

## "Non-cooperative" multi-threading

*Goal:*

The goal is to refresh the knowledge regarding `threads` and `mutexes`. The program will demonstrate the usage of threads to do non-cooperative work on shared data. The access to the shared data must be protected by using mutexes.

*Requirement:*

- The problem will require to execute a number of independent operations, that operate on shared data.
- There shall be several threads launched at the beginning, and each thread shall execute a lot of operations. The operations to be executed are to be randomly choosen, and with randomly choosen parameters.
- The main thread shall wait for all other threads to end and, then, it shall check that the invariants are obeyed.
- The operations must be synchronized in order to operate correctly.

At a bank, we have to keep track of the balance of some accounts. Also, each account has an associated log (the list of records of operations performed on that account). Each operation record shall have a unique serial number, that is incremented for each operation performed in the bank.

We have concurrently run transfer operations, to be executed on multiple threads. Each operation transfers a given amount of money from one account to someother account, and also appends the information about the transfer to the logs of both accounts.

From time to time, as well as at the end of the program, a consistency check shall be executed. It shall verify that the amount of money in each account corresponds with the operations records associated to that account, and also that all operations on each account appear also in the logs of the source or destination of the transfer.

Two transaction involving distinct accounts must be able to proceed independently (without having to wait for the same mutex).

## Producer-consumer synchronization

*Goal:*

Create two threads, a `producer` and a `consumer`, with the producer feeding the consumer.

*Requirement:*

Compute the scalar product of two vectors.

Create two threads. The first thread (producer) will compute the products of pairs of elements - one from each vector - and will feed the second thread. The second thread (consumer) will sum up the products computed by the first one. The two threads will be `synchronized` with a `condition variable` and a `mutex`. The consumer will be cleared to use each product as soon as it is computed by the producer thread.

## Simple parallel tasks

*Goal:*

Divide a simple task between threads. The task can easily be divided in sub-tasks requiring no cooperation at all.

*Requirement:*

Write several programs to compute the product of two matrices.

Have a function that computes a single element of the resulting matrix.

Have a second function whose each call will constitute a parallel task (that is, this function will be called on several threads in parallel). This function will call the above one several times consecutively to compute several elements of the resulting matrix. Consider the following ways of splitting the work between tasks (for the examples, consider the final matrix being 9x9 and the work split into 4 tasks):

- Each task computes consecutive elements, going row after row. So, task 0 computes rows 0 and 1, plus elements 0-1 of row 2 (20 elements in total); task 1 computes the remainder of row 2, row 3, and elements 0-3 of row 4 (20 elements); task 2 computes the remainder of row 4, row 5, and elements 0-5 of row 6 (20 elements); finally, task 3 computes the remaining elements (21 elements).
- Each task computes consecutive elements, going column after column. This is like the previous example, but interchanging the rows with the columns: task 0 takes columns 0 and 1, plus elements 0 and 1 from column 2, and so on.
- Each task takes every k-th element (where k is the number of tasks), going row by row. So, task 0 takes elements (0,0), (0,4), (0,8), (1,3), (1,7), (2,2), (2,6), (3,1), (3,5), (4,0), etc.

For running the tasks, also implement 2 approaches:

1. Create an `actual thread` for each task (use the low-level thread mechanism from the programming language);
2. Use a `thread pool`.

## Futures and continuations

*Goal:*

The goal is to use `C# TPL futures and continuations` in a more complex scenario, in conjunction with waiting for external events.

*Requirement:*

Write a program that is capable of simultaneously downloading several files through `HTTP`. Use directly the `BeginConnect()/EndConnect()`, `BeginSend()/EndSend()` and `BeginReceive()/EndReceive()` Socket functions, and write a simple parser for the HTTP protocol (it should be able only to get the header lines and to understand the Content-lenght: header line).

Try three implementations:

1. Directly implement the parser on the callbacks (event-driven);
2. Wrap the connect/send/receive operations in tasks, with the callback setting the result of the task;
3. Like the previous, but also use the async/await mechanism.

## Parallelizing techniques

*Goal:*

The goal is to implement a simple but non-trivial parallel algorithm.

*Requirement:*

Perform the multiplication of 2 polynomials. Use both the `regular O(n2) algorithm` and the `Karatsuba algorithm`, and each in both the `sequential form` and the `parallelized form`.

## Parallelizing techniques 2 (parallel explore)

*Goal:*

The goal is to implement a simple but non-trivial parallel algorithm.

*Requirement:*

Given a directed graph, find a `Hamiltonian cycle`, if one exists. Use multiple threads to parallelize the search. The search should start from a fixed vertex (no need to take each vertex as the starting point), however, the splitting of the work between threads should happen at several levels, for all possible choices among the neighbors of each current vertex.
