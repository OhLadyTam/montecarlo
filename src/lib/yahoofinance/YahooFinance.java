
package yahoofinance;

import histquotes.HistQuotesRequest;
import histquotes.Interval;
import quotes.fx.FxQuote;
import quotes.fx.FxQuotesRequest;
import quotes.stock.StockQuotesData;
import quotes.stock.StockQuotesRequest;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;


public class YahooFinance {
    
    public static final String QUOTES_BASE_URL = System.getProperty("yahoofinance.baseurl.quotes", "http://download.finance.yahoo.com/d/quotes.csv");
    public static final String HISTQUOTES_BASE_URL = System.getProperty("yahoofinance.baseurl.histquotes", "https://ichart.yahoo.com/table.csv");
    public static final String HISTQUOTES2_ENABLED = System.getProperty("yahoofinance.histquotes2.enabled", "true");
    public static final String HISTQUOTES2_BASE_URL = System.getProperty("yahoofinance.baseurl.histquotes2", "https://query1.finance.yahoo.com/v7/finance/download/");
    public static final String HISTQUOTES2_SCRAPE_URL = System.getProperty("yahoofinance.scrapeurl.histquotes2", "https://finance.yahoo.com/quote/%5EGSPC/options");
    public static final String HISTQUOTES2_CRUMB_URL = System.getProperty("yahoofinance.crumburl.histquotes2", "https://query1.finance.yahoo.com/v1/test/getcrumb");
    public static final String HISTQUOTES2_CRUMB = System.getProperty("yahoofinance.crumb", "");
    public static final String HISTQUOTES2_COOKIE = System.getProperty("yahoofinance.cookie", "");
    public static final String QUOTES_CSV_DELIMITER = ",";
    public static final String TIMEZONE = "America/New_York";
    
    public static final int CONNECTION_TIMEOUT = 
            Integer.parseInt(System.getProperty("yahoofinance.connection.timeout", "10000"));
    
    public static final Logger logger = Logger.getLogger(YahooFinance.class.getName());
    
    public static Stock get(String symbol) throws IOException {
        return YahooFinance.get(symbol, false);
    }
    

    public static Stock get(String symbol, boolean includeHistorical) throws IOException {
        Map<String, Stock> result = YahooFinance.getQuotes(symbol, includeHistorical);
        return result.get(symbol);
    }
    

    public static Stock get(String symbol, Interval interval) throws IOException {
        return YahooFinance.get(symbol, HistQuotesRequest.DEFAULT_FROM, HistQuotesRequest.DEFAULT_TO, interval);
    }
    
    public static Stock get(String symbol, Calendar from) throws IOException {
        return YahooFinance.get(symbol, from, HistQuotesRequest.DEFAULT_TO, HistQuotesRequest.DEFAULT_INTERVAL);
    }

    public static Stock get(String symbol, Calendar from, Interval interval) throws IOException {
        return YahooFinance.get(symbol, from, HistQuotesRequest.DEFAULT_TO, interval);
    }
    

    public static Stock get(String symbol, Calendar from, Calendar to) throws IOException {
        return YahooFinance.get(symbol, from, to, HistQuotesRequest.DEFAULT_INTERVAL);
    }

    public static Stock get(String symbol, Calendar from, Calendar to, Interval interval) throws IOException {
        Map<String, Stock> result = YahooFinance.getQuotes(symbol, from, to, interval);
        return result.get(symbol);
    }
    

    public static Map<String, Stock> get(String[] symbols) throws IOException {
        return YahooFinance.get(symbols, false);
    }
    

    public static Map<String, Stock> get(String[] symbols, boolean includeHistorical) throws IOException {
        return YahooFinance.getQuotes(Utils.join(symbols, ","), includeHistorical);
    }
    

    public static Map<String, Stock> get(String[] symbols, Interval interval) throws IOException {
        return YahooFinance.getQuotes(Utils.join(symbols, ","), HistQuotesRequest.DEFAULT_FROM, HistQuotesRequest.DEFAULT_TO, interval);
    }
    

    public static Map<String, Stock> get(String[] symbols, Calendar from) throws IOException {
        return YahooFinance.getQuotes(Utils.join(symbols, ","), from, HistQuotesRequest.DEFAULT_TO, HistQuotesRequest.DEFAULT_INTERVAL);
    }
    

    public static Map<String, Stock> get(String[] symbols, Calendar from, Interval interval) throws IOException {
        return YahooFinance.getQuotes(Utils.join(symbols, ","), from, HistQuotesRequest.DEFAULT_TO, interval);
    }
    

    public static Map<String, Stock> get(String[] symbols, Calendar from, Calendar to) throws IOException {
        return YahooFinance.getQuotes(Utils.join(symbols, ","), from, to, HistQuotesRequest.DEFAULT_INTERVAL);
    }
    

    public static Map<String, Stock> get(String[] symbols, Calendar from, Calendar to, Interval interval) throws IOException {
        return YahooFinance.getQuotes(Utils.join(symbols, ","), from, to, interval);
    }
    

    public static FxQuote getFx(String symbol) throws IOException {
        FxQuotesRequest request = new FxQuotesRequest(symbol);
        return request.getSingleResult();
    }
    

    public static Map<String, FxQuote> getFx(String[] symbols) throws IOException {
        FxQuotesRequest request = new FxQuotesRequest(Utils.join(symbols, ","));
        List<FxQuote> quotes = request.getResult();
        Map<String, FxQuote> result = new HashMap<String, FxQuote>();
        for(FxQuote quote : quotes) {
            result.put(quote.getSymbol(), quote);
        }
        return result;
    }
    
    private static Map<String, Stock> getQuotes(String query, boolean includeHistorical) throws IOException {
        StockQuotesRequest request = new StockQuotesRequest(query);
        List<StockQuotesData> quotes = request.getResult();
        Map<String, Stock> result = new HashMap<String, Stock>();
        
        for(StockQuotesData data : quotes) {
            Stock s = data.getStock();
            result.put(s.getSymbol(), s);
        }
        
        if(includeHistorical) {
            for(Stock s : result.values()) {
                s.getHistory();
            }
        }
        
        return result;
    }
    
    private static Map<String, Stock> getQuotes(String query, Calendar from, Calendar to, Interval interval) throws IOException {
        Map<String, Stock> stocks = YahooFinance.getQuotes(query, false);
        stocks = YahooFinance.fetchHistoricalQuotes(stocks, from, to, interval);
        return stocks;
    }
    
    private static Map<String, Stock> fetchHistoricalQuotes(Map<String, Stock> stocks, Calendar from, Calendar to, Interval interval) throws IOException {
        for(Stock s : stocks.values()) {
            s.getHistory(from, to, interval);
        }
        return stocks;
    }
}
