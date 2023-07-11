const selectOptions = {
    region: [
        { value: '101000', label: '서울' },
        { value: '102000', label: '경기' },
        { value: '103000', label: '광주' },
        { value: '104000', label: '대구' },
        { value: '105000', label: '대전' },
        { value: '106000', label: '부산' },
        { value: '107000', label: '울산' },
        { value: '108000', label: '인천' },
        { value: '109000', label: '강원' },
        { value: '110000', label: '경남' },
        { value: '111000', label: '경북' },
        { value: '112000', label: '전남' },
        { value: '113000', label: '전북' },
        { value: '114000', label: '충북' },
        { value: '115000', label: '충남' },
        { value: '116000', label: '제주' }
    ],
    category: [
        { value: '16', label: '기획·전략' },
        { value: '14', label: '마케팅·홍보' },
        { value: '3', label: '회계·재무' },
        { value: '2', label: 'IT개발·데이터' },
        { value: '12', label: '상품기획·MD' },
        { value: '15', label: '디자인' },
        { value: '6', label: '의료' },
        { value: '10', label: '서비스' },
        { value: '11', label: '생산' },
        { value: '19', label: '교육' }
    ],
    education: [
        { value: '0', label: '학력무관' },
        { value: '1', label: '고등학교졸업' },
        { value: '2', label: '전문대학졸업' },
        { value: '3', label: '대학교 졸업' },
        { value: '4', label: '석사 졸업' },
        { value: '5', label: '박사 졸업' }
    ],
    workType: [
        { value: '1', label: '정규직' },
        { value: '2', label: '계약직' },
        { value: '4', label: '인턴직' },
        { value: '6', label: '파견직' },
        { value: '3', label: '병역특례' },
        { value: '9', label: '프리랜서' },
        { value: '5', label: '아르바이트' }
    ]
};


let selectedValueStrings = {
    workType: "",
    education: "",
    category: "",
    region: ""
}

let selectedValues = {
    workType: [],
    education: [],
    category: [],
    region: []
}


async function updatePrevData() {
    await fetchPrevData()
    savePrevData()
}

async function fetchPrevData() {
    const res = await fetch("/user/prev")
    const jsonData = await res.json()

    if (jsonData.category !== null && jsonData.category !== "") selectedValueStrings.category = jsonData.category
    if (jsonData.region !== null && jsonData.region !== "") selectedValueStrings.region = jsonData.region
    if (jsonData.education !== null && jsonData.education !== "") selectedValueStrings.education = jsonData.education
    if (jsonData.workType !== null && jsonData.workType !== "") selectedValueStrings.workType = jsonData.workType
}

function savePrevData() {
    if (selectedValueStrings.category !== "") {
        const categoryValues = selectedValueStrings.category.split(",")
        selectedValues.category = categoryValues.map(value => {
            const label = selectOptions.category.find(option => option.value === value.toString()).label
            return {value: value.toString(), label}
        })
    }
    if (selectedValueStrings.region !== "") {
        const regionValues = selectedValueStrings.region.split(",")
        selectedValues.region = regionValues.map(value => {
            const label = selectOptions.region.find(option => option.value === value.toString()).label
            return { value: value.toString(), label }
        })
    }
    if (selectedValueStrings.education !== "") {
        const educationValues = selectedValueStrings.education.split(",")
        selectedValues.education = educationValues.map(value => {
            const label = selectOptions.education.find(option => option.value === value.toString()).label
            return { value: value.toString(), label }
        })
    }
    if (selectedValueStrings.workType !== "") {
        const workTypeValues = selectedValueStrings.workType.split(",")
        selectedValues.workType = workTypeValues.map(value => {
            const label = selectOptions.workType.find(option => option.value === value.toString()).label
            return { value: value.toString(), label }
        })
    }
}

function addSelectedItemElement(select, selectedItem) {
    const displayArea = document.createElement("div")
    displayArea.className = "info-div font-noto bg-stone-200 my-2 mr-2 p-2 flex items-center justify-between rounded-lg space-x-6 text-gray-600"

    const textElem = document.createElement("span")
    textElem.textContent = selectedItem.label
    displayArea.appendChild(textElem)

    const deleteBtn = document.createElement("button")
    deleteBtn.className = "text-gray-400 bg-transparent hover:text-red-500 rounded-lg text-xs ml-auto inline-flex items-center"

    const svg= document.createElementNS("http://www.w3.org/2000/svg", "svg")
    svg.setAttribute("class", "w-5 h-5")
    svg.setAttribute("fill", "currentColor")
    svg.setAttribute("viewBox", "0 0 20 20")
    svg.setAttribute("xmlns", "http://www.w3.org/2000/svg")

    const path = document.createElementNS("http://www.w3.org/2000/svg", "path")
    path.setAttribute("fill-rule", "evenodd")
    path.setAttribute("d", "M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z")
    path.setAttribute("clip-rule", "evenodd")
    svg.appendChild(path)

    deleteBtn.appendChild(svg)

    deleteBtn.addEventListener('click', function() {
        const selectId = select.id.replace('Select', '');
        const selectedList = selectedValues[selectId];
        const value = selectedItem.value
        const index = selectedList.findIndex(item => item.value === value);

        if (index !== -1) {
            selectedList.splice(index, 1);
            this.parentElement.remove();
        }
    })

    displayArea.appendChild(deleteBtn)
    select.parentNode.parentNode.lastElementChild.lastElementChild.appendChild(displayArea)
}

function addAllSelectedItem(select) {
    select.parentNode.parentNode.lastElementChild.lastElementChild.innerHTML = ''

    const selectId = select.id.replace('Select', '')
    selectedValues[selectId].forEach((selectedItem) => {
        addSelectedItemElement(select, selectedItem)
    })
}

function updateAllSelectedItem() {
    const selects = document.querySelectorAll('select.optionList')
    selects.forEach((select) => {
        addAllSelectedItem(select)
    })
}


document.addEventListener("DOMContentLoaded", async () => {
    await updatePrevData()
    updateAllSelectedItem()

    const selects = document.querySelectorAll('select.optionList')
    selects.forEach((select) => {
        select.addEventListener('change', function(e) {
            const selectId = select.id.replace('Select', '');
            const value = e.target.value;
            if (value === "-1") {
                return
            }

            const shouldAddValue = !selectedValues[selectId].some(item => item.value === value);

            if (shouldAddValue) {
                const label = e.target.options[e.target.selectedIndex].text;
                selectedValues[selectId].push({ value, label });
                addAllSelectedItem(select)
            }
            console.log(selectedValues)
            select.selectedIndex = 0
        })
    })

    const modalButton = document.querySelectorAll('.modalOpenButton');
    modalButton.forEach(function (button){
        button.addEventListener("click", async function () {
            await updatePrevData()
        })
    })

    const sendButton = document.getElementById("sendInfoButton");
    sendButton.addEventListener('click', function() {
        const workTypeArr = selectedValues.workType.map(item => item.value).join()
        const categoryArr = selectedValues.category.map(item => item.value).join()
        const regionArr = selectedValues.region.map(item => item.value).join()
        const educationArr = selectedValues.education.map(item => item.value).join()

        if (!(workTypeArr > 0 && categoryArr > 0 && regionArr > 0 && educationArr > 0)) {
            const isOk = confirm("모두 입력하지 않았습니다. 계속하시겠습니까?")
            if (!isOk) return
        }

        const object = `{"workType":"${workTypeArr}","category":"${categoryArr}","region":"${regionArr}","education":"${educationArr}"}`
        const result = JSON.parse(object);

        fetch('/user/prev', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(result)
        })
            .then(async (res) => {
                if (res.status === 200) {
                    console.log('success')
                } else {
                    console.log('error')
                }
                location.reload()
            })
    });
});