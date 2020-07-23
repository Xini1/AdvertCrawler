import java.time.LocalDate;
import java.util.Deque;
import java.util.List;
import java.util.Objects;

public class Advert {

    private String advertUrl;
    private String title;
    private String address;
    private float area;
    private int floor;
    private int totalFloors;
    private Deque<PriceHistory> priceHistoryDeque;
    private List<String> phoneNumbers;
    private LocalDate lastEditDate;

    public String getAdvertUrl() {
        return advertUrl;
    }

    public void setAdvertUrl(String advertUrl) {
        this.advertUrl = advertUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public float getArea() {
        return area;
    }

    public void setArea(float area) {
        this.area = area;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public int getTotalFloors() {
        return totalFloors;
    }

    public void setTotalFloors(int totalFloors) {
        this.totalFloors = totalFloors;
    }

    public Deque<PriceHistory> getPriceHistoryDeque() {
        return priceHistoryDeque;
    }

    public void setPriceHistoryDeque(Deque<PriceHistory> priceHistoryDeque) {
        this.priceHistoryDeque = priceHistoryDeque;
    }

    public List<String> getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setPhoneNumbers(List<String> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }

    public LocalDate getLastEditDate() {
        return lastEditDate;
    }

    public void setLastEditDate(LocalDate lastEditDate) {
        this.lastEditDate = lastEditDate;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        Advert advert = (Advert) obj;
        return Objects.equals(advertUrl, advert.advertUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(advertUrl);
    }

    @Override
    public String toString() {
        return "Advert{" +
                "advertUrl='" + advertUrl + '\'' +
                ", title='" + title + '\'' +
                ", address='" + address + '\'' +
                ", area=" + area +
                ", floor=" + floor +
                ", totalFloors=" + totalFloors +
                ", priceHistoryDeque=" + priceHistoryDeque +
                ", phoneNumbers=" + phoneNumbers +
                ", lastEditDate=" + lastEditDate +
                '}';
    }
}
