package grailsstockmarketsimulator

class UrlMappings {

    static mappings = {
        "/companies/$symbol?" (controller: "Companies", parseRequest: true)
        "/users/$userName?" (controller: "Users", parseRequest: true)
        "/users/$userName/stocks/$symbol?" (controller: "UserStocks", parseRequest: true)

        "/"(view:"/index")
        "500"(view:'/error')
        "404"(view:'/notFound')

    }
}
