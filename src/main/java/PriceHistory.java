import java.time.LocalDate;

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

    @Override
    public String toString() {
        return "PriceHistory{" +
                "price=" + price +
                ", date=" + date +
                '}';
    }
}
