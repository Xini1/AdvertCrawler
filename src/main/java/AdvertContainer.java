import java.time.LocalDate;
import java.util.List;

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

    @Override
    public String toString() {
        return "AdvertContainer{" +
                "creationDate=" + creationDate +
                ", adverts=" + adverts +
                '}';
    }
}
