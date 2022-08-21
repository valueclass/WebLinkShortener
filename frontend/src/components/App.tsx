import { Route, Routes } from "react-router";
import { BrowserRouter } from "react-router-dom";
import { AppStateProvider } from "./AppState";
import { Login } from "./Login";
import { Logout } from "./Logout";
import { Nav } from "./Nav";

export default function App() {
    return (
        <AppStateProvider>
            <BrowserRouter>
                <Nav />
                <div className="grid grid-cols-10">
                    <div className="col-start-2 col-span-8 mt-10">
                        <Routes>
                            <Route path="/login" element={<Login />} />
                            <Route path="/logout" element={<Logout />} />
                        </Routes>
                    </div>
                </div>
            </BrowserRouter>
        </AppStateProvider>
    );
}
