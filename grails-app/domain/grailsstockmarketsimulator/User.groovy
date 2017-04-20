package grailsstockmarketsimulator

class User {

    String userName
    String password
    double money

    static hasMany = [company: Company]

    static constraints = {
        userName(blank: false, size: 1..20, unique: true)
        password(blank: flase, size: 1..30)
    }
}
