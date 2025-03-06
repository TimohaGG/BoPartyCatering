package com.boparty.bopartycatering.Models.Order;

public enum Status {
    PAYED("Оплачений",  "#0f2300"),
    CALCULATED("Прорахунок", "#001c23"),
    BOOKED("Заброньовано","#211d00"),
    TEMPLATE("Шаблон", "#202020");

    private final String title;
    private final String color;
    Status(String title, String color) {
        this.title = title;
        this.color = color;
    }

    public String getTitle() {
        return title;
    }

    public String getColor() {
        return color;
    }
}
