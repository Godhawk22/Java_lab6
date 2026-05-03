import java.io.*;
import java.net.Socket;
import java.util.Locale;

public class DistributedClient {
    private static final int WORKER_THREADS = 4;

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java DistributedClient <host> <port>");
            return;
        }

        String host = args[0];
        int port = Integer.parseInt(args[1]);

        try (Socket socket = new Socket(host, port);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true)) {

            out.println("READY");
            String line;
            while ((line = in.readLine()) != null) {
                if (line.equals("END")) {
                    out.println("DONE");
                    break;
                }
                if (!line.startsWith("TASK ")) {
                    continue;
                }

                String[] parts = line.split("\\s+");
                int id = Integer.parseInt(parts[1]);
                double from = Double.parseDouble(parts[2]);
                double to = Double.parseDouble(parts[3]);
                double step = Double.parseDouble(parts[4]);

                double value = calculateParallel(from, to, step);
                out.println(String.format(Locale.US, "RESULT %d %.15f", id, value));
            }
        } catch (Exception e) {
            System.out.println("Client error: " + e.getMessage());
        }
    }

    private static double calculateParallel(double from, double to, double step) throws Exception {
        RecIntegral[] tasks = new RecIntegral[WORKER_THREADS];
        double size = (to - from) / WORKER_THREADS;

        for (int i = 0; i < WORKER_THREADS; i++) {
            double subFrom = from + i * size;
            double subTo = (i == WORKER_THREADS - 1) ? to : subFrom + size;
            tasks[i] = new RecIntegral(subFrom, subTo, step);
            tasks[i].start();
        }

        double total = 0;
        for (RecIntegral task : tasks) {
            task.join();
            total += task.getResult();
        }
        return total;
    }
}
