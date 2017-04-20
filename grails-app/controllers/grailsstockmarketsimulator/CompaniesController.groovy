package grailsstockmarketsimulator

import org.grails.web.json.JSONObject

class CompaniesController {

    def index(String symbol) {
        switch(request.method) {
            case 'GET':
                if(symbol == null)
                    listCompanies();
                else
                    detailCompany(symbol);
                break;
            case 'POST':
                if(symbol == null)
                    createCompany();
                else
                    response.sendError(404);
                break;
            case 'DELETE':
                if(symbol != null)
                    deleteCompany(symbol);
                else
                    response.sendError(404);
                break;
            case 'PUT':
                if(symbol != null)
                    updateCompany(symbol);
                else
                    response.sendError(404);
                break;
            default:
                response.sendError(404);
        }
    }

    private void listCompanies() {
        Company[] companies = Company.findAll();
        String json = "[\n";
        for(int i=0; i<companies.length; ++i) {
            json += getCompanyJson(companies[i], 2);
            if(i < companies.length-1)
                json += ",";
            json += "\n";
        }
        json += "]";
        response.status = 200;
        render(json);
    }

    private void detailCompany(String symbol) {
        Company c = Company.findBySymbol(symbol);
        if(c == null) {
            response.sendError(404);
            return;
        }
        render(getCompanyJson(c,0));
    }

    private void updateCompany(String symbol) {
        Company c = Company.findBySymbol(symbol);
        if(c == null) {
            response.sendError(404);
            return;
        }
        JSONObject json = request.JSON;
        if(!json.has("name") || json.isNull("name")) {
            response.status = 400;
            render("Missing parameter \"name\"");
            return;
        }
        String name = request.JSON.getString("name");
        if(name.length() == 0) {
            response.status = 400;
            render("Parameter \"name\" must have length greater than 0.")
            return;
        }

        c.name = name;
        if(c.save(flush: true)) {
            render(status:200);
        } else {
            response.sendError(500);
        }
    }

    private void deleteCompany(String symbol) {
        Company c = Company.findBySymbol(symbol);
        if(c == null) {
            response.sendError(404);
            return;
        }
        c.delete(flush: true);
        render(status:200);
    }

    private void createCompany() {
        JSONObject json = request.JSON;
        if(!json.has("name") || json.isNull("name")) {
            response.status = 400;
            render("Missing parameter \"name\"");
            return;
        }
        if(!json.has("symbol")) {
            response.status = 400;
            render("Missing parameter \"symbol\"");
            return;
        }
        String name = json.getString("name");
        String symbol = json.getString("symbol");
        if(name.length() == 0) {
            response.status = 400;
            render("Parameter \"name\" must have length greater than 0.")
            return;
        }
        if(symbol.length() == 0) {
            response.status = 400;
            render("Parameter \"symbol\" must have length greater than 0.")
            return;
        }
        if(symbol.contains(" ") || symbol.contains("/")) {
            response.status = 400;
            render("Parameter \"symbol\" must not contain any spaces or '/' characters.")
            return;
        }

        if(Company.findBySymbol(symbol) != null) {
            response.status = 409;
            render("Company with symbol " + symbol + " already exists.");
            return;
        }

        new Company(name: name, symbol: symbol, stockPrice: 50, stocksAvailable: 100).save(flush: true);
        render(status:201);
    }

    private String getCompanyJson(Company c, int precedingSpaces) {
        String preceder = "";
        for(int i=0; i<precedingSpaces; ++i)
            preceder += " ";

        String json = preceder + "{\n";
        json += preceder + "  \"name\": \"" + c.name + "\",\n";
        json += preceder + "  \"symbol\": \"" + c.symbol + "\",\n";
        json += preceder + "  \"availableStocks\": " + c.stocksAvailable + ",\n";
        json += preceder + "  \"stockPrice\": " + c.stockPrice + "\n";
        json += preceder + "}";

        return json;
    }
}