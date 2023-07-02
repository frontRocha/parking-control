import { useContext } from "react"
import { BrowserRouter, Route, Routes, Navigate } from "react-router-dom"
import AuthContextProvider, { AuthContext } from "../Context/Auth/AuthContext"
import Login from "../Pages/Login/Login"
import Panel from "../Pages/Vacancies/Vacancie"
import Signup from "../Pages/Signup/Signup"
import LoaderContext from "../Context/Auth/LoaderContext/LoaderContext"

export default function Router() {

    const AuthenticatedUserLogout = ({ children }: any) => {

        const { loading, isAuthenticated } = useContext(AuthContext)

        if(loading) {
            return <LoaderContext />
        }

        if(!isAuthenticated) {
            return <Navigate to="/login" />
        }

        return children;
    }

    const AuthenticatedUserSignin = ({ children }: any) => {
        const { loading, isAuthenticated } = useContext(AuthContext)

        if(loading) {
            return <LoaderContext />
        }

        if(isAuthenticated) {
            return <Navigate to="/panel" />
        }

        return children;
    }

    return (
        <AuthContextProvider>
            <BrowserRouter>
                <Routes>
                    <Route path="/login" element={<AuthenticatedUserSignin><Login /></AuthenticatedUserSignin>} />
                    <Route path="/signup" element={<AuthenticatedUserSignin><Signup /></AuthenticatedUserSignin>} />
                    <Route path="/panel" element={<AuthenticatedUserLogout><Panel /></AuthenticatedUserLogout>} />
                </Routes>
            </BrowserRouter>
        </AuthContextProvider>
    )
}