const questionField = document.querySelectorAll('.question-field');
const inputField = document.getElementById('chat-input');
questionField.forEach(function (question) {
    question.addEventListener('click', function (event) {
        console.log(event.target.text);
        inputField.value = event.target.text;
        inputHandler(inputField);
    })
});


async function getExamples() {
    const res = await fetch("/chat/example")
    let dataArray = null
    await res.json()
        .then((t) => {
            console.log(t)
            dataArray = t
        })
        .catch(() => console.log(''))

    if (dataArray != null) {
        const chatInput = document.getElementById("chat-input")
        const infoModal = document.getElementById("info-modal")
        chatInput.setAttribute("disabled", "")
        infoModal.setAttribute("disabled", "")
        chatInput.classList.add("cursor-not-allowed")
        infoModal.classList.add("cursor-not-allowed")

        const exampleElements = document.querySelector("#examples div div")

        for (data of dataArray) {
            let exampleElem = document.createElement('a');
            exampleElem.className = 'w-fit bg-white text-violet-600 px-4 py-2 rounded-full border border-violet-300 flex items-center justify-center mt-3 cursor-pointer transition-colors hover:bg-stone-100';
            exampleElem.textContent = data.chat
            exampleElem.setAttribute("chat-id", data.id)

            exampleElem.addEventListener('click', function () {
                const chatId = this.getAttribute("chat-id")

                removeExampleChat()

                const chats = document.getElementById("chats")

                const userChatItem = makeUserChatItem(this.textContent)
                chats.appendChild(userChatItem)

                fetch('/chat/example', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: chatId
                })
                    .then(async (res) => {
                        const data = await res.json()

                        const assistantChatItem = makeAssistantChatItem(data.response)
                        chats.appendChild(assistantChatItem)

                        await setData(JSON.parse(data.result))
                        updateData()
                    })
            })

            exampleElements.appendChild(exampleElem);
        }
    }
}

const p = getExamples()
