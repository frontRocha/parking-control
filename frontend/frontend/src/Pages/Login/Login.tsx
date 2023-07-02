import { useContext, useState } from 'react'
import { useForm, FieldValues } from 'react-hook-form'
import { Link } from 'react-router-dom';
import { AuthContext } from '../../Context/Auth/AuthContext'
import { LoginController } from '../../Controllers/LoginController/LoginController';
import { AuthContextService } from '../../Services/AuthContextService/AuthContextService';
import { UserLoginInterface } from '../../Interfaces/UserLoginInterface/UserLoginInterface';
import LoaderComponent from '../../Components/LoaderComponent/LoaderComponent';

import "./Login.css"

export default function Login() {

    const { handleSubmit, register } = useForm();
    const { signinAuth } = useContext(AuthContext)
    const [error, setError] = useState<string>("")
    const [loading, setLoading] = useState<boolean>(false)

    const authContextService: AuthContextService = new AuthContextService()
    const authLoginContoller: LoginController = new LoginController(authContextService)

    const handleData = async (data: UserLoginInterface | FieldValues): Promise<void> => {
        try {
            showLoader()
            await authLoginContoller.validateData(data)
            await signinAuth(data)
        } catch (err: unknown) {
            setError("Email ou senha incorretos")
        } finally {
            hideLoader()
        }
    }

    const showLoader = (): void => setLoading(true);

    const hideLoader = (): void => setLoading(false)

    return (
        <section className="min-h-[100vh] h-full w-full">
            <div className="min-h-screen bg-gradient-to-r from-blue-500 to-indigo-600 flex items-center justify-center py-12 px-4 sm:px-6 lg:px-8 font-poppins">
                <div className="max-w-md w-full bg-white p-8 rounded-lg shadow-xl animate-fade-in-down">
                    <h2 className="text-3xl text-center font-extrabold text-gray-900 mb-6">Entre na sua conta</h2>
                    <form onSubmit={handleSubmit(handleData)} className="space-y-4">
                        <input type="hidden" name="remember" value="true" />
                        <div className="relative">
                            <label htmlFor="email-address" className="sr-only">Email</label>
                            <input {...register('login')} id="email-address" name="login" type="email" autoComplete="email" required
                                className="appearance-none rounded-lg w-full py-2 px-4 placeholder-gray-500 text-gray-900 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 border border-gray-300"
                                placeholder="Email" />
                        </div>
                        <div className="relative">
                            <label htmlFor="password" className="sr-only">Senha</label>
                            <input {...register('password')} id="password" name="password" type="password" autoComplete="current-password" required
                                className="appearance-none rounded-lg w-full py-2 px-4 placeholder-gray-500 text-gray-900 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 border border-gray-300"
                                placeholder="Password" />
                        </div>
                        <div className="flex items-center justify-between">
                            <div className="flex items-center">
                                <input id="remember-me" name="remember-me" type="checkbox"
                                    className="h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300 rounded" />
                                <label htmlFor="remember-me" className="ml-2 text-sm text-gray-900">Lembre de mim</label>
                            </div>
                        </div>
                        <div className="flex items-center justify-center w-full">
                            <span className='text-red-400 text-sm'>{error}</span>
                        </div>
                        <div>
                            <button type="submit"
                                className="w-full bg-gradient-to-r from-blue-600 to-indigo-600 hover:from-blue-700 hover:to-indigo-700 text-white py-2 px-4 rounded-lg font-medium focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 transform hover:scale-105 transition-all duration-300">
                                {!loading ?
                                    <span className="h-full flex items-center justify-center space-x-2">
                                        <span>
                                            <svg className="h-5 w-5 text-blue-500" fill="none" stroke="currentColor" viewBox="0 0 24 24"
                                                xmlns="http://www.w3.org/2000/svg">
                                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                                    d="M9 5v2m.001 0h2m-2 0H9m11 14a2 2 0 01-2 2H6a2 2 0 01-2-2V9a2 2 0 012-2h2.586a1 1 0 01.707.293l3.414 3.414a1 1 0 001.414 0l3.414-3.414A1 1 0 0115.414 7H18a2 2 0 012 2v10z" />
                                            </svg>
                                        </span>
                                        <span>Entrar</span>
                                    </span>
                                    :
                                    <span className='w-full flex items-center justify-center'><LoaderComponent /></span>}

                            </button>
                        </div>
                        <div className="text-center text-gray-500 text-sm">
                            <span>Não possui uma conta ?</span>
                            <Link to="/signup" className="text-blue-600 hover:underline">Criar conta</Link>
                        </div>
                    </form>
                </div>
            </div>
        </section>
    )
}