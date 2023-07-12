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
        const infoModal = document.getElementById("info-modal")
        if (infoModal != null) {
            infoModal.setAttribute("disabled", "")
            infoModal.classList.add("cursor-not-allowed")
        }

        const chatInput = document.getElementById("chat-input")
        chatInput.setAttribute("disabled", "")
        chatInput.classList.add("cursor-not-allowed")

        const exampleElements = document.querySelector("#examples div div")

        for (data of dataArray) {
            let exampleElem = document.createElement('a');
            exampleElem.className = 'w-fit bg-white text-sm md:text-base lg:text-base text-violet-600 px-4 py-2 rounded-3xl border border-violet-300 flex items-center justify-center mt-3 cursor-pointer transition-colors hover:bg-stone-100';
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

                        setTimeout(() => {
                            chats.appendChild(makeAssistantChatItem(`<div class="flex flex-col space-y-2"><div>위 답변은 예시 답변입니다. 모든 기능을 이용하기 위해선 <a class="text-violet-500" href="/login?redirect=${location.href}">로그인</a>을 해주세요.</div><a href="/" class="w-fit px-4 py-2 border border-violet-300 bg-white rounded-3xl text-sm text-violet-500 hover:bg-stone-100">다시하기</a></div>`, false))
                        }, 1500)

                        await setData(JSON.parse(data.result))
                        updateData()

                        const resultPing = document.getElementById("result-ping")
                        if (resultPing !== null) {
                            resultPing.classList.remove("hidden")
                            resultPing.classList.add("flex")
                        }
                    })
            })

            exampleElements.appendChild(exampleElem);
        }
    }
}

const p = getExamples()
