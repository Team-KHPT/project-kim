const chatInput = document.getElementById("chat-input")
const chatBtn = document.getElementById("chat-btn")



chatInput.addEventListener('input', function() {
    this.innerText = this.value
    this.style.height = (this.value.split("\n").length * 24) + "px"

    if (this.value.length > 0) {
        chatBtn.removeAttribute("disabled")
    } else {
        chatBtn.setAttribute("disabled", "")
    }
})

chatBtn.addEventListener('click', function (event) {
    event.preventDefault()
    sendMessage(chatInput.value)
})

chatInput.addEventListener('keydown', function(event) {
    if (event.key === "Enter") {
        event.preventDefault()
        if (event.shiftKey) {
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

function sendMessage(inputValue) {
    const userChatItem = makeUserChatItem(inputValue)

    document.getElementById('chats').appendChild(userChatItem)

    // const chatItems = Array.from(document.getElementById('chats').children).map(function(chatItem) {
    //     return {
    //         role: chatItem.classList.contains('assistant') ? 'assistant' : 'user',
    //         content: chatItem.querySelector('.w-full').textContent
    //     }
    // })
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
                assistantChatItem.lastChild.textContent = assistantChatItem.lastChild.textContent + event.data.toString().replaceAll("%20", " ")
            })
            eventSource.addEventListener('function', function(event) {
                console.log(event.data)
            })
            eventSource.addEventListener('complete', function(event) {
                eventSource.close();
            })
            eventSource.onerror = function(error) {
                console.log('Error: ', error);
            }
            // const data = await response.json()
            //
            // const assistantChatItem = makeAssistantChatItem(data.content)
            //
            // document.getElementById('chats').appendChild(assistantChatItem)
        })
}

function makeUserChatItem(chat) {
    const userChatItem = document.createElement('div')
    userChatItem.classList.add('user', 'flex', 'space-x-3')

    const userImg = document.createElement('img')
    userImg.classList.add('w-7', 'h-7', 'rounded-lg')
    userImg.src = getUserImage()

    const userText = document.createElement('div')
    userText.classList.add('w-full', 'p-5', 'bg-pink-100', 'rounded-lg')
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

    const assistantText = document.createElement('div')
    assistantText.classList.add('w-full', 'p-5', 'bg-violet-100', 'rounded-lg')
    assistantText.textContent = chat

    assistantChatItem.appendChild(assistantImg)
    assistantChatItem.appendChild(assistantText)

    return assistantChatItem
}
