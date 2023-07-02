import { FieldValues } from "react-hook-form";
import { AxiosPromise } from "axios";
import { AuthContextService } from "../../Services/AuthContextService/AuthContextService";
import { UserLoginInterface } from "../../Interfaces/UserLoginInterface/UserLoginInterface";
import { ResponseUser } from "../../Interfaces/AuthContextInterface/AuthContextInterface";


export class AuthContextController {

    private authContextService: AuthContextService;

    public constructor(authContextService: AuthContextService) {
        this.authContextService = authContextService;
    }

    public setAuthCredentialUser(data: ResponseUser): void {
        localStorage.setItem("@AuthCredential:user", JSON.stringify(data));
    }

    public getAuthCredentialUser(): string | null {
        return localStorage.getItem("@AuthCredential:user");
    }

    public setAuthCredentialToken(token: string): void {
        localStorage.setItem("@AuthCredential:token", JSON.stringify(token));
    }
    
    public getAuthCredentialToken(): string | null {
        return localStorage.getItem("@AuthCredential:token");
    }

    public async authenticateLoginUser(data: UserLoginInterface | FieldValues): Promise<AxiosPromise> {
        return await this.authContextService.authenticateLoginUser(data);
    }

    public async authenticateSignupUser(data: UserLoginInterface | FieldValues): Promise<AxiosPromise> {
        return await this.authContextService.authenticateSignupUser(data);
    }
}