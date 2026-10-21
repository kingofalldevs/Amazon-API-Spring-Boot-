(function () {
    const chat = document.querySelector('.chat');
    const form = document.getElementById('chat-form');
    const input = document.getElementById('message');
    const sendButton = document.getElementById('send');
    const messages = document.getElementById('messages');
    const status = document.getElementById('status');

    const conversationId = chat.dataset.conversationId;
    let busy = false;

    function clearStatus() {
        if (status) {
            status.remove();
        }
    }

    function addMessage(role, text) {
        const bubble = document.createElement('article');
        bubble.className = 'message message-' + role;

        const body = document.createElement('p');
        body.textContent = text;

        bubble.appendChild(body);
        messages.appendChild(bubble);
        bubble.scrollIntoView({ block: 'nearest', behavior: 'smooth' });
        return bubble;
    }

    function setBusy(value) {
        busy = value;
        sendButton.disabled = value;
        input.disabled = value;
        sendButton.textContent = value ? 'Thinking…' : 'Send';
    }

    async function send(message) {
        clearStatus();
        addMessage('user', message);
        const pending = addMessage('assistant', 'Thinking…');
        pending.classList.add('is-pending');

        setBusy(true);
        try {
            const response = await fetch('/chat', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ conversationId: conversationId, message: message })
            });

            if (!response.ok) {
                throw new Error('The server replied with ' + response.status);
            }

            const reply = await response.text();
            pending.querySelector('p').textContent = reply || '(empty reply)';
        } catch (error) {
            pending.querySelector('p').textContent = 'Could not reach the assistant: ' + error.message;
            pending.classList.add('is-error');
        } finally {
            pending.classList.remove('is-pending');
            setBusy(false);
            input.focus();
        }
    }

    form.addEventListener('submit', function (event) {
        event.preventDefault();
        const message = input.value.trim();
        if (!message || busy) {
            return;
        }
        input.value = '';
        send(message);
    });

    // Enter sends, Shift+Enter adds a newline.
    input.addEventListener('keydown', function (event) {
        if (event.key === 'Enter' && !event.shiftKey) {
            event.preventDefault();
            form.requestSubmit();
        }
    });

    input.focus();
})();
