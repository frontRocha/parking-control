export interface DataNewBlockInterface {
    blockName: string;
    totalVacancies: number;
}

export interface DataVetorBlocks extends DataNewBlockInterface {
    id: string;
    vacancieList: DataVacancie[];
}

export interface DataVacancie extends DataVetorBlocks {
    id: string;
    vacancieNumber: number;
    status: boolean;
}