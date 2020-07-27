package by.pakodan.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class AdvertContainer {

    private LocalDate creationDate;
    private List<Advert> adverts;

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public List<Advert> getAdverts() {
        return adverts;
    }

    public void setAdverts(List<Advert> adverts) {
        this.adverts = adverts;
    }

    public List<Advert> getNewAdverts() {
        return adverts.stream()
                .filter(Advert::isNew)
                .collect(Collectors.toList());
    }

    public List<Advert> getFavoriteAdverts() {
        return adverts.stream()
                .filter(Advert::isFavorite)
                .collect(Collectors.toList());
    }

    public static AdvertContainer parseCsv(String csvString) {
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
            Advert advert = Advert.parseCsv(iterator);
            adverts.add(advert);
        }
        container.setAdverts(adverts);

        return container;
    }

    public String toCsv() {
        String advertsCsvString = adverts.stream()
                .map(Advert::toCsv)
                .collect(Collectors.joining("\n"));

        return creationDate.toString() + "\n" + advertsCsvString;
    }
}
