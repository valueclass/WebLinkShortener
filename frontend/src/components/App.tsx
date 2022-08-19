import { Route, Routes } from "react-router";
import { BrowserRouter } from "react-router-dom";
import { AppStateProvider } from "./AppState";
import { Login } from "./Login";
import { Logout } from "./Logout";

export default function App() {
    return (
        <AppStateProvider>
            <BrowserRouter>
                <Routes>
                    <Route path="/login" element={ <Login /> }/>
                    <Route path="/logout" element={ <Logout /> }/>
                </Routes>
            </BrowserRouter>
        </AppStateProvider>
    );
}
