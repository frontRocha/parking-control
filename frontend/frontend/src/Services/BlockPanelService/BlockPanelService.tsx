import axios, { AxiosResponse } from "axios";
import apiUrl from "../../Utils/Config";
import { DataNewBlockInterface } from "../../Pages/BlockPanel/BlockPanel";

export class BlockPanelService {
    public async fetchDataToDatabase(token: string): Promise<AxiosResponse> {
        const response: AxiosResponse = await axios.get(`${apiUrl}/block`, {
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + token
            }
        });

        return response
    }

    public async createDataToDatabase(token: string, data: DataNewBlockInterface): Promise<void> {
        const response: AxiosResponse = await axios.post(`${apiUrl}/block`, data, {
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + token
            }
        });
    }

    public async deleteDataToDatabase(token: string, id: string): Promise<void> {
        const response: AxiosResponse = await axios.delete(`${apiUrl}/block/${id}`, {
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + token
            }
        });
    }
}