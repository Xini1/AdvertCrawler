package model;

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

    public static AdvertContainer parseCsv(String csvString) {
        AdvertContainer container = new AdvertContainer();

        List<String> list = Arrays.stream(csvString.split("\n"))
                .flatMap(s -> Arrays.stream(s.split(";")))
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

    @Override
    public String toString() {
        return "AdvertContainer{" +
                "creationDate=" + creationDate +
                ", adverts=" + adverts +
                '}';
    }
}
