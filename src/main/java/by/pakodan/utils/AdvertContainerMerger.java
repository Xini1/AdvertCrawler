package by.pakodan.utils;

import by.pakodan.model.Advert;
import by.pakodan.model.AdvertContainer;
import by.pakodan.model.PriceHistory;

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

        for (Advert target : olderAdverts) {
            int index = newerAdverts.indexOf(target);

            if (index != -1) {
                Advert newerAdvert = newerAdverts.get(index);

                editAdvert(target, newerAdvert);
                target.setLastRefreshDate(updatingDate);
                target.setNew(false);

                newerAdverts.remove(index);
            }

            LocalDate monthAgo = updatingDate.minusMonths(1);
            if (target.getLastRefreshDate().compareTo(monthAgo) >= 0) {
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

    private void editAdvert(Advert target, Advert newerAdvert) {
        target.setTitle(newerAdvert.getTitle());
        target.setAddress(newerAdvert.getAddress());
        target.setArea(newerAdvert.getArea());
        target.setFloor(newerAdvert.getFloor());
        target.setTotalFloors(newerAdvert.getTotalFloors());
        target.setPhoneNumbers(newerAdvert.getPhoneNumbers());

        Deque<PriceHistory> targetPriceHistoryDeque = target.getPriceHistoryDeque();
        Deque<PriceHistory> newerPriceHistoryDeque = newerAdvert.getPriceHistoryDeque();
        PriceHistory targetPriceHistory = targetPriceHistoryDeque.getFirst();
        PriceHistory newerPriceHistory = newerPriceHistoryDeque.getFirst();
        int targetPrice = targetPriceHistory.getPrice();
        int newerPrice = newerPriceHistory.getPrice();
        if (targetPrice != newerPrice) {
            targetPriceHistoryDeque.addFirst(newerPriceHistory);
        }
    }
}
