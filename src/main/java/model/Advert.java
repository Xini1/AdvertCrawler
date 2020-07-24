package model;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class Advert {

    private String advertUrl;
    private String title;
    private String address;
    private float area;
    private int floor;
    private int totalFloors;
    private Deque<PriceHistory> priceHistoryDeque;
    private List<String> phoneNumbers;
    private LocalDate lastRefreshDate;

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

    public LocalDate getLastRefreshDate() {
        return lastRefreshDate;
    }

    public void setLastRefreshDate(LocalDate lastRefreshDate) {
        this.lastRefreshDate = lastRefreshDate;
    }

    public static Advert parseCsv(Iterator<String> iterator) {
        Advert advert = new Advert();

        advert.setAdvertUrl(iterator.next());
        advert.setTitle(iterator.next());
        advert.setAddress(iterator.next());
        advert.setArea(Float.parseFloat(iterator.next()));
        advert.setFloor(Integer.parseInt(iterator.next()));
        advert.setTotalFloors(Integer.parseInt(iterator.next()));

        Deque<PriceHistory> priceHistoryDeque = new LinkedList<>();
        int priceHistoryCount = Integer.parseInt(iterator.next());
        for (int i = 0; i < priceHistoryCount; i++) {
            PriceHistory priceHistory = PriceHistory.parseCsv(iterator);
            priceHistoryDeque.addFirst(priceHistory);
        }
        advert.setPriceHistoryDeque(priceHistoryDeque);

        List<String> phoneNumbers = new ArrayList<>();
        int phoneNumbersCount = Integer.parseInt(iterator.next());
        for (int i = 0; i < phoneNumbersCount; i++) {
            phoneNumbers.add(iterator.next());
        }
        advert.setPhoneNumbers(phoneNumbers);

        advert.setLastRefreshDate(LocalDate.parse(iterator.next()));

        return advert;
    }

    public String toCsv() {
        StringBuilder builder = new StringBuilder();

        builder.append(advertUrl).append(';')
                .append(title).append(';')
                .append(address).append(';')
                .append(area).append(';')
                .append(floor).append(';')
                .append(totalFloors).append(';')
                .append(priceHistoryDeque.size()).append(';');

        String priceHistoryCsvString = priceHistoryDeque.stream()
                .map(PriceHistory::toCsv)
                .collect(Collectors.joining(";"));

        builder.append(priceHistoryCsvString).append(';')
                .append(phoneNumbers.size()).append(';');

        String phoneNumbersCsvString = String.join(";", phoneNumbers);

        builder.append(phoneNumbersCsvString).append(';')
                .append(lastRefreshDate);

        return builder.toString();
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
                ", lastEditDate=" + lastRefreshDate +
                '}';
    }
}
