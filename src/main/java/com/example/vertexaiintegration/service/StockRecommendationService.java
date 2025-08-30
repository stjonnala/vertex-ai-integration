package com.example.vertexaiintegration.service;

import com.example.vertexaiintegration.model.Stock;
import com.example.vertexaiintegration.model.PerplexityResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Service for getting stock recommendations based on Warren Buffett's investment strategy.
 */
@Service
public class StockRecommendationService {

    private static final Logger log = LoggerFactory.getLogger(StockRecommendationService.class);
    private static final String STOCK_RECOMMENDATION_QUERY = 
            "Please provide a list of the top 10 stocks that are currently available at a good price " +
            "and align with Warren Buffett's investment strategy. For each stock, include the ticker symbol, " +
            "company name, current price, a brief reason for recommendation based on Buffett's principles, " +
            "and potential upside percentage. Format the response as a numbered list with the following format for each stock:\n\n" +
            "1. [TICKER]\n" +
            "Current Price: $[PRICE]\n" +
            "Company: [COMPANY NAME]\n" +
            "Why Warren Buffett Would Like It: [REASON]\n" +
            "Potential Upside: [PERCENTAGE]%\n\n" +
            "Please use real, current market prices for each stock.";

    private final PerplexityService perplexityService;

    // Thread-safe list to store stock recommendations
    private final List<Stock> stockRecommendations = new CopyOnWriteArrayList<>();

    // Last update timestamp
    private long lastUpdateTimestamp = 0;

    public StockRecommendationService(PerplexityService perplexityService) {
        this.perplexityService = perplexityService;
    }

    /**
     * Gets the current stock recommendations.
     * If no recommendations are available, it will query Perplexity AI.
     *
     * @return List of stock recommendations
     */
    public List<Stock> getStockRecommendations() {
        // If no recommendations are available, query Perplexity AI
        if (stockRecommendations.isEmpty()) {
            updateStockRecommendations();
        }
        return new ArrayList<>(stockRecommendations);
    }

    /**
     * Gets the timestamp of the last update.
     *
     * @return Timestamp of the last update in milliseconds
     */
    public long getLastUpdateTimestamp() {
        return lastUpdateTimestamp;
    }

    /**
     * Scheduled task to update stock recommendations every 5 minutes.
     */
    @Scheduled(fixedRate = 300000) // 5 minutes = 300,000 milliseconds
    public void updateStockRecommendations() {
        log.info("Updating stock recommendations...");

        try {
            // Query Perplexity AI for stock recommendations
            PerplexityResponse response = perplexityService.querySimple(STOCK_RECOMMENDATION_QUERY);

            if ("success".equals(response.getStatus()) && response.getAnswer() != null) {
                // Parse the response and update the stock recommendations
                List<Stock> newRecommendations = parseStockRecommendations(response.getAnswer());

                // Update the stock recommendations
                stockRecommendations.clear();
                stockRecommendations.addAll(newRecommendations);

                // Update the last update timestamp
                lastUpdateTimestamp = System.currentTimeMillis();

                log.info("Stock recommendations updated successfully. Found {} recommendations.", newRecommendations.size());
            } else {
                log.error("Failed to get stock recommendations from Perplexity AI: {}", 
                        response.getErrorMessage() != null ? response.getErrorMessage() : "Unknown error");
            }
        } catch (Exception e) {
            log.error("Error updating stock recommendations: {}", e.getMessage(), e);
        }
    }

    /**
     * Parses the response from Perplexity AI to extract stock recommendations.
     *
     * @param response The response from Perplexity AI
     * @return List of stock recommendations
     */
    private List<Stock> parseStockRecommendations(String response) {
        List<Stock> recommendations = new ArrayList<>();

        try {
            // First try to parse using the format from the issue description
            Pattern stockPattern = Pattern.compile("(\\d+)\\. ([A-Z\\.]+)\\s*\\n?Current Price: \\$(\\d+\\.?\\d*)\\s*\\n?Why Warren Buffett Would Like It: (.+?)\\s*\\n?Potential Upside: (\\d+\\.?\\d*)%", Pattern.DOTALL);
            Matcher stockMatcher = stockPattern.matcher(response);

            while (stockMatcher.find() && recommendations.size() < 10) {
                try {
                    String ticker = stockMatcher.group(2).trim();
                    double price = Double.parseDouble(stockMatcher.group(3));
                    String reason = stockMatcher.group(4).trim();
                    double upside = Double.parseDouble(stockMatcher.group(5));

                    // Extract company name from the reason if possible
                    String companyName = ticker;
                    Pattern companyPattern = Pattern.compile("\\*\\*Company:\\*\\* ([^\\*]+)|Company: ([^\\n]+)|\\*\\*Company\\*\\*: ([^\\*]+)");
                    Matcher companyMatcher = companyPattern.matcher(reason);
                    if (companyMatcher.find()) {
                        String match = companyMatcher.group(1);
                        if (match == null) match = companyMatcher.group(2);
                        if (match == null) match = companyMatcher.group(3);
                        if (match != null) {
                            companyName = match.trim();
                        }
                    }

                    // Create the stock object
                    Stock stock = new Stock(ticker, companyName, price, reason, upside);
                    recommendations.add(stock);
                    log.debug("Parsed stock: {}", stock);
                } catch (NumberFormatException e) {
                    log.warn("Failed to parse number in stock data: {}", e.getMessage());
                }
            }

            // If we couldn't parse any stocks with the first pattern, try another approach
            if (recommendations.isEmpty()) {
                // Try to match the format with ticker at the beginning of a line
                Pattern altStockPattern = Pattern.compile("(\\d+)\\. ([A-Z\\.]+)\\s*\\n(.*?)Current Price: \\$(\\d+\\.?\\d*)\\s*\\n(.*?)Potential Upside: (\\d+\\.?\\d*)%", Pattern.DOTALL);
                Matcher altStockMatcher = altStockPattern.matcher(response);

                while (altStockMatcher.find() && recommendations.size() < 10) {
                    try {
                        String ticker = altStockMatcher.group(2).trim();
                        String middleText = altStockMatcher.group(3);
                        double price = Double.parseDouble(altStockMatcher.group(4));
                        String reason = altStockMatcher.group(5).trim();
                        double upside = Double.parseDouble(altStockMatcher.group(6));

                        // Try to extract company name
                        String companyName = ticker;
                        Pattern companyPattern = Pattern.compile("Company: ([^\\n]+)");
                        Matcher companyMatcher = companyPattern.matcher(middleText);
                        if (companyMatcher.find()) {
                            companyName = companyMatcher.group(1).trim();
                        }

                        // Create the stock object
                        Stock stock = new Stock(ticker, companyName, price, reason, upside);
                        recommendations.add(stock);
                        log.debug("Parsed stock with alternative pattern: {}", stock);
                    } catch (NumberFormatException e) {
                        log.warn("Failed to parse number in stock data (alt pattern): {}", e.getMessage());
                    }
                }
            }

            // If we still couldn't parse any stocks, try the simpler approach
            if (recommendations.isEmpty()) {
                log.warn("Failed to parse stock recommendations using regex. Trying simpler approach...");
                recommendations = parseStockRecommendationsSimple(response);
            }
        } catch (Exception e) {
            log.error("Error parsing stock recommendations: {}", e.getMessage(), e);
        }

        return recommendations;
    }

    /**
     * A simpler approach to parsing stock recommendations when the structured approach fails.
     * This method looks for stock tickers (all caps) and tries to extract information around them.
     *
     * @param response The response from Perplexity AI
     * @return List of stock recommendations
     */
    private List<Stock> parseStockRecommendationsSimple(String response) {
        List<Stock> recommendations = new ArrayList<>();

        try {
            // Try to find numbered list items with stock information
            Pattern numberedListPattern = Pattern.compile("(\\d+)\\. ([A-Z\\.]+)");
            Matcher numberedListMatcher = numberedListPattern.matcher(response);

            while (numberedListMatcher.find() && recommendations.size() < 10) {
                String ticker = numberedListMatcher.group(2).trim();

                // Skip if ticker is too short or too long
                if (ticker.length() < 1 || ticker.length() > 5) {
                    continue;
                }

                // Find the end of this stock's section (next numbered item or end of text)
                int startPos = numberedListMatcher.start();
                int endPos = response.length();
                Matcher nextItemMatcher = numberedListPattern.matcher(response.substring(numberedListMatcher.end()));
                if (nextItemMatcher.find()) {
                    endPos = numberedListMatcher.end() + nextItemMatcher.start();
                }

                // Extract the section for this stock
                String stockSection = response.substring(startPos, endPos);

                // Try to extract company name
                String companyName = ticker;
                Pattern companyPattern = Pattern.compile("Company:\\s*([^\\n]+)");
                Matcher companyMatcher = companyPattern.matcher(stockSection);
                if (companyMatcher.find()) {
                    companyName = companyMatcher.group(1).trim();
                } else {
                    // Try alternative patterns
                    Pattern altCompanyPattern = Pattern.compile("\\*\\*Company\\*\\*:\\s*([^\\*\\n]+)");
                    Matcher altCompanyMatcher = altCompanyPattern.matcher(stockSection);
                    if (altCompanyMatcher.find()) {
                        companyName = altCompanyMatcher.group(1).trim();
                    }
                }

                // Try to extract price
                double price = 0.0;
                Pattern pricePattern = Pattern.compile("Current Price:\\s*\\$?(\\d+\\.?\\d*)");
                Matcher priceMatcher = pricePattern.matcher(stockSection);
                if (priceMatcher.find()) {
                    try {
                        price = Double.parseDouble(priceMatcher.group(1));
                    } catch (NumberFormatException e) {
                        log.warn("Failed to parse price for {}: {}", ticker, e.getMessage());
                        // Use a realistic default price based on the ticker
                        price = getDefaultPriceForTicker(ticker);
                    }
                } else {
                    // Try alternative price pattern
                    Pattern altPricePattern = Pattern.compile("\\$\\s*(\\d+\\.?\\d*)");
                    Matcher altPriceMatcher = altPricePattern.matcher(stockSection);
                    if (altPriceMatcher.find()) {
                        try {
                            price = Double.parseDouble(altPriceMatcher.group(1));
                        } catch (NumberFormatException e) {
                            log.warn("Failed to parse price with alt pattern for {}: {}", ticker, e.getMessage());
                            price = getDefaultPriceForTicker(ticker);
                        }
                    } else {
                        price = getDefaultPriceForTicker(ticker);
                    }
                }

                // Try to extract reason
                String reason = "";
                Pattern reasonPattern = Pattern.compile("Why Warren Buffett Would Like It:\\s*(.+?)\\s*Potential Upside:", Pattern.DOTALL);
                Matcher reasonMatcher = reasonPattern.matcher(stockSection);
                if (reasonMatcher.find()) {
                    reason = reasonMatcher.group(1).trim();
                } else {
                    // Just use the stock section as the reason
                    reason = stockSection.replaceAll("\\n", " ").trim();
                }

                // Try to extract upside
                double upside = 10.0; // Default upside
                Pattern upsidePattern = Pattern.compile("Potential Upside:\\s*(\\d+\\.?\\d*)%");
                Matcher upsideMatcher = upsidePattern.matcher(stockSection);
                if (upsideMatcher.find()) {
                    try {
                        upside = Double.parseDouble(upsideMatcher.group(1));
                    } catch (NumberFormatException e) {
                        log.warn("Failed to parse upside for {}: {}", ticker, e.getMessage());
                    }
                }

                // Create the stock object
                Stock stock = new Stock(ticker, companyName, price, reason, upside);
                recommendations.add(stock);
                log.debug("Parsed stock with simple approach: {}", stock);
            }

            // If we still couldn't parse any stocks, fall back to the original approach
            if (recommendations.isEmpty()) {
                log.warn("Failed to parse with numbered list approach. Falling back to ticker search approach.");
                recommendations = parseStockRecommendationsByTicker(response);
            }
        } catch (Exception e) {
            log.error("Error parsing stock recommendations using simple approach: {}", e.getMessage(), e);
        }

        return recommendations;
    }

    /**
     * Returns a realistic default price for a given ticker symbol.
     * This is used when we can't parse the price from the response.
     *
     * @param ticker The ticker symbol
     * @return A realistic default price for the ticker
     */
    private double getDefaultPriceForTicker(String ticker) {
        // Return realistic prices for common stocks
        switch (ticker) {
            case "AAPL": return 180.0;  // Apple
            case "MSFT": return 330.0;  // Microsoft
            case "AMZN": return 140.0;  // Amazon
            case "GOOGL": return 140.0; // Google
            case "META": return 300.0;  // Meta (Facebook)
            case "TSLA": return 250.0;  // Tesla
            case "BRK.A": return 500000.0; // Berkshire Hathaway Class A
            case "BRK.B": return 370.0;  // Berkshire Hathaway Class B
            case "JPM": return 140.0;   // JPMorgan Chase
            case "JNJ": return 160.0;   // Johnson & Johnson
            case "V": return 220.0;     // Visa
            case "PG": return 150.0;    // Procter & Gamble
            case "UNH": return 470.0;   // UnitedHealth Group
            case "HD": return 330.0;    // Home Depot
            case "BAC": return 30.0;    // Bank of America
            case "MA": return 400.0;    // Mastercard
            case "XOM": return 110.0;   // Exxon Mobil
            case "NVDA": return 400.0;  // NVIDIA
            case "DIS": return 90.0;    // Disney
            case "KO": return 65.0;     // Coca-Cola
            case "PFE": return 30.0;    // Pfizer
            case "CSCO": return 50.0;   // Cisco
            case "WMT": return 60.0;    // Walmart
            case "MRK": return 120.0;   // Merck
            case "CVX": return 160.0;   // Chevron
            case "MCD": return 290.0;   // McDonald's
            case "COST": return 500.0;  // Costco
            default: return 100.0;      // Default price for unknown tickers
        }
    }

    /**
     * Original ticker-based parsing approach.
     * This method looks for stock tickers (all caps) and tries to extract information around them.
     *
     * @param response The response from Perplexity AI
     * @return List of stock recommendations
     */
    private List<Stock> parseStockRecommendationsByTicker(String response) {
        List<Stock> recommendations = new ArrayList<>();

        try {
            // Look for stock tickers (typically 1-5 capital letters)
            Pattern tickerPattern = Pattern.compile("\\b([A-Z]{1,5})\\b");
            Matcher matcher = tickerPattern.matcher(response);

            while (matcher.find() && recommendations.size() < 10) {
                String ticker = matcher.group(1);

                // Skip common words that might be all caps
                if (ticker.equals("I") || ticker.equals("A") || ticker.equals("CEO") || 
                    ticker.equals("CFO") || ticker.equals("AI") || ticker.equals("PE")) {
                    continue;
                }

                // Extract a snippet around the ticker (100 chars before and after)
                int start = Math.max(0, matcher.start() - 100);
                int end = Math.min(response.length(), matcher.end() + 100);
                String snippet = response.substring(start, end);

                // Try to extract company name (often follows the ticker)
                String companyName = ticker;
                Pattern companyPattern = Pattern.compile(ticker + "\\s*[-:)]?\\s*([A-Za-z\\s.,]+)");
                Matcher companyMatcher = companyPattern.matcher(snippet);
                if (companyMatcher.find()) {
                    companyName = companyMatcher.group(1).trim();
                }

                // Try to extract price (look for $ followed by numbers)
                double price = getDefaultPriceForTicker(ticker);
                Pattern pricePattern = Pattern.compile("\\$\\s*(\\d+\\.?\\d*)");
                Matcher priceMatcher = pricePattern.matcher(snippet);
                if (priceMatcher.find()) {
                    try {
                        price = Double.parseDouble(priceMatcher.group(1));
                    } catch (NumberFormatException e) {
                        log.warn("Failed to parse price in ticker approach for {}: {}", ticker, e.getMessage());
                    }
                }

                // Extract a reason (just use the snippet as the reason)
                String reason = snippet.replaceAll("\\n", " ").trim();

                // Add the stock to the recommendations
                recommendations.add(new Stock(ticker, companyName, price, reason, 10.0));
                log.debug("Parsed stock with ticker approach: {}", ticker);
            }
        } catch (Exception e) {
            log.error("Error parsing stock recommendations using ticker approach: {}", e.getMessage(), e);
        }

        return recommendations;
    }
}
