function addIconsToChat() {
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
                fetch('/chat/delete', {
                    method: 'DELETE'
                })
                    .then(response => {
                        if (response.ok) {
                            console.log('마지막 인자 두 개 삭제 성공');
                            location.reload()
                        } else {
                            console.log('마지막 인자 두 개 삭제 실패');
                        }
                    })
                    .catch(error => {
                        console.error('요청 중 오류 발생:', error);
                    });

                const parentDiv = btn.parentElement.parentElement.parentElement
                const nextSibling = btn.parentElement.parentElement.parentElement.nextElementSibling;

                parentDiv.remove();
                nextSibling.remove();
            }
        })
    })
}