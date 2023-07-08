import { useContext, useEffect, useState } from "react"
import { AuthContext } from "../../Context/Auth/AuthContext";
import { Pen, Trash } from "@phosphor-icons/react";
import ModalBlockPanel from "./ModalBlockPanel/ModalBlockPanel";
import LoaderComponent from "../../Components/LoaderComponent/LoaderComponent";
import { BlockPanelService } from "../../Services/BlockPanelService/BlockPanelService";
import { BlockPanelController } from "../../Controllers/BlockPanelController/BlockPanelController";
import VacanciesPanel from "./VacanciesPanel/VacanciesPanel";
import { DataNewBlockInterface, DataVetorBlocks } from "../../Interfaces/BlockPanelInterface/BlockPanelInterface";



export default function BlockPanel() {

    const { token, user } = useContext(AuthContext)
    const [vetor, setVetor] = useState<DataVetorBlocks[]>([])
    const [loading, setLoading] = useState<boolean>(false)
    const [block, setBlock] = useState<string>("")

    useEffect(() => {
        fetchDataToDatabase()
    }, [])

    const blockPanelServiceInstance: BlockPanelService = new BlockPanelService();
    const blockPanelControllerInstance: BlockPanelController = new BlockPanelController(token, blockPanelServiceInstance);

    const fetchDataToDatabase = async (): Promise<unknown> => {
        try {
            showLoader()
            validateUser()
            const response = await blockPanelControllerInstance.fetchDataToDatabase();

            setVetor(response.data);
        } catch (err: unknown) {
            return err;
        } finally {
            hideLoader();
        }
    }

    const createDataToDatabase = async (data: DataNewBlockInterface): Promise<unknown> => {
        try {
            showLoader()
            validateUser()
            const response = await blockPanelControllerInstance.createDataToDatabase(data);

            fetchDataToDatabase();
        } catch (err: unknown) {
            return err;
        } finally {
            hideLoader();
        }
    }

    const deleteDataToDatabase = async (id: string): Promise<unknown> => {
        try {
            showLoader()
            validateUser()
            const response = await blockPanelControllerInstance.deleteDataToDatabase(id);

            fetchDataToDatabase();
        } catch (err: unknown) {
            return err;
        } finally {
            hideLoader();
        }
    }

    const showLoader = (): void => {
        setLoading(true);
    }

    const hideLoader = (): void => {
        setLoading(false);
    }

    const validateUser = (): void => {
        if (!user || !token || !token.length) {
            throw new Error("User is not found")
        }
    }

    const setVacancieBlock = (id: string) => {
        setBlock(id)
    }

    return (
        <>
            <section className={`${block && "overflow-y-hidden"} bg-[#EFF3F3] min-h-full h-screen min-w-full font-poppins overflow-x-hidden relative`}>
                <VacanciesPanel id={block} token={token} />
                <div className="max-w-[1400px] mx-auto py-10 pl-10 h-full">
                    <div>
                        <h1 className="text-xl text-[#292d3450]">Zona de controle</h1>
                    </div>
                    {loading ?
                        <div className="w-full h-full flex items-center justify-center">
                            <LoaderComponent color="#7B68EE" />
                        </div> :
                        <div className="pt-8 px-8 w-full grid grid-cols-4 gap-16">
                            {vetor.map((item: DataVetorBlocks) => (
                                <div className="max-w-[278px] max-h-[115px] shadow-2xl shadow-[#0000000e]">
                                    <div className="w-full h-full bg-white border border-[#7B68EE] rounded-md rounded-b-none py-3 px-3 font-light flex flex-col gap-8">
                                        <div className="flex justify-between items-center">
                                            <div>
                                                <h3 className="text-[#7B68EE] text-xl leading-none">Bloco {item.blockName[0].toUpperCase()}</h3>
                                                <p className="text-sm text-[#292d34]">{item.totalVacancies} Vagas</p>
                                            </div>
                                            <div className="flex gap-2">
                                                <div>
                                                    <Pen className="rounded-md bg-[#7B68EE] max-w-[35px] max-h-[35px] px-2 py-2 text-white text-sm" size={20} />
                                                </div>
                                                <div onClick={() => deleteDataToDatabase(item.id)}>
                                                    <Trash className="rounded-md bg-red-400 max-w-[35px] max-h-[35px] px-2 py-2 text-white text-sm" size={20} />
                                                </div>
                                            </div>
                                        </div>
                                        <div>
                                            <p className="text-sm text-[#292d34]">Disponíveis: {item.totalVacancies}</p>
                                        </div>
                                    </div>
                                    <div>
                                        <button onClick={() => setVacancieBlock(item.id)} className="w-full py-[0.63rem] bg-[#7B68EE] text-white text-md font-light text-center rounded-t-none rounded-md leading-none">
                                            Ver mais
                                        </button>
                                    </div>
                                </div>
                            ))}
                        </div>
                    }
                </div>
                <ModalBlockPanel dataForm={createDataToDatabase} />

            </section>
        </>
    )
}