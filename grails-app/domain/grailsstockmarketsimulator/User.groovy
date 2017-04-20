package grailsstockmarketsimulator

class User {

    String userName
    String password
    double money

    static hasMany = [stocks: Stock]

    static constraints = {
        userName(blank: false, size: 1..20, unique: true)
        password(blank: false, size: 1..30)
    }
}
