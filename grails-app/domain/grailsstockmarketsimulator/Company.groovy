package grailsstockmarketsimulator

class Company {

    String name;
    String symbol;
    int stocksAvailable;
    double stockPrice;

    static hasMany = [stocks: Stock]

    static constraints = {
        name(blank: false, size: 1..20)
        symbol(blank: false, size: 1..8, unique: true)
    }

}