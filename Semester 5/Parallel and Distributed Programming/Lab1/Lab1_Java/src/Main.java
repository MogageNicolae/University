import java.io.*;
import java.util.*;

public class Main {
    static int rowSize = 10;
    static int columnSize = 10000;
    static int smallSize = 5;
    static int[][] resultMatrix = new int[rowSize][columnSize];

    public static int[][] generateMatrix(int numRows, int numCols, int bound) {
        int[][] matrix = new int[numRows][numCols];
        Random random = new Random();

        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                matrix[i][j] = random.nextInt(bound);
            }
        }

        return matrix;
    }

    public static void writeMatrixToFile(int numRows, int numCols, int[][] matrix, PrintWriter printWriter) {
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                printWriter.print(matrix[i][j] + " ");
            }
            printWriter.println();
        }
    }

    public static void createFile(String filename) {
        try (FileWriter fileWriter = new FileWriter(filename);
             PrintWriter printWriter = new PrintWriter(fileWriter)) {
            writeMatrixToFile(rowSize, columnSize, generateMatrix(rowSize, columnSize, 101), printWriter);
            writeMatrixToFile(smallSize, smallSize, generateMatrix(smallSize, smallSize, 2), printWriter);
        } catch (IOException e) {
            System.err.println("An error occurred while writing to the file: " + e.getMessage());
        }
    }

    public static int[][] readMatrixFromFile(int numRows, int numCols, Scanner scanner) {
        int[][] matrix = new int[numRows][numCols];

        for (int row = 0; row < numRows; row++) {
            String line = scanner.nextLine();
            String[] values = line.split(" ");
            for (int col = 0; col < numCols; col++) {
                matrix[row][col] = Integer.parseInt(values[col]);
            }
        }

        return matrix;
    }

    public static MatrixPair readFile(String filename) {
        try {
            File file = new File(filename);
            Scanner scanner = new Scanner(file);
            int[][] bigMatrix = readMatrixFromFile(rowSize, columnSize, scanner);
            int[][] smallMatrix = readMatrixFromFile(smallSize, smallSize, scanner);
            scanner.close();
            return new MatrixPair(bigMatrix, smallMatrix);
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + e.getMessage());
        }
        return null;
    }

    public static void createBiggerMatrix(MatrixPair matrixPair) {
        int noRows = matrixPair.getMatrix1().length;
        int noCols = matrixPair.getMatrix1()[0].length;
        int[][] biggerMatrix = new int[noRows + 2][noCols + 2];

        for (int i = 0; i < noRows; i++) {
            biggerMatrix[i + 1][0] = matrixPair.getMatrix1()[i][0];
            biggerMatrix[i + 1][noCols + 1] = matrixPair.getMatrix1()[i][noCols - 1];

            for (int j = 0; j < noCols; j++) {
                biggerMatrix[i + 1][j + 1] = matrixPair.getMatrix1()[i][j];
            }
        }
        for (int i = 0; i < noCols; i++) {
            biggerMatrix[0][i + 1] = matrixPair.getMatrix1()[0][i];
            biggerMatrix[noRows + 1][i + 1] = matrixPair.getMatrix1()[noRows - 1][i];
        }
        biggerMatrix[0][0] = matrixPair.getMatrix1()[0][0];
        biggerMatrix[0][noCols + 1] = matrixPair.getMatrix1()[0][noCols - 1];
        biggerMatrix[noRows + 1][0] = matrixPair.getMatrix1()[noRows - 1][0];
        biggerMatrix[noRows + 1][noCols + 1] = matrixPair.getMatrix1()[noRows - 1][noCols - 1];

        matrixPair.setMatrix1(biggerMatrix);
    }


    public static void sequential(MatrixPair matrixPair) {
        int sum;
        for (int i = 1; i < rowSize + 1; i++) {
            for (int j = 1; j < columnSize + 1; j++) {
                sum = 0;
                for (int index1 = i - 1; index1 <= i + 1; index1++) {
                    for (int index2 = j - 1; index2 <= j + 1; index2++) {
                        sum += matrixPair.getMatrix1()[index1][index2] * matrixPair.getMatrix2()[index1 - i + 1][index2 - j + 1];
                    }
                }
                resultMatrix[i - 1][j - 1] = sum;
            }
        }
    }

    public static void multiThread(MatrixPair matrixPair, int size, boolean onRows, int noOfCores) {
        int batchSize = size / noOfCores;
        int batchReminder = size % noOfCores;
        int start = 1;
        int end;
        MyThread[] threads = new MyThread[noOfCores + 1];

        for (int i = 0; i < noOfCores; i++) {
            end = start + batchSize;
            if (batchReminder > 0) {
                --batchReminder;
                ++end;
            }
            threads[i] = new MyThread(start, end, onRows, matrixPair, resultMatrix);
            threads[i].start();
            start = end;
        }

        for (int i = 0; i < noOfCores; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException interruptedException) {
            }
        }
    }

    public static void printMatrix(int[][] matrix) {
        int noCols = matrix[0].length;

        for (int[] ints : matrix) {
            for (int j = 0; j < noCols; ++j) {
                System.out.print(ints[j] + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    private static double runScript(String[] args, String filename) {
        MatrixPair matrixPair = readFile(filename);
        createBiggerMatrix(matrixPair);
        long startTime;
        if (Objects.equals(args[1], "sec")) {
            startTime = System.nanoTime();
            sequential(matrixPair);
        }
        else if (Objects.equals(args[1], "row")) {
            startTime = System.nanoTime();
            multiThread(matrixPair, rowSize, true, Integer.parseInt(args[0]));
        }
        else {
            startTime = System.nanoTime();
            multiThread(matrixPair, columnSize, false, Integer.parseInt(args[0]));
        }
        long endTime = System.nanoTime();
        return (double)(endTime - startTime)/1E6;
//        printMatrix(resultMatrix);
    }

    public static void main(String[] args) {
        String filename = "data.txt";
//        createFile(filename);
        double time = runScript(args, filename);

        try (FileWriter fileWriter = new FileWriter("output-" + args[1] + ".txt");
             PrintWriter printWriter = new PrintWriter(fileWriter)) {
            writeMatrixToFile(rowSize, columnSize, resultMatrix, printWriter);
        } catch (IOException e) {
            System.err.println("An error occurred while writing to the file: " + e.getMessage());
        }

        System.out.println(time);
    }
}