package by.pakodan.model;

public enum Status {
    COMMON("Обычное"),
    FAVORITE("Избранное"),
    IGNORED("Игнорируемое");

    private String title;

    Status(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
