const chatInput = document.getElementById("chat-input")

chatInput.addEventListener('input', function() {
    this.innerText = this.value
    this.style.height = (this.value.split("\n").length * 24) + "px"

    const chatBtn = document.getElementById("chat-btn")

    if (this.value.length > 0) {
        chatBtn.removeAttribute("disabled")
    } else {
        chatBtn.setAttribute("disabled", "")
    }
})


chatInput.addEventListener('keydown', function(event) {
    if (event.key === "Enter") {
        event.preventDefault()
        if (event.shiftKey) {
            console.log("shift", this.value, this.style.height)
            this.style.height = (this.offsetHeight + 24) + "px"
            if ((this.offsetHeight + 24) > 200) {
                this.style.overflowY = ''
            }
            this.value = this.value += '\n'
            return
        }

        const inputValue = this.value
        this.value = ''

        const userChatItem = document.createElement('div')
        userChatItem.classList.add('user', 'flex', 'space-x-3')

        const userImg = document.createElement('img')
        userImg.classList.add('w-7', 'h-7', 'rounded-lg')

        setUserImage()
        userImg.src = getUserImage()

        const userText = document.createElement('div')
        userText.classList.add('w-full', 'p-5', 'bg-pink-100', 'rounded-lg')
        userText.textContent = inputValue

        userChatItem.appendChild(userImg)
        userChatItem.appendChild(userText)

        document.getElementById('chats').appendChild(userChatItem)

        const chatItems = Array.from(document.getElementById('chats').children).map(function(chatItem) {
            return {
                role: chatItem.classList.contains('assistant') ? 'assistant' : 'user',
                content: chatItem.querySelector('.w-full').textContent
            }
        })

        fetch('/chat', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(chatItems)
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
                const data =  await response.json()

                const assistantChatItem = document.createElement('div')
                assistantChatItem.classList.add('assistant', 'flex', 'space-x-3')

                const assistantImg = document.createElement('img')
                assistantImg.classList.add('w-7', 'h-7', 'rounded-lg')
                assistantImg.src = "/images/logo-rev.png"

                const assistantText = document.createElement('div')
                assistantText.classList.add('w-full', 'p-5', 'bg-violet-100', 'rounded-lg')
                assistantText.textContent = data.content

                assistantChatItem.appendChild(assistantImg)
                assistantChatItem.appendChild(assistantText)

                document.getElementById('chats').appendChild(assistantChatItem)
            })
    }
})