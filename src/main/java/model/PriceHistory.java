package model;

import java.time.LocalDate;
import java.util.Iterator;
import java.util.Objects;

public class PriceHistory {

    private int price;
    private LocalDate date;

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public static PriceHistory parseCsv(Iterator<String> iterator) {
        PriceHistory priceHistory = new PriceHistory();
        priceHistory.setPrice(Integer.parseInt(iterator.next()));
        priceHistory.setDate(LocalDate.parse(iterator.next()));
        return priceHistory;
    }

    public String toCsv() {
        return price + ";" + date.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        PriceHistory that = (PriceHistory) obj;
        return price == that.price;
    }

    @Override
    public int hashCode() {
        return Objects.hash(price);
    }

    @Override
    public String toString() {
        return "PriceHistory{" +
                "price=" + price +
                ", date=" + date +
                '}';
    }
}
