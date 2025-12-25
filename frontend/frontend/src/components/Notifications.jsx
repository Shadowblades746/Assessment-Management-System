import { Toast, ToastContainer } from 'react-bootstrap';
export default function NotificationHolder({ notifications = [], client }) { 

    function close(index) {
        client.removeNotification(index);
    }
    return (
        <ToastContainer className="p-4" position="bottom-end" style={{ zIndex: 9 }}>
            {notifications.map((message) => (
                <>
                    <Toast bg={message.bg} className="text-white" key={message.id} onClick={() => close(message.id)}>
                        <Toast.Header>
                            <strong className="me-auto">{message.title}</strong>
                            <small>{message.subtitle}</small>
                        </Toast.Header>
                        <Toast.Body>{message.text}</Toast.Body>
                    </Toast>
                </>
            ))}
        </ToastContainer>
    )
}