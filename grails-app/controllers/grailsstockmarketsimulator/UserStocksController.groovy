package grailsstockmarketsimulator

import org.grails.web.json.JSONObject
import grailsstockmarketsimulator.StockReaderService

class UserStocksController {

    def index(String userName, String symbol) {

        StockReaderService.updateStockPrices();

        User user = User.findByUserName(userName);
        if(user == null) {
            response.sendError(404);
            return;
        }
        switch (request.method) {
            case 'GET':
                if(symbol == null)
                    listUserStocks(user);
                else
                    listUserStocksForCompany(user, symbol);
                break;
            case 'POST':
                if(symbol != null)
                    parseAction(user, symbol);
                else
                    response.sendError(404)
                break;
            default:
                response.sendError(404)
        }
    }

    private void listUserStocks(User user) {
        Stock[] stocks = user.stocks;
        String json = "{\n";

        for(int i=0; i<stocks.length; ++i) {
            Stock stock = stocks[i];
            json += "  \"" + stock.company.symbol + "\": {\n";
            json += "    \"stocks\": " + stock.stocks + ",\n";
            json += "    \"averagePrice\": " + stock.averagePrice + "\n";
            json += "  }";
            if(i < stocks.length - 1)
                json += ",";
            json += "\n";
        }

        json += "}";
        render(json);
    }

    private void listUserStocksForCompany(User user, String symbol) {
        Company company = Company.findBySymbol(symbol);
        if(company == null) {
            response.sendError(404);
            return;
        }
        Stock stock = Stock.findByUserAndCompany(user, company);
        if(stock == null) {
            response.sendError(404);
            return;
        }

        String json = "{\n";
        json += "  \"stocks\": " + stock.stocks + ",\n";
        json += "  \"averagePrice\": " + stock.averagePrice + "\n";
        json += "}";
        render(json);
    }

    private void parseAction(User user, String symbol) {
        Company company = Company.findBySymbol(symbol);
        if(company == null) {
            response.sendError(404);
            return;
        }
        JSONObject json = request.JSON
        if(!json.has("action") || json.isNull("action")) {
            response.status = 400
            render("Missing parameter \"action\".")
            return;
        }
        String action = json.getString("action");
        if(action.equals("buy")) {
            if (company.stocksAvailable > 0 && user.money >= company.stockPrice) {
                company.stocksAvailable--;
                company.save();
                user.money -= company.stockPrice;
                user.save();
                Stock s = Stock.findByUserAndCompany(user, company);
                if (s == null) {
                    s = new Stock(user: user, company: company, stocks: 0, averagePrice: 0.0).save(flush: true);
                }
                s.averagePrice = (s.averagePrice * s.stocks + company.stockPrice) / (s.stocks + 1);
                s.stocks += 1;
                s.save(flush: true);
                render("{ \"status\": \"success\" }");
            } else {
                render("{ \"status\": \"failed\" }");
            }
        } else if(action.equals("sell")) {
            Stock s = Stock.findByUserAndCompany(user, company);
            if(s == null || s.stocks == 0) {
                render("{ \"status\": \"failed\" }");
                return;
            }
            s.stocks -= 1;
            if(s.stocks > 0)
                s.save(flush: true);
            else
                s.delete(flush: true);
            user.money += company.stockPrice;
            company.stocksAvailable += 1;
            user.save(flush: true);
            company.save(flush: true);
            render("{ \"status\": \"success\" }");
        } else {
            response.status = 400;
            render("Parameter \"action\" must have value \"buy\" or \"sell\".");
            return;
        }
    }
}
