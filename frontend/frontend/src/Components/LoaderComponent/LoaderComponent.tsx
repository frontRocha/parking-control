import './LoaderComponent.css'

interface Props {
    color: string;
}

export default function LoaderComponent({ color }: Props) {
    return(
        <div className={`simple-spinner`}>
            <span className={`border-2 border-white`} style={{ borderRightColor: color }}></span>
        </div>
    )
}