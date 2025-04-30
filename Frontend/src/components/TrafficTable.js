// src/components/TrafficTable.js
import React from "react";

const TrafficTable = ({ data }) => {
  return (
    <table className="w-full border border-collapse border-gray-300">
      <thead>
        <tr className="bg-gray-200">
          <th className="px-4 py-2 border border-gray-300">IP Address</th>
          <th className="px-4 py-2 border border-gray-300">Request Count</th>
          <th className="px-4 py-2 border border-gray-300">Limit</th>
        </tr>
      </thead>
      <tbody>
        {data
          .filter((item) => item.requestCount < item.limit)
          .map((item, index) => (
            <tr key={index} className="text-center">
              <td className="px-4 py-2 border border-gray-300">
                {item.ipAddress}
              </td>
              <td className="px-4 py-2 border border-gray-300">
                {item.requestCount}
              </td>
              <td className="px-4 py-2 border border-gray-300">{item.limit}</td>
            </tr>
          ))}
      </tbody>
    </table>
  );
};

export default TrafficTable;
