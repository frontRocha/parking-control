import { Fragment, useState } from 'react'
import { Dialog, Transition } from '@headlessui/react'
import { CirclesThreePlus, Plus, X } from '@phosphor-icons/react'
import { FieldValues, useForm } from 'react-hook-form'
import { DataVetorBlocks } from '../../../Interfaces/BlockPanelInterface/BlockPanelInterface';

export interface DataFormProps {
    dataForm: (dataForm: DataVetorBlocks) => void;
}

export default function ModalBlockPanel({ dataForm }: DataFormProps) {
    const { handleSubmit, register, reset } = useForm()
    const [isOpen, setIsOpen] = useState<boolean>(false)
    const [error, setError] = useState<string>("")

    const closeModal = () => {
        setIsOpen(false);
        resetFieldsValues()
    }

    const openModal = () => setIsOpen(true);

    const handleSubmitDataForm = (data: FieldValues) => {
        dataForm(data as DataVetorBlocks);
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
            <div className="fixed flex items-center justify-center">
                <button onClick={openModal} className="fixed bottom-10 right-10 bg-[#7B68EE] text-white px-8 py-3 rounded-md shadow-2xl text-sm shadow-[#0000000e] flex gap-4"><Plus className="w-[20px] h-[20px]" size={0} /> <span>Criar bloco</span></button>
            </div>

            <Transition appear show={isOpen} as={Fragment}>
                <Dialog as="div" className="relative z-10" onClose={closeModal}>
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
                                        <span className="flex gap-3 items-center"><CirclesThreePlus className="w-[20px] h-[20px] bg-gray-800 text-white rounded-full" /> Criando novo bloco</span>
                                        <X className="w-[20px] h-[20px] cursor-pointer" onClick={closeModal} />
                                    </Dialog.Title>
                                    <div className="mt-2 px-4 py-4">
                                        <form className="flex gap-4">
                                            <div className="flex flex-col">
                                                <label className="text-[#292D34] font-light" htmlFor="letterInput">Nome do bloco</label>
                                                <input className="outline-0 border-b-black border-b-[1px] max-w-[30px] text-center" type="text" id="letterInput" placeholder="A" maxLength={1} {...register("blockName")} />
                                            </div>
                                            <div className="flex flex-col">
                                                <label className="text-[#292D34] font-light" htmlFor="numberInput">Quantidade de vagas</label>
                                                <input className="outline-0 border-b-black border-b-[1px] max-w-[50px] text-center" id="numberInput" type="number" placeholder="1" {...register("totalVacancies")} />
                                            </div>
                                        </form>
                                    </div>
                                    <div className="px-2 pb-2 mt-4 flex justify-end gap-2">
                                        <button
                                            type="button"
                                            className="inline-flex justify-center rounded-md border border-transparent bg-red-400 px-4 py-2 text-sm font-medium text-white hover:bg-red-500"
                                            onClick={closeModal}
                                        >
                                            Cancelar
                                        </button>
                                        <button
                                            type="button"
                                            className="inline-flex justify-center rounded-md border border-[#7B68EE] bg-white px-4 py-2 text-sm font-medium text-[#7B68EE] hover:text-white hover:bg-[#5647aa]"
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