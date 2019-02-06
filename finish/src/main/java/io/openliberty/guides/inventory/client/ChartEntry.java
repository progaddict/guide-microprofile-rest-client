package io.openliberty.guides.inventory.client;

public class ChartEntry {

    private String date;
    private String minute;
    private Double close;

    public String getDate() {
        return date;
    }

    public ChartEntry setDate(final String date) {
        this.date = date;
        return this;
    }

    public String getMinute() {
        return minute;
    }

    public ChartEntry setMinute(final String minute) {
        this.minute = minute;
        return this;
    }

    public Double getClose() {
        return close;
    }

    public ChartEntry setClose(final Double close) {
        this.close = close;
        return this;
    }
}
