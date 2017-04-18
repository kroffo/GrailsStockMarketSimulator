package grailsstockmarketsimulator

import java.util.ArrayList;

class Company {
    private static final int DEFAULT_NUMBER_OF_STOCKS = 100.0;
    private static final double DEFAULT_STOCK_VALUE = 50.0;

    // Used for dummy data until the db is hooked up
    private static ArrayList<Company> TEST_COMPANIES = new ArrayList<>();

    private String name;
    private String symbol;
    private int stocksAvailable;
    private double stockValue;

    static constraints = {
        name(blank: false, size: 1..20)
        symbol(blank: false, size: 1..8, unique: true)
    }

    public static boolean addCompany(String name, String symbol) {
        Company c = new Company(name, symbol, Company.DEFAULT_STOCK_VALUE, Company.DEFAULT_NUMBER_OF_STOCKS);
        TEST_COMPANIES.add(c);
        return true;
    }

    private Company(String n, String s, double sv, int sa) {
        name = n;
        symbol = s;
        stockValue = sv;
        stocksAvailable = sa;
    }

    static Company[] getCompanies() {
        return TEST_COMPANIES.toArray(new Company[TEST_COMPANIES.size()]);
    }

    static Company getCompany(String symbol) {
        for(Company c : TEST_COMPANIES)
            if(c.getSymbol().equals(symbol))
                return c;
        return null;
    }

    static boolean deleteCompany(String symbol) {
        for(int i=0; i<TEST_COMPANIES.size(); ++i)
            if(TEST_COMPANIES.get(i).getSymbol().equals(symbol)) {
                TEST_COMPANIES.remove(i);
                return true;
            }
        return false;
    }

    public String getName() {
        return name;
    }

    public String getSymbol() {
        return symbol;
    }

    public int getNumberOfStocksAvailable() {
        return stocksAvailable;
    }
    public double getStockPrice() {
        return stockValue;
    }

    public boolean updateName(String name) {
        this.name = name;
        return true;
    }
}
