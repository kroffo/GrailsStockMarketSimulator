package grailsstockmarketsimulator

class UrlMappings {

    static mappings = {
//        "/$controller/$action?/$id?(.$format)?"{
//            constraints {
//                // apply constraints here
//            }
//        }
        "/companies/$symbol?" (controller: "Companies", parseRequest: true)
        "/users/$userName?" (controller: "Users", parseRequest: true)

        "/"(view:"/index")
        "500"(view:'/error')
        "404"(view:'/notFound')


    }
}
