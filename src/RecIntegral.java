

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */



/**
 *
 * @author Oleg
 */
public class RecIntegral extends Thread implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    private double from, to, step;
    private Double result; // может быть null

    // ===================== Проверка диапазона =====================
    private boolean isValid(double value) {
        return value >= 0.000001 && value <= 1000000;
    }

    // ===================== Конструктор =====================
    public RecIntegral(double from, double to, double step) throws InvalidRangeException {

        if (!isValid(from) || !isValid(to) || !isValid(step)) {
            throw new InvalidRangeException(
                "Значения должны быть в диапазоне от 0.000001 до 1000000"
            );
        }

        this.from = from;
        this.to = to;
        this.step = step;
    }

    // ===================== Парсинг строки =====================
    public static double parseAndValidate(String value) throws InvalidRangeException {

        if (value == null || value.isEmpty()) {
            throw new InvalidRangeException("Пустое значение");
        }

        if (!value.matches("[-+]?[0-9]+([.,][0-9]+)?")) {
            throw new InvalidRangeException("Допустимы только числовые значения");
        }

        value = value.replace(',', '.');

        double parsedValue = Double.parseDouble(value);

        if (parsedValue < 0.000001 || parsedValue > 1000000) {
            throw new InvalidRangeException(
                "Значения должны быть в диапазоне от 0.000001 до 1000000"
            );
        }

        return parsedValue;
    }

    // ===================== Численные методы =====================
    private static boolean hasTanDiscontinuity(double a, double b) {
        double left = Math.min(a, b);
        double right = Math.max(a, b);
        double kStart = Math.ceil((left - Math.PI / 2.0) / Math.PI);
        double point = Math.PI / 2.0 + kStart * Math.PI;
        return point > left && point < right;
    }

    private static double integrateSimpson(double a, double b, double h) {
        int n = (int) Math.ceil(Math.abs((b - a) / h));
        if (n < 2) {
            n = 2;
        }
        if (n % 2 != 0) {
            n++;
        }

        double stepSize = (b - a) / n;
        double sum = Math.tan(a) + Math.tan(b);

        for (int i = 1; i < n; i++) {
            double x = a + i * stepSize;
            double factor = (i % 2 == 0) ? 2.0 : 4.0;
            sum += factor * Math.tan(x);
        }

        return sum * stepSize / 3.0;
    }


    private static double integrateTanSafe(double a, double b, double h) {
        if (!hasTanDiscontinuity(a, b)) {
            return integrateSimpson(a, b, h);
        }

        final double epsilon = 1e-7;
        double left = Math.min(a, b);
        double right = Math.max(a, b);
        double sign = (a <= b) ? 1.0 : -1.0;

        double sum = 0.0;
        double segmentStart = left;
        long kStart = (long) Math.ceil((left - Math.PI / 2.0) / Math.PI);

        for (long k = kStart; ; k++) {
            double asymptote = Math.PI / 2.0 + k * Math.PI;
            if (asymptote >= right) {
                break;
            }

            double segEnd = asymptote - epsilon;
            if (segEnd > segmentStart) {
                sum += integrateSimpson(segmentStart, segEnd, h);
            }
            segmentStart = asymptote + epsilon;
        }

        if (segmentStart < right) {
            sum += integrateSimpson(segmentStart, right, h);
        }

        return sign * sum;
    }
    // ===================== Поток вычисления =====================
    @Override
    public void run() {

        System.out.println(
            Thread.currentThread().getName()
            + " start: [" + from + " ; " + to + "]"
        );

        result = integrateTanSafe(from, to, step);

        System.out.println(
            Thread.currentThread().getName()
            + " end: [" + from + " ; " + to + "]"
            + " Result = " + result
        );
    }

    // ===================== Getters =====================
    public double getFrom() { return from; }
    public double getTo() { return to; }
    public double getStep() { return step; }
    public Double getResult() { return result; }

    // ===================== Setters =====================
    public void setFrom(double from) { this.from = from; }
    public void setTo(double to) { this.to = to; }
    public void setStep(double step) { this.step = step; }
    public void setResult(Double result) { this.result = result; }

    // ===================== Вложенный калькулятор =====================
    public static class IntegralCalculator {

        public static double integrateTan(double a, double b, double h) {
            return integrateTanSafe(a, b, h);
        }
    }

    // ===================== Исключение =====================
    public static class InvalidRangeException extends Exception {

        public InvalidRangeException(String message) {
            super(message);
        }
    }
}
