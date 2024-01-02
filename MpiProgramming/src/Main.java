import mpi.MPI;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// Modify Run Configuration -> change size of communicator

public class Main {
    private static Polynomial fixedPolynomial() {
        List<Integer> coefficients = new ArrayList<>();

        coefficients.add(2);
        coefficients.add(7);
        coefficients.add(2);
        coefficients.add(5);
        coefficients.add(3);
        coefficients.add(4);
        coefficients.add(5);
        coefficients.add(6);

        return new Polynomial(coefficients);
    }

    private static Polynomial generatePolynomial(int degree) {
        Random random = new Random();

        List<Integer> coefficients = new ArrayList<>();

        for (int i = 0; i <= degree; i++)
            coefficients.add(random.nextInt(10) + 1);

        return new Polynomial(coefficients);
    }

    private static void runMaster(Polynomial p1, Polynomial p2, int size) {
        long startTime = System.nanoTime();

        // the length of each segment to be calculated by a worker process
        int length = p1.getLength() / (size - 1);

        int start = 0;
        int end = length;

        // distribute tasks to worker processes
        for (int i = 1; i < size; i++) {
            /* Send(Object buf, int offset, int count, mpi.Datatype datatype, int dest, int tag)
            buf - the data to be sent
            offset - the starting offset in the buffer array
            count - the number of elements to be sent
            datatype - the datatype of the elements in the buffer
            dest - the rank of the target process to which the data is being sent
            tag - an integer tag that can be used to distinguish different messages */

            // send the segment bound data to each worker process
            MPI.COMM_WORLD.Send(new int[]{start}, 0, 1, MPI.INT, i, 0);
            MPI.COMM_WORLD.Send(new int[]{end}, 0, 1, MPI.INT, i, 0);
            // send the polynomial data to each worker process
            MPI.COMM_WORLD.Send(new Object[]{p1}, 0, 1, MPI.OBJECT, i, 0);
            MPI.COMM_WORLD.Send(new Object[]{p2}, 0, 1, MPI.OBJECT, i, 0);

            start += length;
            end += length;
            if (i + 1 == size - 1)
                end = p1.getLength();
        }

        /* Recv(Object buf, int offset, int count, mpi.Datatype datatype, int source, int tag)
        buf - the buffer where the received data will be stored
        offset - the starting index in the buffer where the received data should be placed
        count - the number of elements to receive
        datatype - the MPI data type of the elements in the buffer
        source - the rank of the source process from which you want to receive the message
        tag - an integer tag that can be used to distinguish different kinds of messages */

        // variable to store the results
        Object[] results = new Object[size - 1];

        // receive the results from the worker processes
        for (int i = 1; i < size; i++)
            MPI.COMM_WORLD.Recv(results, i - 1, 1, MPI.OBJECT, i, 0);

        // combine the results from the worker processes
        Polynomial result = Multiplication.combineResults(results);

        long endTime = System.nanoTime();
        double duration = (endTime - startTime) / 1000000000.0;

        System.out.println("Master <0> got the result (" + result + ") in " + duration + " seconds.");
    }

    private static void runRegularWorker(int rank) {
        // variables to store the segment bound data
        int[] s = new int[1];
        int[] e = new int[1];
        // variables to store the polynomial data
        Object[] p1 = new Object[1];
        Object[] p2 = new Object[1];

        // receive the segment bound data from the master process
        MPI.COMM_WORLD.Recv(s, 0, 1, MPI.INT, 0, 0);
        MPI.COMM_WORLD.Recv(e, 0, 1, MPI.INT, 0, 0);
        // receive the polynomial data from the master process
        MPI.COMM_WORLD.Recv(p1, 0, 1, MPI.OBJECT, 0, 0);
        MPI.COMM_WORLD.Recv(p2, 0, 1, MPI.OBJECT, 0, 0);

        // extract the segment bounds from the received segment bound data
        int start = s[0];
        int end = e[0];
        // extract the polynomials from the received polynomial data
        Polynomial polynomial1 = (Polynomial) p1[0];
        Polynomial polynomial2 = (Polynomial) p2[0];

        // perform regular multiplication on the assigned segment
        Polynomial result = Multiplication.multiplyRegularWithBounds(start, end, polynomial1, polynomial2);

        // send the result back to the master process
        MPI.COMM_WORLD.Send(new Object[]{result}, 0, 1, MPI.OBJECT, 0, 0);

        System.out.println("Worker <" + rank + "> had [" + start + "," + end + ") and calculated the coefficients " + result.getCoefficients() + ".");
    }

    private static void runKaratsubaWorker(int rank) {
        // variables to store the segment bound data
        int[] s = new int[1];
        int[] e = new int[1];
        // variables to store the polynomial data
        Object[] p1 = new Object[1];
        Object[] p2 = new Object[1];

        // receive the segment bound data from the master process
        MPI.COMM_WORLD.Recv(s, 0, 1, MPI.INT, 0, 0);
        MPI.COMM_WORLD.Recv(e, 0, 1, MPI.INT, 0, 0);
        // receive the polynomial data from the master process
        MPI.COMM_WORLD.Recv(p1, 0, 1, MPI.OBJECT, 0, 0);
        MPI.COMM_WORLD.Recv(p2, 0, 1, MPI.OBJECT, 0, 0);

        // extract the segment bounds from the received segment bound data
        int start = s[0];
        int end = e[0];
        // extract the polynomials from the received polynomial data
        Polynomial polynomial1 = (Polynomial) p1[0];
        Polynomial polynomial2 = (Polynomial) p2[0];

        // set the coefficients outside the assigned segment to zero
        for (int i = 0; i < start; i++)
            polynomial1.setCoefficient(i, 0);
        for (int j = end; j < polynomial1.getLength(); j++)
            polynomial1.setCoefficient(j, 0);

        // perform Karatsuba multiplication on the assigned segment
        Polynomial result = Multiplication.multiplyKaratsuba(polynomial1, polynomial2);

        // send the result back to the master process
        MPI.COMM_WORLD.Send(new Object[]{result}, 0, 1, MPI.OBJECT, 0, 0);

        System.out.println("Worker <" + rank + "> had [" + start + "," + end + ") and calculated the coefficients " + result.getCoefficients() + ".");
    }

    public static void main(String[] args) {
        // initialize the MPI environment
        MPI.Init(args);

        // MPI_COMM_WORLD - default communicator groups all the processes so that they can communicate

        // the unique identifier for the process
        int rank = MPI.COMM_WORLD.Rank();
        // the number of launched processes
        int size = MPI.COMM_WORLD.Size();

        if (rank == 0) {
            // the master process

            Random random = new Random();
            int degree = random.nextInt(10) + 1;
            Polynomial p1 = generatePolynomial(degree);
            Polynomial p2 = generatePolynomial(degree);

            System.out.println("Polynomial 1: " + p1);
            System.out.println("Polynomial 2: " + p2);

            runMaster(p1, p2, size);
        } else {
            // the worker processes

            runRegularWorker(rank);
            //runKaratsubaWorker(rank);
        }

        // stop the MPI environment
        MPI.Finalize();
    }
}
