// Stock component to display a single stock recommendation
const StockCard = ({ stock, index }) => {
    return (
        <div className="stock-card">
            <div className="stock-ticker">{index + 1}. {stock.ticker}</div>
            <div className="stock-company">{stock.companyName}</div>
            <div className="stock-price">Current Price: ${stock.currentPrice.toFixed(2)}</div>
            <div className="stock-reason">
                <strong>Why Warren Buffett Would Like It:</strong> {stock.reasonForRecommendation}
            </div>
            <div className="stock-upside">Potential Upside: {stock.potentialUpside.toFixed(1)}%</div>
        </div>
    );
};

// Main App component
const App = () => {
    const [stocks, setStocks] = React.useState([]);
    const [loading, setLoading] = React.useState(true);
    const [error, setError] = React.useState(null);
    const [lastUpdated, setLastUpdated] = React.useState('Never');

    // Function to fetch stock recommendations
    const fetchStocks = async () => {
        setLoading(true);
        setError(null);
        
        try {
            const response = await fetch('/api/stocks/with-metadata');
            
            if (!response.ok) {
                throw new Error(`HTTP error! Status: ${response.status}`);
            }
            
            const data = await response.json();
            setStocks(data.recommendations);
            setLastUpdated(data.lastUpdated);
            setLoading(false);
        } catch (err) {
            setError(`Failed to fetch stock recommendations: ${err.message}`);
            setLoading(false);
        }
    };

    // Function to manually trigger an update
    const triggerUpdate = async () => {
        setLoading(true);
        setError(null);
        
        try {
            const response = await fetch('/api/stocks/update', {
                method: 'POST'
            });
            
            if (!response.ok) {
                throw new Error(`HTTP error! Status: ${response.status}`);
            }
            
            // Wait a moment for the update to process
            setTimeout(fetchStocks, 2000);
        } catch (err) {
            setError(`Failed to trigger update: ${err.message}`);
            setLoading(false);
        }
    };

    // Fetch stocks when component mounts
    React.useEffect(() => {
        fetchStocks();
        
        // Set up auto-refresh every 5 minutes
        const intervalId = setInterval(fetchStocks, 300000);
        
        // Clean up interval on component unmount
        return () => clearInterval(intervalId);
    }, []);

    return (
        <div>
            <button className="refresh-button" onClick={triggerUpdate} disabled={loading}>
                {loading ? 'Updating...' : 'Refresh Recommendations'}
            </button>
            
            <div className="last-updated">
                Last updated: {lastUpdated}
            </div>
            
            {error && (
                <div className="error">
                    {error}
                </div>
            )}
            
            {loading ? (
                <div className="loading">
                    Loading stock recommendations...
                </div>
            ) : (
                <div className="row">
                    {stocks.length === 0 ? (
                        <div className="col-12">
                            <div className="stock-card">
                                <p>No stock recommendations available yet. Please check back later.</p>
                            </div>
                        </div>
                    ) : (
                        stocks.map((stock, index) => (
                            <div className="col-md-6" key={stock.ticker}>
                                <StockCard stock={stock} index={index} />
                            </div>
                        ))
                    )}
                </div>
            )}
        </div>
    );
};

// Render the App component
ReactDOM.render(<App />, document.getElementById('root'));