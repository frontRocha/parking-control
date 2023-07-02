import axios, { AxiosPromise, AxiosResponse } from "axios";
import { FieldValues } from 'react-hook-form'
import { UserLoginInterface } from "../../Interfaces/UserLoginInterface/UserLoginInterface";
import apiUrl from "../../Utils/Config";

export class AuthContextService {
    public async authenticateLoginUser(data: UserLoginInterface | FieldValues): Promise<AxiosPromise> {
        const response: AxiosResponse = await axios.post(`${apiUrl}/login`, data, {
                headers: {
                    'Content-Type': 'application/json',
                }
            });

        return response;
    }

    public async authenticateSignupUser(data: UserLoginInterface | FieldValues): Promise<AxiosPromise> {
        const response: AxiosResponse = await axios.post(`${apiUrl}/signup`, data, {
                headers: {
                    'Content-Type': 'application/json',
                }
            });

        return response;
    }

    public async validateData(data: UserLoginInterface | FieldValues): Promise<void> {
        if(!data.login.trim() || !data.login.length) {
            throw new Error("Digite um mail válido");
        }

        if(!data.password || !data.password.trim()) {
            throw new Error("Digite uma senha válida");
        }
    }
}