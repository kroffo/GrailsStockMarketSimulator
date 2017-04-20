package grailsstockmarketsimulator

import org.grails.web.json.JSONObject

class UsersController {

    def index(String userName) {
        switch (request.method) {
            case 'GET':
                if (userName == null)
                    listUsers()
                else
                    detailUser(userName)
                break
            case 'POST':
                if (userName == null)
                    createUser()
                else
                    response.sendError(404)
                break
            case 'DELETE':
                if (userName != null)
                    deleteUser(userName)
                else
                    response.sendError(404)
                break
            case 'PUT':
                if (userName != null)
                    updateUser(userName)
                else
                    response.sendError(404)
                break
            default:
                response.sendError(404)
        }
    }

    private void listUsers() {
        User[] users = User.findAll()
        String json = "[\n"
        for(int i = 0 ; i < users.length; ++i) {
            json += getUserJson(users[i], 2)
            if(i < users.length - 1)
                json += ","
            json += "\n"
        }
        json += "]"
        response.status = 200
        render(json)
    }

    private void detailUser(String userName) {
        User u = User.findByUserName(userName)
        if(u == null) {
            response.sendError(404)
            return
        }
        render(getUserJson(u,0))
    }

    private void updateUser(String userName) {
        User u = User.findByUserName(userName)
        if(u == null) {
            response.sendError(404)
            return
        }
        JSONObject json = request.JSON
        if(!json.has("password") || json.isNull("password")) {
            response.status = 400
            render("Missing parameter \"password\".")
            return
        }
        String password = request.JSON.getString("password")
        if(password.length() == 0) {
            response.status = 400
            render("Parameter \"password\" must have length greater than 0.")
            return
        }
        u.password = password
        if(u.save(flush: true)) {
            render(status:200)
        } else {
            response.sendError(500)
        }
    }

    private void deleteUser(String userName) {
        User u = User.findByUserName(userName)
        if(u == null) {
            response.sendError(404)
            return
        }
        u.delete(flush: true)
        render(status:200)
    }

    private void createUser() {
        JSONObject json = request.JSON
        if(!json.has("name") || json.isNull("name")) {
            response.status = 400
            render("Missing parameter\"name\"")
            return
        }
        if(!json.has("password")) {
            response.status = 400
            render("Missing parameter \"password\"")
            return
        }
        String userName = request.JSON.getString("name")
        String password = request.JSON.getString("password")
        if(userName.length() == 0) {
            response.status = 400
            render("Parameter\"userName\" must have a length greater than 0.")
            return
        }
        if(password.length() == 0) {
            response.status = 400
            render("Parameter\"password\" must have a length greater than 0.")
            return
        }
        if(User.findByUserName(userName) != null) {
            response.status = 409
            render("User with the user name " + userName + " already exists.")
            return
        }

        new User(userName: userName, password: password, money: 5000).save(flush: true)
        render(status: 201)
    }

    private String getUserJson(User u, int precedingSpaces) {
        String preceder = ""
        for(int i = 0; i < precedingSpaces; ++i) {
            preceder += " "
        }

        String json = preceder + "{\n"
        json += preceder + "  \"name\": \"" + u.userName + "\",\n"
        json += preceder + "  \"password\": \"" + u.password + "\",\n"
        json += preceder + "  \"money\": " + u.money + "\n"
        json += preceder + "}"

        return json
    }
}
