package by.pakodan.ui;

public enum AdvertViewMode {

    ALL("Все"),
    NEW("Новые"),
    FAVORITE("Избранные"),
    IGNORED("Игнорируемые");

    private String title;

    AdvertViewMode(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
