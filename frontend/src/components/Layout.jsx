import Navbar from "./Navbar";
import Sidebar from "./Sidebar";

function Layout({ children }) {
   return (
     <div style={{ height: "100vh", display: "flex", flexDirection: "column" }}>
       <Navbar />

       <div style={{ display: "flex", flex: 1, overflow: "hidden" }}>
         <Sidebar />
         <div
           style={{
             flex: 1,
             padding: "40px",
             backgroundColor: "#f5f6fa",
             overflowY: "auto",
             width: "100%",
//              display: "flex",
//              justifyContent: "center", // Centering content horizontally
//              alignItems: "center", // Centering content vertically
//              textAlign: "center",
           }}
         >
           {children}
         </div>
       </div>
     </div>
   );
 }

export default Layout;
