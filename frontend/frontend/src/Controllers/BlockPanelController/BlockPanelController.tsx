import { AxiosResponse } from "axios";
import { DataNewBlockInterface } from "../../Pages/BlockPanel/BlockPanel";
import { BlockPanelService } from "../../Services/BlockPanelService/BlockPanelService";

export class BlockPanelController {
    private token: string;
    private blockPanelService: BlockPanelService

    public constructor(token: string, blockPanelService: BlockPanelService) {
        this.token = token;
        this.blockPanelService = blockPanelService;
    }

    public async fetchDataToDatabase(): Promise<AxiosResponse> {
        return await this.blockPanelService.fetchDataToDatabase(this.token)
    }

    public async createDataToDatabase(data: DataNewBlockInterface): Promise<void> {
        return await this.blockPanelService.createDataToDatabase(this.token, data)
    }

    public async deleteDataToDatabase(id: string): Promise<void> {
        return await this.blockPanelService.deleteDataToDatabase(this.token, id);
    }
}