import { X } from '@phosphor-icons/react'
import { useEffect, useState } from 'react'
import axios, { AxiosResponse } from 'axios'
import apiUrl from '../../../Utils/Config'
import VacanciesPanelModal from './VacanciesPanelModal/VacanciesPanelModal'
import { DataVetorBlocks } from '../../../Interfaces/BlockPanelInterface/BlockPanelInterface'

export default function VacanciesPanel({ id, token }: any) {
    const [isOpen, setIsOpen] = useState(false)
    const [vetor, setVetor] = useState<DataVetorBlocks>()

    useEffect(() => {
        if (id) {
            openPanel()
            fetchData()
            return;
        }

        closePanel()
    }, [id])

    const fetchData = async () => {
        const response: AxiosResponse = await axios.get(`${apiUrl}/block/${id}`, {
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + token
            }
        });

        setVetor(response.data)
    }

    const closePanel = () => {
        setIsOpen(false)
    }

    const openPanel = () => {
        setIsOpen(true)
    }

    return (
        <div className={`top-8 font-poppins z-40 ${isOpen ? "absolute left-0 duration-500" : "absolute left-full duration-300"} w-screen h-full bg-white`}>
            <div className="absolute py-1 rounded-b-none rounded-t-lg -top-[1.89rem] bg-white w-full max-w-[150px] text-center border border-b-[#696e7571]">
                <div className="flex items-center justify-center">
                    <span className="text-sm text-[#7B68EE] relative left-1">Bloco {vetor && vetor.blockName}</span>
                    <span><X className="max-w-[10px] max-h-[10px] relative left-8 text-[#696e75d5] cursor-pointer" onClick={closePanel} /></span>
                </div>
            </div>
            <div className="flex flex-col relative h-[94vh] overflow-auto">
                {vetor && vetor.vacancieList.map((item: any, index: number) => (
                    <div className="border border-x-none border-t-[#292d3450] py-2 px-2 flex justify-between" key={index}>
                        <div className="flex items-center gap-2">
                            <div className={`${item.status ? "bg-red-400" : "bg-green-400"} w-[8px] h-[8px] rounded-full`}></div>
                            <span className="text-[#7B68EE] text-sm">Vaga {item.vacancieNumber}</span>
                        </div>
                        <div className="flex gap-2">
                            {item.status 
                            ? 
                            <>
                                <button className="text-xs bg-red-400 py-2 px-4 text-white rounded-sm">Ver detalhes</button>
                            </> 
                            : 
                            <>
                                <button className="text-xs bg-red-400 py-2 px-4 text-white rounded-sm">Excluir vaga</button>
                                <VacanciesPanelModal blockName={vetor.blockName} blockId={vetor.id} vacancieNumber={item.vacancieNumber} vacancieId={item.id} />
                            </>
                            }
                        </div>
                    </div>
                ))}
            </div>
        </div>
    )
}