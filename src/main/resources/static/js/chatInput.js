const chatInput = document.getElementById("chat-input")
const chatBtn = document.getElementById("chat-btn")

let inputAvailable = true


function inputHandler(inp) {
    inp.innerText = inp.value
    inp.style.height = (inp.value.split("\n").length * 24) + "px"

    if (inp.value.length > 0) {
        if (!inputAvailable) {
            return
        }
        chatBtn.removeAttribute("disabled")
    } else {
        chatBtn.setAttribute("disabled", "")
    }
}


chatInput.addEventListener('input', function() {
    inputHandler(this)
})

chatBtn.addEventListener('click', function (event) {
    event.preventDefault()
    if (!inputAvailable) {
        return
    }
    sendMessage(chatInput.value)
    chatInput.value = ''
})

chatInput.addEventListener('keydown', function(event) {
    if (event.key === "Enter") {
        event.preventDefault()
        if (event.shiftKey || !inputAvailable) {
            if ((this.offsetHeight + 24) > 200) {
                this.style.overflowY = ''
            } else {
                this.style.height = (this.offsetHeight + 24) + "px"
            }
            this.value = this.value += '\n'
            return
        }

        sendMessage(this.value)
        this.value = ''
    }
})

function createCustomElement(text) {
    const div = document.createElement('div');
    div.className = 'process w-fit mb-2 p-2 pl-4 pr-16 bg-violet-200 rounded-lg text-sm flex';
    div.innerHTML = `<svg class="animate-spin -ml-1 mr-3 h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24"><circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle><path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path></svg><span>${text}</span>`;

    return div;
}

function sendMessage(inputValue) {
    inputAvailable = false

    const userChatItem = makeUserChatItem(inputValue)

    document.getElementById('chats').appendChild(userChatItem)

    const chatItem = {
        role: "user",
        content: inputValue
    }

    fetch('/chat', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(chatItem)
    })
        .then(async (response) => {
            if (response.status === 401) {
                alert("로그인이 필요합니다.")
                location.href = "/login"
                return
            }
            if (response.status === 500) {
                alert("ERROR")
                return
            }

            const assistantChatItem = makeAssistantChatItem("")

            document.getElementById('chats').appendChild(assistantChatItem)


            const eventSource = new EventSource("/chat/events")
            eventSource.addEventListener('message', function(event) {
                assistantChatItem.lastChild.textContent = assistantChatItem.lastChild.textContent + event.data.toString().replaceAll("%20", " ").replaceAll("%0A", "\n")
                const chats = document.getElementById('chats')

                chats.scrollTo(0, chats.scrollHeight)
            })
            eventSource.addEventListener('function', function(event) {
                console.log(event.data)
            })
            eventSource.addEventListener('process', function(event) {
                console.log(event.data)
                const chats = document.getElementById('chats')
                const processElem = chats.querySelector(".process span")
                if (event.data === "fine") {
                    processElem.textContent = ""
                    return
                }
                if (processElem == null) {
                    const process = createCustomElement(event.data)
                    chats.lastChild.lastChild.appendChild(process)
                } else {
                    processElem.textContent = event.data
                }

            })
            eventSource.addEventListener('result', function(event) {
                console.log(event.data)
                refreshData()
            })
            eventSource.addEventListener('err', function (event) {
                alert(event.data)
                inputAvailable = true
            })
            eventSource.addEventListener('complete', function(event) {
                console.log(event.data)
                eventSource.close();
                inputAvailable = true
            })
            eventSource.onerror = function(error) {
                console.log('Error: ', error);
                inputAvailable = true
            }
        })
}

function makeUserChatItem(chat) {
    const userChatItem = document.createElement('div')
    userChatItem.classList.add('user', 'flex', 'space-x-3')

    const userImg = document.createElement('img')
    userImg.classList.add('w-7', 'h-7', 'rounded-lg')
    userImg.src = getUserImage()

    const userText = document.createElement('span')
    userText.classList.add('w-full', 'p-5', 'bg-pink-100', 'rounded-lg')
    userText.style.whiteSpace = "pre-wrap"
    userText.textContent = chat

    userChatItem.appendChild(userImg)
    userChatItem.appendChild(userText)

    return userChatItem
}

function makeAssistantChatItem(chat) {
    const assistantChatItem = document.createElement('div')
    assistantChatItem.classList.add('assistant', 'flex', 'space-x-3')

    const assistantImg = document.createElement('img')
    assistantImg.classList.add('w-7', 'h-7', 'rounded-lg')
    assistantImg.src = "/images/logo-rev.png"

    const assistantText = document.createElement('span')
    assistantText.classList.add('w-full', 'p-5', 'bg-violet-100', 'rounded-lg')
    assistantText.style.whiteSpace = 'pre-wrap'
    assistantText.textContent = chat

    assistantChatItem.appendChild(assistantImg)
    assistantChatItem.appendChild(assistantText)

    return assistantChatItem
}

window.addEventListener("load", async (event) => {
    const res = await fetch("/chat/all")
    const chats = await res.json()
    console.log(chats)
    for (const chat of chats) {
        if (chat.role === "user") {
            document.getElementById('chats').appendChild(makeUserChatItem(chat.content))
        } else {
            document.getElementById('chats').appendChild(makeAssistantChatItem(chat.content))
        }
    }
    await refreshData()
})
