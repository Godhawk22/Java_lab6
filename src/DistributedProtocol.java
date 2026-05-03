public final class DistributedProtocol {
    private DistributedProtocol() {}

    public static String taskLine(int id, RecIntegral rec) {
        return "TASK " + id + " " + rec.getFrom() + " " + rec.getTo() + " " + rec.getStep();
    }

    public static String resultLine(int id, double value) {
        return "RESULT " + id + " " + value;
    }
}
