import React, { useEffect, useState } from "react";
import axios from "axios";
import TrafficTable from "./TrafficTable";

const TrafficDashboard = () => {
  const [trafficData, setTrafficData] = useState([]);
  const [errorMessage, setErrorMessage] = useState("");

  const fetchTrafficData = async () => {
    try {
      const response = await axios.get("http://localhost:8080/traffic");
      
      // If successful (status 200), set traffic data
      if (Array.isArray(response.data)) {
        setTrafficData(response.data);
        setErrorMessage(""); // clear previous errors
      } else {
        // In case backend returns non-array response (shouldn't happen here)
        setTrafficData([]);
        setErrorMessage("Unexpected data format received.");
      }

    } catch (error) {
      // Axios throws for 4xx/5xx responses here
      console.warn("Request failed:", error);

      setTrafficData([]); // clear any existing data
      if (error.response && typeof error.response.data === "string") {
        setErrorMessage(error.response.data); // error message from backend
      } else {
        setErrorMessage("⚠️ An unknown error occurred. Please try again later.");
      }
    }
  };

  useEffect(() => {
    fetchTrafficData();
    const interval = setInterval(fetchTrafficData, 3000);
    return () => clearInterval(interval);
  }, []);

  return (
    <div style={{ padding: "2rem", fontFamily: "Arial, sans-serif" }}>
      <h1 style={{ textAlign: "center", fontSize: "30px" }}>🚦 Traffic Monitor</h1>

      {errorMessage ? (
        <div style={{
          backgroundColor: "#fff3cd",
          color: "#856404",
          padding: "1rem",
          borderRadius: "8px",
          border: "1px solid #ffeeba",
          marginTop: "2rem",
          textAlign: "center",
          fontSize: "18px"
        }}>
          {errorMessage}
        </div>
      ) : (
        trafficData.length > 0 ? (
          <TrafficTable data={trafficData} />
        ) : (
          <p style={{ textAlign: "center", fontSize: "24px", marginTop: "2rem" }}>
            Loading traffic data...
          </p>
        )
      )}
    </div>
  );
};

export default TrafficDashboard;
