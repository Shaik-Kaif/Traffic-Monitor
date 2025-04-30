import React from "react";
import ReactDOM from "react-dom/client";
import Marque from "./components/Marque";
import Header from "./components/Header";

import React from "react";
import ReactDOM from "react-dom/client";
import TrafficDashboard from "./components/TrafficDashboard";

const root = ReactDOM.createRoot(document.getElementById("root"));
root.render(
  <React.StrictMode>
    <TrafficDashboard />
  </React.StrictMode>
);



// const App=()=>{
//     return(<>
//         <Marque/>
//         <Header/>
//     </>);
// }
// const root = ReactDOM.createRoot(document.getElementById("root"));
// root.render( <App/> );
