let data = []


const refreshData = async (isInit = false) => {
    const resultPing = document.getElementById("result-ping")
    if (resultPing !== null) {
        resultPing.classList.remove("flex")
        resultPing.classList.add("hidden")
    }

    await fetch("/result")
        .then(async (res) => {
            data = await res.json()
            console.log(data)
            if (data.length > 0) {
                const resultPing = document.getElementById("result-ping")
                if (resultPing !== null) {
                    resultPing.classList.remove("hidden")
                    resultPing.classList.add("flex")
                }

                updateData()
            }
            if (data.length <= 3 && !isInit) {
                alert("분석 결과가 적습니다. 사전 정보의 범위를 수정해주세요.\n(Or.. maybe you're looking for special jobs..!)")
            }
        })
}

const setData = async (newData) => {
    data = newData
}

const updateData = () => {
    let container = document.getElementById('result-list')
    container.innerHTML = ''

    for (const item of data) {
        let div = document.createElement('a')
        div.setAttribute("data-modal-target", "defaultModal")
        div.setAttribute("data-modal-toggle","defaultModal")
        div.className = "p-2 flex flex-col border rounded-lg space-y-2 text-stone-600 cursor-pointer hover:bg-stone-100 hover:shadow-inner"
        div.addEventListener("click", () => {
            // console.log(item)
            const modalCompany = document.getElementById("modal-company")
            const modalTitle = document.getElementById("modal-title")
            const modalUrl = document.getElementById("modal-url")
            const modalRegion = document.getElementById("modal-region")
            const modalEducation = document.getElementById("modal-education")
            const modalCareer = document.getElementById("modal-career")
            const modalTime = document.getElementById("modal-time")
            const modalSalary = document.getElementById("modal-salary")
            const modalType = document.getElementById("modal-type")

            modalCompany.innerText = item.company
            modalTitle.innerText = item.title
            modalUrl.setAttribute("href", item.url)
            modalRegion.innerHTML = item.location
            modalEducation.innerText = item.education
            modalCareer.innerText = item.experience_level
            modalTime.innerText = "09:00 ~ 18:00"
            modalSalary.innerText = item.salary
            modalType.innerText = item.type
        })

        div.innerHTML = `
                <div class="text-xs truncate text-ellipsis">${item.company}</div>
                <div class="text-black font-bold truncate text-ellipsis">${item.title}</div>
                <div class="flex justify-between">
                  <div class="w-1/3 flex justify-start">
                    <img class="w-4 h-4" src="/icons/location.png" alt="loc">
                    <span class="w-full text-xs truncate text-ellipsis">${item.location}</span>
                  </div>
                  <div class="w-1/3 flex justify-start">
                    <img class="w-4 h-4" src="/icons/profile.png" alt="edu">
                    <span class="text-xs truncate text-ellipsis">${item.education}</span>
                  </div>
                  <div class="w-1/3 flex justify-start">
                    <img class="w-4 h-4" src="/icons/approval.png" alt="years">
                    <span class="text-xs truncate text-ellipsis">${item.experience_level}</span>
                  </div>
                </div>
            `

        container.appendChild(div)
    }
}