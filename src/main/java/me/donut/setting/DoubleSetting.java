package me.donut.setting;

public class DoubleSetting extends Setting<Double> {
    private final double min;
    private final double max;
    private final double step;

    public DoubleSetting(String name, double value, double min, double max, double step) {
        super(name, value);
        this.min = min;
        this.max = max;
        this.step = step;
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    public double getStep() {
        return step;
    }

    public void increment() {
        value = Math.min(max, value + step);
    }

    public void decrement() {
        value = Math.max(min, value - step);
    }

    @Override
    public void next() {
        if (step > 0) {
            increment();
        }
    }
}
