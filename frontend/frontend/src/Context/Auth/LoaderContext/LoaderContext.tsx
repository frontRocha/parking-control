import './LoaderContext.css'

export default function LoaderContext() {
    return(
        <div className="preloader bg-[#EFF3F3] w-screen h-full flex flex-col justify-center items-center">
			<svg viewBox="0 0 102 102" fill="none" xmlns="http://www.w3.org/2000/svg">
				<path className="big-circle" d="M101 51C101 78.6142 78.6142 101 51 101C23.3858 101 1 78.6142 1 51" stroke="#7B68EE" stroke-width="3"/>
				<path className="small-circle" d="M91 51C91 28.9086 73.0914 11 51 11C28.9086 11 11 28.9086 11 51" stroke="#7B68EE" stroke-width="3"/>
			</svg>
            <p className='font-poppins mt-2'>Carregando...</p>
		</div>
    )
}