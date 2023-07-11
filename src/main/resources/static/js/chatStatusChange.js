const copyBtns = document.querySelectorAll(".copyButton")
copyBtns.forEach(btn => {
    btn.addEventListener("click", () => {
        const text = btn.parentNode.parentNode.children[0]
        const range = document.createRange();
        range.selectNode(text);
        window.getSelection().removeAllRanges();
        window.getSelection().addRange(range);

        // 클립보드에 복사
        document.execCommand("copy");

        // 선택해제
        window.getSelection().removeAllRanges();
    })
})

const deleteBtns = document.querySelectorAll(".deleteButton")
deleteBtns.forEach(btn => {
    btn.addEventListener("click", () => {
        if (confirm('해당 채팅을 삭제하시겠습니까?')) {
            const parentDiv = btn.parentElement.parentElement.parentElement
            const nextSibling = btn.parentElement.parentElement.parentElement.nextElementSibling;
            parentDiv.remove();
            nextSibling.remove();
        }
    })
})