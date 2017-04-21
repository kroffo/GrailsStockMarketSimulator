package grailsstockmarketsimulator

import grails.transaction.Transactional
import org.grails.web.json.JSONObject
import org.grails.web.json.JSONArray

@Transactional
class StockReaderService {

    private static final String BASE_URL = "http://finance.google.com/finance/info?client=ig&q=NASDAQ:";
    private static long previousUpdateTime = 0;

    static void updateStockPrices() {

        Company[] companies = Company.findAll();
        String[] symbols = new String[companies.length];
        for(int i=0; i<companies.length; ++i) {
            symbols[i] = companies[i].symbol;
        }

        long currentTime = System.currentTimeMillis();

        // Don't update more than once every ten seconds
        if(currentTime - previousUpdateTime > 10000)
            previousUpdateTime = currentTime;
        else
            return;

        String urlString = BASE_URL;
        URL url = null;
        HttpURLConnection connection = null;
        JSONArray arr = null;
        for(String sym : symbols)
            urlString += sym + ",";
        try {
            url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "text/plain");

            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            BufferedReader ins = new BufferedReader(
                    new InputStreamReader(
                            connection.getInputStream()
                    )
            );

            String inputLine;
            String response = "";
            while ((inputLine = ins.readLine()) != null) {
                response = response + inputLine + "\n";
            }
            ins.close();
            if(response == null) {
                return;
            }
            // the url returns two '/' characters at the start.
            // Remove them for valid JSON.
            response = response.substring(3);
            arr = new JSONArray(response);
        } catch(IOException e) {
            return;
        } finally {
            connection.disconnect();
        }

        if(arr != null) {
            int length = arr.length();
            for(int i=0; i<length; ++i) {
                JSONObject obj = arr.getJSONObject(i);
                String sym = obj.getString("t");
                String[] priceStringComponents = obj.getString("l").split(",");
                String priceString = "";
                for(int j=0; j<priceStringComponents.length; ++j)
                    priceString += priceStringComponents[j];
                double price = Double.parseDouble(priceString);
                Company c = Company.findBySymbol(sym);
                c.stockPrice = price;
                c.save();
            }
        }
    }
}
