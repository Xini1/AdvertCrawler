package utils;

import model.Advert;
import model.AdvertContainer;
import model.PriceHistory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class AdvertContainerMerger {

    public AdvertContainer merge(AdvertContainer olderContainer, AdvertContainer newerContainer) {
        LocalDate updatingDate = newerContainer.getCreationDate();

        List<Advert> olderAdverts = olderContainer.getAdverts();
        List<Advert> newerAdverts = newerContainer.getAdverts();
        List<Advert> mergedAdverts = new ArrayList<>();

        LocalDate monthAgo = updatingDate.minusMonths(1);

        for (Advert target : olderAdverts) {
            int index = newerAdverts.indexOf(target);

            if (index != -1) {
                Advert newerAdvert = newerAdverts.get(index);

                boolean isEdited = editAdvert(target, newerAdvert);

                if (isEdited) {
                    target.setLastEditDate(updatingDate);
                }

                newerAdverts.remove(index);
            }

            if (target.getLastEditDate().compareTo(monthAgo) >= 0) {
                mergedAdverts.add(target);
            }
        }

        if (!newerAdverts.isEmpty()) {
            mergedAdverts.addAll(newerAdverts);
        }

        AdvertContainer mergedContainer = new AdvertContainer();
        mergedContainer.setCreationDate(updatingDate);
        mergedContainer.setAdverts(mergedAdverts);

        return mergedContainer;
    }

    private boolean editAdvert(Advert target, Advert newerAdvert) {
        boolean isEdited = false;

        if (!target.getTitle().equals(newerAdvert.getTitle())) {
            target.setTitle(newerAdvert.getTitle());
            isEdited = true;
        }

        if (!target.getAddress().equals(newerAdvert.getAddress())) {
            target.setAddress(newerAdvert.getAddress());
            isEdited = true;
        }

        if (target.getArea() != newerAdvert.getArea()) {
            target.setArea(newerAdvert.getArea());
            isEdited = true;
        }

        if (target.getFloor() != newerAdvert.getFloor()) {
            target.setFloor(newerAdvert.getFloor());
            isEdited = true;
        }

        if (target.getTotalFloors() != newerAdvert.getTotalFloors()) {
            target.setTotalFloors(newerAdvert.getTotalFloors());
            isEdited = true;
        }

        Deque<PriceHistory> targetPriceHistoryDeque = target.getPriceHistoryDeque();
        PriceHistory newerPriceHistory = newerAdvert.getPriceHistoryDeque().getFirst();
        if (!targetPriceHistoryDeque.getFirst().equals(newerPriceHistory)) {
            targetPriceHistoryDeque.addFirst(newerPriceHistory);
            isEdited = true;
        }

        List<String> phoneNumbers = target.getPhoneNumbers();
        List<String> newerAdvertPhoneNumbers = newerAdvert.getPhoneNumbers();
        if (!phoneNumbers.equals(newerAdvertPhoneNumbers)) {
            target.setPhoneNumbers(newerAdvertPhoneNumbers);
            isEdited = true;
        }

        return isEdited;
    }
}
