package by.pakodan;

import by.pakodan.model.Advert;
import by.pakodan.model.AdvertContainer;
import by.pakodan.model.PriceHistory;
import by.pakodan.model.Status;
import by.pakodan.utils.FileUtils;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class AdvertContainerSaveConverter {

    public static void main(String[] args) {
        FileUtils fileUtils = new FileUtils();
        String oldSave = fileUtils.readFromFile(AdvertContainer.class.getName() + ".save");
        AdvertContainer savedContainer = parseAdvertContainerCsv(oldSave);
        fileUtils.writeToFile(savedContainer.toCsv(), AdvertContainer.class.getName() + ".csv");
    }

    private static AdvertContainer parseAdvertContainerCsv(String csvString) {
        AdvertContainer container = new AdvertContainer();

        if (csvString.isEmpty()) {
            container.setCreationDate(LocalDate.now());
            container.setAdverts(new ArrayList<>());
            return container;
        }

        List<String> list = Arrays.stream(csvString.split("\n"))
                .flatMap(s -> Arrays.stream(s.split(";")))
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        Iterator<String> iterator = list.iterator();

        LocalDate creationDate = LocalDate.parse(iterator.next());
        container.setCreationDate(creationDate);

        List<Advert> adverts = new ArrayList<>();
        while (iterator.hasNext()) {
            Advert advert = parseAdvertCsv(iterator);
            adverts.add(advert);
        }
        container.setAdverts(adverts);

        return container;
    }

    private static Advert parseAdvertCsv(Iterator<String> iterator) {
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

        boolean isFavorite = Boolean.parseBoolean(iterator.next());
        advert.setStatus(isFavorite ? Status.FAVORITE : Status.COMMON);

        advert.setNew(Boolean.parseBoolean(iterator.next()));

        return advert;
    }
}
