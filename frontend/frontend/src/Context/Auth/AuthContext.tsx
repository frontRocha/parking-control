import { createContext, useEffect, useState } from 'react'
import { Navigate } from 'react-router-dom'
import { FieldValues } from 'react-hook-form'
import { AuthContextController } from '../../Controllers/AuthContextController/AuthContextController';
import { AuthContextService } from '../../Services/AuthContextService/AuthContextService';
import { AuthChildren, AuthContextProviderInterface, ResponseUser } from '../../Interfaces/AuthContextInterface/AuthContextInterface';
import { AxiosResponse } from 'axios';

export const AuthContext: React.Context<AuthContextProviderInterface> = createContext({} as AuthContextProviderInterface);

export default function AuthContextProvider({ children }: AuthChildren) {
    const [user, setUser] = useState<ResponseUser>();
    const [loading, setLoading] = useState<boolean>(true);

    const authContextServiceInstance: AuthContextService = new AuthContextService();
    const authContextControllerInstance: AuthContextController = new AuthContextController(authContextServiceInstance);

    useEffect(() => {
        receiveDataUser();
    }, []);

    const receiveDataUser = (): void => {
        const receiveUser: string | null = getAuthCredentialUser();
        const receiveToken: string | null = getAuthCredentialToken();

        if (receiveUser && receiveToken && receiveUser.trim() && receiveToken.trim()) {
            setUser(JSON.parse(receiveUser));
        }

        hideLoader();
    };

    const signinAuth = async (data: FieldValues): Promise<undefined> => {
        try {
            const response: AxiosResponse = await authContextControllerInstance.authenticateLoginUser(data);
            setAuthCredentialUser(response.data.data);
            setAuthCredentialToken(response.data.token);
            setDataUser(response.data.data);
        } catch (err: unknown) {
            if (err instanceof Error) {
                throw err;
            }
        }
    }

    const signupAuth = async (data: FieldValues): Promise<undefined> => {
        try {
            const response: AxiosResponse = await authContextControllerInstance.authenticateSignupUser(data);
            setAuthCredentialUser(response.data.data);
            setAuthCredentialToken(response.data.token);
            setDataUser(response.data.data);
        } catch (err: unknown) {
            if (err instanceof Error) {
                throw err;
            }
        }
    }

    const runLogout = (): void => {
        localStorage.clear();
        removeDataUser();

        <Navigate to="/login" />;
    };

    const getAuthCredentialUser = (): string | null => {
        return authContextControllerInstance.getAuthCredentialUser();
    }

    const setAuthCredentialUser = (data: ResponseUser): void => authContextControllerInstance.setAuthCredentialUser(data);

    const getAuthCredentialToken = (): string | null => {
        return authContextControllerInstance.getAuthCredentialToken();
    }

    const setAuthCredentialToken = (token: string): void => authContextControllerInstance.setAuthCredentialToken(token);

    const setDataUser = (data: ResponseUser): void => setUser(data);

    const removeDataUser = (): void => setUser(undefined);

    const hideLoader = (): void => setLoading(false);

    return (
        <AuthContext.Provider value={{ isAuthenticated: !!user, user, loading, signinAuth, signupAuth, runLogout }}>
            {children}
        </AuthContext.Provider>
    )
}
