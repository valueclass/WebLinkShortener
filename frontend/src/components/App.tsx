import { Route, Routes } from "react-router";
import { BrowserRouter } from "react-router-dom";
import { Account } from "./Account";
import { AppStateProvider } from "./AppState";
import { Login } from "./Login";
import { Logout } from "./Logout";
import { Nav } from "./Nav";

export default function App() {
    return (
        <AppStateProvider>
            <BrowserRouter>
                <Nav />
                <div className="grid grid-cols-12 sm:grid-cols-10">
                    <div className="col-start-2 sm:col-start-2 col-span-10 sm:col-span-8 mt-10">
                        <Routes>
                            <Route path="/login" element={<Login />} />
                            <Route path="/logout" element={<Logout />} />
                            <Route path="/account" element={<Account />} />
                        </Routes>
                    </div>
                </div>
            </BrowserRouter>
        </AppStateProvider>
    );
}
