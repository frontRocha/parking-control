import { UserLoginInterface } from "../../Interfaces/UserLoginInterface/UserLoginInterface";
import { AuthContextService } from "../../Services/AuthContextService/AuthContextService";
import { FieldValues } from 'react-hook-form'

export class LoginController {
    private authContextService: AuthContextService;

    public constructor(authContextService: AuthContextService) {
        this.authContextService = authContextService;
    }

    public async validateData(data: UserLoginInterface | FieldValues): Promise<void> {
        await this.authContextService.validateData(data)
    }
}