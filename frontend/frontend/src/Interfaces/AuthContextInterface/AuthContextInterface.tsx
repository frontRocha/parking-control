import { FieldValues } from "react-hook-form";

export interface ResponseUser {
    email: string;
    id: number;
}

export interface AuthContextProviderInterface {
    isAuthenticated: boolean;
    user: ResponseUser | undefined;
    token: string;
    loading: boolean;
    signinAuth: (data: FieldValues) => Promise<void | unknown>;
    signupAuth: (data: FieldValues) => Promise<void | unknown>;
    runLogout: () => void;
}

export type AuthChildren = {
    children: React.ReactNode;
};