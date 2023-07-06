document.addEventListener("DOMContentLoaded", function () {
    const selectTags = document.querySelectorAll("select");
    const sendButton = document.getElementById("sendInfoButton");

    // 배열 선언
    let selectValues = {
        workType: [],
        education: [],
        category: [],
        region: []
    };

    fetch("/user/prev")
        .then(async (response) => {
            const res = await response.json()

            const category = res.category.split(",");
            const region = res.region.split(",");
            const education = res.education.split(",");
            const workType = res.type.split(",");

            const modalButton = document.getElementById('modalOpenButton');
            modalButton.addEventListener("click", function () {
                if(category.length === 0) {
                    addList('category', document.getElementById('categorySelect').value);
                } else {
                    for(let i=0; i<category.length; i++) {
                        addList('category',category[i]);
                    }
                }
                if(region.length === 0) {
                    addList('region', document.getElementById('regionSelect').value);
                } else {
                    for(let i=0; i<region.length; i++) {
                        addList('region',region[i]);
                    }
                }
                if(education.length === 0) {
                    addList('education', document.getElementById('educationSelect').value);
                } else {
                    for(let i=0; i<education.length; i++) {
                        addList('education',education[i]);
                    }
                }
                if(workType.length === 0) {
                    addList('workType', document.getElementById('workTypeSelect').value);
                } else {
                    for(let i=0; i<workType.length; i++) {
                        addList('workType',workType[i]);
                    }
                }
            });
        })

    // 배열 Id, 배열에 넣을 값
    const addList = (selectId, selectValue) => {
        const selectTag = document.getElementById(selectId + "Select");
        if (selectValue === '') {
            selectValue = selectTag.value;
        }
        // 숫자가 없다면 -1 반환
        if (selectValues[selectId].findIndex(v => v === selectValue) === -1) {
            selectValues[selectId].push(selectValue);
            console.log(selectValues)

            let selectText = selectTag.querySelector(`[value="${selectValue}"]`).innerText;
            let plusField = document.getElementById(selectId + "Plus").children[0];

            // 새 div 생성
            let newDiv = document.createElement("div");
            newDiv.id = selectValue;
            newDiv.className =
                "info-div font-noto w-40 h-10 bg-stone-200 mt-2 mb-4 mr-2 px-3 py-5 flex items-center justify-between rounded-lg space-x-2 text-gray-600";

            // 선택된 정보를 div의 innerText로 추가
            newDiv.innerText = selectText;

            // 엑스 버튼 생성
            let deleteButton = document.createElement("button");
            deleteButton.className =
                "text-gray-400 bg-transparent hover:text-red-500 rounded-lg text-xs ml-auto inline-flex items-center";

            // SVG 태그 생성
            const svg = document.createElementNS(
                "http://www.w3.org/2000/svg",
                "svg"
            );

            // SVG 속성 추가
            svg.setAttribute("class", "w-5 h-5");
            svg.setAttribute("fill", "currentColor");
            svg.setAttribute("viewBox", "0 0 20 20");
            svg.setAttribute("xmlns", "http://www.w3.org/2000/svg");

            // Path 태그 생성
            const path = document.createElementNS(
                "http://www.w3.org/2000/svg",
                "path"
            );

            // Path 속성 추가
            path.setAttribute("fill-rule", "evenodd");
            path.setAttribute(
                "d",
                "M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z"
            );
            path.setAttribute("clip-rule", "evenodd");

            // Path 태그를 SVG 태그에 추가
            svg.appendChild(path);

            // SVG 태그를 버튼에 삽입
            deleteButton.appendChild(svg);

            // 엑스 버튼을 누르면 해당 div 삭제
            deleteButton.addEventListener("click", function (e) {
                e.stopPropagation(); // 클릭 이벤트가 부모 요소로 전파되는 것을 막음
                selectValues[selectId].splice(selectValues[selectId].findIndex(v => v === newDiv.id), 1);
                newDiv.remove();
            });

            // div에 엑스 버튼 추가
            newDiv.appendChild(deleteButton);

            // 생성된 div를 plusField에 추가
            plusField.appendChild(newDiv);
        }
    }

    selectTags.forEach(function(select) {
        select.addEventListener("change", function(event) {
            addList(event.target.parentElement.id, event.target.value);
        });
    });
    sendButton.addEventListener('click', function() {
        let workTypeArr = selectValues.workType.join();
        let categoryArr = selectValues.category.join();
        let regionArr = selectValues.region.join();
        let educationArr = selectValues.education.join();

        let object = `{"type":"${workTypeArr}","category":"${categoryArr}","region":"${regionArr}","education":"${educationArr}"}`
        let result = JSON.parse(object);

        selectValues.workType = [];
        selectValues.category = [];
        selectValues.region = [];
        selectValues.education = [];
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
            })
    });
});