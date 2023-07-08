import { Fragment, useContext, useState } from 'react'
import { Dialog, Transition } from '@headlessui/react'
import { BookOpenText, X } from '@phosphor-icons/react'
import { FieldValues, useForm } from 'react-hook-form'
import InputMask from 'react-input-mask';
import apiUrl from '../../../../Utils/Config';
import axios, { AxiosResponse } from 'axios';
import { AuthContext } from '../../../../Context/Auth/AuthContext';

export default function VacanciesPanelModal({ blockName, blockId, vacancieNumber, vacancieId }: any) {
    const { handleSubmit, register, reset } = useForm()
    const [isOpen, setIsOpen] = useState<boolean>(false)

    const { token } = useContext(AuthContext)

    const closeModal = () => {
        setIsOpen(false);
        resetFieldsValues()
    }

    const openModal = () => setIsOpen(true);

    const handleSubmitDataForm = async (data: FieldValues) => {
        console.log(data)
        const response: AxiosResponse = await axios.post(`${apiUrl}/allocation/create/${vacancieId}`, data, {
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + token
            }
        });

        console.log(response.data)
        
        resetFieldsValues();
    }

    const resetFieldsValues = () => {
        reset({
            blockName: "",
            totalVacancies: ""
        });
    }

    return (
        <>
            <div className="flex items-center justify-center">
                <button onClick={openModal} className="text-xs bg-[#7B68EE] text-white px-4 py-2 rounded-sm">Criar alocação</button>
            </div>

            <Transition appear show={isOpen} as={Fragment}>
                <Dialog as="div" className="relative z-50" onClose={closeModal}>
                    <Transition.Child
                        as={Fragment}
                        enter="ease-out duration-300"
                        enterFrom="opacity-0"
                        enterTo="opacity-100"
                        leave="ease-in duration-200"
                        leaveFrom="opacity-100"
                        leaveTo="opacity-0"
                    >
                        <div className="fixed inset-0 bg-black bg-opacity-25" />
                    </Transition.Child>

                    <div className="fixed inset-0 overflow-y-auto">
                        <div className="flex min-h-full items-center justify-center p-4 text-center font-poppins">
                            <Transition.Child
                                as={Fragment}
                                enter="ease-out duration-300"
                                enterFrom="opacity-0 scale-95"
                                enterTo="opacity-100 scale-100"
                                leave="ease-in duration-200"
                                leaveFrom="opacity-100 scale-100"
                                leaveTo="opacity-0 scale-95"
                            >
                                <Dialog.Panel className="w-full max-w-md transform overflow-hidden rounded-2xl bg-white text-left align-middle shadow-xl transition-all">
                                    <Dialog.Title
                                        as="h3"
                                        className="text-sm font-medium text-gray-800 border-b-[1px] border-[#cacccf] py-2 px-2 flex items-center justify-between"
                                    >
                                        <span className="flex gap-2 items-center"><BookOpenText className="w-[25px] h-[25px] px-1 py-1 bg-gray-800 text-white rounded-full" /> Criando alocação</span>
                                        <X className="w-[20px] h-[20px] cursor-pointer" onClick={closeModal} />
                                    </Dialog.Title>
                                    <div className="mt-2 px-4 py-4">
                                        <form className="flex flex-col items-start gap-4 text-gray-800">
                                            <div className="flex flex-col">
                                                <label htmlFor="name">Nome do cliente</label>
                                                <input type="text" id="name" className="outline-none border-b-[1px] border-b-gray-400 text-sm" {...register("name")} />
                                            </div>
                                            <div className="flex flex-col">
                                                <label htmlFor="lastName">Sobrenome do cliente</label>
                                                <input type="text" id="lastName" className="outline-none border-b-[1px] border-b-gray-400 text-sm" {...register("lastName")} />
                                            </div>
                                            <div className="flex flex-col">
                                                <label htmlFor="customerName">Placa do carro</label>
                                                <InputMask className="outline-none border-b-[1px] border-b-gray-400 text-sm" mask="aaa-9a99" maskChar={null} placeholder="ABC-1D23" {...register("plateCar")} />
                                            </div>
                                            <div className="flex flex-col">
                                                <label htmlFor="customerName">Bloco</label>
                                                <input className="outline-none border-b-[1px] border-b-gray-400 text-sm text-[#7B68EE]" disabled value={blockName} />
                                            </div>
                                            <div className="flex flex-col">
                                                <label htmlFor="customerName">Vaga</label>
                                                <input className="outline-none border-b-[1px] border-b-gray-400 text-sm text-[#7B68EE]" disabled value={vacancieNumber} />
                                            </div>
                                        </form>
                                    </div>
                                    <div className="px-2 pb-2 mt-4 flex justify-end gap-2">
                                        <button
                                            type="button"
                                            className="inline-flex justify-center rounded-md border border-transparent bg-red-400 px-4 py-2 text-sm font-light text-white hover:bg-red-500"
                                            onClick={closeModal}
                                        >
                                            Cancelar
                                        </button>
                                        <button
                                            type="button"
                                            className="inline-flex justify-center rounded-md border border-[#7B68EE] bg-white px-4 py-2 text-sm font-light text-[#7B68EE] hover:text-white hover:bg-[#5647aa]"
                                            onClick={handleSubmit(handleSubmitDataForm)}
                                        >
                                            Confirmar
                                        </button>
                                    </div>
                                </Dialog.Panel>
                            </Transition.Child>
                        </div>
                    </div>
                </Dialog>
            </Transition>
        </>
    )
}